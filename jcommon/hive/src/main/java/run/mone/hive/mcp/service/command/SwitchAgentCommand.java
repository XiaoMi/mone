package run.mone.hive.mcp.service.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.bo.AgentMarkdownDocument;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

/**
 * 切换Agent命令处理类
 * 处理 /switch 命令和包含AgentMarkdownDocument数据的消息
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class SwitchAgentCommand extends RoleBaseCommand {

    public SwitchAgentCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(Message message) {
        if (message == null) {
            return false;
        }

        String content = message.getContent();
        Object data = message.getData();

        // 匹配 /switch 命令或包含AgentMarkdownDocument数据的消息
        return (content != null && content.trim().toLowerCase().startsWith("/switch")) ||
                (data != null && data instanceof AgentMarkdownDocument);
    }

    @Override
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        return content.trim().toLowerCase().startsWith("/switch");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        try {
            Object data = message.getData();

            // 处理包含AgentMarkdownDocument数据的消息
            if (data instanceof AgentMarkdownDocument md) {
                handleAgentMarkdownDocument(message, sink, from, role, md);
                return;
            }

            // 处理 /switch 命令
            String content = message.getContent();
            if (content != null && content.trim().toLowerCase().startsWith("/switch")) {
                handleSwitchCommand(message, sink, from, role, content);
                return;
            }

            sendErrorAndComplete(sink, "无效的切换命令格式");

        } catch (Exception e) {
            log.error("处理切换agent命令失败: {}", e.getMessage(), e);
            sendErrorAndComplete(sink, "切换agent失败: " + e.getMessage());
        }
    }

    /**
     * 处理包含AgentMarkdownDocument数据的消息
     */
    private void handleAgentMarkdownDocument(Message message, FluxSink<String> sink, String from, ReactorRole role, AgentMarkdownDocument md) {
        try {
            AgentMarkdownDocument tmp = roleService.getMarkdownDocument(md, role);
            if (tmp != null) {
                message.setData(tmp);
                // 放入到配置中
                role.getRoleConfig().put(Const.AGENT_CONFIG, GsonUtils.gson.toJson(tmp));

                // 只是切换agent,不需要下发指令
                if (message.getContent().equals(Const.SWITCH_AGENT)) {
                    sendMessages(sink,
                            "🔄 正在切换Agent配置...\n",
                            String.format("📋 已加载配置文件: %s\n", md.getFileName()),
                            "✅ Agent切换完毕\n"
                    );
                    sink.complete();
                    return;
                }

                // 如果不是纯切换命令，继续处理消息
                sendMessages(sink,
                        "🔄 已加载Agent配置文件: " + md.getFileName() + "\n",
                        "💡 配置已更新，继续处理您的消息...\n"
                );

                // 将消息传递给role处理
                role.putMessage(message);

            } else {
                sendErrorAndComplete(sink, "无法加载Agent配置文件: " + md.getFileName());
            }

        } catch (Exception e) {
            log.error("处理AgentMarkdownDocument失败: {}", e.getMessage(), e);
            sendErrorAndComplete(sink, "处理Agent配置失败: " + e.getMessage());
        }
    }

    /**
     * 处理 /switch 命令
     */
    private void handleSwitchCommand(Message message, FluxSink<String> sink, String from, ReactorRole role, String content) {
        try {
            // 解析命令：/switch <filename>
            String commandPart = content.substring("/switch".length()).trim();
            if (commandPart.isEmpty()) {
                sendErrorAndComplete(sink, "请指定要切换的配置文件名，格式: /switch <filename>");
                return;
            }

            String fileName = commandPart.trim();
            if (fileName.isEmpty()) {
                sendErrorAndComplete(sink, "请指定要切换的配置文件名");
                return;
            }

            // 创建AgentMarkdownDocument对象
            AgentMarkdownDocument document = new AgentMarkdownDocument();
            document.setFileName(fileName);

            // 尝试加载配置文件
            AgentMarkdownDocument tmp = roleService.getMarkdownDocument(document, role);
            if (tmp != null) {
                // 更新消息数据和内容
                message.setData(tmp);
                message.setContent(Const.SWITCH_AGENT);

                // 放入到配置中
                role.getRoleConfig().put(Const.AGENT_CONFIG, GsonUtils.gson.toJson(tmp));

                sendMessages(sink,
                        "🔄 正在切换Agent配置...\n",
                        String.format("📋 已加载配置文件: %s\n", fileName),
                        "✅ Agent切换完毕\n"
                );

                log.info("成功切换Agent配置, from: {}, fileName: {}", from, fileName);
            } else {
                sendErrorAndComplete(sink, "无法找到或加载配置文件: " + fileName);
            }
        } catch (Exception e) {
            log.error("处理switch命令失败: {}", e.getMessage(), e);
            sendErrorAndComplete(sink, "切换Agent配置失败: " + e.getMessage());
        } finally {
            sink.complete();
        }
    }

    @Override
    public String getCommandName() {
        return "/switch";
    }

    @Override
    public String getCommandDescription() {
        return "切换Agent配置文件，格式: /switch <filename>";
    }
}
