
package run.mone.mcp.writer.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.writer.service.WriterService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class WriterFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private final WriterService writerService;
    private final ObjectMapper objectMapper;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["expandArticle", "summarizeArticle", "writeNewArticle", "polishArticle", "suggestImprovements", "createOutline", "editArticle"],
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
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        String result;

        try {
            result = switch (operation) {
                case "expandArticle" -> writerService.expandArticle((String) arguments.get("article"));
                case "summarizeArticle" -> writerService.summarizeArticle((String) arguments.get("article"));
                case "writeNewArticle" -> writerService.writeNewArticle((String) arguments.get("topic"));
                case "polishArticle" -> writerService.polishArticle((String) arguments.get("article"));
                case "suggestImprovements" -> writerService.suggestImprovements((String) arguments.get("article"));
                case "createOutline" -> writerService.createOutline((String) arguments.get("topic"));
                case "editArticle" -> writerService.editArticle((String) arguments.get("article"), (String) arguments.get("instructions"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    public String getName() {
        return "writerOperation";
    }

    public String getDesc() {
        return "Perform various writing operations including expanding, summarizing, writing new articles, polishing, suggesting improvements, creating outlines, and editing articles.";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
