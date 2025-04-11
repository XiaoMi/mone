package run.mone.mcp.chat.service;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.common.RoleType;
import run.mone.hive.configs.Const;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.StreamMessageType;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.schema.Message;
import run.mone.mcp.chat.tool.DocumentProcessingTool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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

    @Value("${mcp.hub.path:}")
    private String mcpPath;

    private ConcurrentHashMap<String, ReactorRole> roleMap = new ConcurrentHashMap<>();

    @PostConstruct
    @SneakyThrows
    public void init() {
        //启用mcp
        if (StringUtils.isNotEmpty(mcpPath)) {
            McpHubHolder.put(Const.DEFAULT, new McpHub(Paths.get(mcpPath)));
        }
    }

    public ReactorRole createRole(String owner, String clientId) {
        ReactorRole minzai = new ReactorRole("minzai", new CountDownLatch(1), llm);
        minzai.setScheduledTaskHandler(role -> {
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
        });
        //支持使用聊天工具
        minzai.getTools().add(new ChatTool());
        minzai.getTools().add(new AttemptCompletionTool());
        minzai.getTools().add(new DocumentProcessingTool());
        minzai.setOwner(owner);
        minzai.setClientId(clientId);
        //一直执行不会停下来
        minzai.run();
        return minzai;
    }


    //根据from进行隔离(比如Athena 不同 的project就是不同的from)
    public Flux<String> receiveMsg(Message message) {
        String from = message.getSentFrom().toString();
        if (!roleMap.containsKey(from)) {
            roleMap.putIfAbsent(from, createRole(from, message.getClientId()));
        }
        ReactorRole minzai = roleMap.get(from);
        return Flux.create(sink -> {
            message.setSink(sink);
            minzai.putMessage(message);
        });
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
