package run.mone.local.docean.bootstrap;


import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangping17
 */
@Slf4j
public class TianyeBootstrap {

    public static void main(String... args) {
        try {
            log.info("tianye client start version:0.0.1");
            Aop.ins().init(Maps.newLinkedHashMap());
            Ioc.ins().init("com.xiaomi.youpin","run.mone.local.docean");
            DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder().websocket(false).port(8999).build());
            server.start();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}