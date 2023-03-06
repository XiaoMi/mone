package run.mone.hera.operator;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;

/**
 * @author shanwb
 * @date 2023-01-31
 */
public class HeraBootstrap {

    public static void main(String[] args) throws InterruptedException {
        Ioc.ins().init("run.mone.docean.plugin","com.xiaomi.youpin.docean.plugin","run.mone.hera.operator");
        //Mvc.ins();
        Mvc.ins().getMvcConfig().setResponseOriginalValue(true);

        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder().port(8998).websocket(false).build());
        server.start();
    }

}
