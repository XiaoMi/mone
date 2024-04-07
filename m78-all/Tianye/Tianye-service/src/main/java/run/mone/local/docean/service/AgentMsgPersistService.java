package run.mone.local.docean.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Value;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.po.Message;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.DoceanRpcClient;
import run.mone.local.docean.rpc.TianyeCmd;

import javax.annotation.Resource;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/28/24 15:11
 */
@Slf4j
@Service
public class AgentMsgPersistService {

    @Value("${agent.msg.remote.record}")
    private boolean remoteRecord;

    @Resource
    private NutDao dao;

    @Resource
    private DoceanRpcClient client;


    /**
     * 尝试将消息持久化到本地，并根据配置决定是否也持久化到远程。
     * 如果在过程中发生异常，则记录错误日志并返回false。
     *
     * @param message 要持久化的消息对象
     * @return 持久化操作是否成功
     */
    public boolean persist(Message message) {
        try {
            log.info("persisting msg:{}, with remoteRecord:{}", message, remoteRecord);
            storeLocal(message);
            if (remoteRecord) {
                storeRemote(message);
            }
            return true;
        } catch (Exception e) {
            log.error("Error while try to persist agent msg, nested exception is:", e);
            return false;
        }
    }

    public void storeLocal(Message message) {
        log.info("try store locally!");
        dao.insert(message);
    }

    public void storeRemote(Message message) {
        log.info("try store remote!");
        AiMessage remoteMsg = AiMessage.newBuilder()
                .setCmd("STORE")
                .setFrom(TianyeContext.ins().getUserName())
                .setData(message.getData())
                .setMessage(message.getRole())
                .setTopicId(message.getTopicId())
                .build();
        log.info("remote msg:{}", remoteMsg);
        AiResult res = client.req(TianyeCmd.messageReq, client.getServerAddr(), remoteMsg);
    }
}
