package run.mone.hive.mcp.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.JsonUtils;

/**
 * Role命令处理基础类
 * 专门用于处理RoleService中的命令逻辑
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@RequiredArgsConstructor
@Slf4j
public abstract class RoleBaseCommand {

    protected final RoleService roleService;

    /**
     * 检查命令是否匹配
     * @param message 用户输入的消息
     * @return 是否匹配此命令
     */
    public abstract boolean matches(Message message);

    /**
     * 检查命令是否匹配（基于字符串内容）
     * @param content 消息内容
     * @return 是否匹配此命令
     */
    public abstract boolean matches(String content);

    /**
     * 执行命令
     * @param message 原始消息
     * @param sink 响应流的sink
     * @param from 发送者标识
     * @param role 当前的ReactorRole实例（可能为null）
     */
    public abstract void execute(Message message, FluxSink<String> sink, String from, ReactorRole role);

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
     * 创建成功响应并完成流
     * @param sink 响应流的sink
     * @param message 成功消息
     */
    protected void sendSuccessAndComplete(FluxSink<String> sink, String message) {
        sink.next(JsonUtils.toolResult(message));
        sink.complete();
    }

    /**
     * 创建错误响应并完成流
     * @param sink 响应流的sink
     * @param errorMessage 错误消息
     */
    protected void sendErrorAndComplete(FluxSink<String> sink, String errorMessage) {
        sink.next("❌ " + errorMessage + "\n");
        sink.complete();
    }

    /**
     * 发送多行消息
     * @param sink 响应流的sink
     * @param messages 消息列表
     */
    protected void sendMessages(FluxSink<String> sink, String... messages) {
        for (String message : messages) {
            sink.next(message);
        }
    }

    /**
     * 检查字符串是否匹配指定的命令列表
     * @param content 要检查的内容
     * @param commands 命令列表
     * @return 是否匹配
     */
    protected boolean matchesAnyCommand(String content, String... commands) {
        if (content == null) {
            return false;
        }
        String trimmed = content.trim().toLowerCase();
        for (String command : commands) {
            if (trimmed.equals(command.toLowerCase()) || trimmed.startsWith(command.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查字符串是否包含指定的关键词
     * @param content 要检查的内容
     * @param keywords 关键词列表
     * @return 是否包含
     */
    protected boolean containsAnyKeyword(String content, String... keywords) {
        if (content == null) {
            return false;
        }
        String trimmed = content.trim().toLowerCase();
        for (String keyword : keywords) {
            if (trimmed.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
