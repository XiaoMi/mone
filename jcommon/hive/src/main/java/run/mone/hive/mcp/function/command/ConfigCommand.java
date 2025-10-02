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
        String trimmed = message.trim().toLowerCase();
        return trimmed.equals("/config") || trimmed.startsWith("/config put");
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            String trimmed = message.trim().toLowerCase();
            
            // 检查是否是 put config 命令
            if (trimmed.startsWith("/config put")) {
                // 构建 put config 消息
                Message putConfigMessage = Message.builder()
                        .role("user")
                        .sentFrom(ownerId)
                        .content(message) // 传递原始消息，包含 key=value
                        .data("PUT_CONFIG") // 特殊标识，让RoleService知道这是设置配置的请求
                        .build();
                
                log.info("发送设置配置请求到RoleService, ownerId: {}, message: {}", ownerId, message);
                return sendToRoleService(putConfigMessage);
            } else {
                // 构建包含获取配置请求的消息，发送给RoleService处理
                Message configMessage = Message.builder()
                        .role("user")
                        .sentFrom(ownerId)
                        .content("/config")
                        .data("GET_CONFIG") // 特殊标识，让RoleService知道这是获取配置的请求
                        .build();

                log.info("发送获取配置请求到RoleService, ownerId: {}", ownerId);
                return sendToRoleService(configMessage);
            }
        } catch (Exception e) {
            log.error("处理配置命令失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("处理配置命令失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/config";
    }

    @Override
    public String getCommandDescription() {
        return "获取或设置配置信息 (用法: /config 或 /config put key=value)";
    }
}
