
package run.mone.mcp.filesystem.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Slf4j
public class FilesystemFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "filesystem_executor";
    private String desc = "Execute filesystem operations";
    private List<String> allowedDirectories;
    private ObjectMapper objectMapper;

    private String filesystemToolSchema = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["read_file", "read_multiple_files", "write_file", "edit_file", "create_directory", "list_directory", "directory_tree", "move_file", "search_files", "get_file_info", "list_allowed_directories"],
                        "description": "Type of filesystem operation to execute"
                    },
                    "path": {
                        "type": "string",
                        "description": "File or directory path"
                    },
                    "paths": {
                        "type": "array",
                        "items": {"type": "string"},
                        "description": "Array of file paths for read_multiple_files operation"
                    },
                    "content": {
                        "type": "string",
                        "description": "Content to write to file"
                    },
                    "edits": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "oldText": {"type": "string"},
                                "newText": {"type": "string"}
                            },
                            "required": ["oldText", "newText"]
                        },
                        "description": "Array of edit operations for edit_file operation"
                    },
                    "dryRun": {
                        "type": "boolean",
                        "description": "Preview changes without applying them"
                    },
                    "source": {
                        "type": "string",
                        "description": "Source path for move_file operation"
                    },
                    "destination": {
                        "type": "string",
                        "description": "Destination path for move_file operation"
                    },
                    "pattern": {
                        "type": "string",
                        "description": "Search pattern for search_files operation"
                    },
                    "excludePatterns": {
                        "type": "array",
                        "items": {"type": "string"},
                        "description": "Patterns to exclude in search_files operation"
                    }
                },
                "required": ["operation"]
            }
            """;

    public FilesystemFunction(List<String> allowedDirectories, ObjectMapper objectMapper) {
        this.allowedDirectories = allowedDirectories.stream()
                .map(this::normalizePath)
                .collect(Collectors.toList());
        this.objectMapper = objectMapper;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String operation = (String) args.get("operation");
        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation is required");
        }

        try {
            switch (operation) {
                case "read_file":
                    return readFile((String) args.get("path"));
                case "read_multiple_files":
                    return readMultipleFiles((List<String>) args.get("paths"));
                case "write_file":
                    return writeFile((String) args.get("path"), (String) args.get("content"));
                case "edit_file":
                    return editFile((String) args.get("path"), (List<Map<String, String>>) args.get("edits"), (Boolean) args.get("dryRun"));
                case "create_directory":
                    return createDirectory((String) args.get("path"));
                case "list_directory":
                    return listDirectory((String) args.get("path"));
                case "directory_tree":
                    return directoryTree((String) args.get("path"));
                case "move_file":
                    return moveFile((String) args.get("source"), (String) args.get("destination"));
                case "search_files":
                    return searchFiles((String) args.get("path"), (String) args.get("pattern"), (List<String>) args.get("excludePatterns"));
                case "get_file_info":
                    return getFileInfo((String) args.get("path"));
                case "list_allowed_directories":
                    return listAllowedDirectories();
                default:
                    throw new IllegalArgumentException("Unsupported operation: " + operation);
            }
        } catch (Exception ex) {
            log.error("Error executing filesystem operation", ex);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + ex.getMessage())),
                    true
            );
        }
    }

    private String normalizePath(String path) {
        return Paths.get(path).normalize().toString();
    }

    private Path validatePath(String requestedPath) throws IOException {
        Path path = Paths.get(requestedPath).normalize().toAbsolutePath();
        if (!allowedDirectories.stream().anyMatch(dir -> path.startsWith(dir))) {
            throw new SecurityException("Access denied - path outside allowed directories: " + path);
        }
        return Paths.get(requestedPath);
    }

    private McpSchema.CallToolResult readFile(String path) throws IOException {
        Path validPath = validatePath(path);
        String content = Files.readString(validPath);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(content)),
                false
        );
    }

    private McpSchema.CallToolResult readMultipleFiles(List<String> paths) {
        StringBuilder result = new StringBuilder();
        for (String path : paths) {
            try {
                Path validPath = validatePath(path);
                String content = Files.readString(validPath);
                result.append(path).append(":\n").append(content).append("\n---\n");
            } catch (Exception e) {
                result.append(path).append(": Error - ").append(e.getMessage()).append("\n---\n");
            }
        }
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(result.toString())),
                false
        );
    }

    private McpSchema.CallToolResult writeFile(String path, String content) throws IOException {
        Path validPath = validatePath(path);
        Files.writeString(validPath, content);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Successfully wrote to " + path)),
                false
        );
    }

    private McpSchema.CallToolResult editFile(String path, List<Map<String, String>> edits, Boolean dryRun) throws IOException {
        Path validPath = validatePath(path);
        String content = Files.readString(validPath);
        StringBuilder diff = new StringBuilder();

        for (Map<String, String> edit : edits) {
            String oldText = edit.get("oldText");
            String newText = edit.get("newText");
            if (content.contains(oldText)) {
                diff.append("--- ").append(path).append("\n");
                diff.append("+++ ").append(path).append("\n");
                diff.append("@@ -1,1 +1,1 @@\n");
                diff.append("-").append(oldText).append("\n");
                diff.append("+").append(newText).append("\n");
                content = content.replace(oldText, newText);
            } else {
                throw new IllegalArgumentException("Could not find exact match for edit:\n" + oldText);
            }
        }

        if (!dryRun) {
            Files.writeString(validPath, content);
        }

        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(diff.toString())),
                false
        );
    }

    private McpSchema.CallToolResult createDirectory(String path) throws IOException {
        Path validPath = validatePath(path);
        Files.createDirectories(validPath);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Successfully created directory " + path)),
                false
        );
    }

    private McpSchema.CallToolResult listDirectory(String path) throws IOException {
        Path validPath = validatePath(path);
        List<String> entries = Files.list(validPath)
                .map(p -> (Files.isDirectory(p) ? "[DIR] " : "[FILE] ") + p.getFileName())
                .sorted()
                .collect(Collectors.toList());
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(String.join("\n", entries))),
                false
        );
    }

    private McpSchema.CallToolResult directoryTree(String path) throws IOException {
        Path validPath = validatePath(path);
        Map<String, Object> tree = buildDirectoryTree(validPath);
        String jsonTree = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(jsonTree)),
                false
        );
    }

    private Map<String, Object> buildDirectoryTree(Path path) throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", path.getFileName().toString());
        result.put("type", Files.isDirectory(path) ? "directory" : "file");

        if (Files.isDirectory(path)) {
            List<Map<String, Object>> children = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    children.add(buildDirectoryTree(entry));
                }
            }
            result.put("children", children);
        }

        return result;
    }

    private McpSchema.CallToolResult moveFile(String source, String destination) throws IOException {
        Path validSourcePath = validatePath(source);
        Path validDestPath = validatePath(destination);
        Files.move(validSourcePath, validDestPath);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Successfully moved " + source + " to " + destination)),
                false
        );
    }

    private McpSchema.CallToolResult searchFiles(String path, String pattern, List<String> excludePatterns) throws IOException {
        Path validPath = validatePath(path);
        List<String> results = Files.walk(validPath)
                .filter(p -> {
                    String relativePath = validPath.relativize(p).toString();
                    return p.getFileName().toString().toLowerCase().contains(pattern.toLowerCase()) &&
                            (excludePatterns == null || excludePatterns.stream().noneMatch(relativePath::matches));
                })
                .map(Path::toString)
                .collect(Collectors.toList());

        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(results.isEmpty() ? "No matches found" : String.join("\n", results))),
                false
        );
    }

    private McpSchema.CallToolResult getFileInfo(String path) throws IOException {
        Path validPath = validatePath(path);
        BasicFileAttributes attrs = Files.readAttributes(validPath, BasicFileAttributes.class);
        Map<String, String> info = new LinkedHashMap<>();
        info.put("size", String.valueOf(attrs.size()));
        info.put("created", attrs.creationTime().toString());
        info.put("modified", attrs.lastModifiedTime().toString());
        info.put("accessed", attrs.lastAccessTime().toString());
        info.put("isDirectory", String.valueOf(attrs.isDirectory()));
        info.put("isFile", String.valueOf(attrs.isRegularFile()));
        info.put("permissions", Files.getPosixFilePermissions(validPath).toString());

        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(info.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue())
                        .collect(Collectors.joining("\n")))),
                false
        );
    }

    private McpSchema.CallToolResult listAllowedDirectories() {
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Allowed directories:\n" + String.join("\n", allowedDirectories))),
                false
        );
    }
}
