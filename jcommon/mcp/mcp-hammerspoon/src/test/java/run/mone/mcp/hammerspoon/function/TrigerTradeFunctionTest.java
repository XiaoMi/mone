package run.mone.mcp.hammerspoon.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.struct.enums.TimeZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hammerspoon.function.trigertrade.SellPutOptionDecisionFunction;
import run.mone.mcp.hammerspoon.function.trigertrade.TigerTradeSdkUtil;
import run.mone.mcp.hammerspoon.function.trigertrade.dto.OptionDetailBO;

@SpringBootTest
class TrigerTradeFunctionTest {

    @Autowired
    private LLM llm;

    private TrigerTradeProFunction trigerTradeProFunction;

    private SellPutOptionDecisionFunction sellPutOptionDecisionFunction;

    @BeforeEach
    void setUp() {
        trigerTradeProFunction = new TrigerTradeProFunction();
        trigerTradeProFunction.setLlm(llm);

        sellPutOptionDecisionFunction = new SellPutOptionDecisionFunction();
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
    void testAnalyzeOptionsChainWithApi() {
        OptionChainModel basicModel = new OptionChainModel("TSLA", "2025-03-14", TimeZoneId.NewYork);
        List<OptionDetailBO> optionDetailBOList = TigerTradeSdkUtil.getOptionChainDetail(basicModel, "put");
        System.out.println("111111111");
        System.out.println(new Gson().toJson(optionDetailBOList));

        String c = "基于以下期权链数据，请分析并推荐最佳的行权价。\n" +
                "请考虑以下因素：" +
                "1. 当前股价 " +
                "2. 隐含波动率 " +
                "3. 成交量 " +
                "4. 未平仓量。" +
                "给出你的分析理由。" +
                "期权链数据如下：\n" + new Gson().toJson(optionDetailBOList);


        System.out.println("length:" + c.length());

        LLM vllm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_DEEPSEEK_V3).build());
        String res = vllm.chat(c);

        System.out.println(res);
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

    @Test
    void testAiSellPut() {
        Map<String, Object> args = new HashMap<>();
        args.put("command", "sellPutOptionDecisionAndClick");
        args.put("stockNameOrCode", "TSLA");

        sellPutOptionDecisionFunction.apply(args);
    }
}