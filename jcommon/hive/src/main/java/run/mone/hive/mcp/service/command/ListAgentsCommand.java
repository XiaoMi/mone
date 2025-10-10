package run.mone.hive.mcp.service.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.bo.AgentMarkdownDocument;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;
import run.mone.hive.service.MarkdownService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 获取Agent列表命令处理类
 * 处理 /list 命令和 LIST_AGENTS 数据类型
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ListAgentsCommand extends RoleBaseCommand {

    private final MarkdownService markdownService = new MarkdownService();

    public ListAgentsCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(Message message) {
        if (message == null) {
            return false;
        }
        String content = message.getContent();
        Object data = message.getData();
        
        return (content != null && content.trim().toLowerCase().equals("/list")) ||
               (data != null && "LIST_AGENTS".equals(data.toString()));
    }

    @Override
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        return content.trim().toLowerCase().equals("/list");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        if (role == null) {
            sendErrorAndComplete(sink, "没有找到Agent实例");
            return;
        }

        try {
            sink.next("📋 正在扫描agent配置文件...\n");

            // 获取workspace路径
            String workspacePath = role.getWorkspacePath();
            if (workspacePath == null || workspacePath.isEmpty()) {
                sendErrorAndComplete(sink, "无法获取workspace路径");
                return;
            }

            // 构建.hive目录路径
            Path hiveDir = Paths.get(workspacePath, ".hive");

            // 检查目录是否存在
            if (!Files.exists(hiveDir) || !Files.isDirectory(hiveDir)) {
                sendErrorAndComplete(sink, ".hive目录不存在: " + hiveDir.toString());
                return;
            }

            // 获取所有.md文件并解析
            Map<String, String> agentMap = getAgentListFromWorkspace(hiveDir);

            if (agentMap.isEmpty()) {
                sendSuccessAndComplete(sink, "📝 未找到任何agent配置文件(.md)\n");
                return;
            }

            // 构建返回结果
            StringBuilder result = new StringBuilder();
            result.append("📋 可用的Agent配置文件:\n\n");
            
            int index = 1;
            for (Map.Entry<String, String> entry : agentMap.entrySet()) {
                String filename = entry.getKey();
                String agentName = entry.getValue();
                result.append(String.format("%d. **%s** (%s)\n", index++, 
                    agentName != null ? agentName : "未命名", filename));
            }
            
            result.append("\n💡 使用 `/agent/<filename> [message]` 来加载指定的agent配置\n");

            sendSuccessAndComplete(sink, result.toString());

        } catch (Exception e) {
            log.error("获取agent列表失败: {}", e.getMessage(), e);
            sendErrorAndComplete(sink, "获取agent列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定目录下所有.md文件的文件名和name映射
     * @param directory 目录路径
     * @return Map<filename, agentName>
     */
    private Map<String, String> getAgentListFromWorkspace(Path directory) {
        Map<String, String> agentMap = new HashMap<>();
        
        try {
            // 遍历目录下的所有.md文件
            List<Path> mdFiles = Files.list(directory)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".md"))
                    .collect(Collectors.toList());

            for (Path mdFile : mdFiles) {
                String filename = mdFile.getFileName().toString();
                String agentName = null;
                
                try {
                    // 解析markdown文件获取name
                    AgentMarkdownDocument document = markdownService.readFromFile(mdFile.toString());
                    if (document != null && document.getName() != null && !document.getName().trim().isEmpty()) {
                        agentName = document.getName().trim();
                    }
                } catch (Exception e) {
                    log.warn("解析markdown文件失败: {}, 错误: {}", filename, e.getMessage());
                    // 如果解析失败，agentName保持为null
                }
                
                agentMap.put(filename, agentName);
            }
            
        } catch (IOException e) {
            log.error("读取目录失败: {}", e.getMessage(), e);
        }
        
        return agentMap;
    }

    @Override
    public String getCommandName() {
        return "/list";
    }

    @Override
    public String getCommandDescription() {
        return "获取可用的Agent配置文件列表";
    }
}
