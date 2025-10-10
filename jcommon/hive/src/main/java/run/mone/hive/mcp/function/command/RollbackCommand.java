package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

/**
 * 回滚命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class RollbackCommand extends BaseCommand {

    public RollbackCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return message.trim().toLowerCase().startsWith("/rollback");
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            String[] parts = message.split("\\s+");
            String messageId = null;
            if (parts.length > 1) {
                messageId = parts[1];
            }

            boolean success = roleService.rollbackHistory(Message.builder().sentFrom(ownerId).id(messageId).build());
            String resultText;
            if (success) {
                if (messageId != null) {
                    resultText = "上下文已回滚到消息 " + messageId + " 之前";
                } else {
                    resultText = "上下文已回滚上一轮对话";
                }
            } else {
                resultText = "回滚失败，没有找到指定消息或历史记录为空";
            }

            String finalMessage = String.format("<rollback-result success=\"%s\">%s</rollback-result>", success, resultText);
            return Flux.just(createSuccessResult(finalMessage));
        } catch (Exception e) {
            log.error("回滚操作失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("回滚操作失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/rollback";
    }

    @Override
    public String getCommandDescription() {
        return "回滚";
    }
}
