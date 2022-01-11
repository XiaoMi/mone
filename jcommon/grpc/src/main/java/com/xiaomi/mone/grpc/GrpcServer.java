/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.mone.grpc;

import com.xiaomi.mone.grpc.common.GrpcServerConfig;
import com.xiaomi.mone.grpc.service.MeshServiceImpl;
import com.xiaomi.mone.grpc.demo.PushMsg;
import com.xiaomi.mone.grpc.server.filter.SimpleServerTransportFilter;
import com.xiaomi.mone.grpc.server.interceptor.ServerMessageInterceptor;
import io.grpc.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Data
@Slf4j
public class GrpcServer {

    private int port;

    private Server server;

    /**
     * 服务列表
     */
    private List<BindableService> serviceList;

    private Runnable shutdowncallBack = () -> {
        log.info("shutdown");
        this.server.shutdown();
    };

    public void start() throws IOException, InterruptedException {
        ExecutorService executor = new ThreadPoolExecutor(GrpcServerConfig.GRPC_POOL_SIZE, GrpcServerConfig.GRPC_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(GrpcServerConfig.GRPC_POOL_QUEUE_SIZE));

        ServerBuilder<?> builder = ServerBuilder
                .forPort(this.port)
                .executor(executor)
                .intercept(new ServerMessageInterceptor())
                .addTransportFilter(new SimpleServerTransportFilter());

        serviceList.stream().forEach(s->builder.addService(s));
        // builder.addService(ProtoReflectionService.newInstance());
        this.server = builder.build();
        Runtime.getRuntime().addShutdownHook(new Thread(this.shutdowncallBack));
        server.start();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            MeshServiceImpl.queueMap.forEach((k, v) -> {
                try {
                    PushMsg msg = PushMsg.newBuilder()
                            .setData(k + ":" + System.currentTimeMillis())
                            .build();
                    v.onNext(msg);
                } catch (Throwable ex) {
                    log.error(ex.getMessage());
                }

            });
        }, 0, 1, TimeUnit.SECONDS);
        server.awaitTermination();
    }
}

