package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import org.junit.Test;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 * @date 2022/9/22 10:24
 */
public class HttpPluginTest {

    @Test
    public void testServer() throws IOException {
        Config config = new Config();
        config.put("http_server_port", "8888");
        Ioc.ins().putBean(config).init("com.xiaomi.youpin.docean", "run.mone.docean.plugin.http.server");
        System.in.read();
    }
}
