package run.mone.mcp.hammerspoon.function;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.spec.McpSchema;

@SpringBootTest
class TrigerTradeFunctionTest {

    @Autowired
    private LLM llm;

    private TrigerTradeProFunction trigerTradeProFunction;

    @BeforeEach
    void setUp() {
        trigerTradeProFunction = new TrigerTradeProFunction();
        trigerTradeProFunction.setLlm(llm);
    }

    @Test
    void testAnalyzeOptionsChain() {
        // Prepare test data
        String testBase64Image = "data:image/png;base64,"+llm.imageToBase64("/tmp/abcd.png", "png");

        // Prepare function arguments
        Map<String, Object> args = new HashMap<>();
        args.put("command", "analyzeOptionsChain");
        args.put("base64Image", testBase64Image);

        // Execute the function
        McpSchema.CallToolResult result = trigerTradeProFunction.apply(args);

        // Verify results
        assertNotNull(result);
    }

    @Test
    void testAnalyzeOptionsChainText() {
        // Prepare test data - 模拟期权链数据
        String testOptionsChainText = """
                当前股价：12.50
                到期日：2024-06-21
                行权价  |  隐含波动率  |  成交量  |  未平仓量  |  最新价
                11.00  |    45%      |   156   |    789    |   1.25
                11.50  |    42%      |   234   |    1023   |   0.95
                12.00  |    40%      |   567   |    2145   |   0.75
                12.50  |    38%      |   789   |    3456   |   0.55
                13.00  |    37%      |   432   |    1678   |   0.35
                """;

        // Prepare function arguments
        Map<String, Object> args = new HashMap<>();
        args.put("command", "analyzeOptionsChainText");
        args.put("optionsChainText", testOptionsChainText);

        // Execute the function
        McpSchema.CallToolResult result = trigerTradeProFunction.apply(args);

        // Verify results
        assertNotNull(result);
        assertFalse(result.isError());
        
        // 验证返回的内容包含关键信息
        String content = ((McpSchema.TextContent)result.content().get(0)).text();
        assertTrue(content.length() > 0, "Analysis result should not be empty");
    }

    @Test
    void testAnalyzeOptionsChainTextWithEmptyInput() {
        // Prepare function arguments with empty input
        Map<String, Object> args = new HashMap<>();
        args.put("command", "analyzeOptionsChainText");
        args.put("optionsChainText", "");

        // Execute the function
        McpSchema.CallToolResult result = trigerTradeProFunction.apply(args);

        // Verify results
        assertNotNull(result);
        assertTrue(result.isError());
        String content = ((McpSchema.TextContent)result.content().get(0)).text();
        assertEquals("No options chain text provided", content);
    }

    @Test
    void testAnalyzeOptionsChainTextWithNullInput() {
        // Prepare function arguments with null input
        Map<String, Object> args = new HashMap<>();
        args.put("command", "analyzeOptionsChainText");
        args.put("optionsChainText", null);

        // Execute the function
        McpSchema.CallToolResult result = trigerTradeProFunction.apply(args);

        // Verify results
        assertNotNull(result);
        assertTrue(result.isError());
        String content = ((McpSchema.TextContent)result.content().get(0)).text();
        assertEquals("No options chain text provided", content);
    }
}