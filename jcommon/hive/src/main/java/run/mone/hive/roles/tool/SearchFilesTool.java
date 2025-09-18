package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * File content search tool using regular expressions
 * 
 * This tool performs regex searches across files in a specified directory, providing context-rich results. 
 * It searches for patterns or specific content across multiple files, displaying each match with 
 * encapsulating context for better understanding and analysis.
 * 
 * Use this tool when you need to:
 * - Find specific patterns, functions, or content across multiple files
 * - Search for TODO comments, FIXME notes, or other markers
 * - Locate usage of specific APIs, functions, or variables
 * - Analyze code patterns and implementations across a codebase
 * - Debug by finding error messages or log statements
 * - Research how certain features are implemented
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class SearchFilesTool implements ITool {

    public static final String name = "search_files";

    // Maximum number of results to prevent overwhelming output
    private static final int MAX_RESULTS = 300;
    
    // Maximum output size to prevent memory issues (0.25MB)
    private static final int MAX_OUTPUT_BYTES = 256 * 1024;
    
    // Context lines before and after each match
    private static final int CONTEXT_LINES = 1;
    
    // Maximum file size to search (1MB)
    private static final long MAX_FILE_SIZE = 1024 * 1024;
    
    // Common directories to ignore during recursive search
    private static final Set<String> IGNORED_DIRECTORIES = Set.of(
        "node_modules", "__pycache__", ".git", ".svn", ".hg", 
        "target", "build", "dist", "out", "vendor", "deps",
        ".idea", ".vscode", ".gradle", ".m2", "Pods"
    );
    
    // Binary file extensions to skip
    private static final Set<String> BINARY_EXTENSIONS = Set.of(
        "jar", "class", "exe", "dll", "so", "dylib", "a", "lib",
        "zip", "rar", "tar", "gz", "bz2", "7z",
        "jpg", "jpeg", "png", "gif", "bmp", "ico", "svg",
        "mp3", "mp4", "avi", "mov", "wmv", "flv",
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"
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
                Request to perform a regex search across files in a specified directory, providing context-rich results. 
                This tool searches for patterns or specific content across multiple files, displaying each match with 
                encapsulating context.
                
                **When to use this tool:**
                - Find specific patterns, functions, or content across multiple files
                - Search for TODO comments, FIXME notes, or other code markers
                - Locate usage of specific APIs, functions, or variables across a codebase
                - Analyze code patterns and implementations
                - Debug by finding error messages or log statements
                - Research how certain features are implemented
                - Find configuration values or constants
                - Locate test cases or documentation references
                
                **Search capabilities:**
                - Recursive directory search with intelligent filtering
                - Regular expression pattern matching with full Java regex syntax
                - File type filtering using glob patterns (*.java, *.js, etc.)
                - Context-aware results showing lines before and after matches
                - Binary file detection and exclusion
                - Size limits to prevent overwhelming output
                - Grouped results by file for easy navigation
                
                **Performance features:**
                - Automatic exclusion of common build/cache directories
                - File size limits to avoid processing huge files
                - Result count limits with truncation warnings
                - Memory-efficient streaming processing
                """;
    }

    @Override
    public String parameters() {
        return """
                - path: (required) The path of the directory to search in (relative to the current working directory). This directory will be recursively searched.
                - regex: (required) The regular expression pattern to search for. Uses Java regex syntax.
                - file_pattern: (optional) Glob pattern to filter files (e.g., '*.ts' for TypeScript files). If not provided, it will search all files (*).
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
            <search_files>
            <path>Directory path here</path>
            <regex>Your regex pattern here</regex>
            <file_pattern>file pattern here (optional)</file_pattern>
            %s
            </search_files>
            """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: Search for TODO comments in Java files
                <search_files>
                <path>src/main/java</path>
                <regex>TODO:|FIXME:</regex>
                <file_pattern>*.java</file_pattern>
                </search_files>
                
                Example 2: Find function definitions in JavaScript
                <search_files>
                <path>src</path>
                <regex>function\\s+\\w+\\s*\\(</regex>
                <file_pattern>*.js</file_pattern>
                </search_files>
                
                Example 3: Search for specific API usage
                <search_files>
                <path>.</path>
                <regex>@RequestMapping|@GetMapping|@PostMapping</regex>
                <file_pattern>*.java</file_pattern>
                </search_files>
                
                Example 4: Find error handling patterns
                <search_files>
                <path>src</path>
                <regex>catch\\s*\\([^)]+\\)|throw\\s+new</regex>
                </search_files>
                
                Example 5: Search for configuration properties
                <search_files>
                <path>src/main/resources</path>
                <regex>spring\\.datasource\\.|server\\.port</regex>
                <file_pattern>*.properties</file_pattern>
                </search_files>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // Validate required parameters
            if (!inputJson.has("path") || StringUtils.isBlank(inputJson.get("path").getAsString())) {
                log.error("search_files operation missing required path parameter");
                result.addProperty("error", "Missing required parameter 'path'");
                return result;
            }

            if (!inputJson.has("regex") || StringUtils.isBlank(inputJson.get("regex").getAsString())) {
                log.error("search_files operation missing required regex parameter");
                result.addProperty("error", "Missing required parameter 'regex'");
                return result;
            }

            String path = inputJson.get("path").getAsString();
            String regex = inputJson.get("regex").getAsString();
            String filePattern = inputJson.has("file_pattern") ? inputJson.get("file_pattern").getAsString() : null;

            return performFileSearch(path, regex, filePattern);

        } catch (Exception e) {
            log.error("Exception occurred while executing search_files operation", e);
            result.addProperty("error", "Failed to execute search_files operation: " + e.getMessage());
            return result;
        }
    }

    /**
     * Perform the file search operation
     */
    private JsonObject performFileSearch(String path, String regex, String filePattern) {
        JsonObject result = new JsonObject();

        try {
            Path searchPath = Paths.get(path);
            
            // Check if directory exists
            if (!Files.exists(searchPath)) {
                log.error("Directory not found: {}", path);
                result.addProperty("error", "Directory not found: " + path);
                return result;
            }

            // Check if path points to a file instead of directory
            if (!Files.isDirectory(searchPath)) {
                log.error("Path points to a file, not a directory: {}", path);
                result.addProperty("error", "Path points to a file, not a directory: " + path + 
                                           ". This tool searches directories recursively.");
                return result;
            }

            // Check directory permissions
            if (!Files.isReadable(searchPath)) {
                log.error("Directory is not readable: {}", path);
                result.addProperty("error", "Directory is not readable: " + path);
                return result;
            }

            // Compile regex pattern
            Pattern regexPattern;
            try {
                regexPattern = Pattern.compile(regex, Pattern.MULTILINE);
            } catch (Exception e) {
                log.error("Invalid regex pattern: {}", regex, e);
                result.addProperty("error", "Invalid regex pattern: " + e.getMessage());
                return result;
            }

            // Compile file pattern if provided
            PathMatcher fileMatcher = null;
            if (filePattern != null && !filePattern.trim().isEmpty()) {
                try {
                    fileMatcher = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);
                } catch (Exception e) {
                    log.warn("Invalid file pattern: {}, ignoring", filePattern);
                }
            }

            // Perform search
            List<SearchResult> searchResults = searchFiles(searchPath, regexPattern, fileMatcher);

            // Format results
            String formattedResults = formatSearchResults(searchResults, searchPath, regex, filePattern);
            
            // Build result object
            result.addProperty("result", formattedResults);
            result.addProperty("searchPath", path);
            result.addProperty("regex", regex);
            if (filePattern != null) {
                result.addProperty("filePattern", filePattern);
            }
            result.addProperty("totalMatches", searchResults.size());
            result.addProperty("wasLimited", searchResults.size() >= MAX_RESULTS);

            // Add detailed results for programmatic access
            JsonArray resultsArray = new JsonArray();
            Map<String, List<SearchResult>> groupedResults = groupResultsByFile(searchResults, searchPath);
            
            for (Map.Entry<String, List<SearchResult>> entry : groupedResults.entrySet()) {
                JsonObject fileResult = new JsonObject();
                fileResult.addProperty("file", entry.getKey());
                fileResult.addProperty("matches", entry.getValue().size());
                
                JsonArray matchesArray = new JsonArray();
                for (SearchResult match : entry.getValue()) {
                    JsonObject matchObj = new JsonObject();
                    matchObj.addProperty("line", match.lineNumber);
                    matchObj.addProperty("column", match.column);
                    matchObj.addProperty("matchText", match.matchText);
                    matchObj.addProperty("lineContent", match.lineContent);
                    matchesArray.add(matchObj);
                }
                fileResult.add("matchDetails", matchesArray);
                resultsArray.add(fileResult);
            }
            result.add("detailedResults", resultsArray);

            // Add summary statistics
            JsonObject summary = new JsonObject();
            summary.addProperty("filesSearched", countFilesSearched(searchPath, fileMatcher));
            summary.addProperty("filesWithMatches", groupedResults.size());
            summary.addProperty("totalMatches", searchResults.size());
            summary.addProperty("wasLimited", searchResults.size() >= MAX_RESULTS);
            result.add("summary", summary);

            log.info("Successfully searched {} files in directory: {}, found {} matches", 
                    summary.get("filesSearched").getAsInt(), path, searchResults.size());

            return result;

        } catch (IOException e) {
            log.error("IO exception while searching directory: {}", path, e);
            result.addProperty("error", "Failed to search directory: " + e.getMessage());
        } catch (Exception e) {
            log.error("Exception while searching directory: {}", path, e);
            result.addProperty("error", "Error searching directory: " + e.getMessage());
        }

        return result;
    }

    /**
     * Search files recursively for the given pattern
     */
    private List<SearchResult> searchFiles(Path searchPath, Pattern regexPattern, PathMatcher fileMatcher) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        AtomicInteger resultCount = new AtomicInteger(0);

        Files.walkFileTree(searchPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                // Skip ignored directories
                String dirName = dir.getFileName().toString();
                if (IGNORED_DIRECTORIES.contains(dirName) || dirName.startsWith(".")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // Stop if we've reached the result limit
                if (resultCount.get() >= MAX_RESULTS) {
                    return FileVisitResult.TERMINATE;
                }

                try {
                    // Skip if file doesn't match pattern
                    if (fileMatcher != null && !fileMatcher.matches(file.getFileName())) {
                        return FileVisitResult.CONTINUE;
                    }

                    // Skip binary files
                    if (isBinaryFile(file)) {
                        return FileVisitResult.CONTINUE;
                    }

                    // Skip large files
                    if (attrs.size() > MAX_FILE_SIZE) {
                        log.debug("Skipping large file: {} ({}bytes)", file, attrs.size());
                        return FileVisitResult.CONTINUE;
                    }

                    // Search file content
                    List<SearchResult> fileResults = searchInFile(file, regexPattern, searchPath);
                    
                    // Add results up to the limit
                    for (SearchResult result : fileResults) {
                        if (resultCount.get() >= MAX_RESULTS) {
                            break;
                        }
                        results.add(result);
                        resultCount.incrementAndGet();
                    }

                } catch (Exception e) {
                    log.debug("Error searching file: {}, error: {}", file, e.getMessage());
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                log.debug("Failed to visit file: {}, error: {}", file, exc.getMessage());
                return FileVisitResult.CONTINUE;
            }
        });

        return results;
    }

    /**
     * Search for pattern within a single file
     */
    private List<SearchResult> searchInFile(Path file, Pattern pattern, Path basePath) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            String relativePath = basePath.relativize(file).toString().replace('\\', '/');
            
            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines.get(lineIndex);
                Matcher matcher = pattern.matcher(line);
                
                while (matcher.find()) {
                    SearchResult result = new SearchResult();
                    result.filePath = relativePath;
                    result.lineNumber = lineIndex + 1;
                    result.column = matcher.start() + 1;
                    result.matchText = matcher.group();
                    result.lineContent = line;
                    
                    // Add context lines
                    result.beforeContext = getContextLines(lines, lineIndex, -CONTEXT_LINES, 0);
                    result.afterContext = getContextLines(lines, lineIndex, 1, CONTEXT_LINES + 1);
                    
                    results.add(result);
                }
            }
        } catch (Exception e) {
            // Try with different encoding if UTF-8 fails
            try {
                List<String> lines = Files.readAllLines(file, StandardCharsets.ISO_8859_1);
                String relativePath = basePath.relativize(file).toString().replace('\\', '/');
                
                for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                    String line = lines.get(lineIndex);
                    Matcher matcher = pattern.matcher(line);
                    
                    while (matcher.find()) {
                        SearchResult result = new SearchResult();
                        result.filePath = relativePath;
                        result.lineNumber = lineIndex + 1;
                        result.column = matcher.start() + 1;
                        result.matchText = matcher.group();
                        result.lineContent = line;
                        
                        // Add context lines
                        result.beforeContext = getContextLines(lines, lineIndex, -CONTEXT_LINES, 0);
                        result.afterContext = getContextLines(lines, lineIndex, 1, CONTEXT_LINES + 1);
                        
                        results.add(result);
                    }
                }
            } catch (Exception e2) {
                log.debug("Failed to read file with both UTF-8 and ISO-8859-1: {}", file);
            }
        }
        
        return results;
    }

    /**
     * Get context lines around a match
     */
    private List<String> getContextLines(List<String> allLines, int centerLine, int start, int end) {
        List<String> context = new ArrayList<>();
        
        int startLine = Math.max(0, centerLine + start);
        int endLine = Math.min(allLines.size(), centerLine + end);
        
        for (int i = startLine; i < endLine; i++) {
            if (i != centerLine) { // Don't include the match line itself in context
                context.add(allLines.get(i));
            }
        }
        
        return context;
    }

    /**
     * Check if file is likely binary
     */
    private boolean isBinaryFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        int lastDotIndex = fileName.lastIndexOf('.');
        
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            String extension = fileName.substring(lastDotIndex + 1);
            if (BINARY_EXTENSIONS.contains(extension)) {
                return true;
            }
        }
        
        // Additional check: read first few bytes to detect binary content
        try {
            byte[] bytes = Files.readAllBytes(file);
            if (bytes.length > 1024) {
                bytes = Arrays.copyOf(bytes, 1024); // Only check first 1KB
            }
            
            // Simple heuristic: if more than 30% of bytes are non-printable, consider it binary
            int nonPrintable = 0;
            for (byte b : bytes) {
                if (b < 32 && b != 9 && b != 10 && b != 13) { // Not tab, newline, or carriage return
                    nonPrintable++;
                }
            }
            
            return (double) nonPrintable / bytes.length > 0.3;
            
        } catch (Exception e) {
            return false; // If we can't read it, assume it's text
        }
    }

    /**
     * Count total files that would be searched
     */
    private int countFilesSearched(Path searchPath, PathMatcher fileMatcher) {
        AtomicInteger count = new AtomicInteger(0);
        
        try {
            Files.walkFileTree(searchPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    String dirName = dir.getFileName().toString();
                    if (IGNORED_DIRECTORIES.contains(dirName) || dirName.startsWith(".")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (fileMatcher == null || fileMatcher.matches(file.getFileName())) {
                        if (!isBinaryFile(file) && attrs.size() <= MAX_FILE_SIZE) {
                            count.incrementAndGet();
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.debug("Error counting files: {}", e.getMessage());
        }
        
        return count.get();
    }

    /**
     * Group search results by file
     */
    private Map<String, List<SearchResult>> groupResultsByFile(List<SearchResult> results, Path basePath) {
        Map<String, List<SearchResult>> grouped = new LinkedHashMap<>();
        
        for (SearchResult result : results) {
            grouped.computeIfAbsent(result.filePath, k -> new ArrayList<>()).add(result);
        }
        
        return grouped;
    }

    /**
     * Format search results for display
     */
    private String formatSearchResults(List<SearchResult> results, Path searchPath, String regex, String filePattern) {
        if (results.isEmpty()) {
            return "No matches found.";
        }

        StringBuilder output = new StringBuilder();
        
        // Add header with search summary
        if (results.size() >= MAX_RESULTS) {
            output.append(String.format("Showing first %d of %d+ results. Use a more specific search if necessary.\n\n", 
                    MAX_RESULTS, MAX_RESULTS));
        } else {
            String resultText = results.size() == 1 ? "1 result" : String.format("%,d results", results.size());
            output.append(String.format("Found %s.\n\n", resultText));
        }

        // Group results by file
        Map<String, List<SearchResult>> groupedResults = groupResultsByFile(results, searchPath);
        
        int currentBytes = output.toString().getBytes(StandardCharsets.UTF_8).length;
        boolean wasLimited = false;
        
        for (Map.Entry<String, List<SearchResult>> entry : groupedResults.entrySet()) {
            String filePath = entry.getKey();
            List<SearchResult> fileResults = entry.getValue();
            
            // Check size limit before adding file header
            String fileHeader = filePath + "\n│----\n";
            if (currentBytes + fileHeader.getBytes(StandardCharsets.UTF_8).length > MAX_OUTPUT_BYTES) {
                wasLimited = true;
                break;
            }
            
            output.append(fileHeader);
            currentBytes += fileHeader.getBytes(StandardCharsets.UTF_8).length;
            
            for (int i = 0; i < fileResults.size(); i++) {
                SearchResult result = fileResults.get(i);
                
                // Build result block
                StringBuilder resultBlock = new StringBuilder();
                
                // Add before context
                for (String contextLine : result.beforeContext) {
                    resultBlock.append("│").append(contextLine.replaceAll("\\s+$", "")).append("\n");
                }
                
                // Add match line
                resultBlock.append("│").append(result.lineContent.replaceAll("\\s+$", "")).append("\n");
                
                // Add after context
                for (String contextLine : result.afterContext) {
                    resultBlock.append("│").append(contextLine.replaceAll("\\s+$", "")).append("\n");
                }
                
                // Add separator between results
                if (i < fileResults.size() - 1) {
                    resultBlock.append("│----\n");
                }
                
                // Check size limit
                if (currentBytes + resultBlock.toString().getBytes(StandardCharsets.UTF_8).length > MAX_OUTPUT_BYTES) {
                    wasLimited = true;
                    break;
                }
                
                output.append(resultBlock);
                currentBytes += resultBlock.toString().getBytes(StandardCharsets.UTF_8).length;
            }
            
            if (wasLimited) {
                break;
            }
            
            // Add file closing
            String fileClosing = "│----\n\n";
            if (currentBytes + fileClosing.getBytes(StandardCharsets.UTF_8).length > MAX_OUTPUT_BYTES) {
                wasLimited = true;
                break;
            }
            
            output.append(fileClosing);
            currentBytes += fileClosing.getBytes(StandardCharsets.UTF_8).length;
        }
        
        // Add truncation warning if needed
        if (wasLimited) {
            String warning = "\n[Results truncated due to exceeding the 0.25MB size limit. Please use a more specific search pattern.]";
            if (currentBytes + warning.getBytes(StandardCharsets.UTF_8).length < MAX_OUTPUT_BYTES) {
                output.append(warning);
            }
        }
        
        return output.toString().trim();
    }

    /**
     * Search result data class
     */
    private static class SearchResult {
        String filePath;
        int lineNumber;
        int column;
        String matchText;
        String lineContent;
        List<String> beforeContext = new ArrayList<>();
        List<String> afterContext = new ArrayList<>();
    }
}
