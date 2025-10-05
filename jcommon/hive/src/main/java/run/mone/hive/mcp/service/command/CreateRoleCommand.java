package run.mone.hive.mcp.service.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

import java.util.HashMap;
import java.util.Map;

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
            // 创建新的role
            ReactorRole newRole = roleService.createRole(message);
            
            if (newRole != null) {
                // 将新创建的role添加到roleMap中
                roleService.getRoleMap().put(from, newRole);
                
                // 构建成功响应
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Role创建成功");
                response.put("timestamp", System.currentTimeMillis());
                
                // 添加Role详细信息
                Map<String, Object> data = new HashMap<>();
                data.put("owner", from);
                data.put("clientId", message.getClientId());
                data.put("userId", message.getUserId());
                data.put("agentId", message.getAgentId());
                data.put("agentName", roleService.getAgentName());
                data.put("status", "ready");
                response.put("data", data);
                
                // 格式化输出
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonResponse = gson.toJson(response);
                
                sendSuccessAndComplete(sink, jsonResponse);
                
                log.info("成功创建新的Role实例, from: {}, clientId: {}", from, message.getClientId());
            } else {
                // 构建失败响应
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Role创建失败，请检查系统配置");
                errorResponse.put("timestamp", System.currentTimeMillis());
                
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                sendErrorAndComplete(sink, gson.toJson(errorResponse));
                
                log.error("创建Role失败, from: {}", from);
            }

        } catch (Exception e) {
            log.error("处理创建role命令失败: {}", e.getMessage(), e);
            
            // 构建异常响应
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "创建Role失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            sendErrorAndComplete(sink, gson.toJson(errorResponse));
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
