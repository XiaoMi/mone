package run.mone.hive.mcp.function.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

import java.util.List;

/**
 * 命令处理基础类
 * 所有具体的命令处理类都应该继承这个基础类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@RequiredArgsConstructor
@Slf4j
public abstract class BaseCommand {

    protected final RoleService roleService;

    /**
     * 检查命令是否匹配
     * @param message 用户输入的消息
     * @return 是否匹配此命令
     */
    public abstract boolean matches(String message);

    /**
     * 执行命令
     * @param clientId 客户端ID
     * @param userId 用户ID
     * @param agentId 代理ID
     * @param ownerId 所有者ID
     * @param message 原始消息
     * @param timeout 超时时间
     * @return 命令执行结果
     */
    public abstract Flux<McpSchema.CallToolResult> execute(
            String clientId, 
            String userId, 
            String agentId, 
            String ownerId, 
            String message, 
            long timeout
    );

    /**
     * 获取命令名称
     * @return 命令名称
     */
    public abstract String getCommandName();

    /**
     * 获取命令描述
     * @return 命令描述
     */
    public abstract String getCommandDescription();

    /**
     * 创建标准的消息ID响应
     * @param messageId 消息ID
     * @return CallToolResult
     */
    protected McpSchema.CallToolResult createMessageIdResult(String messageId) {
        String idTag = "<hive-msg-id>" + messageId + "</hive-msg-id>";
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(idTag)), false);
    }

    /**
     * 创建错误响应
     * @param errorMessage 错误消息
     * @return CallToolResult
     */
    protected McpSchema.CallToolResult createErrorResult(String errorMessage) {
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("❌ " + errorMessage)), false);
    }

    /**
     * 创建成功响应
     * @param message 成功消息
     * @return CallToolResult
     */
    protected McpSchema.CallToolResult createSuccessResult(String message) {
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(message)), false);
    }

    /**
     * 发送消息到RoleService并返回响应流
     * @param message 要发送的消息
     * @return 响应流
     */
    protected Flux<McpSchema.CallToolResult> sendToRoleService(Message message) {
        // 1. 创建消息ID响应
        Flux<McpSchema.CallToolResult> idFlux = Flux.just(createMessageIdResult(message.getId()));

        // 2. 创建RoleService响应流
        Flux<McpSchema.CallToolResult> responseFlux = roleService.receiveMsg(message)
                .onErrorResume((e) -> Flux.just("ERROR:" + e.getMessage()))
                .map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));

        // 3. 串联两个流
        return Flux.concat(idFlux, responseFlux);
    }
}
