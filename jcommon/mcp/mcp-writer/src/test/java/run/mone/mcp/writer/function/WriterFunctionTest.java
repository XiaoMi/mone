
package run.mone.mcp.writer.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.writer.service.WriterService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WriterFunctionTest {

    @Autowired
    private WriterFunction writerFunction;

    @Autowired
    private WriterService writerService;

    @BeforeEach
    void setUp() {
        // Any setup code if needed
    }

    @Test
    void testWriteNewArticle() throws InterruptedException {
        // Prepare test data
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("operation", "writeNewArticle");
        arguments.put("topic", "The benefits of regular exercise");

        // Call the function
        Flux<McpSchema.CallToolResult> result = writerFunction.apply(arguments);
        result.subscribe(new Subscriber<McpSchema.CallToolResult>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(McpSchema.CallToolResult callToolResult) {
                System.out.println("write new article result: " + callToolResult);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("write new article completed");
            }
        });

        // Assertions
        // assertNotNull(result);

        TimeUnit.SECONDS.sleep(60);
    }
}
