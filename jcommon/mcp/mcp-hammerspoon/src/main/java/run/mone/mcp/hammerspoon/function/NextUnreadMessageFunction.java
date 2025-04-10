package run.mone.mcp.hammerspoon.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
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
public class NextUnreadMessageFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "next_unread_message";
    private String desc = "切换到下一个未读消息";
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

    public NextUnreadMessageFunction() {
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
            
            // 确保窗口最大化
            McpSchema.CallToolResult result = ensureWindowMaximized(pythonServerUrl);
            if (result != null) return result;

            // 切换到下一个未读消息
            result = switchToNextUnreadMessage(pythonServerUrl);
            if (result != null) return result;

            Thread.sleep(500);  // 等待消息切换完成

            // 点击聊天记录区域
            result = clickChatArea(pythonServerUrl);
            if (result != null) return result;

            // 第一次截图
            CaptureResult capture1 = captureWindow(pythonServerUrl, "第一次");
            if (capture1.error() != null) return capture1.error();

            // 向上翻页
            result = scrollChatContentUpward(pythonServerUrl);
            if (result != null) return result;

            Thread.sleep(500);

            // 第二次截图
            CaptureResult capture2 = captureWindow(pythonServerUrl, "第二次");
            if (capture2.error() != null) return capture2.error();

            // 点击输入框
            result = clickInputBox(pythonServerUrl);
            if (result != null) return result;

            // 拼接图片
            return mergeImages(pythonServerUrl, capture2.base64(), capture1.base64(), 
                             capture2.mimeType(), capture1.mimeType());

        } catch (Exception e) {
            log.error("Error in NextUnreadMessageFunction", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }

    private record CaptureResult(String base64, String mimeType, McpSchema.CallToolResult error) {}

    private McpSchema.CallToolResult ensureWindowMaximized(String pythonServerUrl) throws IOException {
        Request request = new Request.Builder()
                .url(pythonServerUrl + "/is_window_maximized")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "{}";
            log.info("Response from /is_window_maximized: {}", responseBody);

            if (!response.isSuccessful()) {
                log.error("HTTP Error calling /is_window_maximized: {} - {}", response.code(), responseBody);
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("HTTP Error: " + response.code() + " - " + responseBody)),
                        true
                );
            }

            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            if (!jsonResponse.has("maximized") || !jsonResponse.get("maximized").asBoolean()) {
                return maximizeWindow(pythonServerUrl);
            }
        }
        return null;
    }

    private McpSchema.CallToolResult maximizeWindow(String pythonServerUrl) throws IOException {
        RequestBody body = RequestBody.create(new byte[0], null);
        Request request = new Request.Builder()
                .url(pythonServerUrl + "/maximize_window")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "{}";
            log.info("Response from /maximize_window: {}", responseBody);

            if (!response.isSuccessful()) {
                log.error("HTTP Error calling /maximize_window: {} - {}", response.code(), responseBody);
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("HTTP Error: " + response.code() + " - " + responseBody)),
                        true
                );
            }
        }
        return null;
    }

    private CaptureResult captureWindow(String pythonServerUrl, String captureType) throws IOException {
        Request request = new Request.Builder()
                .url(pythonServerUrl + "/capture_window")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("HTTP Error calling /capture_window: {}", response.code());
                return new CaptureResult(null, null, new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(captureType + "截图失败: HTTP Error " + response.code())),
                        true
                ));
            }

            String responseBody = response.body() != null ? response.body().string() : "{}";
            log.info("{}截图 responseBody:{}", captureType, responseBody);
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            
            if (jsonResponse.has("image") && jsonResponse.has("mimetype")) {
                return new CaptureResult(
                    jsonResponse.get("image").asText(),
                    jsonResponse.get("mimetype").asText(),
                    null
                );
            } else {
                log.error("{}截图无效的响应格式: {}", captureType, responseBody);
                return new CaptureResult(null, null, new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(captureType + "截图失败: 无效的响应格式")),
                        true
                ));
            }
        }
    }

    private McpSchema.CallToolResult scrollChatContentUpward(String pythonServerUrl) throws IOException {
        RequestBody body = RequestBody.create(new byte[0], null);
        Request request = new Request.Builder()
                .url(pythonServerUrl + "/viewChatContentUpward")
                .post(body)
                .build();

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
        }
        return null;
    }

    private McpSchema.CallToolResult clickInputBox(String pythonServerUrl) throws IOException {
        RequestBody body = RequestBody.create(new byte[0], null);
        Request request = new Request.Builder()
                .url(pythonServerUrl + "/click_input_box")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "{}";
            log.info("Response from /click_input_box: {}", responseBody);

            if (!response.isSuccessful()) {
                log.error("HTTP Error calling /click_input_box: {} - {}", response.code(), responseBody);
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("点击输入框失败: HTTP Error " + response.code() + " - " + responseBody)),
                        true
                );
            }
        }
        return null;
    }

    private McpSchema.CallToolResult mergeImages(String pythonServerUrl, String image1Base64, String image2Base64,
                                               String mimeType1, String mimeType2) throws IOException {
        Request request = new Request.Builder()
                .url(pythonServerUrl + "/merge_images")
                .post(RequestBody.create(
                        objectMapper.writeValueAsString(Map.of(
                                "image1", image1Base64,
                                "image2", image2Base64,
                                "mimeType1", mimeType1,
                                "mimeType2", mimeType2
                        )),
                        MediaType.parse("application/json; charset=utf-8")
                ))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("HTTP Error calling /merge_images: {}", response.code());
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("图片拼接失败: HTTP Error " + response.code())),
                        true
                );
            }

            String responseBody = response.body() != null ? response.body().string() : "{}";
            log.info("merge_images responseBody:{}", responseBody);
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            
            if (jsonResponse.has("image") && jsonResponse.has("mimetype")) {
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.ImageContent(null, null, "image",
                                jsonResponse.get("image").asText(),
                                jsonResponse.get("mimetype").asText())),
                        false
                );
            } else {
                log.error("图片拼接无效的响应格式: {}", responseBody);
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("图片拼接失败: 无效的响应格式")),
                        true
                );
            }
        }
    }

    private McpSchema.CallToolResult switchToNextUnreadMessage(String pythonServerUrl) throws IOException {
        RequestBody body = RequestBody.create(new byte[0], null);
        Request request = new Request.Builder()
                .url(pythonServerUrl + "/next_unread_message")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "{}";
            log.info("Response from /next_unread_message: {}", responseBody);

            if (!response.isSuccessful()) {
                log.error("HTTP Error calling /next_unread_message: {} - {}", response.code(), responseBody);
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("切换未读消息失败: HTTP Error " + response.code() + " - " + responseBody)),
                        true
                );
            }
        }
        return null;
    }

    private McpSchema.CallToolResult clickChatArea(String pythonServerUrl) throws IOException {
        RequestBody body = RequestBody.create(new byte[0], null);
        Request request = new Request.Builder()
                .url(pythonServerUrl + "/click_chat_area")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "{}";
            log.info("Response from /click_chat_area: {}", responseBody);

            if (!response.isSuccessful()) {
                log.error("HTTP Error calling /click_chat_area: {} - {}", response.code(), responseBody);
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("点击聊天区域失败: HTTP Error " + response.code() + " - " + responseBody)),
                        true
                );
            }
        }
        return null;
    }
}
