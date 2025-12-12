package run.mone.mcp.chat.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.spring.starter.NotifiableMcpFunction;

import java.util.List;
import java.util.Map;

/**
 * 计算两个数之和的 MCP Tool
 *
 * <p>使用 NotifiableMcpFunction 基类，自动提供：
 * <ul>
 * <li>工具开始执行通知</li>
 * <li>工具完成通知</li>
 * <li>工具错误通知</li>
 * <li>自定义进度通知</li>
 * </ul>
 *
 * @author goodjava@qq.com
 * @date 2025/1/21
 */
@Slf4j
@Component
public class AddTwoNumbersFunction extends NotifiableMcpFunction {

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

    /**
     * 实现业务逻辑处理
     *
     * <p>基类会自动处理：
     * <ul>
     * <li>发送开始通知（工具开始执行）</li>
     * <li>捕获异常并发送错误通知</li>
     * <li>发送完成通知（工具执行成功）</li>
     * </ul>
     */
    @Override
    protected Flux<McpSchema.CallToolResult> processArguments(Map<String, Object> arguments) {
        log.info("AddTwoNumbers arguments: {}", arguments);
        String clientId = extractClientId(arguments);

        // ===== 获取用户信息（来自 Bearer Token 验证）=====
        // 方式1: 获取完整的用户信息 Map
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>) arguments.get(Const.USER_INFO);
        if (userInfo != null && !userInfo.isEmpty()) {
            log.info("从 Bearer Token 获取到用户信息: {}", userInfo);
            String tokenUsername = (String) userInfo.get(Const.TOKEN_USERNAME);
            log.info("Token验证用户 - username: {}, clientId: {}", tokenUsername, clientId);
        }
        // 方式2: 直接获取 userId 和 username（便捷方式）
        String tokenUserId = (String) arguments.get(Const.TOKEN_USER_ID);
        String tokenUsername = (String) arguments.get(Const.TOKEN_USERNAME);
        if (tokenUserId != null) {
            log.info("便捷方式获取用户 - userId: {}, username: {}", tokenUserId, tokenUsername);
        }

        // ===== 获取参数 =====
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

        // ===== 发送自定义进度通知：开始计算 =====
        sendCustomNotification(
                "calculation-progress",
                McpSchema.LoggingLevel.INFO,
                String.format("🧮 开始计算：%.2f + %.2f", a, b),
                clientId
        );

        // 模拟计算耗时（让用户能看到通知效果）
        try {
            Thread.sleep(500); // 0.5秒延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 计算结果
        double result = a + b;

        // ===== 发送自定义进度通知：计算完成 =====
        sendCustomNotification(
                "calculation-progress",
                McpSchema.LoggingLevel.INFO,
                String.format("✅ 计算完成：%.2f + %.2f = %.2f", a, b, result),
                clientId
        );

        // 构造返回结果
        String resultText = String.format("计算结果：%.2f + %.2f = %.2f", a, b, result);
        log.info("AddTwoNumbers result: {}", resultText);

        return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(resultText)),
                false
        ));
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
