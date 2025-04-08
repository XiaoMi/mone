package run.mone.mcp.chat.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chat.service.ChatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ChatFunctionTest {

    @Autowired
    private ChatFunction chatFunction;

    @Autowired
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        // Any setup code if needed
    }

    @Test
    void testChat() throws InterruptedException {
        // Prepare test data
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("message", "你好，请介绍下你自己");
        
        // Add some context messages (optional)
        List<Map<String, String>> context = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "我想了解更多关于AI的知识");
        Map<String, String> assistantMessage = new HashMap<>();
        assistantMessage.put("role", "assistant");
        assistantMessage.put("content", "我很乐意为您介绍AI相关知识。您有什么具体想了解的方面吗？");
        context.add(userMessage);
        context.add(assistantMessage);
        arguments.put("context", context);

        // Call the function
        Flux<McpSchema.CallToolResult> result = chatFunction.apply(arguments);
        result.subscribe(new Subscriber<McpSchema.CallToolResult>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(McpSchema.CallToolResult callToolResult) {
                System.out.println("Chat response: " + callToolResult);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Chat completed");
            }
        });

        // Wait for the response
        TimeUnit.SECONDS.sleep(30);
    }
}
