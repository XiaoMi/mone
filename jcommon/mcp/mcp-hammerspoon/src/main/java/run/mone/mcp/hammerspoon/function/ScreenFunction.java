package run.mone.mcp.hammerspoon.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import run.mone.hive.mcp.spec.McpSchema;

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
    private String name = "captureWeComWindow";
    private String desc = "查看企业微信软件截图";
    private static final String PYTHON_SERVER_URL_ENV = "PYTHON_SERVER_URL";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {},
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

    private String getPythonServerUrl() {
        String url = System.getenv(PYTHON_SERVER_URL_ENV);
        if (url == null || url.isEmpty()) {
            throw new RuntimeException(PYTHON_SERVER_URL_ENV + " 环境变量未设置");
        }
        return url;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String pythonServerUrl = getPythonServerUrl();

            // 构建请求
            Request request = new Request.Builder()
                    .url(pythonServerUrl + "/capture_window")
                    .get()
                    .build();

            // 发送请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("HTTP Error: " + response.code())),
                            true
                    );
                }

                // 处理响应
                String responseBody = response.body() != null ? response.body().string() : "{}";
                log.info("responseBody:{}",responseBody);
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                if (jsonResponse.has("image") && jsonResponse.has("mimetype")) {
                    String imageBase64 = jsonResponse.get("image").asText();
                    String mimeType = jsonResponse.get("mimetype").asText();
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.ImageContent(null, null, "image", imageBase64, mimeType)),
                            false
                    );
                } else {
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Invalid response format")),
                            true
                        );
                }

            }
        } catch (Exception e) {
            log.error("Error calling Python server", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }
}
