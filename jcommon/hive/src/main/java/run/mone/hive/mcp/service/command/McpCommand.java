package run.mone.hive.mcp.service.command;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP配置刷新命令处理类
 * 处理 /mcp 命令，支持刷新指定的MCP服务器配置
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class McpCommand extends RoleBaseCommand {

    public McpCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(Message message) {
        if (message == null) {
            return false;
        }

        String content = message.getContent();
        return content != null && content.trim().toLowerCase().startsWith("/mcp");
    }

    @Override
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        return content.trim().toLowerCase().startsWith("/mcp");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        try {
            String content = message.getContent();
            if (content == null || !content.trim().toLowerCase().startsWith("/mcp")) {
                sendErrorAndComplete(sink, "无效的MCP命令格式");
                return;
            }

            // 解析命令：/mcp <operation> [serverName]
            String commandPart = content.substring("/mcp".length()).trim();
            if (commandPart.isEmpty()) {
                sendErrorAndComplete(sink, "请指定MCP操作，格式: /mcp <operation> [serverName]，支持的操作: refresh, list, delete");
                return;
            }

            String[] parts = commandPart.split("\\s+");
            if (parts.length < 1) {
                sendErrorAndComplete(sink, "支持的操作: refresh, list, delete，格式: /mcp <operation> [serverName]");
                return;
            }

            String operation = parts[0].toLowerCase();
            
            switch (operation) {
                //目前当add用的
                case "refresh":
                    if (parts.length < 2) {
                        sendErrorAndComplete(sink, "refresh操作需要指定服务器名称，格式: /mcp refresh <serverName|all>");
                        return;
                    }
                    String serverName = parts[1].trim();
                    if (serverName.isEmpty()) {
                        sendErrorAndComplete(sink, "请指定要刷新的服务器名称或使用 'all' 刷新所有服务器");
                        return;
                    }
                    handleMcpRefresh(message, sink, from, role, serverName);
                    break;
                    
                case "list":
                    handleMcpList(message, sink, from, role);
                    break;
                    
                case "delete":
                    if (parts.length < 2) {
                        sendErrorAndComplete(sink, "delete操作需要指定服务器名称，格式: /mcp delete <serverName>");
                        return;
                    }
                    String deleteServerName = parts[1].trim();
                    if (deleteServerName.isEmpty()) {
                        sendErrorAndComplete(sink, "请指定要删除的服务器名称");
                        return;
                    }
                    handleMcpDelete(message, sink, from, role, deleteServerName);
                    break;
                    
                default:
                    sendErrorAndComplete(sink, "不支持的操作: " + operation + "，支持的操作: refresh, list, delete");
                    break;
            }

        } catch (Exception e) {
            log.error("处理MCP命令失败: {}", e.getMessage(), e);
            sendErrorAndComplete(sink, "MCP操作失败: " + e.getMessage());
        }
    }

    /**
     * 处理MCP刷新操作
     */
    private void handleMcpRefresh(Message message, FluxSink<String> sink, String from, ReactorRole role, String serverName) {
        try {
            if (role == null) {
                sendErrorAndComplete(sink, "无法获取Role实例，MCP刷新失败");
                return;
            }

            // 检查是否有McpHub实例
            if (role.getMcpHub() == null) {
                sendErrorAndComplete(sink, "当前Role未配置McpHub，无法执行MCP操作");
                return;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "");
            result.put("serverName", serverName);
            result.put("timestamp", System.currentTimeMillis());

            if ("all".equalsIgnoreCase(serverName)) {
                // 刷新所有MCP服务器
                refreshAllMcpServers(role, result);
            } else {
                // 刷新指定MCP服务器
                refreshSpecificMcpServer(role, serverName, result);
            }

            // 返回JSON格式的结果
            String jsonResult = GsonUtils.gson.toJson(result);
            sink.next(jsonResult);
            sink.complete();

            log.info("MCP刷新操作完成, from: {}, serverName: {}, result: {}", from, serverName, result);

        } catch (Exception e) {
            log.error("处理MCP刷新失败: {}", e.getMessage(), e);

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "MCP刷新失败: " + e.getMessage());
            errorResult.put("serverName", serverName);
            errorResult.put("timestamp", System.currentTimeMillis());

            String jsonResult = GsonUtils.gson.toJson(errorResult);
            sink.next(jsonResult);
            sink.complete();
        }
    }

    /**
     * 刷新所有MCP服务器
     */
    private void refreshAllMcpServers(ReactorRole role, Map<String, Object> result) {
        try {
            // 获取当前所有连接的服务器名称
            var connections = role.getMcpHub().getConnections();
            if (connections == null || connections.isEmpty()) {
                result.put("message", "当前没有活动的MCP服务器连接");
                return;
            }

            int successCount = 0;
            int totalCount = connections.size();
            StringBuilder details = new StringBuilder();

            for (String serverName : connections.keySet()) {
                try {
                    role.getMcpHub().refreshMcpServer(serverName);
                    successCount++;
                    details.append(String.format("✅ %s: 刷新成功\n", serverName));
                    log.info("成功刷新MCP服务器: {}", serverName);
                } catch (Exception e) {
                    details.append(String.format("❌ %s: 刷新失败 - %s\n", serverName, e.getMessage()));
                    log.error("刷新MCP服务器失败: {}, 错误: {}", serverName, e.getMessage(), e);
                }
            }

            result.put("success", successCount > 0);
            result.put("message", String.format("刷新完成: %d/%d 成功\n%s", successCount, totalCount, details.toString()));
            result.put("successCount", successCount);
            result.put("totalCount", totalCount);

        } catch (Exception e) {
            result.put("message", "刷新所有MCP服务器时发生错误: " + e.getMessage());
            log.error("刷新所有MCP服务器失败", e);
        }
    }

    /**
     * 刷新指定的MCP服务器
     */
    private void refreshSpecificMcpServer(ReactorRole role, String serverName, Map<String, Object> result) {
        try {
            this.roleService.addMcp(Lists.newArrayList(serverName), role);
            result.put("success", true);
            result.put("message", String.format("MCP服务器 '%s' 刷新成功", serverName));
            log.info("成功刷新MCP服务器: {}", serverName);
        } catch (Exception e) {
            result.put("message", String.format("刷新MCP服务器 '%s' 失败: %s", serverName, e.getMessage()));
            log.error("刷新MCP服务器失败: {}, 错误: {}", serverName, e.getMessage(), e);
        }
    }

    @Override
    public String getCommandName() {
        return "/mcp";
    }

    /**
     * 处理MCP列表操作
     */
    private void handleMcpList(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        try {
            if (role == null) {
                sendErrorAndComplete(sink, "无法获取Role实例，MCP列表获取失败");
                return;
            }

            // 检查是否有McpHub实例
            if (role.getMcpHub() == null) {
                sendErrorAndComplete(sink, "当前Role未配置McpHub，无法获取MCP服务器列表");
                return;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取MCP服务器列表成功");
            result.put("timestamp", System.currentTimeMillis());

            // 获取所有服务器列表
            var servers = role.getMcpHub().getServers();
            if (servers == null || servers.isEmpty()) {
                result.put("servers", new java.util.ArrayList<>());
                result.put("totalCount", 0);
                result.put("message", "当前没有配置的MCP服务器");
            } else {
                java.util.List<Map<String, Object>> serverList = new java.util.ArrayList<>();
                for (var server : servers) {
                    Map<String, Object> serverInfo = new HashMap<>();
                    serverInfo.put("name", server.getName());
                    serverInfo.put("status", server.getStatus());
                    serverInfo.put("error", server.getError());
                    serverInfo.put("toolsCount", server.getTools() != null ? server.getTools().size() : 0);
                    serverList.add(serverInfo);
                }
                result.put("servers", serverList);
                result.put("totalCount", servers.size());
            }

            // 返回JSON格式的结果
            String jsonResult = GsonUtils.gson.toJson(result);
            sink.next(jsonResult);
            sink.complete();

            log.info("MCP列表获取完成, from: {}, totalCount: {}", from, servers != null ? servers.size() : 0);

        } catch (Exception e) {
            log.error("处理MCP列表获取失败: {}", e.getMessage(), e);

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "获取MCP服务器列表失败: " + e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());

            String jsonResult = GsonUtils.gson.toJson(errorResult);
            sink.next(jsonResult);
            sink.complete();
        }
    }

    /**
     * 处理MCP删除操作
     */
    private void handleMcpDelete(Message message, FluxSink<String> sink, String from, ReactorRole role, String serverName) {
        try {
            if (role == null) {
                sendErrorAndComplete(sink, "无法获取Role实例，MCP删除失败");
                return;
            }

            // 检查是否有McpHub实例
            if (role.getMcpHub() == null) {
                sendErrorAndComplete(sink, "当前Role未配置McpHub，无法执行MCP删除操作");
                return;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "");
            result.put("serverName", serverName);
            result.put("timestamp", System.currentTimeMillis());

            // 检查服务器是否存在
            var connections = role.getMcpHub().getConnections();
            if (connections == null || !connections.containsKey(serverName)) {
                result.put("message", String.format("MCP服务器 '%s' 不存在", serverName));
            } else {
                try {
                    // 删除连接
                    role.getMcpHub().removeConnection(serverName);
                    result.put("success", true);
                    result.put("message", String.format("MCP服务器 '%s' 删除成功", serverName));
                    log.info("成功删除MCP服务器: {}", serverName);
                } catch (Exception e) {
                    result.put("message", String.format("删除MCP服务器 '%s' 失败: %s", serverName, e.getMessage()));
                    log.error("删除MCP服务器失败: {}, 错误: {}", serverName, e.getMessage(), e);
                }
            }

            // 返回JSON格式的结果
            String jsonResult = GsonUtils.gson.toJson(result);
            sink.next(jsonResult);
            sink.complete();

            log.info("MCP删除操作完成, from: {}, serverName: {}, result: {}", from, serverName, result);

        } catch (Exception e) {
            log.error("处理MCP删除失败: {}", e.getMessage(), e);

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "MCP删除失败: " + e.getMessage());
            errorResult.put("serverName", serverName);
            errorResult.put("timestamp", System.currentTimeMillis());

            String jsonResult = GsonUtils.gson.toJson(errorResult);
            sink.next(jsonResult);
            sink.complete();
        }
    }

    @Override
    public String getCommandDescription() {
        return "MCP服务器管理命令，支持的操作: refresh <serverName|all>, list, delete <serverName>";
    }
}
