package run.mone.mcp.hammerspoon.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "command": {
                        "type": "string",
                        "enum": ["searchAndOpenStock", "captureAppWindow", "maximizeAppWindow", "moveToAppAndClick"],
                        "description": "The operation type to perform"
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
}
