package run.mone.m78.service.agent.rpc;





import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.RpcVersion;
import com.xiaomi.data.push.rpc.common.Pair;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.m78.service.agent.rpc.processor.MessageProcessor;
import run.mone.m78.service.agent.rpc.processor.PingProcessor;
import run.mone.m78.service.common.SafeRun;

import javax.annotation.PostConstruct;

import static run.mone.m78.api.constant.AgentConstant.AGENT_PORT;

/**
 * @author goodjava@qq.com
 * @date 2024/2/23 13:17
 */
@Service
@Slf4j
public class AgentRpcService {


    @Getter
    private RpcServer rpcServer;

    @PostConstruct
    public void init() {
        SafeRun.run(() -> {
            startRpcServer();
        });
    }


    public void startRpcServer() {
        log.info("agent manager start port:{} begin:{}", AGENT_PORT, new RpcVersion());
        rpcServer = new RpcServer("", "tianye_rpc_manaer", false);
        rpcServer.setListenPort(AGENT_PORT);
        //注册处理器
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(TianyeCmd.pingReq, new PingProcessor()),
                new Pair<>(TianyeCmd.messageRes, new MessageProcessor()),
                new Pair<>(TianyeCmd.messageReq, new MessageProcessor())
        ));
        rpcServer.init();
        rpcServer.start(config -> config.setIdle(false));
        log.info("agent rpc server start finish");
    }
}
