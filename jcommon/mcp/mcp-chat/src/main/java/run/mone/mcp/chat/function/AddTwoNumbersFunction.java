package run.mone.mcp.chat.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chat.service.NotificationService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计算两个数之和的 MCP Tool
 *
 * @author goodjava@qq.com
 * @date 2025/1/21
 */
@Slf4j
@Component
public class AddTwoNumbersFunction implements McpFunction {

    @Resource
    private NotificationService notificationService;

    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "a": {
                        "type": "number",
                        "description": "第一个数字"
                    },
                    "b": {
                        "type": "number",
                        "description": "第二个数字"
                    }
                },
                "required": ["a", "b"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("AddTwoNumbers arguments: {}", arguments);

        // ===== 获取用户信息（来自 Bearer Token 验证）=====
        // 方式1: 获取完整的用户信息 Map
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>) arguments.get(Const.USER_INFO);
        if (userInfo != null && !userInfo.isEmpty()) {
            log.info("从 Bearer Token 获取到用户信息: {}", userInfo);
            String tokenUserId = (String) userInfo.get("userId");
            String tokenUsername = (String) userInfo.get("username");
            String tokenAvatar = (String) userInfo.get("avatar");
            log.info("Token验证用户 - userId: {}, username: {}, avatar: {}", tokenUserId, tokenUsername, tokenAvatar);
        }
        // 方式2: 直接获取 userId 和 username（便捷方式）
        String tokenUserId = (String) arguments.get(Const.TOKEN_USER_ID);
        String tokenUsername = (String) arguments.get(Const.TOKEN_USERNAME);
        if (tokenUserId != null) {
            log.info("便捷方式获取用户 - userId: {}, username: {}", tokenUserId, tokenUsername);
        }

        // ===== 原有业务逻辑 =====
        try {
            // 获取参数
            Object aObj = arguments.get("a");
            Object bObj = arguments.get("b");

            if (aObj == null || bObj == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：参数 a 和 b 不能为空")),
                        true
                ));
            }

            // 转换为数字
            double a = convertToDouble(aObj);
            double b = convertToDouble(bObj);

            // ===== 广播 notification 告知所有客户端开始计算 =====
            if (notificationService != null) {
                try {
                    Map<String, Object> notificationParams = new HashMap<>();
                    notificationParams.put("message", String.format("🧮 开始计算：%.2f + %.2f", a, b));
                    notificationParams.put("status", "calculating");
                    notificationParams.put("operation", "add");
                    notificationParams.put("operand_a", a);
                    notificationParams.put("operand_b", b);
                    notificationParams.put("timestamp", System.currentTimeMillis());

                    notificationService.broadcastNotification(
                        "notifications/progress",
                        notificationParams
                    );
                    log.info("✅ 已广播计算开始通知");
                } catch (Exception e) {
                    // Notification 发送失败不影响主流程
                    log.warn("⚠️ 发送计算开始通知失败: {}", e.getMessage());
                }
            } else {
                log.warn("⚠️ NotificationService 未注入，无法发送通知");
            }

            // 模拟计算耗时（可选，让用户能看到通知效果）
            try {
                Thread.sleep(500); // 0.5秒延迟
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 计算结果
            double result = a + b;

            // ===== 广播计算完成的 notification =====
            if (notificationService != null) {
                try {
                    Map<String, Object> completeParams = new HashMap<>();
                    completeParams.put("message", String.format("✅ 计算完成：%.2f + %.2f = %.2f", a, b, result));
                    completeParams.put("status", "completed");
                    completeParams.put("result", result);
                    completeParams.put("timestamp", System.currentTimeMillis());

                    notificationService.broadcastNotification(
                        "notifications/progress",
                        completeParams
                    );
                    log.info("✅ 已广播计算完成通知");
                } catch (Exception e) {
                    log.warn("⚠️ 发送计算完成通知失败: {}", e.getMessage());
                }
            }

            // 构造返回结果
            String resultText = String.format("计算结果：%.2f + %.2f = %.2f", a, b, result);
            log.info("AddTwoNumbers result: {}", resultText);

            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(resultText)),
                    false
            ));

        } catch (Exception e) {
            log.error("计算两数之和时发生错误", e);

            // 广播错误通知
            if (notificationService != null) {
                try {
                    Map<String, Object> errorParams = new HashMap<>();
                    errorParams.put("message", "❌ 计算出错：" + e.getMessage());
                    errorParams.put("status", "error");
                    errorParams.put("error", e.getMessage());
                    errorParams.put("timestamp", System.currentTimeMillis());

                    notificationService.broadcastNotification(
                        "tools/calculation/error",
                        errorParams
                    );
                    log.info("✅ 已广播错误通知");
                } catch (Exception notifyError) {
                    log.warn("⚠️ 发送错误通知失败: {}", notifyError.getMessage());
                }
            }

            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：" + e.getMessage())),
                    true
            ));
        }
    }

    /**
     * 将对象转换为 double 类型
     */
    private double convertToDouble(Object obj) {
        if (obj instanceof Number n) {
            return n.doubleValue();
        } else if (obj instanceof String) {
            return Double.parseDouble((String) obj);
        } else {
            throw new IllegalArgumentException("无法将 " + obj + " 转换为数字");
        }
    }

    @Override
    public String getName() {
        return "add_two_numbers";
    }

    @Override
    public String getDesc() {
        return "计算两个数的和。例如：计算 3 + 5，计算 1.5 + 2.3 等";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
