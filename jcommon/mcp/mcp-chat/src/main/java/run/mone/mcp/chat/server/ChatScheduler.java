package run.mone.mcp.chat.server;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import io.grpc.internal.ServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chat.function.ChatFunction;
import run.mone.mcp.chat.function.Message;
import run.mone.mcp.chat.service.ChatService;

@Component
@EnableScheduling
public class ChatScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ChatScheduler.class);
    private static final Duration INACTIVE_THRESHOLD = Duration.ofSeconds(30);

    @Resource
    private ChatFunction function;

    @Resource
    private ChatService chatService;

    @Resource
    private GrpcServerTransport serverTransport;

    @Scheduled(fixedRate = 5000)
    public void scheduledTask() {
        logger.debug("Scheduled task running at: {}", System.currentTimeMillis());
        Map<String, List<Message>> allHistory = function.getAllHistory();
        System.out.println(allHistory);

        // Check each client's conversation
        allHistory.forEach((clientId, msgList) -> {
            // Check if the conversation is inactive
            if (!msgList.isEmpty()) {
                Message lastMessage = msgList.get(msgList.size() - 1);
                // Check if last message is from assistant and it's been more than 10 seconds
                if ("assistant".equals(lastMessage.getRole()) &&
                        Duration.between(lastMessage.getTime(), Instant.now()).compareTo(INACTIVE_THRESHOLD) > 0) {

                    logger.info("Conversation for client {} inactive for more than 10 seconds. Sending proactive message.", clientId);

                    // Build context from chat history
                    StringBuilder context = new StringBuilder();
                    for (Message msg : msgList) {
                        context.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
                    }

                    StringBuilder sb = new StringBuilder();

                    // Send proactive message
                    chatService.sendProactiveMessage(context.toString()).doOnComplete(() -> {
                                List<Message> clientHistory = function.getClientHistory(clientId);
                                String id = UUID.randomUUID().toString();
                                String response = sb.toString();
                                clientHistory.add(Message.builder().role("assistant").content(response).time(Instant.now()).build());
                                Lists.newArrayList("[BEGIN]", response, "[DONE]").forEach(it -> {
                                    Map<String, Object> params = new HashMap<>();
                                    params.put(Const.CLIENT_ID, clientId);
                                    params.put("cmd", "notify_athena");
                                    params.put("data", it);
                                    params.put("id", id);
                                    serverTransport.sendMessage(new McpSchema.JSONRPCNotification("", "msg", params));
                                });
                            })
                            .subscribe(
                                    response -> {
                                        logger.info("Proactive message sent to client {}: {}", clientId, response);
                                        sb.append(response);
                                    },
                                    error -> logger.error("Error sending proactive message to client: " + clientId, error)
                            );
                }
            }
        });
    }

    @PostConstruct
    public void init() {
        logger.info("ChatScheduler initialized");
    }

    @PreDestroy
    public void destroy() {
        logger.info("ChatScheduler shutting down");
    }
} 