package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.utils.RemoteFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * File reading tool for examining the contents of existing files
 * 
 * This tool is used to read and examine the contents of files at specified paths.
 * It supports various file types including text files, source code, configuration files,
 * and can handle binary files by returning their Base64 encoded content.
 * 
 * Use this tool when you need to:
 * - Analyze code in existing files
 * - Review text files or configuration files
 * - Extract information from files
 * - Examine file contents before making modifications
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ReadFileTool implements ITool {

    public static final String name = "read_file";

    private final boolean isRemote;

    public ReadFileTool() {
        this(false);
    }

    public ReadFileTool(boolean isRemote) {
        this.isRemote = isRemote;
    }

    // Common text file extensions
    private static final List<String> TEXT_EXTENSIONS = Arrays.asList(
        ".txt", ".md", ".json", ".xml", ".yml", ".yaml", ".properties", ".conf", ".config",
        ".java", ".js", ".ts", ".tsx", ".jsx", ".py", ".rb", ".php", ".go", ".rs", ".c", ".cpp", ".h", ".hpp",
        ".css", ".scss", ".sass", ".less", ".html", ".htm", ".svg", ".sql", ".sh", ".bat", ".ps1",
        ".dockerfile", ".gitignore", ".gitattributes", ".editorconfig", ".prettierrc", ".eslintrc"
    );

    // Binary file extensions that should be handled as Base64
    private static final List<String> BINARY_EXTENSIONS = Arrays.asList(
        ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp", ".ico", ".tiff",
        ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx",
        ".zip", ".rar", ".7z", ".tar", ".gz", ".exe", ".dll", ".so", ".dylib"
    );

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                Request to read the contents of a file at the specified path. Use this when you need to 
                examine the contents of an existing file you do not know the contents of, for example to 
                analyze code, review text files, or extract information from configuration files.
                
                **When to use this tool:**
                - Analyze existing source code files
                - Review configuration files or settings
                - Extract information from text-based files
                - Examine file contents before making modifications
                - Read documentation or README files
                - Check log files or data files
                
                **File type support:**
                - Text files: Returns content as readable text
                - Source code: Supports all common programming languages
                - Configuration files: JSON, XML, YAML, Properties, etc.
                - Binary files: Returns Base64 encoded content with metadata
                
                **Important notes:**
                - Only use this tool on files, NOT directories
                - File path should be relative to current working directory
                - For large files, content may be truncated for readability
                - Binary files are automatically detected and handled appropriately
                """;
    }

    @Override
    public String parameters() {
        return """
                - path: (required) The path of the file to read (relative to the current working directory)
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
            <task_progress>
            Checklist here (optional)
            </task_progress>
            """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
            <read_file>
            <path>File path here</path>
            %s
            </read_file>
            """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: Read a Java source file
                <read_file>
                <path>src/main/java/com/example/UserService.java</path>
                </read_file>
                
                Example 2: Read a configuration file
                <read_file>
                <path>src/main/resources/application.properties</path>
                </read_file>
                
                Example 3: Read a JSON configuration
                <read_file>
                <path>config/database.json</path>
                </read_file>
                
                Example 4: Read a README file
                <read_file>
                <path>README.md</path>
                </read_file>
                
                Example 5: Read a script file
                <read_file>
                <path>scripts/build.sh</path>
                </read_file>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // Validate required parameters
            if (!inputJson.has("path") || StringUtils.isBlank(inputJson.get("path").getAsString())) {
                log.error("read_file operation missing required path parameter");
                result.addProperty("error", "Missing required parameter 'path'");
                return result;
            }

            String path = inputJson.get("path").getAsString();
            return performReadFile(path);

        } catch (Exception e) {
            log.error("Exception occurred while executing read_file operation", e);
            result.addProperty("error", "Failed to execute read_file operation: " + e.getMessage());
            return result;
        }
    }

    /**
     * Perform the file reading operation
     */
    private JsonObject performReadFile(String path) {
        JsonObject result = new JsonObject();

        if (!isRemote) {
            try {
                Path filePath = Paths.get(path);
                File file = filePath.toFile();

                // Check if file exists
                if (!file.exists()) {
                    log.error("File not found: {}", path);
                    result.addProperty("error", "File not found: " + path);
                    return result;
                }

                // Check if path points to a directory
                if (file.isDirectory()) {
                    log.error("Path points to a directory, not a file: {}", path);
                    result.addProperty("error", "Path points to a directory, not a file: " + path +
                            ". Use this tool only on files, not directories.");
                    return result;
                }

                // Check file permissions
                if (!file.canRead()) {
                    log.error("File is not readable: {}", path);
                    result.addProperty("error", "File is not readable: " + path);
                    return result;
                }

                // Get file information
                long fileSize = file.length();
                String fileExtension = getFileExtension(path);
                String fileType = determineFileType(fileExtension);

                // Handle different file types
                if (isBinaryFile(fileExtension)) {
                    return handleBinaryFile(filePath, fileSize, fileType, result);
                } else {
                    return handleTextFile(filePath, fileSize, fileType, result);
                }


            } catch (IOException e) {
                log.error("IO exception while reading file: {}", path, e);
                result.addProperty("error", "Failed to read file: " + e.getMessage());
            } catch (Exception e) {
                log.error("Exception while reading file: {}", path, e);
                result.addProperty("error", "Error reading file: " + e.getMessage());
            }
        }

        if (isRemote) {
            try {
                log.info("Reading remote file: {}", path);

                // 获取远程文件内容
                String content = RemoteFileUtils.getRemoteFileContent(path);

                // 确定文件类型
                String fileExtension = getFileExtension(path);
                String fileType = determineFileType(fileExtension);

                // 设置响应结果
                result.addProperty("result", content);
                result.addProperty("fileType", fileType);
                result.addProperty("encoding", "UTF-8");

                log.info("Successfully read remote file: {}, type: {}", path, fileType);

                return result;
            } catch (IOException e) {
                log.error("Failed to read remote file: {}", path, e);
                result.addProperty("error", "Failed to read remote file: " + e.getMessage());
                return result;
            }
        }

        return result;
    }

    /**
     * Handle reading text files
     */
    private JsonObject handleTextFile(Path filePath, long fileSize, String fileType, JsonObject result) throws IOException {
        // For very large files, we might want to limit the content
        final long MAX_TEXT_FILE_SIZE = 1024 * 1024; // 1MB
        
        if (fileSize > MAX_TEXT_FILE_SIZE) {
            log.warn("File is quite large ({} bytes), reading anyway but content might be truncated", fileSize);
        }

        String content = Files.readString(filePath, StandardCharsets.UTF_8);
        
        // Truncate content if it's extremely large
        final int MAX_CONTENT_LENGTH = 100000; // 100KB of text
        boolean wasTruncated = false;
        if (content.length() > MAX_CONTENT_LENGTH) {
            content = content.substring(0, MAX_CONTENT_LENGTH) + "\n\n[Content truncated due to size limit...]";
            wasTruncated = true;
        }

        result.addProperty("result", content);
        result.addProperty("fileType", fileType);
        result.addProperty("fileSize", fileSize);
        result.addProperty("contentLength", content.length());
        result.addProperty("wasTruncated", wasTruncated);
        result.addProperty("encoding", "UTF-8");

        log.info("Successfully read text file: {}, type: {}, size: {} bytes", 
                filePath, fileType, fileSize);

        return result;
    }

    /**
     * Handle reading binary files
     */
    private JsonObject handleBinaryFile(Path filePath, long fileSize, String fileType, JsonObject result) throws IOException {
        // For binary files, we provide Base64 encoded content and metadata
        final long MAX_BINARY_FILE_SIZE = 10 * 1024 * 1024; // 10MB
        
        if (fileSize > MAX_BINARY_FILE_SIZE) {
            result.addProperty("error", 
                "Binary file is too large to read (" + fileSize + " bytes). " +
                "Maximum supported size is " + MAX_BINARY_FILE_SIZE + " bytes.");
            return result;
        }

        byte[] fileContent = Files.readAllBytes(filePath);
        String base64Content = Base64.getEncoder().encodeToString(fileContent);

        result.addProperty("result", "Binary file content (Base64 encoded):\n" + base64Content);
        result.addProperty("fileType", fileType);
        result.addProperty("fileSize", fileSize);
        result.addProperty("encoding", "Base64");
        result.addProperty("isBinary", true);
        result.addProperty("base64Content", base64Content);

        log.info("Successfully read binary file: {}, type: {}, size: {} bytes", 
                filePath, fileType, fileSize);

        return result;
    }

    /**
     * Get file extension from path
     */
    private String getFileExtension(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < path.length() - 1) {
            return path.substring(lastDotIndex).toLowerCase();
        }
        return "";
    }

    /**
     * Determine file type based on extension
     */
    private String determineFileType(String extension) {
        if (extension.isEmpty()) {
            return "unknown";
        }

        return switch (extension) {
            case ".java" -> "Java source code";
            case ".js", ".jsx" -> "JavaScript";
            case ".ts", ".tsx" -> "TypeScript";
            case ".py" -> "Python";
            case ".rb" -> "Ruby";
            case ".php" -> "PHP";
            case ".go" -> "Go";
            case ".rs" -> "Rust";
            case ".c" -> "C source code";
            case ".cpp", ".cc", ".cxx" -> "C++ source code";
            case ".h", ".hpp" -> "Header file";
            case ".css" -> "CSS stylesheet";
            case ".scss", ".sass" -> "Sass stylesheet";
            case ".less" -> "Less stylesheet";
            case ".html", ".htm" -> "HTML document";
            case ".xml" -> "XML document";
            case ".json" -> "JSON data";
            case ".yml", ".yaml" -> "YAML configuration";
            case ".properties" -> "Properties file";
            case ".conf", ".config" -> "Configuration file";
            case ".md" -> "Markdown document";
            case ".txt" -> "Text file";
            case ".sql" -> "SQL script";
            case ".sh" -> "Shell script";
            case ".bat" -> "Batch script";
            case ".ps1" -> "PowerShell script";
            case ".dockerfile" -> "Docker configuration";
            case ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp" -> "Image file";
            case ".pdf" -> "PDF document";
            case ".doc", ".docx" -> "Word document";
            case ".xls", ".xlsx" -> "Excel spreadsheet";
            case ".zip", ".rar", ".7z", ".tar", ".gz" -> "Archive file";
            default -> "file (" + extension + ")";
        };
    }

    /**
     * Check if file should be treated as binary
     */
    private boolean isBinaryFile(String extension) {
        return BINARY_EXTENSIONS.contains(extension);
    }

    /**
     * Check if file is a known text file type
     */
    private boolean isTextFile(String extension) {
        return TEXT_EXTENSIONS.contains(extension);
    }

    /**
     * Validate file path for security
     */
    private boolean isPathSafe(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        
        // Check for path traversal attacks
        if (path.contains("..") || path.contains("~")) {
            return false;
        }
        
        // Check for absolute paths (may be unsafe in some contexts)
        if (path.startsWith("/") || (path.length() > 1 && path.charAt(1) == ':')) {
            log.warn("Detected absolute path, consider using relative path: {}", path);
        }
        
        return true;
    }

    /**
     * Get file size in human readable format
     */
    private String getHumanReadableFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
}
