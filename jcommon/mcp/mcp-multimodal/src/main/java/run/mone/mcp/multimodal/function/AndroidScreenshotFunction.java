package run.mone.mcp.multimodal.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.spring.starter.WebSocketCaller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Android 截图获取工具
 * 通过 WebSocket 从 Android 设备获取当前屏幕截图
 *
 * @author goodjava@qq.com
 * @date 2025/12/16
 */
@Slf4j
@Component
public class AndroidScreenshotFunction implements McpFunction {

    private static final Gson gson = new Gson();

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "clientId": {
                        "type": "string",
                        "description": "Android 客户端的 WebSocket 连接 ID，用于标识目标设备"
                    },
                    "timeout": {
                        "type": "integer",
                        "description": "超时时间（秒），默认 30 秒"
                    }
                },
                "required": ["clientId"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String clientId = (String) arguments.get("clientId");
        int timeout = parseIntArgOrDefault(arguments, "timeout", 30);

        if (clientId == null || clientId.isEmpty()) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误: clientId 不能为空")), true));
        }

        try {
            log.info("获取 Android 截图: clientId={}, timeout={}s", clientId, timeout);

            // 构建截图请求
            Map<String, Object> data = new HashMap<>();
            data.put("action", "screenshot");

            // 通过 WebSocket 调用 Android 客户端
            WebSocketCaller caller = WebSocketCaller.getInstance();
            Map<String, Object> response = caller.call(clientId, "screenshot", data, timeout, TimeUnit.SECONDS);

            // 解析响应
            JsonObject result = parseScreenshotResponse(response);

            if (result.has("error")) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("截图失败: " + result.get("error").getAsString())), true));
            }

            // 构建成功响应
            StringBuilder sb = new StringBuilder();
            sb.append("截图成功!\n");

            if (result.has("width") && result.has("height")) {
                sb.append("分辨率: ").append(result.get("width").getAsInt())
                        .append("x").append(result.get("height").getAsInt()).append("\n");
            }

            if (result.has("image")) {
                String imageBase64 = result.get("image").getAsString();
                sb.append("图片大小: ").append(imageBase64.length() / 1024).append(" KB (Base64)\n");
                sb.append("图片数据: ").append(imageBase64.substring(0, Math.min(100, imageBase64.length()))).append("...\n");

                // 将完整的图片数据放入结果
                result.addProperty("imageBase64", imageBase64);
            }

            log.info("成功获取 Android 截图: clientId={}", clientId);

            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(sb.toString()),
                            new McpSchema.TextContent("详细数据: " + gson.toJson(result))), false));

        } catch (Exception e) {
            log.error("获取 Android 截图失败: clientId={}", clientId, e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误: " + e.getMessage())), true));
        }
    }

    /**
     * 解析截图响应
     */
    private JsonObject parseScreenshotResponse(Map<String, Object> response) {
        JsonObject result = new JsonObject();

        if (response == null) {
            result.addProperty("error", "响应为空");
            return result;
        }

        // 检查是否有 data 字段（嵌套响应）
        Object dataObj = response.get("data");
        Map<String, Object> data = response;
        if (dataObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) dataObj;
            data = dataMap;
        }

        // 提取图片数据
        if (data.containsKey("image")) {
            result.addProperty("image", String.valueOf(data.get("image")));
        }

        // 提取分辨率信息
        if (data.containsKey("width")) {
            result.addProperty("width", parseIntFromObject(data.get("width")));
        }
        if (data.containsKey("height")) {
            result.addProperty("height", parseIntFromObject(data.get("height")));
        }

        // 检查成功状态
        if (data.containsKey("success")) {
            boolean success = Boolean.parseBoolean(String.valueOf(data.get("success")));
            result.addProperty("success", success);
            if (!success && data.containsKey("error")) {
                result.addProperty("error", String.valueOf(data.get("error")));
            }
        }

        return result;
    }

    /**
     * 从 Object 解析整数
     */
    private int parseIntFromObject(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    /**
     * 解析整数参数，带默认值
     */
    private int parseIntArgOrDefault(Map<String, Object> arguments, String key, int defaultValue) {
        Object value = arguments.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public String getName() {
        return "android_screenshot";
    }

    @Override
    public String getDesc() {
        return "通过 WebSocket 从 Android 设备获取当前屏幕截图。" +
                "需要提供 Android 客户端的 WebSocket 连接 ID (clientId)，" +
                "返回截图的 Base64 编码数据和分辨率信息。";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
