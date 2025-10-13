package run.mone.hive.mcp.service.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

/**
 * 中断命令处理类
 * 处理 /exit, /stop, /interrupt, /cancel 等中断命令
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class InterruptCommand extends RoleBaseCommand {

    public InterruptCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(Message message) {
        return matches(message.getContent());
    }

    @Override
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        String trimmed = content.trim().toLowerCase();
        return trimmed.equals("/exit") ||
                trimmed.equals("/stop") ||
                trimmed.equals("/interrupt") ||
                trimmed.equals("/cancel") ||
                containsAnyKeyword(trimmed, "停止", "中断", "取消");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        if (role == null) {
            sendErrorAndComplete(sink, "没有找到要中断的Agent: " + from);
            return;
        }

        try {
            if (role.isInterrupted()) {
                // 如果已经是中断状态，提示用户
                sendMessages(sink,
                    "⚠️ Agent " + from + " 已经处于中断状态\n",
                    "💡 发送任何非中断命令将自动重置中断状态并继续执行\n"
                );
            } else {
                // 执行中断
                role.interrupt();
                log.info("Agent {} 收到中断命令，已被中断", from);
                sendMessages(sink,
                    "🛑 Agent " + from + " 已被强制中断\n",
                    "💡 发送任何新命令将自动重置中断状态并继续执行\n"
                );
            }
            sink.complete();
        } catch (Exception e) {
            log.error("中断Agent失败: {}", e.getMessage(), e);
            sendErrorAndComplete(sink, "中断Agent失败: " + e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return "/interrupt";
    }

    @Override
    public String getCommandDescription() {
        return "中断/停止Agent执行";
    }
}
