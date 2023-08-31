package run.mone.docean.plugin.http.server;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2022/9/22 10:12
 * <p>
 * 直接拉起来一个http server,不用再手动启动
 */
@DOceanPlugin
@Slf4j
public class HttpServerPlugin implements IPlugin {

    @Override
    public boolean start(Ioc ioc) {
        log.info("start http server plugin");
        final Config config = ioc.getBean(Config.class);
        boolean disable = Boolean.valueOf(config.get("docean_http_server_plugin", "false"));
        boolean cookie = Boolean.valueOf(config.get("docean_http_server_cookie", "true"));
        if (!disable) {
            new Thread(() -> Safe.run(() -> {
                DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder()
                        .cookie(cookie)
                        .port(Integer.valueOf(config.get("docean_http_server_port", "8080")))
                        .websocket(false)
                        .build());
                server.start();
            })).start();
        }
        return true;
    }
}

