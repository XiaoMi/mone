package run.mone.mcp.chat.function;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chat.server.RoleService;
import run.mone.mcp.chat.service.ChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ChatFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private final ChatService chatService;

    private final ConcurrentHashMap<String, List<Message>> history = new ConcurrentHashMap<>();

    private final RoleService roleService;

    private boolean useAgent = true;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "message": {
                        "type": "string",
                        "description": "The message content from user"
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
        String clientId = arguments.get(Const.CLIENT_ID).toString();

        String message = (String) arguments.get("message");
        String context = (String) arguments.get("context");
        if (StringUtils.isEmpty(context)) {
            context = "";
        }

        // Get or create history list for this client
        List<Message> clientHistory = history.computeIfAbsent(clientId, k -> new ArrayList<>());

        // Add user message to client history
        clientHistory.add(new Message("user", message));

        try {
            //使用agent 模式
            if (useAgent) {
                return roleService.receiveMsg(run.mone.hive.schema.Message.builder().role("user").content(message).data(message).build()).map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
            }

            StringBuilder sb = new StringBuilder();
            Flux<String> result = chatService.chat(message, context);
            return result.doOnNext(sb::append).map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false)).doOnComplete(() -> clientHistory.add(new Message("assistant", sb.toString())));
        } catch (Exception e) {
            String errorMessage = "Error: " + e.getMessage();
            clientHistory.add(new Message("assistant", errorMessage));
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(errorMessage)), true));
        }
    }

    public String getName() {
        return "stream_minzai_chat";
    }

    public String getDesc() {
        return "和minzai聊天，问问minzai问题。支持各种形式如：'minzai'、'请minzai告诉我'、'让minzai帮我看看'、'minzai你知道吗'等。支持上下文连续对话。";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    public List<Message> getClientHistory(String clientId) {
        return history.getOrDefault(clientId, new ArrayList<>());
    }

    public Map<String, List<Message>> getAllHistory() {
        return history;
    }
}