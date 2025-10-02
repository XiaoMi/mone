package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

/**
 * 退出命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ExitCommand extends BaseCommand {

    public ExitCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return "/exit".equalsIgnoreCase(message.trim());
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            roleService.offlineAgent(Message.builder().sentFrom(ownerId).build());
            return Flux.just(createSuccessResult("agent已退出"));
        } catch (Exception e) {
            log.error("退出agent失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("退出agent失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/exit";
    }

    @Override
    public String getCommandDescription() {
        return "退出";
    }
}
