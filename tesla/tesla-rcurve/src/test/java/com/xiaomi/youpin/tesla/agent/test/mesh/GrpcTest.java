package com.xiaomi.youpin.tesla.agent.test.mesh;

import com.google.common.collect.Lists;
import com.xiaomi.mone.grpc.GrpcClient;
import com.xiaomi.mone.grpc.demo.GrpcMeshRequest;
import com.xiaomi.mone.grpc.demo.GrpcMeshResponse;
import com.xiaomi.mone.grpc.demo.MeshAddress;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 1/9/21
 */
public class GrpcTest {

    /**
     * 测试grpc协议,注意要拉起proxy下边的服务
     */
    @Test
    public void testCall() {
        GrpcClient client = new GrpcClient();
        MeshAddress address = MeshAddress.newBuilder().setIp("127.0.0.1").setPort(7779).build();
        GrpcMeshRequest request = GrpcMeshRequest.newBuilder()
                .setApp("demo_one_app")
                .setServiceName("com.xiaomi.mone.mds.service.DemoOneService")
                .setMethodName("demo")
                .setTimeout(2000L)
                .addAllParamTypes(Lists.newArrayList("java.lang.Integer", "java.lang.Integer"))
                .addAllParams(Lists.newArrayList("1", "2"))
                .build();
        GrpcMeshResponse res = client.call(address, request);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    /**
     * 测试proxy连通性
     */
    @Test
    public void testCallVersion() {
        GrpcClient client = new GrpcClient();
        MeshAddress address = MeshAddress.newBuilder().setIp("127.0.0.1").setPort(7779).build();
        GrpcMeshRequest request = GrpcMeshRequest.newBuilder()
                .setMethodName("$version$")
                .build();
        GrpcMeshResponse res = client.call(address, request);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

}
