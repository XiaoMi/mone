package run.mone.mcp.hammerspoon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hammerspoon.function.DingTalkFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author shanwb
 * @date 2025-02-08
 */
public class DingTalkFunctionTest {

   private DingTalkFunction dingTalkFunction;

    @BeforeEach
    void setUp() {
        dingTalkFunction = new DingTalkFunction();
    }

    @Test
    void testDingDingSendMessage() throws IOException {

        Map<String, Object> args = new HashMap<>();
        args.put("command", "dingTalkSendMessage");
        args.put("contactName", "用户AAA");
        args.put("message", "哈哈哈");

        McpSchema.CallToolResult result = dingTalkFunction.apply(args);
        System.out.println(result);
        //assertFalse(result.isError());
    }

    @Test
    void testDingTalkCaptureActiveWindow() throws IOException {

        Map<String, Object> args = new HashMap<>();
        args.put("command", "dingTalkCaptureWindow");
        //args.put("command", "captureActiveWindow");

        McpSchema.CallToolResult result = dingTalkFunction.apply(args);
        System.out.println(result);
        assertFalse(result.isError());
    }

}
