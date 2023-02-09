package com.xiaomi.mone.grpc;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.mone.grpc.common.GrpcReflectUtils;
import com.xiaomi.mone.grpc.common.InitGrpcService;
import com.xiaomi.mone.grpc.context.GrpcServerContext;
import com.xiaomi.mone.grpc.demo.GrpcMeshRequest;
import com.xiaomi.mone.grpc.demo.GrpcMeshResponse;
import com.xiaomi.mone.grpc.demo.HelloServiceImpl;
import com.xiaomi.mone.grpc.demo.MeshAddress;
import com.xiaomi.mone.grpc.service.MeshServiceImpl;
import io.grpc.BindableService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import run.mone.mesh.bo.SideCarRequest;
import run.mone.mesh.bo.SideCarResponse;
import run.mone.mesh.obs.ClientObs;
import run.mone.mesh.processor.client.GetSideCarInfoProcessor;
import run.mone.mesh.service.SideCarServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Slf4j
public class GrpcTest {

    private String app = "testApp";

    private boolean getName = true;

    /**
     * 模拟服务器
     *
     * @throws IOException
     */
    @Test
    public void testServer() throws IOException {
        InitGrpcService service = new InitGrpcService();
        service.init();
        GrpcServer server = new GrpcServer(new GrpcServerContext());
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
        server.start("8080");
    }



    /**
     * 客户端端直接调用
     */
    @Test
    public void testSideCarClientCall() {
        GrpcClient client = new GrpcClient();
        client.start("127.0.0.1:8080:" + app + ":false");
        SideCarRequest request = SideCarRequest.newBuilder()
                .setCmd("sidecar")
                .build();
        SideCarResponse res = client.call(request);
        System.out.println(new String(res.getData().toByteArray()));
    }





    /**
     * @throws InterruptedException
     */
    @Test
    public void testSideCarClientListen() throws InterruptedException {
        GrpcClient client = new GrpcClient();
        client.start("127.0.0.1:8080:" + app);
        //obs 就可以向server发送信息
        client.listen(new ClientObs(client.getProcessorMap()), true);
    }


    private void sleep(int i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testClient() {
        GrpcClient client = new GrpcClient();
    }


    /**
     * 模拟客户端
     */
    @Test
    public void testMeshClient2() {
        GrpcClient client = new GrpcClient();
        MeshAddress address = MeshAddress.newBuilder()
                .setIp("127.0.0.1")
                .setPort(8080)
                .build();

        client.start(address);
        GrpcMeshRequest request = GrpcMeshRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setServiceName("com.xiaomi.mone.grpc.service.DemoService")
                .setMethodName("sum")
                .addAllParamTypes(Lists.newArrayList("int", "int"))
                .addAllParams(Lists.newArrayList("100", "200"))
                .build();

        IntStream.range(0, 100).forEach(i -> {
            try {
                GrpcMeshResponse res = client.call(request);
                System.out.println(res.getData());
            } catch (Throwable ex) {
                log.error(ex.getMessage());
            }
            sleep(5);
        });

        client.shutdown();
    }
}
