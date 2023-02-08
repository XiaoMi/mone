package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.Ioc;
import org.junit.Test;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 * 模拟一个小服务
 * test_app
 * /tmp/test.sock
 */
public class DoceanMeshTest {


    @Test
    public void testMesh() throws IOException {
        Ioc.ins().init("com.xiaomi.youpin");
        System.in.read();
    }
}
