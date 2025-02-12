package run.mone.mcp.word.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.word.service.WordService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class WordFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {


    private final WordService wordService;
    private String name = "word";
    private String description = "word operations including input text,insert image,save to local,word count,etc";

    private String toolScheme = """
        {
            "type": "object",
            "properties": {
                "operation": {
                    "type": "string",
                    "enum": ["inputText","deleteText", "createWord", "wordCount"],
                    "description": "The operation to perform on Word"
                },
                "fileName": {
                    "type": "string",
                    "description": "Name of the Word file"
                },
                "textTitle": {
                    "type": "string",
                    "description": "Title of the text"
                },
                "filePath": {
                        "type": "string",
                        "description": "Path to the Word file"
                },
                "text": {
                        "type": "string",
                        "description": "The text to be inputted into the Word document"
                }
            },
            "required": ["operation", "filePath"]
        }
        """;

    @Autowired
    public WordFunction(WordService wordService) {
        this.wordService = wordService;
    }
    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        String filePath = (String) arguments.get("filePath");

        try {
            String result = switch (operation) {
                case "createWord" -> wordService.createWord(
                        filePath,
                        (String) arguments.get("fileName"),
                        (String) arguments.get("textTitle")
                );
                case "wordCount" -> wordService.countWords(filePath);
                case "inputText" -> wordService.inputText(filePath, (String) arguments.get("text"));
                case "deleteText" -> wordService.deleteText(filePath);
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            log.error("Error executing Word operation", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }
}
