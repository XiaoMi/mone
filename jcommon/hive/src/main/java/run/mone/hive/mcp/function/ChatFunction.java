package run.mone.hive.mcp.function;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import run.mone.hive.bo.TokenReq;
import run.mone.hive.bo.TokenRes;
import run.mone.hive.checkpoint.FileCheckpointManager;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.command.CommandManager;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * goodjava@qq.com
 * 每个Agent都具备chat能力
 */
@RequiredArgsConstructor
@Data
@Slf4j
public class ChatFunction implements McpFunction {

    @Value("${hive.checkpoint.enable:false}")
    private boolean enableCheckPoint;

    private RoleService roleService;

    private final String agentName;

    private final long timeout;

    private CommandManager commandManager;

    private String desc;

    //支持权限验证
    private Function<TokenReq, TokenRes> tokenFunc = (req) -> TokenRes.builder().userId(req.getUserId()).success(true).build();


    @Override
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
        this.commandManager = new CommandManager(roleService);
    }

    public ChatFunction(String agentName, long timeout, RoleService roleService) {
        this.agentName = agentName;
        this.timeout = timeout;
        this.roleService = roleService;
        this.commandManager = new CommandManager(roleService);
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
        log.info("chat arguments:{}", arguments);
        //这个agent的拥有者
        String ownerId = arguments.getOrDefault(Const.OWNER_ID, "owner_id").toString();
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

        // 尝试使用命令管理器处理命令
        var commandResult = commandManager.executeCommand(message, clientId, userId, agentId, ownerId, timeout);
        if (commandResult.isPresent()) {
            return commandResult.get();
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

        if (FileCheckpointManager.enableCheck(enableCheckPoint)) {
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
        } else {
            // 创建处理Agent响应的Flux
            return roleService.receiveMsg(userMessage)
                    .onErrorResume((e) -> Flux.just("ERROR:" + e.getMessage()))
                    .map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
        }
    }


    public String getName() {
        return "stream_%s_chat".formatted(agentName);
    }

    public String getDesc() {
        String str = "和%s聊天，问问%s问题。支持各种形式如：'%s'、'请%s告诉我'、'让%s帮我看看'、'%s你知道吗'等。支持上下文连续对话。";
        if (StringUtils.isNotEmpty(desc)) {
            str = desc;
        }
        return str.formatted(agentName, agentName, agentName, agentName, agentName, agentName);
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

}
