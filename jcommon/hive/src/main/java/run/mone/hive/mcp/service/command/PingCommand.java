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
 * Ping命令处理类
 * 处理 /ping 命令，返回 pong 响应
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class PingCommand extends RoleBaseCommand {

    public PingCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(Message message) {
        if (message == null) {
            return false;
        }
        String content = message.getContent();
        Object data = message.getData();
        
        return (content != null && content.trim().toLowerCase().equals("/ping")) ||
               (data != null && "PING".equals(data.toString()));
    }

    @Override
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        return content.trim().toLowerCase().equals("/ping");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        try {
            // 构建标准响应格式
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "pong");
            response.put("timestamp", System.currentTimeMillis());
            
            // 添加一些额外的信息
            Map<String, Object> data = new HashMap<>();
            data.put("status", "alive");
            data.put("from", from);
            if (role != null) {
                data.put("roleOwner", role.getOwner());
                data.put("roleState", role.getState().get().toString());
            }
            response.put("data", data);

            // 格式化输出
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonResponse = gson.toJson(response);

            sendSuccessAndComplete(sink, jsonResponse);

        } catch (Exception e) {
            log.error("处理ping命令失败: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "ping命令执行失败: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            sendErrorAndComplete(sink, gson.toJson(errorResponse));
        }
    }

    @Override
    public String getCommandName() {
        return "/ping";
    }

    @Override
    public String getCommandDescription() {
        return "测试连接状态，返回pong响应 (用法: /ping)";
    }
}
