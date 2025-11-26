package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.common.Safe;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.MemoryTool;
import run.mone.hive.schema.Message;

import java.util.List;

/**
 * 清空历史命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ClearCommand extends BaseCommand {

    public ClearCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return "/clear".equalsIgnoreCase(message.trim());
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            Safe.run(() -> {
                if (null != MemoryTool.memoryManager) {
                    MemoryTool.memoryManager.getLongTermMemory().getHistoryManager().reset();
                }
            });
            
            roleService.clearHistory(Message.builder().sentFrom(ownerId).build());
            
            return Flux.just(createSuccessResult("聊天历史已清空"));
        } catch (Exception e) {
            log.error("清空历史失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("清空历史失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/clear";
    }

    @Override
    public String getCommandDescription() {
        return "清空历史";
    }
}
