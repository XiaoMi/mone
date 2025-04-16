package run.mone.mcp.chat.function;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chat.service.RoleService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ChatFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private final RoleService roleService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "message": {
                        "type": "string",
                        "description": "The message content from user. Use '/clear' to clear chat history"
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
        String ownerId = arguments.get(Const.OWNER_ID).toString();
        String clientId = arguments.get(Const.CLIENT_ID).toString();
        String message = (String) arguments.get("message");

        // Handle clear history command
        if ("/clear".equalsIgnoreCase(message.trim())) {
            roleService.clearHistory(run.mone.hive.schema.Message.builder()
                    .clientId(clientId)
                    .role("user")
                    .sentFrom(ownerId)
                    .content(message)
                    .data(message)
                    .build());
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("聊天历史已清空")),
                false
            ));
        }

        try {
            return roleService.receiveMsg(run.mone.hive.schema.Message.builder()
                            .clientId(clientId)
                            .role("user")
                            .sentFrom(ownerId)
                            .content(message)
                            .data(message)
                            .build())
                    .map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
        } catch (Exception e) {
            String errorMessage = "Error: " + e.getMessage();
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(errorMessage)), true));
        }
    }

    public String getName() {
        return "stream_minzai_chat";
    }

    public String getDesc() {
        return "和minzai聊天，问问minzai问题。支持各种形式如：'minzai'、'请minzai告诉我'、'让minzai帮我看看'、'minzai你知道吗'等。支持上下文连续对话。使用 '/clear' 可以清空聊天历史。";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

}