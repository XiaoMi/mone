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

import com.xiaomi.mone.grpc.demo.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Slf4j
public class GrpcClient {

    private ConcurrentHashMap<String, ManagedChannel> channelMap = new ConcurrentHashMap<>();

    /**
     * 发送消息到服务端
     *
     * @param address
     * @param request
     * @return
     */
    public GrpcMeshResponse call(MeshAddress address, GrpcMeshRequest request) {
        String key = address.getIp() + ":" + address.getPort();
        ManagedChannel ch = channelMap.compute(key, (k, v) -> {
            if (null == v) {
                ManagedChannel channel = ManagedChannelBuilder.forAddress(address.getIp(), address.getPort())
                        .usePlaintext()
                        .build();
                return channel;
            } else {
                return v;
            }
        });
        MeshServiceGrpc.MeshServiceBlockingStub stub = MeshServiceGrpc.newBlockingStub(ch);
        GrpcMeshResponse response = stub.call(request);
        return response;
    }

    /**
     * 监听server推回来的信息
     *
     * @param address
     * @param request
     * @throws InterruptedException
     */
    public void listen(MeshAddress address, GrpcMeshRequest request) throws InterruptedException {
        String key = address.getIp() + ":" + address.getPort();
        ManagedChannel ch = channelMap.compute(key, (k, v) -> {
            if (null == v) {
                ManagedChannel channel = ManagedChannelBuilder.forAddress(address.getIp(), address.getPort())
                        .usePlaintext()
                        .build();
                return channel;
            } else {
                return v;
            }
        });
        MeshServiceGrpc.MeshServiceStub stub = MeshServiceGrpc.newStub(ch);
        while (true) {
            CountDownLatch latch = new CountDownLatch(1);
            stub.listen(request, new StreamObserver<PushMsg>() {
                @Override
                public void onNext(PushMsg value) {
                    System.out.println(value);
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("onError:" + t);
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    System.out.println("completed");
                    latch.countDown();
                }
            });
            latch.await();
            TimeUnit.SECONDS.sleep(5);
        }
    }

    public void shutdown() {
        channelMap.values().stream().forEach(it -> {
            it.shutdown();
        });
    }

}
