package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;

/**
 * 帮助命令处理类
 * 显示所有可用的命令列表和使用说明
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class HelpCommand extends BaseCommand {

    private final CommandManager commandManager;

    public HelpCommand(RoleService roleService, CommandManager commandManager) {
        super(roleService);
        this.commandManager = commandManager;
    }

    @Override
    public boolean matches(String message) {
        String trimmed = message.trim().toLowerCase();
        return trimmed.equals("/help") || trimmed.equals("/h") || trimmed.equals("/?");
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            StringBuilder helpText = new StringBuilder();
            helpText.append("📚 **可用命令列表**\n\n");
            
            // 获取所有命令并生成帮助信息
            List<BaseCommand> commands = commandManager.getAllCommands();
            
            helpText.append(String.format("以下是所有可用的特殊命令（共 %d 个）：\n\n", commands.size()));
            
            // 动态遍历所有命令
            for (int i = 0; i < commands.size(); i++) {
                BaseCommand command = commands.get(i);
                helpText.append(String.format("%d. **%s** - %s\n", 
                    i + 1, 
                    command.getCommandName(), 
                    command.getCommandDescription()));
            }
            
            helpText.append("\n### 📖 **常用命令示例**\n");
            helpText.append("```\n");
            helpText.append("/help                           # 显示此帮助信息\n");
            helpText.append("/clear                          # 清空聊天历史\n");
            helpText.append("/list                           # 列出可用的agent配置\n");
            helpText.append("/config                         # 显示当前配置信息\n");
            helpText.append("/rollback <messageId>           # 回滚到指定消息\n");
            helpText.append("/kill list                      # 列出所有进程\n");
            helpText.append("/kill <processId>               # 杀死指定进程\n");
            helpText.append("/detach all                     # 分离所有进程\n");
            helpText.append("/agent/backend-agent.md         # 加载后端配置\n");
            helpText.append("/agent/frontend-agent.md 你好   # 加载配置并发送消息\n");
            helpText.append("/create                         # 创建新的role实例\n");
            helpText.append("/refresh                        # 刷新配置\n");
            helpText.append("/cancel                         # 取消当前执行\n");
            helpText.append("/exit                           # 退出agent\n");
            helpText.append("```\n");
            
            helpText.append("\n### 💡 **使用提示**\n");
            helpText.append("- 命令不区分大小写\n");
            helpText.append("- 可以使用 `/h` 或 `/?` 作为 `/help` 的简写\n");
            helpText.append("- 部分命令支持参数，具体用法请参考上述示例\n");
            helpText.append("- 如需了解特定命令的详细用法，请直接尝试使用该命令\n");
            helpText.append("- 如需更多帮助，请直接询问相关问题\n");

            return Flux.just(createSuccessResult(helpText.toString()));
        } catch (Exception e) {
            log.error("生成帮助信息失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("生成帮助信息失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/help";
    }

    @Override
    public String getCommandDescription() {
        return "显示帮助信息";
    }
}
