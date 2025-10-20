package run.mone.hive.mcp.service.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.FluxSink;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Role命令工厂类
 * 负责管理所有的Role命令处理类
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class RoleCommandFactory {

    private final List<RoleBaseCommand> commands = new ArrayList<>();

    public RoleCommandFactory(RoleService roleService) {
        // 注册所有Role命令
        registerCommand(new InterruptCommand(roleService));
        registerCommand(new RefreshConfigCommand(roleService));
        registerCommand(new ListAgentsCommand(roleService));
        registerCommand(new CreateRoleCommand(roleService));
        registerCommand(new ConfigCommand(roleService));
        registerCommand(new SwitchAgentCommand(roleService));
        registerCommand(new InitCommand(roleService));
        registerCommand(new PingCommand(roleService));
        registerCommand(new McpCommand(roleService));
        registerCommand(new CompressionCommand(roleService));
        
        // 自动扫描和注册带@RoleCommand注解的命令
        scanAndRegisterAnnotatedCommands(roleService);
    }

    /**
     * 注册命令
     *
     * @param command 命令处理类
     */
    public void registerCommand(RoleBaseCommand command) {
        commands.add(command);
        log.debug("注册Role命令: {} - {}", command.getCommandName(), command.getCommandDescription());
    }

    /**
     * 扫描并注册带@RoleCommand注解的命令
     *
     * @param roleService RoleService实例
     */
    private void scanAndRegisterAnnotatedCommands(RoleService roleService) {
        try {
            if (!ApplicationContextHolder.isInitialized()) {
                log.warn("ApplicationContext未初始化，跳过自动扫描命令");
                return;
            }

            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            Map<String, Object> commandBeans = applicationContext.getBeansWithAnnotation(RoleCommand.class);

            List<RoleBaseCommand> annotatedCommands = new ArrayList<>();
            
            for (Object bean : commandBeans.values()) {
                if (bean instanceof RoleBaseCommand) {
                    RoleBaseCommand command = (RoleBaseCommand) bean;
                    annotatedCommands.add(command);
                    
                    RoleCommand annotation = bean.getClass().getAnnotation(RoleCommand.class);
                    log.info("发现带@RoleCommand注解的命令: {} - {} (priority: {})", 
                            annotation.value().isEmpty() ? command.getCommandName() : annotation.value(),
                            annotation.description().isEmpty() ? command.getCommandDescription() : annotation.description(),
                            annotation.priority());
                }
            }

            // 按优先级排序，优先级数值越小越靠前
            annotatedCommands.sort(Comparator.comparingInt(cmd -> 
                    cmd.getClass().getAnnotation(RoleCommand.class).priority()));

            // 注册排序后的命令
            for (RoleBaseCommand command : annotatedCommands) {
                // 避免重复注册（如果已经手动注册过）
                boolean alreadyRegistered = commands.stream()
                        .anyMatch(existingCmd -> existingCmd.getClass().equals(command.getClass()));
                
                if (!alreadyRegistered) {
                    registerCommand(command);
                    log.info("自动注册Role命令: {}", command.getCommandName());
                } else {
                    log.debug("命令已存在，跳过注册: {}", command.getCommandName());
                }
            }

        } catch (Exception e) {
            log.error("扫描和注册带注解的命令失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 查找匹配的命令（基于Message）
     * 按优先级排序，优先级高的命令优先匹配
     *
     * @param message 用户输入的消息
     * @return 匹配的命令，如果没有找到则返回空
     */
    public Optional<RoleBaseCommand> findCommand(Message message) {
        return commands.stream()
                .sorted(this::compareCommandPriority)
                .filter(command -> command.matches(message))
                .findFirst();
    }

    /**
     * 查找匹配的命令（基于字符串内容）
     * 按优先级排序，优先级高的命令优先匹配
     *
     * @param content 消息内容
     * @return 匹配的命令，如果没有找到则返回空
     */
    public Optional<RoleBaseCommand> findCommand(String content) {
        return commands.stream()
                .sorted(this::compareCommandPriority)
                .filter(command -> command.matches(content))
                .findFirst();
    }

    /**
     * 比较命令优先级
     * 有@RoleCommand注解的按priority排序，没有注解的按默认优先级100处理
     *
     * @param cmd1 命令1
     * @param cmd2 命令2
     * @return 比较结果
     */
    private int compareCommandPriority(RoleBaseCommand cmd1, RoleBaseCommand cmd2) {
        int priority1 = getPriority(cmd1);
        int priority2 = getPriority(cmd2);
        return Integer.compare(priority1, priority2);
    }

    /**
     * 获取命令的优先级
     *
     * @param command 命令
     * @return 优先级
     */
    private int getPriority(RoleBaseCommand command) {
        RoleCommand annotation = command.getClass().getAnnotation(RoleCommand.class);
        return annotation != null ? annotation.priority() : 100; // 默认优先级100
    }

    /**
     * 执行命令（基于Message）
     *
     * @param message 用户输入的消息
     * @param sink    响应流的sink
     * @param from    发送者标识
     * @param role    当前的ReactorRole实例
     * @return 是否找到并执行了命令
     */
    public boolean executeCommand(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        Optional<RoleBaseCommand> commandOpt = findCommand(message);
        if (commandOpt.isPresent()) {
            RoleBaseCommand command = commandOpt.get();
            log.info("执行Role命令: {} for from: {}", command.getCommandName(), from);
            try {
                command.execute(message, sink, from, role);
                return true;
            } catch (Exception e) {
                log.error("执行Role命令失败: {}, 错误: {}", command.getCommandName(), e.getMessage(), e);
                sink.next("❌ 命令执行失败: " + e.getMessage() + "\n");
                return true; // 即使执行失败，也表示找到了命令
            } finally {
                sink.complete();
            }
        }
        return false;
    }

    /**
     * 执行命令（基于字符串内容）
     *
     * @param content 消息内容
     * @param message 完整的消息对象
     * @param sink    响应流的sink
     * @param from    发送者标识
     * @param role    当前的ReactorRole实例
     * @return 是否找到并执行了命令
     */
    public boolean executeCommand(String content, Message message, FluxSink<String> sink, String from, ReactorRole role) {
        Optional<RoleBaseCommand> commandOpt = findCommand(content);
        if (commandOpt.isPresent()) {
            RoleBaseCommand command = commandOpt.get();
            log.info("执行Role命令: {} for from: {}", command.getCommandName(), from);
            try {
                command.execute(message, sink, from, role);
                return true;
            } catch (Exception e) {
                log.error("执行Role命令失败: {}, 错误: {}", command.getCommandName(), e.getMessage(), e);
                sink.next("❌ 命令执行失败: " + e.getMessage() + "\n");
                sink.complete();
                return true; // 即使执行失败，也表示找到了命令
            }
        }
        return false;
    }

    /**
     * 获取所有命令的描述信息
     *
     * @return 命令描述字符串
     */
    public String getAllCommandDescriptions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Role命令：");

        for (int i = 0; i < commands.size(); i++) {
            RoleBaseCommand command = commands.get(i);
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
     *
     * @return 命令数量
     */
    public int getCommandCount() {
        return commands.size();
    }

    /**
     * 获取所有命令列表
     *
     * @return 所有命令的副本列表
     */
    public List<RoleBaseCommand> getAllCommands() {
        return new ArrayList<>(commands);
    }
}
