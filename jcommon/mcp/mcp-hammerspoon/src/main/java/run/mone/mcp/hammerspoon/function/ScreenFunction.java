package run.mone.mcp.hammerspoon.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import run.mone.hive.mcp.spec.McpSchema;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author shanwb
 * @date 2025-02-08
 */
@Data
@Slf4j
public class ScreenFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "captureDingTalkWindow";
    private String desc = "查看DingTalk(钉钉)软件截图";
    private static final String HAMMERSPOON_URL = "http://localhost:27123/execute";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
            
                },
                "required": []
            }
            """;

    public ScreenFunction() {
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

            List<String> luaCode = new ArrayList<>();


            luaCode.add("return captureAppWindow(\"企业微信\")");

            McpSchema.CallToolResult res = new McpSchema.CallToolResult(new ArrayList<>(), false);
            for (String code : luaCode) {
                res = executeHammerspoonCommand(code);
            }
            return res;
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
