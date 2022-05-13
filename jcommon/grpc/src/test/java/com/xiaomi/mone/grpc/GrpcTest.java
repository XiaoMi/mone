package com.xiaomi.mone.grpc;

import com.google.common.collect.Lists;
import com.xiaomi.mone.grpc.common.GrpcReflectUtils;
import com.xiaomi.mone.grpc.common.InitGrpcService;
import com.xiaomi.mone.grpc.demo.*;
import com.xiaomi.mone.grpc.service.MeshServiceImpl;
import io.grpc.BindableService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Slf4j
public class GrpcTest {

    @Test
    public void testServer() throws IOException, InterruptedException {
        InitGrpcService service = new InitGrpcService();
        service.init();
        GrpcServer server = new GrpcServer();
        MeshServiceImpl meshService = new MeshServiceImpl();
        meshService.setInvoker((request, observer) -> {
            Object res = GrpcReflectUtils.invokeMethod(request.getServiceName(),
                    request.getMethodName(), request.getParamTypesList().stream().toArray(String[]::new),
                    request.getParamsList().stream().toArray(String[]::new));
            return res;
        });
        List<BindableService> serviceList = Lists.newArrayList(new HelloServiceImpl(), meshService);
        server.setPort(8080);
        server.setServiceList(serviceList);
        server.start();
    }


    @Test
    public void testClient() {
        GrpcClient client = new GrpcClient();
    }

    @Test
    public void testMeshClient2() {
        GrpcClient client = new GrpcClient();
        MeshAddress address = MeshAddress.newBuilder()
                .setIp("127.0.0.1")
                .setPort(8080)
                .build();
        GrpcMeshRequest request = GrpcMeshRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setServiceName("com.xiaomi.mone.grpc.service.DemoService")
                .setMethodName("sum")
                .addAllParamTypes(Lists.newArrayList("int", "int"))
                .addAllParams(Lists.newArrayList("100", "200"))
                .build();

//        new Thread(() -> {
//            try {
//                client.listen(address, request);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();

        IntStream.range(0, 100).forEach(i -> {
            try {
                GrpcMeshResponse res = client.call(address, request);
                System.out.println(res.getData());
            } catch (Throwable ex) {
                log.error(ex.getMessage());
            }
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        client.shutdown();
    }
}
