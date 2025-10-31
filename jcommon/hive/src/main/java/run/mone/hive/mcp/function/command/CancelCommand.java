package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

/**
 * 取消/中断命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class CancelCommand extends BaseCommand {

    public CancelCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return message.trim().toLowerCase().startsWith("/cancel");
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            // 构建取消命令的消息
            Message cancelMessage = Message.builder()
                    .sentFrom(ownerId)
                    .role("user")
                    .content("/cancel")
                    .build();
            
            // 通过roleService发送取消命令，让RoleService处理中断逻辑
            // 直接发送取消消息到RoleService，它会自动处理中断逻辑
            Flux<String> resultFlux = roleService.receiveMsg(cancelMessage);
            
            // 订阅结果但不阻塞，让中断逻辑异步执行
            resultFlux.subscribe(
                    result -> log.debug("取消命令执行结果: {}", result),
                    error -> log.error("取消命令执行失败: {}", error.getMessage(), error),
                    () -> log.debug("取消命令处理完成")
            );
            
            return Flux.just(createSuccessResult("🛑 已发送取消指令"));
        } catch (Exception e) {
            log.error("发送取消指令失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("取消指令发送失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/cancel";
    }

    @Override
    public String getCommandDescription() {
        return "取消执行";
    }
}
