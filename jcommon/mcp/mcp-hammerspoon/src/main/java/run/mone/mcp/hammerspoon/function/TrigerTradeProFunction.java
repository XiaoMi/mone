package run.mone.mcp.hammerspoon.function;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

/**
 * @author shanwb
 * @date 2025-03-09
 */
@Data
@Slf4j
public class TrigerTradeProFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "trigerTradeOperation";
    private String desc = "triger trade operations including TrigerTrade(老虎国际pro)";
    private static final String HAMMERSPOON_URL = "http://localhost:27123/execute";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    private LLM llm;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "command": {
                        "type": "string",
                        "enum": ["searchAndOpenStock", "captureAppWindow", "maximizeAppWindow", "clickOptionsChain", "sellPutOption", "sellPutOptionFlow", "analyzeOptionsChain", "analyzeOptionsChainText"],
                        "description": "The operation type to perform  example:帮我买一手小米put 会调用:sellPutOptionFlow"
                    },
                    "stockNameOrCode": {
                        "type": "string",
                        "description": "stock name or stock code"
                    },
                    "appName": {
                        "type": "string",
                        "description": "target operate app name"
                    },
                    "mousePositionX": {
                        "type": "string",
                        "description": "mouse position X"
                    },
                    "mousePositionY": {
                        "type": "string",
                        "description": "mouse position Y"
                    },
                    "quantity": {
                        "type": "string",
                        "description": "quantity of options count"
                    },
                    "base64Image": {
                        "type": "string",
                        "description": "base64 encoded image of the options chain"
                    },
                    "optionsChainText": {
                        "type": "string",
                        "description": "text content of the options chain"
                    }
                },
                "required": ["command"]
            }
            """;

    public TrigerTradeProFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String command = (String) args.get("command");
            String luaCode;

            switch (command) {
                case "searchAndOpenStock":
                    String stockNameOrCode = (String) args.get("stockNameOrCode");
                    luaCode = String.format("return searchStock('%s')",
                            escapeString(stockNameOrCode));
                    break;
                case "sellPutOptionFlow":
                    return executeSellPutOptionFlow(args);

                case "captureAppWindow":
                    luaCode = String.format("return captureAppWindow('%s')",
                            escapeString("老虎国际"));
                    break;

                case "openApp":
                    String appName = (String) args.get("appName");
                    luaCode = String.format("return openApp('%s')", escapeString(appName));

                    break;
                case "moveToAppAndClick":
                    appName = (String) args.get("appName");
                    String mousePositionX = (String) args.get("mousePositionX");
                    String mousePositionY = (String) args.get("mousePositionY");
                    luaCode = String.format("return moveToAppAndClick('%s','%s','%s')", escapeString(appName), mousePositionX, mousePositionY);

                    break;
                case "clickOptionsChain":
                    luaCode = "return clickOptionsChain()";

                    break;
                case "sellPutOption":
                    String quantity = (String) args.get("quantity");
                    if (StringUtils.isBlank(quantity)) {
                        quantity = "1";
                    }
                    luaCode = String.format("return sellPutOption('%s')", escapeString(quantity));
                    break;

                case "analyzeOptionsChain":
                    String base64Image = (String) args.get("base64Image");
                    String content = "";
                    if (StringUtils.isNotEmpty(base64Image)) {
                        JsonObject req = new JsonObject();
                        req.addProperty("role", "user");
                        JsonArray array = new JsonArray();

                        JsonObject obj1 = new JsonObject();
                        obj1.addProperty("type", "text");
                        obj1.addProperty("text", " 帮我提取期权链数据 thx");
                        array.add(obj1);

                        JsonObject obj2 = new JsonObject();
                        obj2.addProperty("type", "image_url");
                        JsonObject img = new JsonObject();
                        img.addProperty("url", base64Image);
                        obj2.add("image_url", img);
                        array.add(obj2);
                        req.add("content", array);

                        LLM vllm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO).build());
                        String res = vllm.chat(Lists.newArrayList(AiMessage.builder().role("user").jsonContent(req).build()));
                        System.out.println(res);
                        content = res;
                    }
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(content)),
                            true
                    );

                case "analyzeOptionsChainText":
                    String optionsChainText = (String) args.get("optionsChainText");
                    if (StringUtils.isNotEmpty(optionsChainText)) {

                        String c = "基于以下期权链数据，请分析并推荐最佳的行权价。请考虑以下因素：1. 当前股价 2. 隐含波动率 3. 成交量 4. 未平仓量。给出你的分析理由。期权链数据如下：\n" + optionsChainText;

                        LLM vllm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_DEEPSEEK_R1).build());
                        String res = vllm.chat(c);
                        return new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(res)),
                                false
                        );
                    }
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("No options chain text provided")),
                            true
                    );

                default:
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Unknown command: " + command)),
                            true
                    );
            }

            return executeHammerspoonCommand(luaCode);
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
