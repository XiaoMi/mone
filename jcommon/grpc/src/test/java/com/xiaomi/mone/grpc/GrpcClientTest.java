package com.xiaomi.mone.grpc;

import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.mone.grpc.task.impl.client.PingTask;
import org.junit.Test;
import run.mone.mesh.bo.SideCarRequest;
import run.mone.mesh.bo.SideCarResponse;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2022/7/4 13:53
 */
public class GrpcClientTest {

    private String app = "testApp";


    /**
     * 测试java 调用golang server
      */
    @Test
    public void testCallGolang() {
        GrpcClient client = new GrpcClient();
        ConcurrentHashMap<String, UdsProcessor<RpcCommand, RpcCommand>> map = new ConcurrentHashMap<>();
        client.setApp(app);
        client.getTaskList().add(new PingTask(app, client));
        client.start("127.0.0.1:50051:" + app);
        SideCarRequest request = SideCarRequest.newBuilder().setCmd("call").build();
        SideCarResponse res = client.call(request);
        System.out.println(res.getMessage());
    }
}
