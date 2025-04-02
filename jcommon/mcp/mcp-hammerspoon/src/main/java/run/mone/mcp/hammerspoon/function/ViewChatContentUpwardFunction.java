package run.mone.mcp.hammerspoon.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Cline
 * @date 2025-04-01
 */
@Data
@Slf4j
public class ViewChatContentUpwardFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "view_chat_content_upward";
    private String desc = "聊天记录向上滚动一页";
    private static final String PYTHON_SERVER_URL_ENV = "PYTHON_SERVER_URL";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    // This tool does not require any input parameters
    private String toolScheme = """
            {
                "type": "object",
                "properties": {},
                "required": []
            }
            """;

    public ViewChatContentUpwardFunction() {
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
            log.error(PYTHON_SERVER_URL_ENV + " 环境变量未设置");
            throw new RuntimeException(PYTHON_SERVER_URL_ENV + " 环境变量未设置");
        }
        return url;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String pythonServerUrl = getPythonServerUrl();

            // 构建请求 - 空的 RequestBody 因为 Python 端不需要参数
            RequestBody body = RequestBody.create(new byte[0], null);
            Request request = new Request.Builder()
                    .url(pythonServerUrl + "/viewChatContentUpward")
                    .post(body) // 使用 POST 方法
                    .build();

            // 发送请求
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "{}";
                log.info("Response from /viewChatContentUpward: {}", responseBody);

                if (!response.isSuccessful()) {
                     log.error("HTTP Error calling /viewChatContentUpward: {} - {}", response.code(), responseBody);
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("HTTP Error: " + response.code() + " - " + responseBody)),
                            true
                    );
                }

                Thread.sleep(500);

                // 成功翻页后，进行截图
                Request captureRequest = new Request.Builder()
                        .url(pythonServerUrl + "/capture_window")
                        .get()
                        .build();

                try (Response captureResponse = client.newCall(captureRequest).execute()) {
                    if (!captureResponse.isSuccessful()) {
                        log.error("HTTP Error calling /capture_window: {}", captureResponse.code());
                        return new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent("截图失败: HTTP Error " + captureResponse.code())),
                                true
                        );
                    }
                    String captureResponseBody = captureResponse.body() != null ? captureResponse.body().string() : "{}";
                    log.info("capture_window responseBody:{}", captureResponseBody);
                    JsonNode jsonResponse = objectMapper.readTree(captureResponseBody);
                    if (jsonResponse.has("image") && jsonResponse.has("mimetype")) {
                        String imageBase64 = jsonResponse.get("image").asText();
                        String mimeType = jsonResponse.get("mimetype").asText();
                        return new McpSchema.CallToolResult(
                                List.of(new McpSchema.ImageContent(null, null, "image", imageBase64, mimeType)),
                                false
                        );
                    } else {
                        log.error("Invalid response format from /capture_window: {}", captureResponseBody);
                        return new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent("截图失败: 无效的响应格式")),
                                true
                        );
                    }
                }

            }
        } catch (Exception e) {
            log.error("Error in ViewChatContentBelowFunction", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }
}
