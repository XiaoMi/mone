package com.xiaomi.miapi.test;

import com.xiaomi.miapi.util.GrpcReflectionCall;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2022/9/2 14:34
 */
public class GrpcTest {

    @Test
    public void testCall() {
        GrpcReflectionCall call = new GrpcReflectionCall();
        String res = call.call("127.0.0.1:5566", "com.xiaomi.sautumn.server.grpc.PSLService.call", "{}", 1000);
        System.out.println(res);
    }

    @Test
    public void testCall2() {
        GrpcReflectionCall call = new GrpcReflectionCall();
        String res = call.call("127.0.0.1:8080", "com.xiaomi.mone.grpc.demo.HelloService.hello", "{'firstName':'z','lastName':'zy'}", 1000);
        System.out.println(res);
    }
}
