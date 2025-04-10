package run.mone.mcp.chat.server;

import org.springframework.stereotype.Service;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.StreamMessageType;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 09:49
 */
@Service
public class RoleService {

    @Resource
    private LLM llm;

    @Resource
    private GrpcServerTransport serverTransport;

    private ReactorRole minzai = null;

    @PostConstruct
    public void init() {
        minzai = new ReactorRole("minzai", new CountDownLatch(1), llm) {

            //发到athena
            @Override
            public void sendMessage(Message msg) {
                switch (msg.getType()) {
                    case StreamMessageType.BOT_STREAM_BEGIN: {
                        extracted("[BEGIN]", msg);
                        break;
                    }
                    case StreamMessageType.BOT_STREAM_EVENT: {
                        extracted(msg.getContent(), msg);
                        break;
                    }
                    case StreamMessageType.BOT_STREAM_END: {
                        extracted("[DONE]", msg);
                        break;
                    }
                }

            }

            private void extracted(String value, Message msg) {
                Map<String, Object> params = new HashMap<>();
                params.put(Const.CLIENT_ID, "min");
                params.put("cmd", "notify_athena");
                params.put("data", value);
                params.put("id", msg.getId());
                serverTransport.sendMessage(new McpSchema.JSONRPCNotification("", "msg", params));
            }
        };
    }

    public void receiveMsg(Message message) throws InterruptedException {
        minzai.getRc().getNews().put(message);
    }


}
