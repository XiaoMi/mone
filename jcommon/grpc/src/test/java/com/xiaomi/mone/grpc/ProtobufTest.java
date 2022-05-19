package com.xiaomi.mone.grpc;

import com.xiaomi.mone.grpc.demo.MeshAddress;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 9/11/21
 */
public class ProtobufTest {


    @Test
    public void test1() {
        MeshAddress address = MeshAddress.newBuilder().setIp("127.0.0.1").build();
        System.out.println(address.getIp());
    }
}
