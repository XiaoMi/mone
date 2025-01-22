package run.mone.mcp.playwright.function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PlaywrightFunctionsTest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    
    private PlaywrightFunctions.NavigateFunction navigateFunction;
    private PlaywrightFunctions.ScreenshotFunction screenshotFunction;
    private PlaywrightFunctions.ClickFunction clickFunction;
    private PlaywrightFunctions.FillFunction fillFunction;
    private PlaywrightFunctions.SelectFunction selectFunction;
    private PlaywrightFunctions.HoverFunction hoverFunction;
    private PlaywrightFunctions.EvaluateFunction evaluateFunction;
    private PlaywrightFunctions.GetFunction getFunction;
    private PlaywrightFunctions.PostFunction postFunction;
    private PlaywrightFunctions.PutFunction putFunction;
    private PlaywrightFunctions.PatchFunction patchFunction;
    private PlaywrightFunctions.DeleteFunction deleteFunction;

    @BeforeEach
    void setUp() {
        baseUrl = "https://www.baidu.com";
        
        navigateFunction = new PlaywrightFunctions.NavigateFunction();
        screenshotFunction = new PlaywrightFunctions.ScreenshotFunction();
        clickFunction = new PlaywrightFunctions.ClickFunction();
        fillFunction = new PlaywrightFunctions.FillFunction();
        selectFunction = new PlaywrightFunctions.SelectFunction();
        hoverFunction = new PlaywrightFunctions.HoverFunction();
        evaluateFunction = new PlaywrightFunctions.EvaluateFunction();
        getFunction = new PlaywrightFunctions.GetFunction();
        postFunction = new PlaywrightFunctions.PostFunction();
        putFunction = new PlaywrightFunctions.PutFunction();
        patchFunction = new PlaywrightFunctions.PatchFunction();
        deleteFunction = new PlaywrightFunctions.DeleteFunction();
    }

    @AfterEach
    void tearDown() {
        // 清理浏览器资源
        try {
            if (PlaywrightFunctions.getPage() != null) {
                PlaywrightFunctions.getPage().close();
            }
            if (PlaywrightFunctions.getBrowser() != null) {
                PlaywrightFunctions.getBrowser().close();
            }
            if (PlaywrightFunctions.getPlaywright() != null) {
                PlaywrightFunctions.getPlaywright().close();
            }
        } catch (Exception e) {
            // 忽略清理时的异常
        }
    }

    @Test
    void testNavigate() {
        Map<String, Object> args = new HashMap<>();
        args.put("url", baseUrl);
        args.put("width", 1024);
        args.put("height", 768);
        args.put("waitUntil", "load");

        McpSchema.CallToolResult result = navigateFunction.apply(args);
        assertFalse(result.isError());
        assertTrue(result.content().get(0).toString().contains("Navigated to"));
    }

    @Test
    void testScreenshot() {
        // 先导航到页面
        testNavigate();

        Map<String, Object> args = new HashMap<>();
        args.put("name", "test-screenshot");
        args.put("storeBase64", true);

        McpSchema.CallToolResult result = screenshotFunction.apply(args);
        assertFalse(result.isError());
        assertTrue(result.content().size() > 0);
    }

    @Test
    void testClick() {
        // 先导航到页面
        testNavigate();

        Map<String, Object> args = new HashMap<>();
        args.put("selector", "button");

        McpSchema.CallToolResult result = clickFunction.apply(args);
        // 如果页面上没有按钮,这里会失败
        assertTrue(result.isError());
    }

    @Test
    void testFill() {
        // 先导航到页面
        testNavigate();

        Map<String, Object> args = new HashMap<>();
        args.put("selector", "input");
        args.put("value", "test value");

        McpSchema.CallToolResult result = fillFunction.apply(args);
        // 如果页面上没有输入框,这里会失败
        assertTrue(result.isError());
    }

    @Test
    void testEvaluate() {
        // 先导航到页面
        testNavigate();

        Map<String, Object> args = new HashMap<>();
        args.put("script", "document.title");

        McpSchema.CallToolResult result = evaluateFunction.apply(args);
        assertFalse(result.isError());
    }

    @Test
    void testGet() {
        Map<String, Object> args = new HashMap<>();
        args.put("url", baseUrl);

        McpSchema.CallToolResult result = getFunction.apply(args);
        assertFalse(result.isError());
        assertTrue(result.content().get(0).toString().contains("Performed GET Operation"));
    }

    @Test
    void testPost() {
        Map<String, Object> args = new HashMap<>();
        args.put("url", baseUrl);
        args.put("value", "{\"test\":\"value\"}");

        McpSchema.CallToolResult result = postFunction.apply(args);
        // 如果端点不支持POST,这里会失败
        assertTrue(result.isError());
    }

    @Test
    void testPut() {
        Map<String, Object> args = new HashMap<>();
        args.put("url", baseUrl);
        args.put("value", "{\"test\":\"value\"}");

        McpSchema.CallToolResult result = putFunction.apply(args);
        // 如果端点不支持PUT,这里会失败
        assertTrue(result.isError());
    }

    @Test
    void testPatch() {
        Map<String, Object> args = new HashMap<>();
        args.put("url", baseUrl);
        args.put("value", "{\"test\":\"value\"}");

        McpSchema.CallToolResult result = patchFunction.apply(args);
        // 如果端点不支持PATCH,这里会失败
        assertTrue(result.isError());
    }

    @Test
    void testDelete() {
        Map<String, Object> args = new HashMap<>();
        args.put("url", baseUrl);

        McpSchema.CallToolResult result = deleteFunction.apply(args);
        // 如果端点不支持DELETE,这里会失败
        assertTrue(result.isError());
    }
}
