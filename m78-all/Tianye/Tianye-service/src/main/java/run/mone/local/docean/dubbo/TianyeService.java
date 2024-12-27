package run.mone.local.docean.dubbo;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.RpcVersion;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.Safe;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.local.docean.rpc.processor.PingProcessor;

import java.net.InetAddress;

/**
 * @author goodjava@qq.com
 * @date 2024/2/23 10:36
 */
//@Service
@Slf4j
public class TianyeService {

    private int port = 5678;

    public void init() {
        log.info("tianye init");
        Safe.runAndLog(() -> {
            //获取用户名(从本地系统)
            String username = System.getProperty("user.name");
            log.info("Username: " + username);
            //获取ip(从本地系统)
            String ip = InetAddress.getLocalHost().getHostAddress();
            log.info("IP: " + ip);

//            startRpcServer();
        });
    }

    public String hi() {
        return "hi";
    }


    public void startRpcServer() {
        log.info("agent manager start port:{} begin:{}", port, new RpcVersion());
        RpcServer rpcServer = new RpcServer("", "tianye_client", false);
        rpcServer.setListenPort(port);
        //注册处理器
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(TianyeCmd.pingReq, new PingProcessor())
        ));
        rpcServer.init();
        rpcServer.start(config -> config.setIdle(false));
        log.info("nacos rpc server start finish");
    }

}
