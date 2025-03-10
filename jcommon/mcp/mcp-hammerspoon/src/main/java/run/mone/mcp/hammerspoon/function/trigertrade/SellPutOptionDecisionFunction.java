package run.mone.mcp.hammerspoon.function.trigertrade;

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
import run.mone.hive.mcp.spec.McpSchema;
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

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

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
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String command = (String) args.get("command");
            String stockNameOrCode = (String) args.get("stockNameOrCode");

            if (name.equalsIgnoreCase(command)) {
                //todo 日期自动算
                OptionChainModel basicModel = new OptionChainModel(stockNameOrCode, "2025-03-14", TimeZoneId.NewYork);
                List<OptionDetailBO> optionDetailBOList = TigerTradeSdkUtil.getOptionChainDetail(basicModel, "put");





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
