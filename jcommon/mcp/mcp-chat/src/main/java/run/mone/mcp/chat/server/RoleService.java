package run.mone.mcp.chat.server;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import run.mone.hive.common.RoleType;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.StreamMessageType;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.schema.Message;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 09:49
 */
@Service
public class RoleService {

    @Resource
    private LLM llm;

    @Resource
    private GrpcServerTransport grpcServerTransport;

    private ReactorRole minzai = null;


    @PostConstruct
    public void init() {
        minzai = new ReactorRole("minzai", new CountDownLatch(1), llm);
        minzai.setScheduledTaskHandler(role -> {
            long now = System.currentTimeMillis();
            List<Message> messageList = role.getRc().getMessageList();
            if (!messageList.isEmpty()) {
                String id = UUID.randomUUID().toString();
                Message lastMsg = messageList.get(messageList.size() - 1);
                if (lastMsg.getSentFrom().equals("schedule")) {
                    return;
                }

                if (now - lastMsg.getCreateTime() > TimeUnit.MINUTES.toMillis(1)) {
                    Flux.create(sink -> {
                                //给用户发送消息
                                role.putMessage(Message.builder().sentFrom("schedule").id(id).role(RoleType.assistant.name()).content("用户好久没说话了,和用户随便聊聊吧(根据之前的聊天历史)").sink(sink).build());
                            }).doFirst(() -> {
                                sendMessage(Message.builder().id(id).type(StreamMessageType.BOT_STREAM_BEGIN).build());
                            }).doOnNext(it -> {
                                sendMessage(Message.builder().id(id).content(it.toString()).type(StreamMessageType.BOT_STREAM_EVENT).build());
                            }).doOnComplete(() -> {
                                sendMessage(Message.builder().id(id).type(StreamMessageType.BOT_STREAM_END).build());
                            })
                            .blockLast();
                }
            }
        });
        //支持使用聊天工具
        minzai.getTools().add(new ChatTool());

        //一直执行不会停下来
        minzai.run();
    }

    public Flux<String> receiveMsg(Message message) {
        return Flux.create(sink -> {
            message.setSink(sink);
            minzai.putMessage(message);
        });
    }


    public void sendMessage(Message msg) {
        switch (msg.getType()) {
            case StreamMessageType.BOT_STREAM_BEGIN: {
                sendMsgToAthena("[BEGIN]", msg);
                break;
            }
            case StreamMessageType.BOT_STREAM_EVENT: {
                sendMsgToAthena(msg.getContent(), msg);
                break;
            }
            case StreamMessageType.BOT_STREAM_END: {
                sendMsgToAthena("[DONE]", msg);
                break;
            }
        }
    }

    //会通知到athena
    private void sendMsgToAthena(String value, Message msg) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.CLIENT_ID, "min");
        params.put("cmd", "notify_athena");
        params.put("data", value);
        params.put("id", msg.getId());
        grpcServerTransport.sendMessage(new McpSchema.JSONRPCNotification("", "msg", params));
    }


}
