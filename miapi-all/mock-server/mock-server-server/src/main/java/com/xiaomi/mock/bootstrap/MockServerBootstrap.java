package com.xiaomi.mock.bootstrap;


import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import lombok.extern.slf4j.Slf4j;
/**
 * @author dongzhenxing
 */
@Slf4j
public class MockServerBootstrap {
    public static void main(String... args) {
        try {
            log.info("dc server start version:0.0.1");
            Aop.ins().init(Maps.newLinkedHashMap());
            Ioc.ins().init("com.xiaomi");
            HttpServerConfig config = new HttpServerConfig();
            config.setPort(8888);
            config.setSsl(false);
            config.setWebsocket(true);
            DoceanHttpServer server = new DoceanHttpServer(config);

            server.start();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }

}
