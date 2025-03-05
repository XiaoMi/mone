package run.mone.mcp.writer.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.writer.service.WriterService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class WriterFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private final WriterService writerService;
    private final ObjectMapper objectMapper;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["expandArticle", "summarizeArticle", "writeNewArticle", "polishArticle", "suggestImprovements", "createOutline", "editArticle", "translateText"],
                        "description": "The writing operation to perform"
                    },
                    "article": {
                        "type": "string",
                        "description": "The article content for operations that require an existing article"
                    },
                    "topic": {
                        "type": "string",
                        "description": "The topic for writing a new article or creating an outline"
                    },
                    "instructions": {
                        "type": "string",
                        "description": "Editing instructions for the editArticle operation"
                    }
                },
                "required": ["operation"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");

        return Flux.defer(() -> {
            try {
                Flux<String> result = switch (operation) {
                    case "expandArticle" -> writerService.expandArticle((String) arguments.get("article"));
                    case "summarizeArticle" -> writerService.summarizeArticle((String) arguments.get("article"));
                    case "writeNewArticle" -> writerService.writeNewArticle((String) arguments.get("topic"));
                    case "polishArticle" -> writerService.polishArticle((String) arguments.get("article"));
                    case "suggestImprovements" -> writerService.suggestImprovements((String) arguments.get("article"));
                    case "createOutline" -> writerService.createOutline((String) arguments.get("topic"));
                    case "editArticle" -> writerService.editArticle((String) arguments.get("article"), (String) arguments.get("instructions"));
                    case "translateText" -> writerService.translateText((String) arguments.get("text"), (String) arguments.get("targetLanguage"));
                    default -> throw new IllegalArgumentException("Unknown operation: " + operation);
                };
                return result.map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
            } catch (Exception e) {
                return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
            }
        });
    }

    public String getName() {
        return "stream_writer";
    }

    public String getDesc() {
        return "Perform various writing operations including expanding, summarizing, writing new articles, polishing, suggesting improvements, creating outlines, editing articles, and translating text.";
    }


    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}