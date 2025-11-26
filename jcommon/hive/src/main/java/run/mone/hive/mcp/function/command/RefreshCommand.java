package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

/**
 * 刷新配置命令处理类
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class RefreshCommand extends BaseCommand {

    public RefreshCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        String trimmed = message.trim().toLowerCase();
        return trimmed.startsWith("/refresh") || trimmed.startsWith("/reload");
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            // 构建刷新配置的消息，使用特殊的data标识
            Message refreshMessage = Message.builder()
                    .sentFrom(ownerId)
                    .role("system")
                    .content("刷新配置")
                    .data(Const.REFRESH_CONFIG)
                    .build();

            // 通过roleService刷新配置
            roleService.refreshConfig(refreshMessage, false);
            return Flux.just(createSuccessResult("🔄 配置已刷新，包括MCP连接和角色设置"));
        } catch (Exception e) {
            log.error("刷新配置失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("配置刷新失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/refresh";
    }

    @Override
    public String getCommandDescription() {
        return "刷新配置";
    }
}
