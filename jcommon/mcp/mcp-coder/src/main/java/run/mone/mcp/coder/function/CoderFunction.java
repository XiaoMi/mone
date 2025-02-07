
package run.mone.mcp.coder.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.coder.service.CoderService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CoderFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private final CoderService coderService;
    private final ObjectMapper objectMapper;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["writeUnitTest", "findCodeIssues", "answerTechQuestion", "writeCode", "reviewCode", "designArchitecture", "convertLanguage"],
                        "description": "The coding operation to perform"
                    },
                    "code": {
                        "type": "string",
                        "description": "The code for operations that require existing code"
                    },
                    "question": {
                        "type": "string",
                        "description": "The technical question to be answered"
                    },
                    "description": {
                        "type": "string",
                        "description": "The description of the code to be written or the architecture to be designed"
                    },
                    "sourceLanguage": {
                        "type": "string",
                        "description": "The source programming language for language conversion"
                    },
                    "targetLanguage": {
                        "type": "string",
                        "description": "The target programming language for language conversion"
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
                case "writeUnitTest" -> coderService.writeUnitTest((String) arguments.get("code"));
                case "findCodeIssues" -> coderService.findCodeIssues((String) arguments.get("code"));
                case "answerTechQuestion" -> coderService.answerTechQuestion((String) arguments.get("question"));
                case "writeCode" -> coderService.writeCode((String) arguments.get("description"));
                case "reviewCode" -> coderService.reviewCode((String) arguments.get("code"));
                case "designArchitecture" -> coderService.designArchitecture((String) arguments.get("description"));
                case "convertLanguage" -> coderService.convertLanguage((String) arguments.get("code"), (String) arguments.get("sourceLanguage"), (String) arguments.get("targetLanguage"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    public String getName() {
        return "coderOperation";
    }

    public String getDesc() {
        return "Perform various coding operations including writing unit tests, finding code issues, answering technical questions, writing code, reviewing code, designing architecture, and converting between programming languages.";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
