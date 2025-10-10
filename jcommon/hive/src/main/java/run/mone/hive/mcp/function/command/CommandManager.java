package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 命令管理器
 * 负责管理所有的命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class CommandManager {

    private final List<BaseCommand> commands = new ArrayList<>();

    public CommandManager(RoleService roleService) {
        // 注册所有命令
        registerCommand(new ClearCommand(roleService));
        registerCommand(new ExitCommand(roleService));
        registerCommand(new RollbackCommand(roleService));
        registerCommand(new KillCommand(roleService));
        registerCommand(new DetachCommand(roleService));
        registerCommand(new RefreshCommand(roleService));
        registerCommand(new CancelCommand(roleService));
        registerCommand(new ListCommand(roleService));
        registerCommand(new CreateCommand(roleService));
        registerCommand(new ConfigCommand(roleService));
        registerCommand(new AgentCommand(roleService));
        registerCommand(new HelpCommand(roleService, this));
    }

    /**
     * 注册命令
     * @param command 命令处理类
     */
    public void registerCommand(BaseCommand command) {
        commands.add(command);
        log.debug("注册命令: {} - {}", command.getCommandName(), command.getCommandDescription());
    }

    /**
     * 查找匹配的命令
     * @param message 用户输入的消息
     * @return 匹配的命令，如果没有找到则返回空
     */
    public Optional<BaseCommand> findCommand(String message) {
        return commands.stream()
                .filter(command -> command.matches(message))
                .findFirst();
    }

    /**
     * 执行命令
     * @param message 用户输入的消息
     * @param clientId 客户端ID
     * @param userId 用户ID
     * @param agentId 代理ID
     * @param ownerId 所有者ID
     * @param timeout 超时时间
     * @return 命令执行结果，如果没有找到匹配的命令则返回空
     */
    public Optional<Flux<McpSchema.CallToolResult>> executeCommand(
            String message,
            String clientId,
            String userId,
            String agentId,
            String ownerId,
            long timeout) {
        
        Optional<BaseCommand> commandOpt = findCommand(message);
        if (commandOpt.isPresent()) {
            BaseCommand command = commandOpt.get();
            log.info("执行命令: {} for owner: {}", command.getCommandName(), ownerId);
            try {
                Flux<McpSchema.CallToolResult> result = command.execute(clientId, userId, agentId, ownerId, message, timeout);
                return Optional.of(result);
            } catch (Exception e) {
                log.error("执行命令失败: {}, 错误: {}", command.getCommandName(), e.getMessage(), e);
                McpSchema.CallToolResult errorResult = new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("❌ 命令执行失败: " + e.getMessage())), 
                        false
                );
                return Optional.of(Flux.just(errorResult));
            }
        }
        return Optional.empty();
    }

    /**
     * 获取所有命令的描述信息
     * @return 命令描述字符串
     */
    public String getAllCommandDescriptions() {
        StringBuilder sb = new StringBuilder();
        sb.append("特殊命令：");
        
        for (int i = 0; i < commands.size(); i++) {
            BaseCommand command = commands.get(i);
            sb.append(command.getCommandName())
              .append("(")
              .append(command.getCommandDescription())
              .append(")");
            
            if (i < commands.size() - 1) {
                sb.append("、");
            }
        }
        
        sb.append("。");
        return sb.toString();
    }

    /**
     * 获取命令数量
     * @return 命令数量
     */
    public int getCommandCount() {
        return commands.size();
    }

    /**
     * 获取所有命令列表
     * @return 所有命令的副本列表
     */
    public List<BaseCommand> getAllCommands() {
        return new ArrayList<>(commands);
    }
}
