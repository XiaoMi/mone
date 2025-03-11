package run.mone.mcp.hammerspoon.function.trigertrade;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.struct.enums.TimeZoneId;
import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.AiMessage;
import run.mone.mcp.hammerspoon.function.LocateCoordinates;
import run.mone.mcp.hammerspoon.function.TrigerTradeProFunction;
import run.mone.mcp.hammerspoon.function.trigertrade.dto.OptionDetailBO;

/**
 * @author shanwb
 * @date 2025-03-09
 */
@Data
@Slf4j
public class SellPutOptionDecisionFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "sellPutOptionDecision";
    private String desc = "triger trade app sell put options decision";
    private static final String HAMMERSPOON_URL = "http://localhost:27123/execute";

    private static Gson gson = new Gson();

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private TrigerTradeProFunction trigerTradeProFunction ;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "command": {
                        "type": "string",
                        "enum": ["sellPutOptionDecision"],
                        "description": "The operation type to perform example:帮我选一个put期权 会调用:sellPutOptionDecision"
                    },
                    "stockNameOrCode": {
                        "type": "string",
                        "description": "stock name or stock code"
                    }
                },
                "required": ["command"]
            }
            """;

    public SellPutOptionDecisionFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();


        trigerTradeProFunction = new TrigerTradeProFunction();
        //trigerTradeProFunction.setLlm();
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String command = (String) args.get("command");
            String stockNameOrCode = (String) args.get("stockNameOrCode");

            if (name.equalsIgnoreCase(command)) {
                String luaCode = String.format("return captureAppWindow('%s')",
                        escapeString("老虎国际"));

                McpSchema.CallToolResult result = executeHammerspoonCommand(luaCode);
                McpSchema.ImageContent imageContent = (McpSchema.ImageContent) result.content().get(0);
                String base64 = imageContent.data();

                System.out.println("base64:" + base64.substring(100));

                //图片识别期权链-> markdown
                String testBase64Image = "data:image/jpeg;base64,"+base64;

                // Prepare function arguments
                Map<String, Object> params = new HashMap<>();
                params.put("command", "analyzeOptionsChain");
                params.put("base64Image", testBase64Image);

                // Execute the function
                McpSchema.CallToolResult image = trigerTradeProFunction.apply(args);
                McpSchema.TextContent markdown = (McpSchema.TextContent) image.content().get(0);
                String markdownStr = markdown.data();

                // llm call to analyze markdown
                LLM aLLM = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DEEPSEEK).build());
                String prompt = """
                        作为一位专业的期权卖方策略分析师，您的任务是基于期权链数据，分析并找出推荐最佳的卖出看跌期权(PUT)交易机会。
                        
                        ## 分析流程
                        基于上述信息，请执行以下分析:
                        
                        ### 1. 筛选标准
                        - 分析不同到期日的风险收益比
                        - 评估不同行权价位的安全边际
                        - 计算各期权的年化收益率
                        - 考虑隐含波动率相对历史水平的位置
                        - 评估每个期权合约的流动性
                        
                        ### 2. 风险评估
                        - 计算最大可能亏损
                        - 评估被指派的可能性
                        - 分析如果标的物大幅下跌时的风险暴露
                        - 考虑到期前可能出现的重大事件(财报、分红等)
                        
                        ### 3. 策略建议
                        根据以上分析，请推荐：
                        - 一个最佳的卖出看跌期权交易，包括具体到期日和行权价
                        - 止损建议
                        - 盈利目标(何时平仓)
                        - 可能的调整策略(如果市场转向不利)
                        
                        ### 4. 收益计算
                        - 提供保证金要求估算
                        - 计算潜在收益率(占用保证金的百分比)
                        - 计算年化收益率
                        - 分析盈亏平衡点
                        
                        ## 输出格式
                        请以json形式返回，格式如下：
                        
                        {
                          "identifier":"请严格从我给你的期权链数据中选取",
                          "strike":"行权价",
                          "bidPrice":"买盘价",
                          "askPrice":"卖盘价",
                          "description":"决策描述"
                        }
                        
                        
                        ## 特别注意事项
                        - 优先考虑风险管理而非单纯追求高收益
                        - 考虑当前市场环境和波动率情况
                        - 评估期权的流动性(未平仓合约数量和bid-ask差价)
                        - 考虑任何即将到来的可能影响标的资产价格的事件
                        
                        
                        需要的信息如下：
                        %s
                        """.formatted(markdownStr);
                String aRes = aLLM.chat(List.of(new AiMessage("user", prompt)));
                String bidPrice = null;
                try {
                    JsonObject jsonResponse = gson.fromJson(aRes, JsonObject.class);
                    bidPrice = jsonResponse.get("bidPrice").getAsString();
                    System.out.println("Extracted bidPrice: " + bidPrice);
                } catch (Exception e) {
                    System.out.println("Error parsing LLM response: " + e.getMessage());
                    e.printStackTrace();
                }



                LLM vllm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.OPENROUTER).build());
                LocateCoordinates locateCoordinates = new LocateCoordinates();
                locateCoordinates.setLlm(vllm);

                locateCoordinates.locateCoordinates("", base64);


            }

            return null;
            //return executeHammerspoonCommand(luaCode);
        } catch (Exception e) {
            log.error("Error executing Hammerspoon command", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }

    private McpSchema.CallToolResult executeHammerspoonCommand(String luaCode) {
        try {
            Map<String, String> requestBody = Map.of("code", luaCode);
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            log.info("jsonBody:{}", jsonBody);

            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(HAMMERSPOON_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("HTTP Error: " + response.code())),
                            true
                    );
                }

                String responseBody = response.body() != null ? response.body().string() : "{}";
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                log.info("jsonResponse:{}", jsonResponse);

                boolean success = jsonResponse.get("success").asBoolean();
                if (jsonResponse.has("result")) {
                    String result = jsonResponse.get("result").asText();

                    if (luaCode.contains("captureAppWindow")) {
                        try {
                            Path path = Paths.get(result);
                            if (Files.exists(path) && Files.isRegularFile(path)) {
                                byte[] fileBytes = Files.readAllBytes(path);
                                result = Base64.getEncoder().encodeToString(fileBytes);
                                return new McpSchema.CallToolResult(
                                        List.of(new McpSchema.ImageContent(null, null, "image", result, "image/jpeg")),
                                        !success
                                );
                            }
                        } catch (Exception e) {
                            log.info("it is not a file path:{}", result);
                        }
                    }

                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(result)),
                            !success
                    );
                } else {
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Operation completed successfully")),
                            !success
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error calling Hammerspoon HTTP server", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }

    private String escapeString(String input) {
        if (input == null) return "";
        return input.replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private McpSchema.CallToolResult executeSellPutOptionFlow(Map<String, Object> args) {
        String stockNameOrCode = (String) args.get("stockNameOrCode");
        String quantity = (String) args.get("quantity");
        if (StringUtils.isBlank(quantity)) {
            quantity = "1";
        }

        // 第一步：搜索并打开股票
        String searchLuaCode = String.format("return searchStock('%s')", escapeString(stockNameOrCode));
        McpSchema.CallToolResult searchResult = executeHammerspoonCommand(searchLuaCode);
        if (searchResult.isError()) {
            return searchResult;
        }

        // 第二步：点击期权链
        String optionsChainLuaCode = "return clickOptionsChain()";
        McpSchema.CallToolResult optionsChainResult = executeHammerspoonCommand(optionsChainLuaCode);
        if (optionsChainResult.isError()) {
            return optionsChainResult;
        }

        // 第三步：卖出期权
        String sellPutLuaCode = String.format("return sellPutOption('%s')", escapeString(quantity));
        McpSchema.CallToolResult sellPutResult = executeHammerspoonCommand(sellPutLuaCode);

        // 返回组合结果
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(
                        String.format("卖出期权流程已完成，股票：%s，数量：%s",
                                stockNameOrCode, quantity)
                )),
                sellPutResult.isError()
        );
    }
}
