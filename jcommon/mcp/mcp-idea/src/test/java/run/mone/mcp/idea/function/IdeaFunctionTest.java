
package run.mone.mcp.idea.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IdeaFunctionTest {

    @Autowired
    private CodeReviewFunction codeReviewFunction;

    @Autowired
    private CreateCommentFunction createCommentFunction;

    @Autowired
    private GitPushFunction gitPushFunction;

    @Autowired
    private MethodRenameFunction methodRenameFunction;

    private IdeaFunctions.IdeaOperationFunction ideaFunction;

    private String code = """
                package francesca.filter;
                                
                import com.alibaba.fastjson.JSON;
                import com.xiaomi.support.francescaapi.api.RecordInvocationApi;
                import com.xiaomi.support.francescaapi.domain.InvocationInfoReq;
                import com.xiaomi.youpin.gateway.common.FilterOrder;
                import com.xiaomi.youpin.gateway.dubbo.Dubbo;
                import com.xiaomi.youpin.gateway.dubbo.MethodInfo;
                import com.xiaomi.youpin.gateway.filter.CustomRequestFilter;
                import com.xiaomi.youpin.gateway.filter.FilterContext;
                import com.xiaomi.youpin.gateway.filter.Invoker;
                import com.youpin.xiaomi.tesla.bo.ApiInfo;
                import io.netty.handler.codec.http.FullHttpRequest;
                import io.netty.handler.codec.http.FullHttpResponse;
                import lombok.extern.slf4j.Slf4j;
                                
                import java.util.concurrent.Executors;
                                
                /**
                 * @author wangjunjie
                 */
                @FilterOrder(1205)
                @Slf4j
                public class FrancescaFilter extends CustomRequestFilter {
                                
                    @Override
                    public FullHttpResponse execute(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
                        log.info("francescaFilter--context:{}", JSON.toJSONString(context));
                        log.info("francescaFilter--apiInfo:{}", JSON.toJSONString(apiInfo));
                        log.info("francescaFilter--request:{}", JSON.toJSONString(request));
                        InvocationInfoReq req = new InvocationInfoReq();
                        req.setFilterContext(context);
                        req.setApiInfo(apiInfo);
                        try {
                            FullHttpResponse response = next(context, invoker, apiInfo, request);
                            log.info("francescaFilter--response:{}", JSON.toJSONString(response));
                            req.setFullHttpRequest(request);
                            req.setFullHttpResponse(response);
                            Executors.newCachedThreadPool().execute(() -> recordInvocation(req));
                            return response;
                        } catch (Throwable e) {
                            log.error("francescaFilter--error:{}", JSON.toJSONString(e));
                            req.setFullHttpRequest(request);
                            req.setThrowable(e);
                            Executors.newCachedThreadPool().execute(() -> recordInvocation(req));
                            throw e;
                        }
                    }
                                
                    /**
                     * 记录调用信息
                     */
                    private void recordInvocation(InvocationInfoReq req) {
                        Dubbo dubbo = this.getBean(Dubbo.class);
                        MethodInfo methodInfo = new MethodInfo();
                        methodInfo.setServiceName(RecordInvocationApi.class.getName());
                        methodInfo.setMethodName("recordInvocation");
                        methodInfo.setGroup("staging");
                        methodInfo.setParameterTypes(new String[]{InvocationInfoReq.class.getName()});
                        methodInfo.setArgs(new Object[]{req});
                        try {
                            dubbo.call(methodInfo);
                        } catch (Exception e) {
                            log.error("francescaFilter--send error:{}", JSON.toJSONString(e));
                        }
                    }
                }                                               
                """;

    @BeforeEach
    void setUp() {
        ideaFunction = new IdeaFunctions.IdeaOperationFunction("6667");
//        ideaFunction.setIdeaPort(30000);
    }

    @Test
    void testCloseEditors() {
        // Assuming closeEditors returns a boolean indicating success
        String result = ideaFunction.closeAllEditors(null);
        System.out.println(result);
    }

    @Test
    void testGetContent() {
        String content = ideaFunction.getCurrentEditorContent(null);
        assertNotNull(content, "Content should not be null");
        // Add more specific assertions based on expected content
    }

    @Test
    void  testGetClassName() {
        String content = ideaFunction.getCurrentEditorClassName("zxw_test2");
        System.out.println(content);
    }

    @Test
    void testCodeReview() {
        // Prepare test data
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("code", code);

        // Call the function
        McpSchema.CallToolResult result = codeReviewFunction.apply(arguments);

        // Assertions
        assertNotNull(result);
    }

    @Test
    void testCreateComment() {
        // Prepare test data
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("code", code);

        // Call the function
        Flux<McpSchema.CallToolResult> result = createCommentFunction.apply(arguments);

        // Assertions
        assertNotNull(result);
    }

    @Test
    void testGitPush() {
        // Prepare test data
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("code", code);

        // Call the function
        McpSchema.CallToolResult result = gitPushFunction.apply(arguments);

        // Assertions
        assertNotNull(result);
    }

    @Test
    void testMethodRename() {
        // Prepare test data
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("code", code);

        // Call the function
        McpSchema.CallToolResult result = methodRenameFunction.apply(arguments);

        // Assertions
        assertNotNull(result);
    }
}
