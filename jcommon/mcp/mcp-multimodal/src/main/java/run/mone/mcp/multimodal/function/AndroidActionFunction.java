package run.mone.mcp.multimodal.function;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.spring.starter.WebSocketCaller;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Android 批量操作工具
 * 通过 WebSocket 向 Android 设备发送操作命令，支持批量操作和并行执行
 * <p>
 * 支持的操作类型：
 * - click: 点击指定坐标
 * - long_press: 长按指定坐标
 * - type: 输入文本
 * - scroll: 滚动屏幕
 * - drag: 拖拽操作
 * - press_home: 按Home键
 * - press_back: 按返回键
 * - press_recents: 按最近任务键
 * - open_notifications: 打开通知栏
 * - open_quick_settings: 打开快捷设置
 * - open_app: 打开应用
 * - screenshot: 截图
 *
 * @author goodjava@qq.com
 * @date 2025/12/16
 */
@Slf4j
@Component
public class AndroidActionFunction implements McpFunction {

    private static final Gson gson = new Gson();
    private static final int DEFAULT_TIMEOUT = 30;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "clientId": {
                        "type": "string",
                        "description": "Android 客户端的 WebSocket 连接 ID"
                    },
                    "actions": {
                        "type": "array",
                        "description": "要执行的操作列表，按顺序执行。同一组(group)内的操作会并行执行",
                        "items": {
                            "type": "object",
                            "properties": {
                                "action": {
                                    "type": "string",
                                    "enum": ["click", "long_press", "type", "scroll", "drag", "press_home", "press_back", "press_recents", "open_notifications", "open_quick_settings", "open_app", "screenshot"],
                                    "description": "操作类型"
                                },
                                "group": {
                                    "type": "integer",
                                    "description": "操作分组，同一组的操作会并行执行，不同组按顺序执行。默认每个操作独立一组"
                                },
                                "x": {
                                    "type": "integer",
                                    "description": "X坐标 (用于 click, long_press, scroll)"
                                },
                                "y": {
                                    "type": "integer",
                                    "description": "Y坐标 (用于 click, long_press, scroll)"
                                },
                                "x2": {
                                    "type": "integer",
                                    "description": "目标X坐标 (用于 drag)"
                                },
                                "y2": {
                                    "type": "integer",
                                    "description": "目标Y坐标 (用于 drag)"
                                },
                                "content": {
                                    "type": "string",
                                    "description": "输入内容 (用于 type)"
                                },
                                "direction": {
                                    "type": "string",
                                    "enum": ["up", "down", "left", "right"],
                                    "description": "滚动方向 (用于 scroll)"
                                },
                                "distance": {
                                    "type": "integer",
                                    "description": "滚动距离 (用于 scroll)，默认 500"
                                },
                                "duration": {
                                    "type": "integer",
                                    "description": "持续时间毫秒 (用于 long_press, drag)，默认 1000"
                                },
                                "packageName": {
                                    "type": "string",
                                    "description": "应用包名 (用于 open_app)"
                                },
                                "delay": {
                                    "type": "integer",
                                    "description": "执行前延迟毫秒，用于等待界面响应"
                                }
                            },
                            "required": ["action"]
                        }
                    },
                    "timeout": {
                        "type": "integer",
                        "description": "单个操作的超时时间（秒），默认 30"
                    }
                },
                "required": ["clientId", "actions"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String clientId = (String) arguments.get("clientId");
        int timeout = parseIntArgOrDefault(arguments, "timeout", DEFAULT_TIMEOUT);

        if (clientId == null || clientId.isEmpty()) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误: clientId 不能为空")), true));
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> actions = (List<Map<String, Object>>) arguments.get("actions");
        if (actions == null || actions.isEmpty()) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误: actions 不能为空")), true));
        }

        try {
            log.info("执行 Android 批量操作: clientId={}, actions={}", clientId, actions.size());

            // 按 group 分组，同一组并行执行，不同组顺序执行
            Map<Integer, List<ActionItem>> groupedActions = groupActions(actions);
            List<ActionResult> allResults = new ArrayList<>();

            // 按组顺序执行
            List<Integer> sortedGroups = new ArrayList<>(groupedActions.keySet());
            Collections.sort(sortedGroups);

            for (Integer group : sortedGroups) {
                List<ActionItem> groupActions = groupedActions.get(group);
                log.info("执行第 {} 组操作，共 {} 个", group, groupActions.size());

                // 并行执行同一组的操作
                List<ActionResult> groupResults = executeActionsParallel(clientId, groupActions, timeout);
                allResults.addAll(groupResults);

                // 检查是否有失败
                boolean hasError = groupResults.stream().anyMatch(r -> !r.isSuccess());
                if (hasError) {
                    log.warn("第 {} 组有操作失败，继续执行后续操作", group);
                }
            }

            // 构建结果
            return buildResult(allResults);

        } catch (Exception e) {
            log.error("执行 Android 批量操作失败: clientId={}", clientId, e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误: " + e.getMessage())), true));
        }
    }

    /**
     * 按 group 分组操作
     */
    private Map<Integer, List<ActionItem>> groupActions(List<Map<String, Object>> actions) {
        Map<Integer, List<ActionItem>> grouped = new TreeMap<>();
        int defaultGroup = 0;

        for (int i = 0; i < actions.size(); i++) {
            Map<String, Object> action = actions.get(i);
            int group = parseIntArgOrDefault(action, "group", defaultGroup++);

            ActionItem item = new ActionItem();
            item.setIndex(i);
            item.setAction((String) action.get("action"));
            item.setParams(action);

            grouped.computeIfAbsent(group, k -> new ArrayList<>()).add(item);
        }

        return grouped;
    }

    /**
     * 并行执行一组操作
     */
    private List<ActionResult> executeActionsParallel(String clientId, List<ActionItem> actions, int timeout) {
        if (actions.size() == 1) {
            // 单个操作直接执行
            ActionResult result = executeAction(clientId, actions.get(0), timeout);
            return List.of(result);
        }

        // 多个操作并行执行
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(actions.size(), 5));
        try {
            List<CompletableFuture<ActionResult>> futures = actions.stream()
                    .map(action -> CompletableFuture.supplyAsync(
                            () -> executeAction(clientId, action, timeout), executor))
                    .collect(Collectors.toList());

            // 等待所有操作完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(timeout * actions.size(), TimeUnit.SECONDS);

            return futures.stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            ActionResult errorResult = new ActionResult();
                            errorResult.setSuccess(false);
                            errorResult.setError(e.getMessage());
                            return errorResult;
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("并行执行操作失败", e);
            return actions.stream()
                    .map(a -> {
                        ActionResult r = new ActionResult();
                        r.setIndex(a.getIndex());
                        r.setAction(a.getAction());
                        r.setSuccess(false);
                        r.setError("Parallel execution failed: " + e.getMessage());
                        return r;
                    })
                    .collect(Collectors.toList());
        } finally {
            executor.shutdown();
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
            // 检查是否需要延迟
            int delay = parseIntArgOrDefault(actionItem.getParams(), "delay", 0);
            if (delay > 0) {
                log.debug("延迟 {}ms 后执行操作: {}", delay, actionItem.getAction());
                Thread.sleep(delay);
            }

            // 构建操作命令
            Map<String, Object> command = buildCommand(actionItem);
            String commandJson = gson.toJson(command);

            log.info("执行操作 [{}]: {} -> {}", actionItem.getIndex(), actionItem.getAction(), commandJson);

            // 发送命令到 Android 客户端
            WebSocketCaller caller = WebSocketCaller.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("message", commandJson);

            Map<String, Object> response = caller.call(clientId, "action", data, timeout, TimeUnit.SECONDS);

            // 解析响应
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
            result.setError("Operation timed out after " + timeout + " seconds");
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
                actionParams.put("x", parseIntArgOrDefault(params, "x", 0));
                actionParams.put("y", parseIntArgOrDefault(params, "y", 0));
                break;

            case "long_press":
                actionParams.put("x", parseIntArgOrDefault(params, "x", 0));
                actionParams.put("y", parseIntArgOrDefault(params, "y", 0));
                actionParams.put("duration", parseIntArgOrDefault(params, "duration", 1000));
                break;

            case "type":
                actionParams.put("content", params.getOrDefault("content", ""));
                break;

            case "scroll":
                actionParams.put("x", parseIntArgOrDefault(params, "x", 500));
                actionParams.put("y", parseIntArgOrDefault(params, "y", 800));
                actionParams.put("direction", params.getOrDefault("direction", "up"));
                actionParams.put("distance", parseIntArgOrDefault(params, "distance", 500));
                break;

            case "drag":
                actionParams.put("x", parseIntArgOrDefault(params, "x", 0));
                actionParams.put("y", parseIntArgOrDefault(params, "y", 0));
                actionParams.put("x2", parseIntArgOrDefault(params, "x2", 0));
                actionParams.put("y2", parseIntArgOrDefault(params, "y2", 0));
                actionParams.put("duration", parseIntArgOrDefault(params, "duration", 500));
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

            default:
                log.warn("未知操作类型: {}", action);
        }

        if (!actionParams.isEmpty()) {
            command.put("params", actionParams);
        }

        return command;
    }

    /**
     * 构建返回结果
     */
    private Flux<McpSchema.CallToolResult> buildResult(List<ActionResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("Android 批量操作执行完成\n\n");

        int successCount = 0;
        int failCount = 0;

        for (ActionResult result : results) {
            if (result.isSuccess()) {
                successCount++;
                sb.append(String.format("✅ [%d] %s: 成功\n", result.getIndex(), result.getAction()));
            } else {
                failCount++;
                sb.append(String.format("❌ [%d] %s: 失败 - %s\n",
                        result.getIndex(), result.getAction(), result.getError()));
            }
        }

        sb.append(String.format("\n总计: %d 个操作, %d 成功, %d 失败",
                results.size(), successCount, failCount));

        boolean hasError = failCount > 0;

        // 构建详细结果 JSON
        JsonArray resultArray = new JsonArray();
        for (ActionResult result : results) {
            JsonObject obj = new JsonObject();
            obj.addProperty("index", result.getIndex());
            obj.addProperty("action", result.getAction());
            obj.addProperty("success", result.isSuccess());
            if (result.getError() != null) {
                obj.addProperty("error", result.getError());
            }
            if (result.getResponse() != null) {
                obj.add("response", gson.toJsonTree(result.getResponse()));
            }
            resultArray.add(obj);
        }

        return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(sb.toString()),
                        new McpSchema.TextContent("详细结果: " + resultArray.toString())),
                hasError));
    }

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
        return "android_action";
    }

    @Override
    public String getDesc() {
        return "Android 批量操作工具，通过 WebSocket 向 Android 设备发送操作命令。" +
                "支持的操作：click(点击)、long_press(长按)、type(输入文本)、scroll(滚动)、" +
                "drag(拖拽)、press_home/back/recents(按键)、open_notifications/quick_settings(打开面板)、" +
                "open_app(打开应用)、screenshot(截图)。" +
                "同一 group 的操作会并行执行，不同 group 按顺序执行。";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    /**
     * 操作项
     */
    @Data
    private static class ActionItem {
        private int index;
        private String action;
        private Map<String, Object> params;
    }

    /**
     * 操作结果
     */
    @Data
    private static class ActionResult {
        private int index;
        private String action;
        private boolean success;
        private String error;
        private Map<String, Object> response;
    }
}
