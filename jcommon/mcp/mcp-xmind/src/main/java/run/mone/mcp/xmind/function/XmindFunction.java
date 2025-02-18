package run.mone.mcp.xmind.function;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.xmind.service.XMindParser;
import run.mone.mcp.xmind.service.model.XMindNode;

@Data
@Slf4j
public class XmindFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "xmind_executor";
    private String desc = "Parse and analyze XMind files: Extract complete mind map structure in JSON format";
    private ObjectMapper objectMapper;

    private String xmindToolSchema = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["parse"],
                        "description": "Xmind operations"
                    },
                    "path": {
                        "type": "string",
                        "description": "The path of Xmind file."
                    }
                },
                "required": ["operation", "path"]
            }
            """;


    public XmindFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> params) {

        String operation = (String) params.get("operation");

        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation is required");
        }

        String result;
        try {
            result = switch (operation) {
                case "parse" -> parse((String) params.get("path"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };
            
            log.info("Xmind operation completed successfully: {}", operation);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result)),
                    false
            );

        } catch (Exception e) {
            log.error("Error executing xmind operation", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }

    private String parse(String path) throws IOException {
        XMindParser parser = new XMindParser(path);
        List<XMindNode> nodes = parser.parse();
        return objectMapper.writeValueAsString(nodes);
    }
}