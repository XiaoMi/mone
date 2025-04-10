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
        arguments.put("context", "");

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
