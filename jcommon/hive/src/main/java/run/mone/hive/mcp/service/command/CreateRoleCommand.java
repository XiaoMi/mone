package run.mone.hive.mcp.service.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

/**
 * 创建Role命令处理类
 * 处理 /create 命令和 CREATE_ROLE 数据类型
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class CreateRoleCommand extends RoleBaseCommand {

    public CreateRoleCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(Message message) {
        if (message == null) {
            return false;
        }
        String content = message.getContent();
        Object data = message.getData();
        
        return (content != null && content.trim().toLowerCase().equals("/create")) ||
               (data != null && "CREATE_ROLE".equals(data.toString()));
    }

    @Override
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        return content.trim().toLowerCase().equals("/create");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        try {
            sink.next("🔄 正在创建新的Role实例...\n");

            // 创建新的role
            ReactorRole newRole = roleService.createRole(message);
            
            if (newRole != null) {
                // 将新创建的role添加到roleMap中
                roleService.getRoleMap().put(from, newRole);
                
                sendMessages(sink,
                    "✅ Role创建成功！\n",
                    String.format("📋 Role信息:\n"),
                    String.format("  - Owner: %s\n", from),
                    String.format("  - ClientId: %s\n", message.getClientId()),
                    String.format("  - UserId: %s\n", message.getUserId()),
                    String.format("  - AgentId: %s\n", message.getAgentId()),
                    String.format("  - AgentName: %s\n", roleService.getAgentName()),
                    "💡 Role已准备就绪，可以开始对话了！\n"
                );
                
                log.info("成功创建新的Role实例, from: {}, clientId: {}", from, message.getClientId());
            } else {
                sendMessages(sink, "❌ Role创建失败，请检查系统配置\n");
                log.error("创建Role失败, from: {}", from);
            }
            
            sink.complete();

        } catch (Exception e) {
            log.error("处理创建role命令失败: {}", e.getMessage(), e);
            sendErrorAndComplete(sink, "创建Role失败: " + e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return "/create";
    }

    @Override
    public String getCommandDescription() {
        return "创建新的Role实例";
    }
}
