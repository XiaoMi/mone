package run.mone.hive.mcp.service.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取配置命令处理类
 * 处理 /config 命令和 GET_CONFIG 数据类型
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class GetConfigCommand extends RoleBaseCommand {

    public GetConfigCommand(RoleService roleService) {
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
            configMap.put("systemInfo", systemInfo);

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
        return "获取当前Agent配置信息或设置配置项 (用法: /config 或 /config put key=value)";
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
}
