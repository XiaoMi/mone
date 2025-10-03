package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.bo.MarkdownDocument;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;
import run.mone.hive.service.MarkdownService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 加载agent配置命令处理类
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
            // 解析命令：/agent/<filename> [message]
            String commandPart = message.substring("/agent/".length()).trim();
            if (commandPart.isEmpty()) {
                return Flux.just(createErrorResult("请指定agent配置文件名，格式: /agent/<filename> [message]"));
            }

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

            MarkdownDocument document = new MarkdownDocument();
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
        return "加载agent配置并可选发送消息";
    }
}
