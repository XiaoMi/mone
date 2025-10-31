package run.mone.hive.mcp.service.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

/**
 * 刷新配置命令处理类
 * 处理 /refresh, /reload 等刷新配置命令
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class RefreshConfigCommand extends RoleBaseCommand {

    public RefreshConfigCommand(RoleService roleService) {
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
        return trimmed.equals("/refresh") ||
                trimmed.equals("/reload") ||
                containsAnyKeyword(trimmed, "刷新配置", "重新加载");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        if (role == null) {
            sendErrorAndComplete(sink, "没有找到要刷新配置的Agent: " + from);
            return;
        }

        try {
            sink.next("🔄 开始刷新Agent配置...\n");
            // 执行刷新配置
            roleService.refreshConfig(message,false);
            sendMessages(sink,
                "✅ Agent " + from + " 配置刷新完成！\n",
                "📋 已更新MCP连接和角色设置\n"
            );
        } catch (Exception e) {
            log.error("刷新配置失败: {}", e.getMessage(), e);
            sendErrorAndComplete(sink, "配置刷新失败: " + e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return "/refresh";
    }

    @Override
    public String getCommandDescription() {
        return "刷新Agent配置和MCP连接";
    }
}
