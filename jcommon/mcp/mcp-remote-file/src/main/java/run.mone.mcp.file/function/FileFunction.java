package run.mone.mcp.file.function;

import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClientV2;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class FileFunction implements McpFunction {

    private String name = "stream_remote_file";

    private String desc = "Perform remote file operations including upload, list, delete, createDir, deleteDir and get download URL";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "description": "Operation to perform: 'upload', 'list', 'delete', 'createDir', 'deleteDir', 'download'",
                        "enum": ["upload", "list", "delete", "createDir", "deleteDir", "download"]
                    },
                    "fileName": {
                        "type": "string",
                        "description": "Name of the file to operate on"
                    },
                    "fileBase64": {
                        "type": "string",
                        "description": "Base64 encoded file content for upload operation"
                    },
                    "directoryPath": {
                        "type": "string",
                        "description": "Path of the directory to operate on for directory operations"
                    }
                },
                "required": ["operation"]
            }
            """;

    private static final Gson gson = new Gson();

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        return Flux.defer(() -> {
            try {
                String operation = (String) arguments.get("operation");
                String fileName = (String) arguments.get("fileName");
                log.info("FileFunction operation:{},fileName:{}", operation, fileName);
                
                String result;
                switch (operation) {
                    case "upload":
                        String fileContent = (String) arguments.get("fileBase64");
                        result = uploadFile(fileName, fileContent);
                        break;
                    case "list":
                        result = listFiles(fileName);
                        break;
                    case "delete":
                        result = deleteFile(fileName);
                        break;
                    case "createDir":
                        String dirPath = (String) arguments.get("directoryPath");
                        result = createDirectory(dirPath);
                        break;
                    case "deleteDir":
                        dirPath = (String) arguments.get("directoryPath");
                        result = deleteDirectory(dirPath);
                        break;
                    case "download":
                        result = getDownloadUrl(fileName);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported operation: " + operation);
                }
                
                return Flux.just(
                        new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false),
                        new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("[DONE]")), false)
                );
            } catch (Exception e) {
                log.error("Failed to perform file operation", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Operation failed: " + e.getMessage())), true));
            }
        });
    }

    private String uploadFile(String fileName, String fileContent) throws IOException {
        if (fileContent == null || fileContent.isEmpty()) {
            throw new IOException("File content cannot be empty");
        }
        
        String url = String.format("%s/upload?name=%s&userKey=%s&userSecret=%s&token=%s", 
            getHost(), 
            fileName,
            getUserKey(),
            getUserSecret(),
            getToken());
        byte[] content = java.util.Base64.getDecoder().decode(fileContent);
        HttpClientV2.upload(url, content);
        return "File uploaded successfully";
    }

    private String listFiles(String fileName) throws IOException {
        String url = String.format("%s/list?name=%s&userKey=%s&userSecret=%s&token=%s", 
            getHost(), 
            fileName,
            getUserKey(),
            getUserSecret(),
            getToken());
        return HttpClientV2.get(url, Map.of());
    }

    private String deleteFile(String fileName) throws IOException {
        String url = String.format("%s/delete?name=%s&userKey=%s&userSecret=%s&token=%s", 
            getHost(), 
            fileName,
            getUserKey(),
            getUserSecret(),
            getToken());
        return HttpClientV2.get(url, Map.of());
    }

    private String createDirectory(String directoryPath) throws IOException {
        String url = String.format("%s/createDir?directory=%s&userKey=%s&userSecret=%s&token=%s", 
            getHost(), 
            directoryPath,
            getUserKey(),
            getUserSecret(),
            getToken());
        return HttpClientV2.get(url, Map.of());
    }

    private String deleteDirectory(String directoryPath) throws IOException {
        String url = String.format("%s/deleteDir?directory=%s&userKey=%s&userSecret=%s&token=%s", 
            getHost(), 
            directoryPath,
            getUserKey(),
            getUserSecret(),
            getToken());
        return HttpClientV2.get(url, Map.of());
    }

    private String getDownloadUrl(String fileName) {
        String url = String.format("%s/download?name=%s&userKey=%s&userSecret=%s&token=%s", 
            getHost(), 
            fileName,
            getUserKey(),
            getUserSecret(),
            getToken());
        return String.format("<download_file fileName=\"%s\" fileUrl=\"%s\">%s</download_file>", fileName, url, url);
    }

    private String getHost() {
        return System.getenv().getOrDefault("API_HOST", "http://127.0.0.1:9777");
    }

    private String getUserKey() {
        return System.getenv().getOrDefault("USER_KEY", "wangmin");
    }

    private String getUserSecret() {
        return System.getenv().getOrDefault("USER_SECRET", "123456");
    }

    private String getToken() {
        return System.getenv().getOrDefault("API_TOKEN", "1");
    }
}
