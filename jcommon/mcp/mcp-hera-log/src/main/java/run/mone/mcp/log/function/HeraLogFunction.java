package run.mone.mcp.log.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2025/2/21 10:01
 */
@Slf4j
@Getter
public class HeraLogFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private static String heraLogUrl;
    private static OkHttpClient okHttpClient;

    private static final Gson gson = new Gson();

    private String name = "hera_log_executor";
    private String desc = "Execute hera-log operations (query_project, access_log...)";

    private String githubToolSchema = """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["query_project", "access_log"],
                        "description": "Type of Hera-log operation to execute"
                    },
                    "app_id": {
                        "type": "Long",
                        "description": "miline appId"
                    },
                    "env_id": {
                        "type": "Long",
                        "description": "miline envId"
                    },
                    "user_name": {
                        "type": "String",
                        "description": "user name"
                    },
                    "space_id": {
                        "type": "Long",
                        "description": "hera space tree id"
                    },
                    "store_id": {
                        "type": "Long",
                        "description": "hera store tree id"
                    },
                },
                "required": ["type"]
            }
            """;

    public HeraLogFunction() {
        heraLogUrl = System.getenv().getOrDefault("Hera_log_url", "");
        if (StringUtils.isBlank(heraLogUrl)) {
            throw new IllegalStateException("Hera_log_url environment variable is required");
        }
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(5 * 60, TimeUnit.SECONDS)
                .writeTimeout(5 * 60, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(100, 10, TimeUnit.MINUTES))
                .build();
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String type = (String) args.get("type");
        try {
            return switch (type.toLowerCase()) {
                case "query_project" -> queryAccess(
                        (String) args.get("app_id"),
                        (String) args.get("env_id"));
                case "access_log" -> accessLog(
                        (String) args.get("app_id"),
                        (String) args.get("env_id"),
                        (String) args.get("user_name"),
                        (String) args.get("space_id"),
                        (String) args.get("store_id"));
                default -> throw new IllegalArgumentException("Unsupported operation type: " + type);
            };
        } catch (Exception e) {
            throw new RuntimeException("Hera-log operation failed: " + e.getMessage(), e);
        }
    }

    private McpSchema.CallToolResult accessLog(String appId, String envId, String userName, String spaceId, String storeId) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(envId)) {
            throw new IllegalStateException("参数不能为空");
        }
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("appId", appId);
        requestBody.addProperty("envId", envId);
        requestBody.addProperty("userName", userName);
        requestBody.addProperty("spaceId", spaceId);
        requestBody.addProperty("storeId", storeId);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                requestBody.toString()
        );
        String url = heraLogUrl + "/access/log/auto";
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            if (jsonObject.has("data") && jsonObject.get("data").getAsJsonObject() != null) {
                String msg = jsonObject.get("data").getAsString();
                if (StringUtils.equals("success", msg)) {
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("接入成功")),
                            false);
                }
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("接入失败")),
                        false);

            }
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(responseBody)),
                    false);
        } catch (IOException e) {
            log.error("Failed to execute Hera-log operation", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true);
        }
    }


    public McpSchema.CallToolResult queryAccess(String appId,
                                                String envId) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(envId)) {
            throw new IllegalStateException("参数不能为空");
        }
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("appId", appId);
        requestBody.addProperty("envId", envId);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                requestBody.toString()
        );
        String url = heraLogUrl + "/access/log/enable";
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        // 发送请求并处理响应
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // 获取响应体
            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            if (jsonObject.has("data") && jsonObject.get("data").getAsJsonObject() != null) {
                boolean asBoolean = jsonObject.get("data").getAsJsonObject().get("hasLoggingEnabled").getAsBoolean();
                if (asBoolean) {
                    String logUrl = jsonObject.get("data").getAsJsonObject().get("logUrl").getAsString();
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(logUrl)),
                            false);
                }
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("未接入日志")),
                        false);

            }
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(responseBody)),
                    false);
        } catch (IOException e) {
            log.error("Failed to execute Hera-log operation", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true);
        }
    }
}
