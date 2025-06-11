package run.mone.hive.mcp.function;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


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

        //退出agent
        if ("/exit".equalsIgnoreCase(message.trim())) {
            return exit(ownerId);
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
        return roleService.receiveMsg(Message.builder()
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
//                .timeout(Duration.ofSeconds(timeout))
                .onErrorResume((e) -> Flux.just("ERROR:" + e.getMessage()))
                .map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
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
        roleService.clearHistory(Message.builder().sentFrom(ownerId).build());
        return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("聊天历史已清空")),
                false
        ));
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