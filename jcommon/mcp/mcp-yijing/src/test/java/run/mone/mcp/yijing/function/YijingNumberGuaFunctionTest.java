package run.mone.mcp.yijing.function;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.yijing.service.YijingService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class YijingNumberGuaFunctionTest {

    @Autowired
    private YijingNumberGuaFunction yijingNumberGuaFunction;

    @Autowired
    private YijingService yijingService;

    @Test
    void testYijingNumberGua() throws InterruptedException {
        // 准备测试数据
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("num1", 122);
        arguments.put("num2", 452);
        arguments.put("num3", 877);
        arguments.put("question", "我可以去北京吗？");

        // 调用function
        Flux<McpSchema.CallToolResult> result = yijingNumberGuaFunction.apply(arguments);
        
        result.subscribe(new Subscriber<McpSchema.CallToolResult>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(McpSchema.CallToolResult callToolResult) {
                System.out.println("易经数字卦计算结果: " + callToolResult);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("易经数字卦计算完成");
            }
        });

        // 等待结果
        TimeUnit.SECONDS.sleep(30);
    }

    @Test
    void testYijingService() throws InterruptedException {
        // 测试YijingService的卦象计算
        Map<String, String> guaResult = yijingService.getGuaCalculation(122, 452, 877);
        System.out.println("卦象计算结果: " + guaResult);

        // 测试YijingService的完整计算
        Flux<String> analysisFlux = yijingService.calculateYijingGua(122, 452, 877, "我可以去北京吗？");
        
        analysisFlux.subscribe(new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String analysis) {
                System.out.println("易经解析: " + analysis);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("易经解析完成");
            }
        });

        // 等待结果
        TimeUnit.SECONDS.sleep(30);
    }
}
