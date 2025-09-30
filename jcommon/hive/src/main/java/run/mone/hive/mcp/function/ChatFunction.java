package run.mone.hive.mcp.function;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import run.mone.hive.bo.TokenReq;
import run.mone.hive.bo.TokenRes;
import run.mone.hive.common.Safe;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.MemoryTool;
import run.mone.hive.roles.tool.ProcessManager;
import run.mone.hive.schema.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * goodjava@qq.com
 * 每个Agent都具备chat能力
 *
 */
@RequiredArgsConstructor
@Data
@Slf4j
public class ChatFunction implements McpFunction {

    private RoleService roleService;

    private final String agentName;

    private final long timeout;

    //支持权限验证
    private Function<TokenReq, TokenRes> tokenFunc = (req)-> TokenRes.builder().userId(req.getUserId()).success(true).build();


    @Override
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "message": {
                        "type": "string",
                        "description": "The message content from user."
                    },
                    "context": {
                        "type": "string",
                        "description": "Previous chat history"
                    }
                },
                "required": ["message"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        //这个agent的拥有者
        String ownerId = arguments.get(Const.OWNER_ID).toString();

        String clientId = arguments.get(Const.CLIENT_ID).toString();

        long timeout = Long.parseLong(arguments.getOrDefault(Const.TIMEOUT, String.valueOf(this.timeout)).toString());

        //用户id
        String userId = arguments.getOrDefault(Const.USER_ID, "").toString();

        TokenRes res = tokenFunc.apply(TokenReq.builder().userId(userId).arguments(arguments).build());
        if (!res.isSuccess()) {
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("账号有问题")), true));
        }

        //完成id修正
        userId = res.getUserId();

        String agentId = arguments.getOrDefault(Const.AGENT_ID, "").toString();

        String message = (String) arguments.get("message");

        log.info("message:{}", message);

        String voiceBase64 = arguments.get("voiceBase64") == null ? null : (String) arguments.get("voiceBase64");
        List<String> images = null;
        if (arguments.get("images") != null) {
            String imagesStr = arguments.get("images").toString();
            images = Arrays.asList(imagesStr.split(","));
        }

        //清空历史记录
        if ("/clear".equalsIgnoreCase(message.trim())) {
            return clear(ownerId);
        }

        //上下文回滚
        if (message.trim().toLowerCase().startsWith("/rollback")) {
            return handleRollback(ownerId, message.trim());
        }

        //退出agent
        if ("/exit".equalsIgnoreCase(message.trim())) {
            return exit(ownerId);
        }

        //杀死进程 - 格式: /kill [processId|all]
        if (message.trim().toLowerCase().startsWith("/kill")) {
            return handleKillProcess(message.trim());
        }

        //分离进程 - 格式: /detach <processId>
        if (message.trim().toLowerCase().startsWith("/detach")) {
            return handleDetachProcess(message.trim());
        }

        //刷新配置 - 格式: /refresh 或 /reload
        if (message.trim().toLowerCase().startsWith("/refresh") || message.trim().toLowerCase().startsWith("/reload")) {
            return handleRefreshConfig(ownerId);
        }

        //取消/中断执行 - 格式: /cancel
        if (message.trim().toLowerCase().startsWith("/cancel")) {
            return handleCancelCommand(ownerId);
        }

        try {
            //发送消息
            return sendMsgToAgent(clientId, userId, agentId, ownerId, message, images, voiceBase64, timeout);
        } catch (Exception e) {
            String errorMessage = "ERROR: " + e.getMessage();
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(errorMessage)), true));
        }
    }

    @NotNull
    private Flux<McpSchema.CallToolResult> sendMsgToAgent(String clientId, String userId, String agentId, String ownerId, String message, List<String> images, String voiceBase64, long timeout) {
        Message userMessage = Message.builder()
                .clientId(clientId)
                .userId(userId)
                .agentId(agentId)
                .role("user")
                .sentFrom(ownerId)
                .content(message)
                .data(message)
                .images(images)
                .voiceBase64(voiceBase64)
                .build();


        // 1. 创建一个只包含消息ID的Flux
        String idTag = "<hive-msg-id>" + userMessage.getId() + "</hive-msg-id>";
        McpSchema.CallToolResult idResult = new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(idTag)), false);
        Flux<McpSchema.CallToolResult> idFlux = Flux.just(idResult);

        // 2. 创建处理Agent响应的Flux
        Flux<McpSchema.CallToolResult> agentResponseFlux = roleService.receiveMsg(userMessage)
                .onErrorResume((e) -> Flux.just("ERROR:" + e.getMessage()))
                .map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));

        // 依次串联2个Flux
        return Flux.concat(idFlux, agentResponseFlux);
    }

    @NotNull
    private Flux<McpSchema.CallToolResult> exit(String ownerId) {
        roleService.offlineAgent(Message.builder().sentFrom(ownerId).build());
        return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("agent已退出")),
                false
        ));
    }

    @NotNull
    private Flux<McpSchema.CallToolResult> clear(String ownerId) {
        Safe.run(()->{
            if (null != MemoryTool.memoryManager) {
                MemoryTool.memoryManager.getLongTermMemory().getHistoryManager().reset();
            }
        });
        roleService.clearHistory(Message.builder().sentFrom(ownerId).build());
        return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("聊天历史已清空")),
                false
        ));
    }

    @NotNull
    private Flux<McpSchema.CallToolResult> handleRollback(String ownerId, String message) {
        String[] parts = message.split("\\s+");
        String messageId = null;
        if (parts.length > 1) {
            messageId = parts[1];
        }

        boolean success = roleService.rollbackHistory(Message.builder().sentFrom(ownerId).id(messageId).build());
        String resultText;
        if (success) {
            if (messageId != null) {
                resultText = "上下文已回滚到消息 " + messageId + " 之前";
            } else {
                resultText = "上下文已回滚上一轮对话";
            }
        } else {
            resultText = "回滚失败，没有找到指定消息或历史记录为空";
        }

        String finalMessage = String.format("<rollback-result success=\"%s\">%s</rollback-result>", success, resultText);

        return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(finalMessage)),
                false
        ));
    }

    /**
     * 处理杀死进程命令
     * 支持的格式：
     * - /kill - 杀死所有进程（兼容旧格式）
     * - /kill <processId> - 杀死指定进程
     * - /kill all - 杀死所有进程
     * - /kill list - 列出所有进程
     */
    @NotNull
    private Flux<McpSchema.CallToolResult> handleKillProcess(String message) {
        String[] parts = message.split("\\s+");
        ProcessManager processManager = ProcessManager.getInstance();
        
        // 如果只有 /kill，默认杀死所有进程（兼容旧格式）
        if (parts.length == 1) {
            int killedCount = processManager.killAllProcesses();
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("已杀死 " + killedCount + " 个进程")),
                    false
            ));
        }
        
        String action = parts[1].toLowerCase();
        
        switch (action) {
            case "list":
                // 列出所有进程
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(processManager.getAllProcessesStatus().toString())),
                        false
                ));
                
            case "all":
                // 杀死所有进程
                int killedCount = processManager.killAllProcesses();
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("已杀死 " + killedCount + " 个进程")),
                        false
                ));
                
            default:
                // 杀死指定进程
                String processId = parts[1];
                
                // 先获取进程信息用于显示
                ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                if (processInfo == null) {
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("杀死进程失败：未找到进程 " + processId)),
                            false
                    ));
                }
                
                boolean success = processManager.killProcess(processId);
                
                if (success) {
                    String processDetails = String.format("进程 %s (PID: %d, 命令: %s) 已被杀死", 
                            processId, processInfo.getPid(), processInfo.getCommand());
                    
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(processDetails)),
                            false
                    ));
                } else {
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("杀死进程失败：进程 " + processId + " 可能已经停止或无法终止")),
                            false
                    ));
                }
        }
    }

    /**
     * 处理分离进程命令
     * 支持的格式：
     * - /detach <processId> - 分离指定进程
     * - /detach all - 分离所有进程
     * - /detach list - 列出所有进程
     */
    @NotNull
    private Flux<McpSchema.CallToolResult> handleDetachProcess(String message) {
        String[] parts = message.split("\\s+");
        
        if (parts.length < 2) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("""
                            分离进程命令格式错误！
                            支持的格式：
                            - /detach <processId> - 分离指定进程
                            - /detach all - 分离所有进程  
                            - /detach list - 列出所有进程
                            """)),
                    false
            ));
        }
        
        String action = parts[1].toLowerCase();
        ProcessManager processManager = ProcessManager.getInstance();
        
        switch (action) {
            case "list":
                // 列出所有进程
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(processManager.getAllProcessesStatus().toString())),
                        false
                ));
                
            case "all":
                // 分离所有进程
                int detachedCount = processManager.detachAllProcesses();
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("已分离 " + detachedCount + " 个进程到后台运行")),
                        false
                ));
                
            default:
                // 分离指定进程
                String processId = parts[1];
                boolean success = processManager.detachProcess(processId);
                
                if (success) {
                    ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                    String processDetails = processInfo != null ? 
                            String.format("进程 %s (PID: %d, 命令: %s) 已分离到后台运行", 
                                    processId, processInfo.getPid(), processInfo.getCommand()) :
                            String.format("进程 %s 已分离到后台运行", processId);
                    
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(processDetails)),
                            false
                    ));
                } else {
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("分离进程失败：未找到进程 " + processId)),
                            false
                    ));
                }
        }
    }

    /**
     * 处理刷新配置命令
     * 支持的格式：
     * - /refresh - 刷新agent配置
     * - /reload - 刷新agent配置
     */
    @NotNull
    private Flux<McpSchema.CallToolResult> handleRefreshConfig(String ownerId) {
        // 构建刷新配置的消息，使用特殊的data标识
        Message refreshMessage = Message.builder()
                .sentFrom(ownerId)
                .role("system")
                .content("刷新配置")
                .data(Const.REFRESH_CONFIG)
                .build();
        
        // 通过roleService刷新配置
        try {
            roleService.refreshConfig(refreshMessage);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("🔄 配置已刷新，包括MCP连接和角色设置")),
                    false
            ));
        } catch (Exception e) {
            log.error("刷新配置失败: {}", e.getMessage(), e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("❌ 配置刷新失败: " + e.getMessage())),
                    false
            ));
        }
    }

    /**
     * 处理取消/中断命令
     * 支持的格式：
     * - /cancel - 取消当前执行
     */
    @NotNull
    private Flux<McpSchema.CallToolResult> handleCancelCommand(String ownerId) {
        // 构建取消命令的消息
        Message cancelMessage = Message.builder()
                .sentFrom(ownerId)
                .role("user")
                .content("/cancel")
                .build();
        
        // 通过roleService发送取消命令，让RoleService处理中断逻辑
        try {
            // 直接发送取消消息到RoleService，它会自动处理中断逻辑
            Flux<String> resultFlux = roleService.receiveMsg(cancelMessage);
            
            // 订阅结果但不阻塞，让中断逻辑异步执行
            resultFlux.subscribe(
                    result -> log.debug("取消命令执行结果: {}", result),
                    error -> log.error("取消命令执行失败: {}", error.getMessage(), error),
                    () -> log.debug("取消命令处理完成")
            );
            
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("🛑 已发送取消指令")),
                    false
            ));
        } catch (Exception e) {
            log.error("发送取消指令失败: {}", e.getMessage(), e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("❌ 取消指令发送失败: " + e.getMessage())),
                    false
            ));
        }
    }

    public String getName() {
        return "stream_%s_chat".formatted(agentName);
    }

    public String getDesc() {
        return "和%s聊天，问问%s问题。支持各种形式如：'%s'、'请%s告诉我'、'让%s帮我看看'、'%s你知道吗'等。支持上下文连续对话。"
                .formatted(agentName, agentName, agentName, agentName, agentName, agentName);
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

}
