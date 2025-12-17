package run.mone.mcp.multimodal.tool;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.spring.starter.WebSocketCaller;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Android 批量操作工具
 * 通过 WebSocket 向 Android 设备发送操作命令，按顺序执行
 * <p>
 * 使用场景示例：
 * - 登录流程：输入用户名 -> 输入密码 -> 点击登录按钮
 * - 表单填写：依次填写输入框，然后点击提交
 * - 滑动浏览：多次滑动查看内容
 *
 * @author goodjava@qq.com
 * @date 2025/12/16
 */
@Slf4j
public class AndroidActionTool implements ITool {

    public static final String name = "android_action";

    private static final Gson gson = new Gson();
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
                Execute actions on an Android device via WebSocket connection.
                Actions are executed sequentially in the order provided.

                **Supported actions:**
                - click: Tap at specified coordinates (x, y)
                - long_press: Long press at coordinates (x, y) for duration ms
                - type: Input text content
                - scroll: Scroll in direction (up/down/left/right) at position
                - drag: Drag from (x, y) to (x2, y2)
                - press_home: Press Home button
                - press_back: Press Back button
                - press_recents: Press Recent Apps button
                - open_notifications: Open notification panel
                - open_quick_settings: Open quick settings
                - open_app: Open app by package name
                - screenshot: Take a screenshot

                **Example use cases:**
                - Login flow: type username -> type password -> click login button
                - Form submission: fill fields sequentially, then submit
                """;
    }

    @Override
    public String parameters() {
        return """
                - clientId: (optional) The WebSocket client ID of the target Android device.
                            If not provided, will use the clientId from the ReactorRole context.
                - actions: (required) Array of action objects to execute sequentially. Each action has:
                    - action: The action type (click, type, scroll, etc.)
                    - x, y: Coordinates for click, long_press, scroll
                    - x2, y2: End coordinates for drag
                    - content: Text for type action
                    - direction: Direction for scroll (up/down/left/right)
                    - distance: Scroll distance (default 500)
                    - duration: Duration in ms for long_press, drag
                    - packageName: Package name for open_app
                    - delay: Delay in ms before execution
                - timeout: (optional) Timeout per action in seconds, default 30
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
                <android_action>
                <clientId>Client ID (optional)</clientId>
                <actions>
                [
                    {"action": "type", "content": "username"},
                    {"action": "click", "x": 500, "y": 800}
                ]
                </actions>
                %s
                </android_action>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: Simple login flow
                <android_action>
                <actions>
                [
                    {"action": "type", "content": "myusername"},
                    {"action": "click", "x": 500, "y": 600, "delay": 500},
                    {"action": "type", "content": "mypassword"},
                    {"action": "click", "x": 500, "y": 800}
                ]
                </actions>
                </android_action>

                Example 2: Scroll and click
                <android_action>
                <clientId>7225a92dc463ada1</clientId>
                <actions>
                [
                    {"action": "scroll", "x": 500, "y": 1000, "direction": "up", "distance": 500},
                    {"action": "click", "x": 300, "y": 500, "delay": 300}
                ]
                </actions>
                </android_action>

                Example 3: Open app and interact
                <android_action>
                <actions>
                [
                    {"action": "open_app", "packageName": "com.tencent.mm"},
                    {"action": "click", "x": 400, "y": 600, "delay": 2000}
                ]
                </actions>
                </android_action>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 获取 clientId
            String clientId = null;
            if (inputJson.has("clientId") && !StringUtils.isBlank(inputJson.get("clientId").getAsString())) {
                clientId = inputJson.get("clientId").getAsString();
            } else if (role != null) {
                clientId = role.getClientId();
            }

            if (StringUtils.isEmpty(clientId)) {
                log.error("android_action operation missing required clientId parameter");
                result.addProperty("error", "Missing required parameter 'clientId'");
                result.addProperty("success", false);
                return result;
            }

            // 获取操作列表
            if (!inputJson.has("actions")) {
                result.addProperty("error", "Missing required parameter 'actions'");
                result.addProperty("success", false);
                return result;
            }

            JsonArray actionsArray = inputJson.getAsJsonArray("actions");
            if (actionsArray == null || actionsArray.isEmpty()) {
                result.addProperty("error", "Actions array is empty");
                result.addProperty("success", false);
                return result;
            }

            // 获取超时时间
            int timeout = DEFAULT_TIMEOUT;
            if (inputJson.has("timeout")) {
                timeout = inputJson.get("timeout").getAsInt();
            }

            log.info("执行 Android 批量操作: clientId={}, actions={}", clientId, actionsArray.size());

            // 按顺序执行操作
            List<ActionResult> allResults = new ArrayList<>();
            for (int i = 0; i < actionsArray.size(); i++) {
                JsonObject actionObj = actionsArray.get(i).getAsJsonObject();
                Map<String, Object> actionMap = gson.fromJson(actionObj, Map.class);

                ActionItem item = new ActionItem();
                item.setIndex(i);
                item.setAction((String) actionMap.get("action"));
                item.setParams(actionMap);

                ActionResult actionResult = executeAction(clientId, item, timeout);
                allResults.add(actionResult);

                // 如果操作失败，记录但继续执行后续操作
                if (!actionResult.isSuccess()) {
                    log.warn("操作 [{}] {} 失败: {}", i, item.getAction(), actionResult.getError());
                }
            }

            // 构建返回结果
            return buildResult(allResults);

        } catch (Exception e) {
            log.error("执行 android_action 操作失败", e);
            result.addProperty("error", "Failed to execute android_action: " + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }

    /**
     * 执行单个操作
     */
    private ActionResult executeAction(String clientId, ActionItem actionItem, int timeout) {
        ActionResult result = new ActionResult();
        result.setIndex(actionItem.getIndex());
        result.setAction(actionItem.getAction());

        try {
            // 延迟执行
            int delay = parseIntOrDefault(actionItem.getParams().get("delay"), 0);
            if (delay > 0) {
                log.debug("延迟 {}ms 后执行操作: {}", delay, actionItem.getAction());
                Thread.sleep(delay);
            }

            // 构建命令
            Map<String, Object> command = buildCommand(actionItem);
            String commandJson = gson.toJson(command);

            log.info("执行操作 [{}]: {} -> {}", actionItem.getIndex(), actionItem.getAction(), commandJson);

            // 发送命令
            WebSocketCaller caller = WebSocketCaller.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("message", commandJson);

            Map<String, Object> response = caller.call(clientId, "action", data, timeout, TimeUnit.SECONDS);

            result.setSuccess(true);
            result.setResponse(response);

            // 检查响应中的 success 字段
            if (response != null) {
                Object successObj = response.get("success");
                if (successObj != null && !Boolean.parseBoolean(successObj.toString())) {
                    result.setSuccess(false);
                    result.setError(response.containsKey("error") ?
                            response.get("error").toString() : "Operation failed");
                }
            }

            log.info("操作 [{}] {} 完成: success={}", actionItem.getIndex(), actionItem.getAction(), result.isSuccess());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.setSuccess(false);
            result.setError("Operation interrupted");
        } catch (TimeoutException e) {
            result.setSuccess(false);
            result.setError("Timeout after " + timeout + " seconds");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(e.getMessage());
            log.error("操作 [{}] {} 失败", actionItem.getIndex(), actionItem.getAction(), e);
        }

        return result;
    }

    /**
     * 构建操作命令
     */
    private Map<String, Object> buildCommand(ActionItem actionItem) {
        Map<String, Object> command = new HashMap<>();
        Map<String, Object> params = actionItem.getParams();
        String action = actionItem.getAction();

        command.put("action", action);

        Map<String, Object> actionParams = new HashMap<>();

        switch (action) {
            case "click":
                actionParams.put("x", parseIntOrDefault(params.get("x"), 0));
                actionParams.put("y", parseIntOrDefault(params.get("y"), 0));
                break;

            case "long_press":
                actionParams.put("x", parseIntOrDefault(params.get("x"), 0));
                actionParams.put("y", parseIntOrDefault(params.get("y"), 0));
                actionParams.put("duration", parseIntOrDefault(params.get("duration"), 1000));
                break;

            case "type":
                actionParams.put("content", params.getOrDefault("content", ""));
                break;

            case "scroll":
                actionParams.put("x", parseIntOrDefault(params.get("x"), 500));
                actionParams.put("y", parseIntOrDefault(params.get("y"), 800));
                actionParams.put("direction", params.getOrDefault("direction", "up"));
                actionParams.put("distance", parseIntOrDefault(params.get("distance"), 500));
                break;

            case "drag":
                actionParams.put("x", parseIntOrDefault(params.get("x"), 0));
                actionParams.put("y", parseIntOrDefault(params.get("y"), 0));
                actionParams.put("x2", parseIntOrDefault(params.get("x2"), 0));
                actionParams.put("y2", parseIntOrDefault(params.get("y2"), 0));
                actionParams.put("duration", parseIntOrDefault(params.get("duration"), 500));
                break;

            case "open_app":
                actionParams.put("packageName", params.getOrDefault("packageName", ""));
                break;

            case "press_home":
            case "press_back":
            case "press_recents":
            case "open_notifications":
            case "open_quick_settings":
            case "screenshot":
                // 这些操作不需要额外参数
                break;
        }

        if (!actionParams.isEmpty()) {
            command.put("params", actionParams);
        }

        return command;
    }

    /**
     * 构建返回结果
     */
    private JsonObject buildResult(List<ActionResult> results) {
        JsonObject result = new JsonObject();

        int successCount = 0;
        int failCount = 0;
        StringBuilder summary = new StringBuilder();

        JsonArray detailsArray = new JsonArray();
        for (ActionResult ar : results) {
            if (ar.isSuccess()) {
                successCount++;
                summary.append(String.format("✅ [%d] %s: success\n", ar.getIndex(), ar.getAction()));
            } else {
                failCount++;
                summary.append(String.format("❌ [%d] %s: %s\n", ar.getIndex(), ar.getAction(), ar.getError()));
            }

            JsonObject detail = new JsonObject();
            detail.addProperty("index", ar.getIndex());
            detail.addProperty("action", ar.getAction());
            detail.addProperty("success", ar.isSuccess());
            if (ar.getError() != null) {
                detail.addProperty("error", ar.getError());
            }
            detailsArray.add(detail);
        }

        result.addProperty("success", failCount == 0);
        result.addProperty("totalActions", results.size());
        result.addProperty("successCount", successCount);
        result.addProperty("failCount", failCount);
        result.addProperty("result", summary.toString());
        result.add("details", detailsArray);

        return result;
    }

    private int parseIntOrDefault(Object value, int defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Data
    private static class ActionItem {
        private int index;
        private String action;
        private Map<String, Object> params;
    }

    @Data
    private static class ActionResult {
        private int index;
        private String action;
        private boolean success;
        private String error;
        private Map<String, Object> response;
    }
}