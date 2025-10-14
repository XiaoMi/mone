package run.mone.hive.mcp.service.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.common.RoleType;
import run.mone.hive.context.ConversationContextManager;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

/**
 * 压缩命令处理类
 * 处理对话上下文压缩相关命令
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class CompressionCommand extends RoleBaseCommand {

    public CompressionCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(Message message) {
        if (message == null || message.getContent() == null) {
            return false;
        }
        String content = message.getContent().trim().toLowerCase();
        return isCompressionCommand(content);
    }

    @Override
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        return isCompressionCommand(content.trim().toLowerCase());
    }

    /**
     * 判断是否是压缩命令
     */
    private boolean isCompressionCommand(String content) {
        return content.startsWith("/compress") ||
                content.startsWith("/compact") ||
                content.startsWith("/summarize") ||
                content.startsWith("/smol") ||
                content.contains("压缩对话") ||
                content.contains("总结对话");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        if (role == null) {
            sendErrorAndComplete(sink, "未找到对应的Agent");
            return;
        }

        handleCompressionCommand(message, sink, role);
    }

    /**
     * 处理压缩命令
     */
    private void handleCompressionCommand(Message msg, FluxSink<String> sink, ReactorRole role) {
        if (sink != null) {
            sink.next("🔄 开始压缩对话上下文...\n");
        }

        // 显示当前上下文统计
        ConversationContextManager.ContextStats stats = role.getContextStats();
        if (stats != null && sink != null) {
            sink.next(String.format("📊 当前状态: %d条消息, %d个字符, 约%d个tokens\n",
                    stats.getMessageCount(), stats.getTotalCharacters(), stats.getEstimatedTokens()));
        }

        // 执行压缩
        role.manualCompressContext().thenAccept(success -> {
            if (success) {
                if (sink != null) {
                    ConversationContextManager.ContextStats newStats = role.getContextStats();
                    if (newStats != null) {
                        sink.next(String.format("✅ 压缩完成! 现在有 %d条消息, %d个字符, 约%d个tokens\n",
                                newStats.getMessageCount(), newStats.getTotalCharacters(), newStats.getEstimatedTokens()));
                    } else {
                        sink.next("✅ 对话上下文压缩完成!\n");
                    }
                    sink.next("💡 对话历史已智能总结，重要信息已保留。\n");
                    sink.complete();
                }

                // 添加压缩完成的消息到记忆
                role.putMessage(Message.builder()
                        .role(RoleType.assistant.name())
                        .content("对话上下文已成功压缩，历史信息已智能总结。")
                        .sink(sink)
                        .build());
            } else {
                if (sink != null) {
                    sink.next("❌ 压缩失败，请稍后重试。\n");
                    sink.complete();
                }

                role.putMessage(Message.builder()
                        .role(RoleType.assistant.name())
                        .content("对话压缩失败，当前对话将继续使用原有历史。")
                        .sink(sink)
                        .build());
            }
        }).exceptionally(throwable -> {
            log.error("处理压缩命令时发生异常", throwable);
            if (sink != null) {
                sink.next("❌ 压缩过程中发生异常: " + throwable.getMessage() + "\n");
                sink.complete();
            }
            return null;
        });
    }

    @Override
    public String getCommandName() {
        return "compression";
    }

    @Override
    public String getCommandDescription() {
        return "压缩对话上下文 (/compress, /compact, /summarize, /smol)";
    }
}

