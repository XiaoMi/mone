package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.utils.RemoteFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Directory listing tool for exploring file system structure
 * <p>
 * This tool lists files and directories within a specified directory, with support
 * for both recursive and non-recursive listing modes. It provides detailed information
 * about each file including size, type, and modification time.
 * <p>
 * Use this tool when you need to:
 * - Explore project structure and organization
 * - Understand directory contents before making changes
 * - Find specific files or patterns in directory trees
 * - Get an overview of codebase organization
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ListFilesTool implements ITool {

    public static final String name = "list_files";
    // Default directories to ignore during recursive listing
    private static final Set<String> DEFAULT_IGNORE_DIRECTORIES = Set.of(
            "node_modules", "__pycache__", "env", "venv", "target", "build",
            "dist", "out", "bundle", "vendor", "tmp", "temp", "deps", "Pods",
            ".git", ".svn", ".hg", ".idea", ".vscode", ".gradle", ".m2"
    );
    // Hidden file/directory prefixes to ignore (unless specifically targeting them)
    private static final Set<String> HIDDEN_PREFIXES = Set.of(".", "~");
    // Maximum number of files to return to prevent overwhelming output
    private static final int DEFAULT_LIMIT = 200;
    private static final int MAX_DEPTH = 10; // Prevent infinite recursion
    private final boolean isRemote;

    public ListFilesTool() {
        this(false);
    }
    public ListFilesTool(boolean isRemote) {
        this.isRemote = isRemote;
    }

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
                Request to list files and directories within the specified directory. If recursive is true, 
                it will list all files and directories recursively. If recursive is false or not provided, 
                it will only list the top-level contents.
                
                **When to use this tool:**
                - Explore project structure and organization
                - Understand directory contents before making changes
                - Find specific files or patterns in directory trees
                - Get an overview of codebase organization
                - Analyze file distribution and project layout
                
                **Important notes:**
                - Do not use this tool to confirm the existence of files you may have created
                - The user will let you know if files were created successfully or not
                - Recursive listing may be limited to prevent overwhelming output
                - Hidden files and common build directories are filtered for clarity
                - Results are sorted alphabetically for consistent output
                """;
    }

    @Override
    public String parameters() {
        return """
                - path: (required) The path of the directory to list contents for (relative to the current working directory)
                - recursive: (optional) Whether to list files recursively. Use true for recursive listing, false or omit for top-level only.
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
                <list_files>
                <path>Directory path here</path>
                <recursive>true or false (optional)</recursive>
                %s
                </list_files>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: List top-level contents of source directory
                <list_files>
                <path>src</path>
                </list_files>
                
                Example 2: Recursively list all files in project
                <list_files>
                <path>.</path>
                <recursive>true</recursive>
                </list_files>
                
                Example 3: List contents of specific package
                <list_files>
                <path>src/main/java/com/example</path>
                <recursive>false</recursive>
                </list_files>
                
                Example 4: Explore configuration directory
                <list_files>
                <path>config</path>
                <recursive>true</recursive>
                </list_files>
                
                Example 5: List documentation files
                <list_files>
                <path>docs</path>
                </list_files>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // Validate required parameters
            if (!inputJson.has("path") || StringUtils.isBlank(inputJson.get("path").getAsString())) {
                log.error("list_files operation missing required path parameter");
                result.addProperty("error", "Missing required parameter 'path'");
                return result;
            }

            String path = inputJson.get("path").getAsString();
            boolean recursive = inputJson.has("recursive") &&
                    "true".equalsIgnoreCase(inputJson.get("recursive").getAsString());

            if (isRemote) {
                return performRemoteListFiles(path, recursive);
            } else {
                return performListFiles(path, recursive);
            }

        } catch (Exception e) {
            log.error("Exception occurred while executing list_files operation", e);
            result.addProperty("error", "Failed to execute list_files operation: " + e.getMessage());
            return result;
        }
    }

    private JsonObject performRemoteListFiles(String path, boolean recursive) {
        JsonObject result = new JsonObject();

        try {
            String response = RemoteFileUtils.listFiles(path);

            // 解析响应并构建结果
            result.addProperty("result", response);
            result.addProperty("directoryPath", path);
            result.addProperty("recursive", recursive);

            log.info("Successfully listed remote files in directory: {}, recursive: {}", path, recursive);

            return result;
        } catch (IOException e) {
            log.error("IO exception while listing remote directory: {}", path, e);
            result.addProperty("error", "Failed to list remote directory: " + e.getMessage());
            return result;
        } catch (Exception e) {
            log.error("Exception while listing remote directory: {}", path, e);
            result.addProperty("error", "Error listing remote directory: " + e.getMessage());
            return result;
        }
    }

    /**
     * Perform the directory listing operation
     */
    private JsonObject performListFiles(String path, boolean recursive) {
        JsonObject result = new JsonObject();

        try {
            Path dirPath = Paths.get(path);
            File directory = dirPath.toFile();

            // Check if directory exists
            if (!directory.exists()) {
                log.error("Directory not found: {}", path);
                result.addProperty("error", "Directory not found: " + path);
                return result;
            }

            // Check if path points to a file instead of directory
            if (!directory.isDirectory()) {
                log.error("Path points to a file, not a directory: {}", path);
                result.addProperty("error", "Path points to a file, not a directory: " + path +
                        ". Use this tool only on directories.");
                return result;
            }

            // Check directory permissions
            if (!directory.canRead()) {
                log.error("Directory is not readable: {}", path);
                result.addProperty("error", "Directory is not readable: " + path);
                return result;
            }

            // Check for restricted paths (root, home directory)
            if (isRestrictedPath(dirPath)) {
                log.error("Access to restricted path denied: {}", path);
                result.addProperty("error", "Access to restricted path denied: " + path);
                return result;
            }

            // List files based on recursive flag
            List<FileInfo> files = recursive ?
                    listFilesRecursive(dirPath) :
                    listFilesTopLevel(dirPath);

            // Sort files for consistent output
            files.sort(Comparator.comparing(f -> f.relativePath));

            // Build result
            JsonArray fileArray = new JsonArray();
            for (FileInfo fileInfo : files) {
                JsonObject fileObj = new JsonObject();
                fileObj.addProperty("name", fileInfo.name);
                fileObj.addProperty("path", fileInfo.relativePath);
                fileObj.addProperty("type", fileInfo.isDirectory ? "directory" : "file");
                fileObj.addProperty("size", fileInfo.size);
                fileObj.addProperty("lastModified", fileInfo.lastModified);
                fileObj.addProperty("isDirectory", fileInfo.isDirectory);
                fileObj.addProperty("isHidden", fileInfo.isHidden);
                if (!fileInfo.isDirectory) {
                    fileObj.addProperty("extension", getFileExtension(fileInfo.name));
                }
                fileArray.add(fileObj);
            }

            result.add("files", fileArray);
            result.addProperty("totalCount", files.size());
            result.addProperty("directoryPath", path);
            result.addProperty("recursive", recursive);
            result.addProperty("wasLimited", files.size() >= DEFAULT_LIMIT);

            // Add summary statistics
            long totalSize = files.stream().mapToLong(f -> f.size).sum();
            long fileCount = files.stream().mapToLong(f -> f.isDirectory ? 0 : 1).sum();
            long dirCount = files.stream().mapToLong(f -> f.isDirectory ? 1 : 0).sum();

            JsonObject summary = new JsonObject();
            summary.addProperty("totalFiles", fileCount);
            summary.addProperty("totalDirectories", dirCount);
            summary.addProperty("totalSize", totalSize);
            summary.addProperty("totalSizeFormatted", formatFileSize(totalSize));
            result.add("summary", summary);

            // Generate formatted output for display
            String formattedOutput = formatFileList(files, path, recursive);
            result.addProperty("result", formattedOutput);

            log.info("Successfully listed {} items in directory: {}, recursive: {}",
                    files.size(), path, recursive);

            return result;

        } catch (IOException e) {
            log.error("IO exception while listing directory: {}", path, e);
            result.addProperty("error", "Failed to list directory: " + e.getMessage());
        } catch (Exception e) {
            log.error("Exception while listing directory: {}", path, e);
            result.addProperty("error", "Error listing directory: " + e.getMessage());
        }

        return result;
    }

    /**
     * List files in top-level directory only
     */
    private List<FileInfo> listFilesTopLevel(Path dirPath) throws IOException {
        List<FileInfo> files = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path entry : stream) {
                if (files.size() >= DEFAULT_LIMIT) {
                    break;
                }

                if (shouldIncludeFile(entry, dirPath, false)) {
                    files.add(createFileInfo(entry, dirPath));
                }
            }
        }

        return files;
    }

    /**
     * List files recursively with depth and count limits
     */
    private List<FileInfo> listFilesRecursive(Path dirPath) throws IOException {
        List<FileInfo> files = new ArrayList<>();
        Set<Path> visited = new HashSet<>(); // Prevent infinite loops with symlinks

        walkDirectoryRecursive(dirPath, dirPath, files, visited, 0);

        return files;
    }

    /**
     * Recursive directory walking with limits
     */
    private void walkDirectoryRecursive(Path currentPath, Path basePath, List<FileInfo> files,
                                        Set<Path> visited, int depth) throws IOException {

        if (files.size() >= DEFAULT_LIMIT || depth > MAX_DEPTH) {
            return;
        }

        // Prevent infinite loops with symlinks
        Path realPath = currentPath.toRealPath();
        if (visited.contains(realPath)) {
            return;
        }
        visited.add(realPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentPath)) {
            for (Path entry : stream) {
                if (files.size() >= DEFAULT_LIMIT) {
                    break;
                }

                if (shouldIncludeFile(entry, basePath, true)) {
                    files.add(createFileInfo(entry, basePath));

                    // Recurse into subdirectories
                    if (Files.isDirectory(entry) && !shouldIgnoreDirectory(entry)) {
                        walkDirectoryRecursive(entry, basePath, files, visited, depth + 1);
                    }
                }
            }
        } catch (AccessDeniedException e) {
            // Skip directories we can't access
            log.debug("Access denied to directory: {}", currentPath);
        }
    }

    /**
     * Create FileInfo object from Path
     */
    private FileInfo createFileInfo(Path filePath, Path basePath) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);

        String relativePath = basePath.relativize(filePath).toString();
        String name = filePath.getFileName().toString();
        boolean isDirectory = attrs.isDirectory();
        long size = isDirectory ? 0 : attrs.size();
        boolean isHidden = name.startsWith(".") || Files.isHidden(filePath);

        // Format last modified time
        Instant instant = attrs.lastModifiedTime().toInstant();
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        String lastModified = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new FileInfo(name, relativePath, isDirectory, size, lastModified, isHidden);
    }

    /**
     * Check if file should be included in listing
     */
    private boolean shouldIncludeFile(Path filePath, Path basePath, boolean recursive) {
        String fileName = filePath.getFileName().toString();

        // Always include if we're specifically targeting a hidden directory
        if (isTargetingHiddenDirectory(basePath)) {
            return true;
        }

        // For recursive listing, apply more filtering
        if (recursive) {
            // Skip hidden files unless specifically looking in hidden directory
            if (fileName.startsWith(".")) {
                return false;
            }

            // Skip common ignore directories
            return !Files.isDirectory(filePath) || !shouldIgnoreDirectory(filePath);
        }

        return true;
    }

    /**
     * Check if directory should be ignored during recursive listing
     */
    private boolean shouldIgnoreDirectory(Path dirPath) {
        String dirName = dirPath.getFileName().toString();
        return DEFAULT_IGNORE_DIRECTORIES.contains(dirName) || dirName.startsWith(".");
    }

    /**
     * Check if we're specifically targeting a hidden directory
     */
    private boolean isTargetingHiddenDirectory(Path path) {
        String dirName = path.getFileName().toString();
        return dirName.startsWith(".");
    }

    /**
     * Check if path is restricted (root, home directory)
     */
    private boolean isRestrictedPath(Path path) {
        try {
            Path absolutePath = path.toAbsolutePath().normalize();

            // Check if it's root directory
            Path root = absolutePath.getRoot();
            if (absolutePath.equals(root)) {
                return true;
            }

            // Check if it's home directory
            String homeDir = System.getProperty("user.home");
            if (homeDir != null && absolutePath.equals(Paths.get(homeDir))) {
                return true;
            }

        } catch (Exception e) {
            log.debug("Error checking restricted path: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Format file size in human readable format
     */
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }

    /**
     * Format file list for display
     */
    private String formatFileList(List<FileInfo> files, String path, boolean recursive) {
        StringBuilder sb = new StringBuilder();
        sb.append("Directory listing for: ").append(path);
        if (recursive) {
            sb.append(" (recursive)");
        }
        sb.append("\n");

        if (files.isEmpty()) {
            sb.append("(empty directory)");
            return sb.toString();
        }

        // Group by directories and files
        List<FileInfo> directories = files.stream()
                .filter(f -> f.isDirectory)
                .toList();
        List<FileInfo> regularFiles = files.stream()
                .filter(f -> !f.isDirectory)
                .toList();

        // List directories first
        if (!directories.isEmpty()) {
            sb.append("\nDirectories:\n");
            for (FileInfo dir : directories) {
                sb.append("  📁 ").append(dir.relativePath).append("/\n");
            }
        }

        // Then list files
        if (!regularFiles.isEmpty()) {
            sb.append("\nFiles:\n");
            for (FileInfo file : regularFiles) {
                String icon = getFileIcon(file.name);
                sb.append("  ").append(icon).append(" ").append(file.relativePath);
                sb.append(" (").append(formatFileSize(file.size)).append(")");
                sb.append("\n");
            }
        }

        // Add summary
        sb.append("\nSummary: ");
        sb.append(directories.size()).append(" directories, ");
        sb.append(regularFiles.size()).append(" files");

        if (files.size() >= DEFAULT_LIMIT) {
            sb.append(" (limited to ").append(DEFAULT_LIMIT).append(" items)");
        }

        return sb.toString();
    }

    /**
     * Get appropriate icon for file type
     */
    private String getFileIcon(String fileName) {
        String extension = getFileExtension(fileName);

        return switch (extension) {
            case "java" -> "☕";
            case "js", "jsx" -> "🟨";
            case "ts", "tsx" -> "🔷";
            case "py" -> "🐍";
            case "json" -> "📋";
            case "xml" -> "📄";
            case "yml", "yaml" -> "⚙️";
            case "md" -> "📝";
            case "txt" -> "📄";
            case "html", "htm" -> "🌐";
            case "css" -> "🎨";
            case "png", "jpg", "jpeg", "gif" -> "🖼️";
            case "pdf" -> "📕";
            case "zip", "rar", "tar", "gz" -> "📦";
            default -> "📄";
        };
    }

    /**
         * File information container
         */
        private record FileInfo(String name, String relativePath, boolean isDirectory, long size, String lastModified,
                                boolean isHidden) {
    }
}
