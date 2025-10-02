package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

/**
 * 获取agent列表命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ListCommand extends BaseCommand {

    public ListCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return "/list".equalsIgnoreCase(message.trim());
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            // 构建包含列表请求的消息，发送给RoleService处理
            Message listMessage = Message.builder()
                    .role("user")
                    .sentFrom(ownerId)
                    .content("/list")
                    .data("LIST_AGENTS") // 特殊标识，让RoleService知道这是获取agent列表的请求
                    .build();

            log.info("发送获取agent列表请求到RoleService");
            return sendToRoleService(listMessage);
        } catch (Exception e) {
            log.error("处理获取agent列表命令失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("获取agent列表失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/list";
    }

    @Override
    public String getCommandDescription() {
        return "获取agent列表";
    }
}
