package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

/**
 * 获取配置命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ConfigCommand extends BaseCommand {

    public ConfigCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return "/config".equalsIgnoreCase(message.trim());
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            // 构建包含获取配置请求的消息，发送给RoleService处理
            Message configMessage = Message.builder()
                    .role("user")
                    .sentFrom(ownerId)
                    .content("/config")
                    .data("GET_CONFIG") // 特殊标识，让RoleService知道这是获取配置的请求
                    .build();

            log.info("发送获取配置请求到RoleService, ownerId: {}", ownerId);
            return sendToRoleService(configMessage);
        } catch (Exception e) {
            log.error("处理获取配置命令失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("获取配置失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/config";
    }

    @Override
    public String getCommandDescription() {
        return "获取配置信息";
    }
}
