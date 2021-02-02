package com.xiaomi.youpin.tesla.rcurve.proxy;

import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import com.xiaomi.youpin.tesla.rcurve.proxy.common.Config;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class ProxyServer {


    public void openProxy() {
        log.info("open curve http server");
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder()
                .port(Integer.valueOf(Config.ins().get("curve_http_port", "7778")))
                .websocket(false)
                .build());
        Safe.runAndLog(() -> server.start());
    }

}
