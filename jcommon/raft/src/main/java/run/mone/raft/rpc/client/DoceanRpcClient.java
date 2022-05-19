package run.mone.raft.rpc.client;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.common.InvokeCallback;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.raft.pojo.RaftReq;
import run.mone.raft.pojo.RaftRes;
import run.mone.raft.pojo.RpcCmd;

import javax.annotation.PostConstruct;

/**
 * @author goodjava@qq.com
 * @date 2022/4/17
 */
@Slf4j
@Data
@Component
public class DoceanRpcClient {

    private RpcClient client;

    private boolean init;

    private Gson gson = new Gson();

    @PostConstruct
    public void init() {
        try {
            client = new RpcClient("");
            client.setReconnection(false);
            client.start(config -> config.setIdle(false));
            client.init();
            init = true;
            log.info("docean rpc client start finish");
        } catch (Throwable ex) {
            log.error("init error:{}", ex.getMessage());
        }
    }

    public RaftRes req(String addr, RaftReq req) {
        return req(RpcCmd.raftReq, addr, req);
    }

    public RaftRes req(int code, String addr, RaftReq req) {
        try {
            log.debug("rpc req:{} {}", req.getCmd(), addr);
            RemotingCommand res = client.sendMessage(addr, code, gson.toJson(req), 5000, true);
            byte[] data = res.getBody();
            RaftRes nacosRes = gson.fromJson(new String(data), RaftRes.class);
            return nacosRes;
        } catch (Throwable ex) {
            log.error("rpc req error {}:{}:{}", addr, req.getCmd(), ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void req(String addr, int code, String data, InvokeCallback callback) {
        RemotingCommand req = RemotingCommand.createRequestCommand(code);
        req.setBody(data.getBytes());
        client.sendMessage(addr, req, callback);
    }


}
