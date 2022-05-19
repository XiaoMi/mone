package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import org.junit.Test;

public class HttpServerPluginTest {


    @Test
    public void testServer() throws InterruptedException {
        Ioc.ins().init("com.xiaomi.youpin.docean");
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder().websocket(true).build());
        server.start();
    }
}
