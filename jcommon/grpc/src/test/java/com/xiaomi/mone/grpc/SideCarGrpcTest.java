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

import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.mone.grpc.common.InitGrpcService;
import com.xiaomi.mone.grpc.context.GrpcServerContext;
import com.xiaomi.mone.grpc.task.impl.client.PingTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import run.mone.mesh.bo.SideCarRequest;
import run.mone.mesh.bo.SideCarResponse;
import run.mone.mesh.processor.client.GetSideCarInfoProcessor;
import run.mone.mesh.service.SideCarServiceImpl;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/7/3
 */
@Slf4j
public class SideCarGrpcTest {

    private String app = "testApp";

    private boolean getName = true;


    /**
     * 模拟sidecar server端
     *
     * @throws IOException
     */
    @Test
    public void testSideCarServer() throws IOException {
        InitGrpcService service = new InitGrpcService();
        service.init();
        GrpcServer server = new GrpcServer(new GrpcServerContext());
        server.setPort(8080);
        //定期调用getName(通过双向流发过去的)
        if (getName) {
            server.getTaskList().add(() -> {
                try {
                    SideCarRequest request = SideCarRequest.newBuilder().setApp(app).setCmd("sidecar_info").build();
                    SideCarRequest res = ((SideCarServiceImpl) server.getServiceList().get(0)).callSideCar(request);
                    log.info("res:{}", new String(res.getData().toByteArray()));
                } catch (Throwable ex) {
                    log.error(ex.getMessage());
                }
            });
        }
        server.start("");
    }


    @Test
    public void testSideCarClient() throws InterruptedException {
        GrpcClient client = new GrpcClient();
        ConcurrentHashMap<String, UdsProcessor<RpcCommand, RpcCommand>> map = new ConcurrentHashMap<>();
        GetSideCarInfoProcessor processor = new GetSideCarInfoProcessor();
        map.put(processor.cmd(), processor);
        client.setProcessorMap(map);
        client.setApp(app);
        client.getTaskList().add(new PingTask(app, client));
        client.start("127.0.0.1:8080:" + app);
        Thread.currentThread().join();
    }


    @Test
    public void testSimpleServer() {
        GrpcServer server = new GrpcServer(new GrpcServerContext());
        server.getSidecarService().setGrpcConsumer((req, obs) -> {
            log.info("req cmd:{}", req.getCmd());
            SideCarResponse res = SideCarResponse.newBuilder().build();
            obs.onNext(res);
            obs.onCompleted();
        });
        server.start("8080");
    }


    @Test
    public void testSimpleClient() {
        GrpcClient client = new GrpcClient();
        client.start("127.0.0.1:8080:" + app);
        IntStream.range(0, 3).forEach(i -> {
            SideCarResponse res = client.call(SideCarRequest.newBuilder().putAttachments("grpc", "true").setCmd("test").build());
            log.info("res:{}", res);
        });
    }


}
