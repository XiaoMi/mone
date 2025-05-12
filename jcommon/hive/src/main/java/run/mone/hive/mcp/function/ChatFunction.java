package run.mone.hive.mcp.function;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 每个Agent都具备chat能力
 */
@RequiredArgsConstructor
@Data
public class ChatFunction implements McpFunction {

    private RoleService roleService;

    private final String agentName;

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

        //用户id
        String userId = arguments.getOrDefault(Const.USER_ID, "").toString();
        String agentId = arguments.getOrDefault(Const.AGENT_ID, "").toString();

        String message = (String) arguments.get("message");
        String voiceBase64 = arguments.get("voiceBase64") == null ? null : (String) arguments.get("voiceBase64");
        List<String> images = null;
        if (arguments.get("images") != null) {
            String imagesStr = arguments.get("images").toString();
            images = Arrays.asList(imagesStr.split(","));
        }

        //清空历史记录
        if ("/clear".equalsIgnoreCase(message.trim())) {
            roleService.clearHistory(Message.builder().sentFrom(ownerId).build());
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("聊天历史已清空")),
                    false
            ));
        }

        //退出agent
        if ("/exit".equalsIgnoreCase(message.trim())) {
            roleService.offlineAgent(Message.builder().sentFrom(ownerId).build());
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("agent已退出")),
                    false
            ));
        }


        //会创建一个Agent instance
        try {
            return roleService.receiveMsg(run.mone.hive.schema.Message.builder()
                            .clientId(clientId)
                            .userId(userId)
                            .agentId(agentId)
                            .role("user")
                            .sentFrom(ownerId)
                            .content(message)
                            .data(message)
                            .images(images)
                            .voiceBase64(voiceBase64)
                            .build())
                    .map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
        } catch (Exception e) {
            String errorMessage = "Error: " + e.getMessage();
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(errorMessage)), true));
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