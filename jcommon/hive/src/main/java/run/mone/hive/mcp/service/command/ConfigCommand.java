package run.mone.hive.mcp.service.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.bo.AgentMarkdownDocument;
import run.mone.hive.mcp.service.RoleMeta;
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
 * 获取配置命令处理类
 * 处理 /config 命令和 GET_CONFIG 数据类型
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ConfigCommand extends RoleBaseCommand {

    private final MarkdownService markdownService = new MarkdownService();

    public ConfigCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(Message message) {
        if (message == null) {
            return false;
        }
        String content = message.getContent();
        Object data = message.getData();
        
        return (content != null && (content.trim().toLowerCase().equals("/config") || 
                                   content.trim().toLowerCase().startsWith("/config put"))) ||
               (data != null && ("GET_CONFIG".equals(data.toString()) || "PUT_CONFIG".equals(data.toString())));
    }

    @Override
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        return content.trim().toLowerCase().equals("/config") || 
               content.trim().toLowerCase().startsWith("/config put");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        try {
            String content = message.getContent();
            
            // 检查是否是 put config 命令
            if (content != null && content.trim().toLowerCase().startsWith("/config put")) {
                handlePutConfig(content, sink, role);
                return;
            }
            
            // 创建配置信息Map
            Map<String, Object> configMap = new HashMap<>();
            // Role相关信息
            if (role != null) {
                configMap.put("owner", role.getOwner());
                configMap.put("clientId", role.getClientId());
                configMap.put("workspacePath", role.getWorkspacePath());
                configMap.put("roleState", role.getState().get().toString());
                configMap.put("interrupted", role.isInterrupted());
                
                // RoleMeta信息
                RoleMeta roleMeta = role.getRoleMeta();
                if (roleMeta != null) {
                    Map<String, Object> roleMetaMap = new HashMap<>();
                    roleMetaMap.put("profile", roleMeta.getProfile());
                    roleMetaMap.put("goal", roleMeta.getGoal());
                    roleMetaMap.put("constraints", roleMeta.getConstraints());
                    roleMetaMap.put("workflow", roleMeta.getWorkflow());
                    roleMetaMap.put("outputFormat", roleMeta.getOutputFormat());
                    roleMetaMap.put("roleType", roleMeta.getRoleType());
                    configMap.put("roleMeta", roleMetaMap);
                }
                
                // Role配置信息
                Map<String, String> roleConfig = role.getRoleConfig();
                if (roleConfig != null && !roleConfig.isEmpty()) {
                    configMap.put("roleConfig", new HashMap<>(roleConfig));
                }
            }
            

            // 系统信息
            Map<String, Object> systemInfo = new HashMap<>();
            systemInfo.put("mcpPath", roleService.getMcpPath());
            systemInfo.put("mcpServerList", roleService.getMcpServerList());
            systemInfo.put("delay", roleService.getDelay());
            
            // 添加agent文件列表
            if (role != null) {
                Map<String, String> agentList = getAgentListFromWorkspace(role);
                systemInfo.put("agentList", agentList);

                int messageSize = role.getRc().getMessageList().size();
                configMap.put("messageSize", messageSize);
            }
            
            configMap.put("systemInfo", systemInfo);
            
            // LLM配置选项
            Map<String, String> llmOptions = new HashMap<>();
            llmOptions.put("qwen", "qwen");
            llmOptions.put("glm", "glm_46");
            llmOptions.put("deepseek", "deepseek");
            llmOptions.put("claude", "openrouter_claude_sonnet_45");
            llmOptions.put("gemini", "openrouter_gemini_25_pro");
            configMap.put("llmOptions", llmOptions);

            // 构建标准响应格式
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取配置信息成功");
            response.put("data", configMap);

            // 格式化输出
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonResponse = gson.toJson(response);

            sendSuccessAndComplete(sink, jsonResponse);

        } catch (Exception e) {
            log.error("获取配置信息失败: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取配置信息失败: " + e.getMessage());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            sendErrorAndComplete(sink, gson.toJson(errorResponse));
        }
    }

    @Override
    public String getCommandName() {
        return "/config";
    }

    @Override
    public String getCommandDescription() {
        return "获取当前Agent配置信息、系统信息和可用Agent列表，或设置配置项 (用法: /config 或 /config put key=value)";
    }
    
    /**
     * 处理 put config 命令
     */
    private void handlePutConfig(String content, FluxSink<String> sink, ReactorRole role) {
        try {
            // 解析命令格式: /config put key=value
            String[] parts = content.trim().split("\\s+", 3);
            if (parts.length < 3) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "命令格式错误！正确格式: /config put key=value");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                sendErrorAndComplete(sink, gson.toJson(errorResponse));
                return;
            }
            
            String keyValue = parts[2];
            String[] kvPair = keyValue.split("=", 2);
            if (kvPair.length != 2) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "参数格式错误！正确格式: key=value");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                sendErrorAndComplete(sink, gson.toJson(errorResponse));
                return;
            }
            
            String key = kvPair[0].trim();
            String value = kvPair[1].trim();
            
            if (key.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "配置键不能为空！");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                sendErrorAndComplete(sink, gson.toJson(errorResponse));
                return;
            }
            
            // 存储到 role 的 roleConfig 中
            if (role != null) {
                Map<String, String> roleConfig = role.getRoleConfig();
                if (roleConfig == null) {
                    roleConfig = new HashMap<>();
                    role.setRoleConfig(roleConfig);
                }
                roleConfig.put(key, value);

                role.saveConfig();
                
                // 构建JSON响应
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "配置设置成功！");
                response.put("key", key);
                response.put("value", value);
                
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                sendSuccessAndComplete(sink, gson.toJson(response));
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Role对象为空，无法保存配置");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                sendErrorAndComplete(sink, gson.toJson(errorResponse));
            }
            
        } catch (Exception e) {
            log.error("处理put config命令失败: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "设置配置失败: " + e.getMessage());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            sendErrorAndComplete(sink, gson.toJson(errorResponse));
        }
    }
    
    /**
     * 获取指定workspace下所有.md文件的文件名和name映射
     * @param role ReactorRole实例
     * @return Map<filename, agentName>
     */
    private Map<String, String> getAgentListFromWorkspace(ReactorRole role) {
        Map<String, String> agentMap = new HashMap<>();
        
        try {
            // 获取workspace路径
            String workspacePath = role.getWorkspacePath();
            if (workspacePath == null || workspacePath.isEmpty()) {
                log.warn("无法获取workspace路径");
                return agentMap;
            }

            // 构建.hive目录路径
            Path hiveDir = Paths.get(workspacePath, ".hive");

            // 检查目录是否存在
            if (!Files.exists(hiveDir) || !Files.isDirectory(hiveDir)) {
                log.warn(".hive目录不存在: {}", hiveDir.toString());
                return agentMap;
            }

            // 遍历目录下的所有.md文件
            List<Path> mdFiles = Files.list(hiveDir)
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
            log.error("读取.hive目录失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("获取agent列表失败: {}", e.getMessage(), e);
        }
        
        return agentMap;
    }
}
