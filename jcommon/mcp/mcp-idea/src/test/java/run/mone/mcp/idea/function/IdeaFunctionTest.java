
package run.mone.mcp.idea.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IdeaFunctionTest {

    @Autowired
    private CodeReviewFunction codeReviewFunction;

    private IdeaFunctions.IdeaOperationFunction ideaFunction;

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
        arguments.put("code", "package francesca.filter;\n" +
                "\n" +
                "import com.alibaba.fastjson.JSON;\n" +
                "import com.xiaomi.support.francescaapi.api.RecordInvocationApi;\n" +
                "import com.xiaomi.support.francescaapi.domain.InvocationInfoReq;\n" +
                "import com.xiaomi.youpin.gateway.common.FilterOrder;\n" +
                "import com.xiaomi.youpin.gateway.dubbo.Dubbo;\n" +
                "import com.xiaomi.youpin.gateway.dubbo.MethodInfo;\n" +
                "import com.xiaomi.youpin.gateway.filter.CustomRequestFilter;\n" +
                "import com.xiaomi.youpin.gateway.filter.FilterContext;\n" +
                "import com.xiaomi.youpin.gateway.filter.Invoker;\n" +
                "import com.youpin.xiaomi.tesla.bo.ApiInfo;\n" +
                "import io.netty.handler.codec.http.FullHttpRequest;\n" +
                "import io.netty.handler.codec.http.FullHttpResponse;\n" +
                "import lombok.extern.slf4j.Slf4j;\n" +
                "\n" +
                "import java.util.concurrent.Executors;\n" +
                "\n" +
                "/**\n" +
                " * @author wangjunjie\n" +
                " */\n" +
                "@FilterOrder(1205)\n" +
                "@Slf4j\n" +
                "public class FrancescaFilter extends CustomRequestFilter {\n" +
                "\n" +
                "    @Override\n" +
                "    public FullHttpResponse execute(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {\n" +
                "        log.info(\"francescaFilter--context:{}\", JSON.toJSONString(context));\n" +
                "        log.info(\"francescaFilter--apiInfo:{}\", JSON.toJSONString(apiInfo));\n" +
                "        log.info(\"francescaFilter--request:{}\", JSON.toJSONString(request));\n" +
                "        InvocationInfoReq req = new InvocationInfoReq();\n" +
                "        req.setFilterContext(context);\n" +
                "        req.setApiInfo(apiInfo);\n" +
                "        try {\n" +
                "            FullHttpResponse response = next(context, invoker, apiInfo, request);\n" +
                "            log.info(\"francescaFilter--response:{}\", JSON.toJSONString(response));\n" +
                "            req.setFullHttpRequest(request);\n" +
                "            req.setFullHttpResponse(response);\n" +
                "            Executors.newCachedThreadPool().execute(() -> recordInvocation(req));\n" +
                "            return response;\n" +
                "        } catch (Throwable e) {\n" +
                "            log.error(\"francescaFilter--error:{}\", JSON.toJSONString(e));\n" +
                "            req.setFullHttpRequest(request);\n" +
                "            req.setThrowable(e);\n" +
                "            Executors.newCachedThreadPool().execute(() -> recordInvocation(req));\n" +
                "            throw e;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 记录调用信息\n" +
                "     */\n" +
                "    private void recordInvocation(InvocationInfoReq req) {\n" +
                "        Dubbo dubbo = this.getBean(Dubbo.class);\n" +
                "        MethodInfo methodInfo = new MethodInfo();\n" +
                "        methodInfo.setServiceName(RecordInvocationApi.class.getName());\n" +
                "        methodInfo.setMethodName(\"recordInvocation\");\n" +
                "        methodInfo.setGroup(\"staging\");\n" +
                "        methodInfo.setParameterTypes(new String[]{InvocationInfoReq.class.getName()});\n" +
                "        methodInfo.setArgs(new Object[]{req});\n" +
                "        try {\n" +
                "            dubbo.call(methodInfo);\n" +
                "        } catch (Exception e) {\n" +
                "            log.error(\"francescaFilter--send error:{}\", JSON.toJSONString(e));\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "\n");

        // Call the function
        McpSchema.CallToolResult result = codeReviewFunction.apply(arguments);

        // Assertions
        assertNotNull(result);
    }
}
