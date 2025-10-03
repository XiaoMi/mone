package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.bo.AgentMarkdownDocument;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;
import run.mone.hive.service.MarkdownService;

/**
 * Agent命令处理类
 * 支持两种操作：
 * 1. 加载agent配置文件：/agent/<filename> [message]
 * 2. 切换agent配置：/agent/switch/<fileName>（仅切换，不支持附加消息）
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class AgentCommand extends BaseCommand {

    private final MarkdownService markdownService = new MarkdownService();

    public AgentCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return message.trim().toLowerCase().startsWith("/agent/");
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            // 解析命令：/agent/<filename> [message] 或 /agent/switch/<fileName>
            String commandPart = message.substring("/agent/".length()).trim();
            if (commandPart.isEmpty()) {
                return Flux.just(createErrorResult("请指定操作类型，格式: /agent/<filename> [message] 或 /agent/switch/<fileName>"));
            }

            // 检查是否是切换agent命令
            if (commandPart.startsWith("switch/")) {
                return handleAgentSwitch(clientId, userId, agentId, ownerId, commandPart.substring("switch/".length()).trim());
            } else {
                // 原有的加载配置文件逻辑
                return handleAgentConfig(clientId, userId, agentId, ownerId, commandPart);
            }

        } catch (Exception e) {
            log.error("处理agent命令失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("处理agent命令失败: " + e.getMessage()));
        }
    }

    /**
     * 处理agent切换命令
     */
    private Flux<McpSchema.CallToolResult> handleAgentSwitch(String clientId, String userId, String currentAgentId, String ownerId, String switchPart) {
        try {
            if (switchPart.isEmpty()) {
                return Flux.just(createErrorResult("请指定要切换的配置文件名，格式: /agent/switch/<fileName>"));
            }

            // 只取文件名，不支持附加消息
            String fileName = switchPart.trim();
            
            // 检查是否包含空格（不允许附加消息）
            if (fileName.contains(" ")) {
                return Flux.just(createErrorResult("切换命令不支持附加消息，请使用格式: /agent/switch/<fileName>"));
            }

            if (fileName.isEmpty()) {
                return Flux.just(createErrorResult("请指定要切换的配置文件名"));
            }

            // 创建MarkdownDocument对象
            AgentMarkdownDocument document = new AgentMarkdownDocument();
            document.setFileName(fileName);

            // 构建切换消息，使用固定的切换提示
            String finalMessageContent = Const.SWITCH_AGENT;

            // 构建切换agent的消息，使用MarkdownDocument作为data
            Message switchMessage = Message.builder()
                    .clientId(clientId)
                    .userId(userId)
                    .agentId(currentAgentId)  // 保持当前agentId
                    .role("user")
                    .sentFrom(ownerId)
                    .content(finalMessageContent)
                    .data(document)  // 使用MarkdownDocument对象
                    .build();

            log.info("切换agent配置: {} -> {}", currentAgentId, fileName);
            return sendToRoleService(switchMessage);

        } catch (Exception e) {
            log.error("处理agent切换命令失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("切换agent失败: " + e.getMessage()));
        }
    }

    /**
     * 处理agent配置加载命令（原有逻辑）
     */
    private Flux<McpSchema.CallToolResult> handleAgentConfig(String clientId, String userId, String agentId, String ownerId, String commandPart) {
        try {
            // 分离文件名和消息内容
            String filename;
            String userMessageContent = null;
            
            int spaceIndex = commandPart.indexOf(' ');
            if (spaceIndex > 0) {
                // 有空格，说明后面跟着消息内容
                filename = commandPart.substring(0, spaceIndex).trim();
                userMessageContent = commandPart.substring(spaceIndex + 1).trim();
            } else {
                // 没有空格，只有文件名
                filename = commandPart;
            }

            if (filename.isEmpty()) {
                return Flux.just(createErrorResult("请指定agent配置文件名"));
            }

            AgentMarkdownDocument document = new AgentMarkdownDocument();
            document.setFileName(filename);

            // 构建消息内容
            String finalMessageContent;
            if (userMessageContent != null && !userMessageContent.isEmpty()) {
                // 如果有用户消息，组合配置加载信息和用户消息
                finalMessageContent = userMessageContent;
            } else {
                // 如果没有用户消息，只是加载配置
                finalMessageContent = "加载agent配置: " + filename;
            }

            // 构建包含MarkdownDocument的消息
            Message userMessage = Message.builder()
                    .clientId(clientId)
                    .userId(userId)
                    .agentId(agentId)
                    .role("user")
                    .sentFrom(ownerId)
                    .content(finalMessageContent)
                    .data(document)
                    .build();

            log.info("加载agent配置文件: {}, 用户消息: {}", filename, finalMessageContent);
            return sendToRoleService(userMessage);

        } catch (Exception e) {
            log.error("处理agent配置加载命令失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("加载agent配置失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/agent";
    }

    @Override
    public String getCommandDescription() {
        return "加载agent配置或切换agent身份。支持: /agent/<filename> [message] 或 /agent/switch/<fileName>";
    }
}
