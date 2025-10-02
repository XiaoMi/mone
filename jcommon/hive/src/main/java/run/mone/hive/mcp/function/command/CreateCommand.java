package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

/**
 * 创建role命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class CreateCommand extends BaseCommand {

    public CreateCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return "/create".equalsIgnoreCase(message.trim());
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            // 构建包含创建role请求的消息，发送给RoleService处理
            Message createMessage = Message.builder()
                    .clientId(clientId)
                    .userId(userId)
                    .agentId(agentId)
                    .role("user")
                    .sentFrom(ownerId)
                    .content("/create")
                    .data("CREATE_ROLE") // 特殊标识，让RoleService知道这是创建role的请求
                    .build();

            log.info("发送创建role请求到RoleService, ownerId: {}, clientId: {}", ownerId, clientId);
            return sendToRoleService(createMessage);
        } catch (Exception e) {
            log.error("处理创建role命令失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("创建role失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/create";
    }

    @Override
    public String getCommandDescription() {
        return "创建新role";
    }
}
