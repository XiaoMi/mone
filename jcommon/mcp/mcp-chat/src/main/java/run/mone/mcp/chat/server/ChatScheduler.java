package run.mone.mcp.chat.server;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import run.mone.mcp.chat.function.ChatFunction;
import run.mone.mcp.chat.function.Message;
import run.mone.mcp.chat.service.ChatService;

@Component
@EnableScheduling
public class ChatScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ChatScheduler.class);
    private static final Duration INACTIVE_THRESHOLD = Duration.ofSeconds(10);

    @Resource
    private ChatFunction function;
    
    @Resource
    private ChatService chatService;

    @Scheduled(fixedRate = 5000)
    public void scheduledTask() {
        logger.info("Scheduled task running at: {}", System.currentTimeMillis());
        List<Message> msgList = function.getHistory();
        System.out.println(msgList);
        
        // Check if the conversation is inactive
        if (!msgList.isEmpty()) {
            Message lastMessage = msgList.get(msgList.size() - 1);
            // Check if last message is from assistant and it's been more than 10 seconds
            if ("assistant".equals(lastMessage.getRole()) && 
                Duration.between(lastMessage.getTime(), Instant.now()).compareTo(INACTIVE_THRESHOLD) > 0) {
                
                logger.info("Conversation inactive for more than 10 seconds. Sending proactive message.");
                
                // Build context from chat history
                StringBuilder context = new StringBuilder();
                for (Message msg : msgList) {
                    context.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
                }
                
                // Send proactive message
                chatService.sendProactiveMessage(context.toString())
                    .subscribe(
                        response -> {
                            logger.info("Proactive message sent: {}", response);
                            function.getHistory().add(Message.builder().role("assistant").content(response).time(Instant.now()).build());


                        },
                        error -> logger.error("Error sending proactive message", error)
                    );
            }
        }
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