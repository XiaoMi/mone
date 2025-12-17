package run.mone.mcp.multimodal.tool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.spring.starter.WebSocketCaller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Android 截图获取工具
 * 通过 WebSocket 从 Android 设备获取当前屏幕截图
 * <p>
 * 用于在 ReactorRole 中获取 Android 设备的屏幕截图，
 * 返回的图片数据为 Base64 编码格式，可用于后续的图像分析。
 *
 * @author goodjava@qq.com
 * @date 2025/12/16
 */
@Slf4j
public class AndroidScreenshotTool implements ITool {

    public static final String name = "android_screenshot";

    private static final Gson gson = new Gson();

    /**
     * 默认超时时间（秒）
     */
    private static final int DEFAULT_TIMEOUT = 30;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                Request to capture a screenshot from an Android device via WebSocket connection.
                Use this when you need to see the current screen state of an Android device
                for UI analysis, automation, or debugging purposes.

                **When to use this tool:**
                - Capture the current screen of an Android device
                - Get visual feedback for UI automation tasks
                - Analyze the current app state or screen layout
                - Verify UI changes after performing actions

                **Prerequisites:**
                - The Android device must be connected via WebSocket
                - A valid clientId is required to identify the target device

                **Return value:**
                - Screenshot image in Base64 encoding
                - Screen resolution (width x height)
                - Success/failure status
                """;
    }

    @Override
    public String parameters() {
        return """
                - clientId: (optional) The WebSocket client ID of the target Android device.
                            If not provided, will use the clientId from the ReactorRole context.
                - timeout: (optional) Timeout in seconds, default is 30 seconds.
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                Checklist here (optional)
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                <android_screenshot>
                <clientId>Client ID here (optional)</clientId>
                %s
                </android_screenshot>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: Capture screenshot using default clientId from context
                <android_screenshot>
                </android_screenshot>

                Example 2: Capture screenshot from a specific device
                <android_screenshot>
                <clientId>7225a92dc463ada1</clientId>
                </android_screenshot>

                Example 3: Capture screenshot with custom timeout
                <android_screenshot>
                <clientId>7225a92dc463ada1</clientId>
                <timeout>60</timeout>
                </android_screenshot>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 获取 clientId，优先从参数中获取，否则从 role 中获取
            String clientId = null;
            if (inputJson.has("clientId") && !StringUtils.isBlank(inputJson.get("clientId").getAsString())) {
                clientId = inputJson.get("clientId").getAsString();
            } else if (role != null) {
                clientId = role.getClientId();
            }

            if (StringUtils.isEmpty(clientId)) {
                log.error("android_screenshot operation missing required clientId parameter");
                result.addProperty("error", "Missing required parameter 'clientId'. " +
                        "Please provide clientId or ensure ReactorRole has a valid clientId.");
                result.addProperty("success", false);
                return result;
            }

            // 获取超时时间
            int timeout = DEFAULT_TIMEOUT;
            if (inputJson.has("timeout")) {
                try {
                    timeout = inputJson.get("timeout").getAsInt();
                } catch (Exception e) {
                    log.warn("Invalid timeout value, using default: {}", DEFAULT_TIMEOUT);
                }
            }

            log.info("Capturing Android screenshot: clientId={}, timeout={}s", clientId, timeout);

            // 执行截图操作
            return performScreenshot(clientId, timeout);

        } catch (Exception e) {
            log.error("Exception occurred while executing android_screenshot operation", e);
            result.addProperty("error", "Failed to execute android_screenshot operation: " + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }

    /**
     * 执行截图操作
     *
     * @param clientId 客户端 ID
     * @param timeout  超时时间（秒）
     * @return 截图结果
     */
    private JsonObject performScreenshot(String clientId, int timeout) {
        JsonObject result = new JsonObject();

        try {
            // 通过 WebSocket 调用 Android 客户端 - 使用 callAndroid，action 在根级别
            WebSocketCaller caller = WebSocketCaller.getInstance();
            Map<String, Object> response = caller.callAndroid(clientId, "screenshot", null, timeout, TimeUnit.SECONDS);

            // 解析响应
            return parseScreenshotResponse(response, clientId);

        } catch (TimeoutException e) {
            log.error("Screenshot timeout for clientId: {}", clientId);
            result.addProperty("error", "Screenshot request timed out after " + timeout + " seconds");
            result.addProperty("success", false);
            return result;
        } catch (Exception e) {
            log.error("Screenshot failed for clientId: {}", clientId, e);
            result.addProperty("error", "Screenshot failed: " + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }

    /**
     * 解析截图响应
     */
    @SuppressWarnings("unchecked")
    private JsonObject parseScreenshotResponse(Map<String, Object> response, String clientId) {
        JsonObject result = new JsonObject();

        if (response == null) {
            result.addProperty("error", "Empty response from Android device");
            result.addProperty("success", false);
            return result;
        }

        // 检查是否有 data 字段（嵌套响应）
        Object dataObj = response.get("data");
        Map<String, Object> data = response;
        if (dataObj instanceof Map) {
            data = (Map<String, Object>) dataObj;
        }

        // 检查成功状态
        boolean success = true;
        if (data.containsKey("success")) {
            success = Boolean.parseBoolean(String.valueOf(data.get("success")));
        }

        if (!success) {
            String errorMsg = data.containsKey("error") ?
                    String.valueOf(data.get("error")) : "Unknown error";
            result.addProperty("error", errorMsg);
            result.addProperty("success", false);
            return result;
        }

        // 提取图片数据
        if (data.containsKey("image")) {
            String imageBase64 = String.valueOf(data.get("image"));
            result.addProperty("image", imageBase64);
            result.addProperty("imageSize", imageBase64.length());
        } else {
            result.addProperty("error", "No image data in response");
            result.addProperty("success", false);
            return result;
        }

        // 提取分辨率信息
        if (data.containsKey("width")) {
            result.addProperty("width", parseIntFromObject(data.get("width")));
        }
        if (data.containsKey("height")) {
            result.addProperty("height", parseIntFromObject(data.get("height")));
        }

        result.addProperty("success", true);
        result.addProperty("clientId", clientId);

        // 生成结果描述
        StringBuilder description = new StringBuilder();
        description.append("Screenshot captured successfully from device: ").append(clientId);
        if (result.has("width") && result.has("height")) {
            description.append(" (").append(result.get("width").getAsInt())
                    .append("x").append(result.get("height").getAsInt()).append(")");
        }
        result.addProperty("result", description.toString());

        log.info("Screenshot captured successfully: clientId={}, width={}, height={}",
                clientId,
                result.has("width") ? result.get("width").getAsInt() : "unknown",
                result.has("height") ? result.get("height").getAsInt() : "unknown");

        return result;
    }

    /**
     * 从 Object 解析整数
     */
    private int parseIntFromObject(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}