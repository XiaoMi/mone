
package run.mone.hive.mcp.demo.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Slf4j
public class FileOperationFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "fileOperation";

    private String desc = "File operations including create, delete, read, modify, and list files";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["create", "delete", "read", "modify", "list"],
                       "description":"The operation to perform on the file (create, delete, read, modify, or list)"
                    },
                    "path": {
                        "type": "string",
                        "description":"The file path or directory path for the operation"
                    },
                    "content": {
                        "type": "string",
                        "description":"The content to write to the file (required for create and modify operations)"
                    }
                },
                "required": ["operation", "path"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        String path = (String) arguments.get("path");
        String content = (String) arguments.get("content");

        log.info("operation:{} path:{}", operation, path);

        try {
            String result = switch (operation) {
                case "create" -> createFile(path, content);
                case "delete" -> deleteFile(path);
                case "read" -> readFile(path);
                case "modify" -> modifyFile(path, content);
                case "list" -> listFiles(path);
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (IOException e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    private String createFile(String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes());
        return "File created successfully: " + path;
    }

    private String deleteFile(String path) throws IOException {
        Files.delete(Paths.get(path));
        return "File deleted successfully: " + path;
    }

    private String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    private String modifyFile(String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes());
        return "File modified successfully: " + path;
    }

    private String listFiles(String path) throws IOException {
        return Files.list(Paths.get(path))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.joining("\n"));
    }
}
