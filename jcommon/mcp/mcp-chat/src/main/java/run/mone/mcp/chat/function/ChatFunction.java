package run.mone.mcp.chat.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chat.service.ChatService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ChatFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "message": {
                        "type": "string",
                        "description": "The message content from user"
                    },
                    "context": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "role": {
                                    "type": "string",
                                    "enum": ["user", "assistant"]
                                },
                                "content": {
                                    "type": "string"
                                }
                            }
                        },
                        "description": "Previous chat history"
                    }
                },
                "required": ["message"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String message = (String) arguments.get("message");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> context = (List<Map<String, String>>) arguments.get("context");

        return Flux.defer(() -> {
            try {
                Flux<String> result = chatService.chat(message, context);
                return result.map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
            } catch (Exception e) {
                return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
            }
        });
    }

    public String getName() {
        return "xiaobao_chat";
    }

    public String getDesc() {
        return "和小包聊天，问问小包问题。支持各种形式如：'问问小包'、'请小包告诉我'、'让小包帮我看看'、'小包你知道吗'等。支持上下文连续对话。";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}