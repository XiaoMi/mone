package run.mone.mcp.yijing.task;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import run.mone.hive.common.RoleType;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.StreamMessageType;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/4/14 08:49
 */
@AllArgsConstructor
public class ChatAgentTask implements Runnable{

    private final ReactorRole role;

    private final GrpcServerTransport grpcServerTransport;


    @Override
    public void run() {
        long now = System.currentTimeMillis();
        List<Message> messageList = role.getRc().getMessageList();
        if (!messageList.isEmpty()) {
            String id = UUID.randomUUID().toString();
            Message lastMsg = messageList.get(messageList.size() - 1);
            if (null != lastMsg.getSentFrom() && lastMsg.getSentFrom().equals("schedule")) {
                return;
            }
            if (now - lastMsg.getCreateTime() > TimeUnit.MINUTES.toMillis(1)) {
                Flux.create(sink -> {
                            //给用户发送消息
                            role.putMessage(Message.builder().sentFrom("schedule").id(id).role(RoleType.assistant.name()).content("给用户讲一个有关it或者ai的笑话吧(根据之前的聊天历史)").sink(sink).build());
                        }).doFirst(() -> {
                            sendMessage(Message.builder().clientId(role.getClientId()).id(id).type(StreamMessageType.BOT_STREAM_BEGIN).build(), role);
                        }).doOnNext(it -> {
                            sendMessage(Message.builder().clientId(role.getClientId()).id(id).content(it.toString()).type(StreamMessageType.BOT_STREAM_EVENT).build(), role);
                        }).doOnComplete(() -> {
                            sendMessage(Message.builder().clientId(role.getClientId()).id(id).type(StreamMessageType.BOT_STREAM_END).build(), role);
                        })
                        .blockLast();
            }
        }
    }


    public void sendMessage(Message msg, ReactorRole role) {
        switch (msg.getType()) {
            case StreamMessageType.BOT_STREAM_BEGIN: {
                sendMsgToAthena("[BEGIN]", msg, role);
                break;
            }
            case StreamMessageType.BOT_STREAM_EVENT: {
                sendMsgToAthena(msg.getContent(), msg, role);
                break;
            }
            case StreamMessageType.BOT_STREAM_END: {
                sendMsgToAthena("[DONE]", msg, role);
                break;
            }
        }
    }

    //会通知到athena
    private void sendMsgToAthena(String value, Message msg, ReactorRole role) {
        Map<String, Object> params = new HashMap<>();
        //是通过client_id来找到接受者的
        params.put(Const.CLIENT_ID, msg.getClientId());
        params.put(Const.OWNER_ID, role.getOwner());
        params.put("cmd", "notify_athena");
        params.put("data", value);
        params.put("id", msg.getId());
        grpcServerTransport.sendMessage(new McpSchema.JSONRPCNotification("", "msg", params));
    }

}
