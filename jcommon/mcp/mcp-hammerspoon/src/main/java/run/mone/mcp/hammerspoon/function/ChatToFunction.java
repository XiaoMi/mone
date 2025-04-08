package run.mone.mcp.hammerspoon.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hammerspoon.common.StringUtils;

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
public class ChatToFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "searchAndSendWeComMessage";
    private String desc = "使用企业微信向指定用户/群发送指定信息";
    private static final String PYTHON_SERVER_URL_ENV = "PYTHON_SERVER_URL";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "contactName": {
                        "type": "string",
                        "description": "企业微信联系人/群名称"
                    },
                    "message": {
                        "type": "string",
                        "description": "要发送的消息内容"
                    }
                },
                "required": ["contactName","message"]
            }
            """;

    public ChatToFunction() {
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
            String contactName = (String) args.get("contactName");
            String message = (String) args.get("message");
            if (contactName.endsWith("...")) {
                contactName = contactName.substring(0, contactName.length() - 3);
            }
            // 构建请求体
            Map<String, String> requestBodyMap = Map.of(
                "contactName", StringUtils.escapeWxUserName(contactName),
                "message", message
            );

            String jsonBody = objectMapper.writeValueAsString(requestBodyMap);
            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json; charset=utf-8")
            );

            // 构建请求
            Request request = new Request.Builder()
                    .url(pythonServerUrl + "/searchAndSendWeComMessage")
                    .post(body)
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

            }

            Thread.sleep(2000);

            //截图
            Request captureRequest = new Request.Builder()
                    .url(pythonServerUrl + "/capture_window")
                    .get()
                    .build();

            try (Response response = client.newCall(captureRequest).execute()) {
                if (!response.isSuccessful()) {
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("HTTP Error: " + response.code())),
                            true
                    );
                }
                String responseBody = response.body() != null ? response.body().string() : "{}";
                log.info("capture_window responseBody:{}",responseBody);
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
