package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.ClassFileSource;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.api.SinkReturns;
import run.mone.hive.roles.ReactorRole;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Jar file decompilation tool using CFR
 * <p>
 * This tool decompiles JAR files in the workspace to Java source code using the CFR decompiler,
 * and allows searching the decompiled code using regex patterns. This is useful for analyzing
 * third-party libraries, debugging, reverse engineering, or understanding code dependencies.
 * <p>
 * Use this tool when you need to:
 * - Inspect the implementation of third-party libraries
 * - Debug issues in dependencies where source code is not available
 * - Understand how specific APIs or methods are implemented
 * - Search for specific patterns or implementations across JAR files
 * - Analyze code dependencies and their internal workings
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class JarDecompileTool implements ITool {

    public static final String name = "jar_decompile";

    // Maximum number of search results
    private static final int MAX_RESULTS = 200;

    // Context lines before and after each match
    private static final int CONTEXT_LINES = 3;

    // Maximum output size (1MB)
    private static final int MAX_OUTPUT_BYTES = 1024 * 1024;

    // Temporary directory for decompiled files
    private static final String DECOMPILE_TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "jar_decompile";

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
                Request to decompile JAR files in the workspace and search the decompiled source code.
                This tool uses the CFR decompiler to convert JAR files back to readable Java source code,
                then allows you to search through the decompiled code using regex patterns.

                **When to use this tool:**
                - Inspect implementation details of third-party libraries
                - Debug issues in dependencies without source code
                - Understand how specific APIs or methods work internally
                - Search for specific patterns across compiled code
                - Analyze code dependencies and their implementations
                - Reverse engineer to understand code behavior
                - Find usage examples of internal APIs

                **Features:**
                - Automatic JAR file discovery in workspace
                - High-quality decompilation using CFR (modern Java decompiler)
                - Regex-based code searching with context
                - Support for nested JARs and complex structures
                - Caching of decompiled results for performance
                - No external dependencies required (pure Java)

                **Important notes:**
                - Decompilation may take time for large JARs
                - Results are cached in temporary directory
                - Searching is performed only on decompiled Java files
                - Use specific jar_name to limit scope for better performance
                """;
    }

    @Override
    public String parameters() {
        return """
                - jar_name: (optional) Name or pattern of the JAR file to decompile. If not provided, all JARs in workspace will be listed.
                - regex: (optional) Regular expression pattern to search in decompiled code. If provided, performs search; otherwise just decompiles.
                - class_pattern: (optional) Pattern to filter class files during search (e.g., 'com/example/*.class')
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
                <jar_decompile>
                <jar_name>JAR file name or pattern (optional)</jar_name>
                <regex>Regex pattern to search (optional)</regex>
                <class_pattern>Class file pattern (optional)</class_pattern>
                %s
                </jar_decompile>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: List all JAR files in workspace
                <jar_decompile>
                </jar_decompile>

                Example 2: Decompile a specific JAR
                <jar_decompile>
                <jar_name>commons-lang3-3.12.0.jar</jar_name>
                </jar_decompile>

                Example 3: Search for specific method implementation
                <jar_decompile>
                <jar_name>commons-lang3</jar_name>
                <regex>public static String join\\(</regex>
                </jar_decompile>

                Example 4: Find annotation usage in decompiled code
                <jar_decompile>
                <jar_name>spring-core</jar_name>
                <regex>@Component|@Service|@Repository</regex>
                </jar_decompile>

                Example 5: Search in specific package
                <jar_decompile>
                <jar_name>mylib.jar</jar_name>
                <regex>executeQuery</regex>
                <class_pattern>com/example/dao/*.class</class_pattern>
                </jar_decompile>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // Get workspace path from role
            String workspacePath = role.getWorkspacePath();
            if (StringUtils.isBlank(workspacePath)) {
                workspacePath = System.getProperty("user.dir");
            }

            String jarName = inputJson.has("jar_name") ? inputJson.get("jar_name").getAsString() : null;
            String regex = inputJson.has("regex") ? inputJson.get("regex").getAsString() : null;
            String classPattern = inputJson.has("class_pattern") ? inputJson.get("class_pattern").getAsString() : null;

            // If no jar_name provided, list all JARs
            if (StringUtils.isBlank(jarName)) {
                return listJarFiles(workspacePath);
            }

            // Find matching JAR files
            List<File> jarFiles = findJarFiles(workspacePath, jarName);
            if (jarFiles.isEmpty()) {
                result.addProperty("error", "No JAR files found matching: " + jarName);
                return result;
            }

            // If no regex provided, just decompile the JARs
            if (StringUtils.isBlank(regex)) {
                return decompileJars(jarFiles, workspacePath);
            }

            // Decompile and search
            return decompileAndSearch(jarFiles, regex, classPattern, workspacePath);

        } catch (Exception e) {
            log.error("Exception occurred while executing jar_decompile operation", e);
            result.addProperty("error", "Failed to execute jar_decompile operation: " + e.getMessage());
            return result;
        }
    }

    /**
     * List all JAR files in workspace
     */
    private JsonObject listJarFiles(String workspacePath) {
        JsonObject result = new JsonObject();

        try {
            List<File> jarFiles = findJarFiles(workspacePath, "*.jar");

            JsonArray jarsArray = new JsonArray();
            for (File jar : jarFiles) {
                JsonObject jarInfo = new JsonObject();
                jarInfo.addProperty("name", jar.getName());
                jarInfo.addProperty("path", jar.getAbsolutePath());
                jarInfo.addProperty("size", getHumanReadableSize(jar.length()));
                jarsArray.add(jarInfo);
            }

            result.add("jars", jarsArray);
            result.addProperty("totalJars", jarFiles.size());
            result.addProperty("result", String.format("Found %d JAR files in workspace:\n%s",
                jarFiles.size(),
                jarFiles.stream()
                    .map(f -> f.getName() + " (" + getHumanReadableSize(f.length()) + ")")
                    .collect(Collectors.joining("\n"))));

            log.info("Listed {} JAR files in workspace: {}", jarFiles.size(), workspacePath);
            return result;

        } catch (Exception e) {
            log.error("Failed to list JAR files", e);
            result.addProperty("error", "Failed to list JAR files: " + e.getMessage());
            return result;
        }
    }

    /**
     * Decompile JAR files and return summary
     */
    private JsonObject decompileJars(List<File> jarFiles, String workspacePath) {
        JsonObject result = new JsonObject();

        try {
            JsonArray decompiled = new JsonArray();
            StringBuilder summary = new StringBuilder();
            summary.append("Decompiled JAR files:\n\n");

            for (File jarFile : jarFiles) {
                JsonObject jarResult = decompileSingleJar(jarFile);
                decompiled.add(jarResult);

                summary.append(String.format("✓ %s\n", jarFile.getName()));
                if (jarResult.has("outputPath")) {
                    summary.append(String.format("  Output: %s\n", jarResult.get("outputPath").getAsString()));
                }
                if (jarResult.has("classCount")) {
                    summary.append(String.format("  Classes: %d\n", jarResult.get("classCount").getAsInt()));
                }
                summary.append("\n");
            }

            result.add("decompiled", decompiled);
            result.addProperty("totalJars", jarFiles.size());
            result.addProperty("result", summary.toString());

            log.info("Successfully decompiled {} JAR files", jarFiles.size());
            return result;

        } catch (Exception e) {
            log.error("Failed to decompile JARs", e);
            result.addProperty("error", "Failed to decompile JARs: " + e.getMessage());
            return result;
        }
    }

    /**
     * Decompile JARs and search for pattern
     */
    private JsonObject decompileAndSearch(List<File> jarFiles, String regex, String classPattern, String workspacePath) {
        JsonObject result = new JsonObject();

        try {
            Pattern regexPattern;
            try {
                regexPattern = Pattern.compile(regex, Pattern.MULTILINE);
            } catch (Exception e) {
                log.error("Invalid regex pattern: {}", regex, e);
                result.addProperty("error", "Invalid regex pattern: " + e.getMessage());
                return result;
            }

            PathMatcher classMatcher = null;
            if (classPattern != null && !classPattern.trim().isEmpty()) {
                try {
                    classMatcher = FileSystems.getDefault().getPathMatcher("glob:" + classPattern);
                } catch (Exception e) {
                    log.warn("Invalid class pattern: {}, ignoring", classPattern);
                }
            }

            List<SearchResult> allResults = new ArrayList<>();

            for (File jarFile : jarFiles) {
                // Decompile the JAR
                JsonObject decompileResult = decompileSingleJar(jarFile);
                if (decompileResult.has("error")) {
                    log.warn("Failed to decompile {}: {}", jarFile.getName(), decompileResult.get("error").getAsString());
                    continue;
                }

                String outputPath = decompileResult.get("outputPath").getAsString();
                Path decompiledRoot = Paths.get(outputPath);

                // Search in decompiled files
                List<SearchResult> jarResults = searchInDecompiledFiles(decompiledRoot, regexPattern, classMatcher, jarFile.getName());
                allResults.addAll(jarResults);

                if (allResults.size() >= MAX_RESULTS) {
                    break;
                }
            }

            // Format results
            String formattedResults = formatSearchResults(allResults, regex);

            result.addProperty("result", formattedResults);
            result.addProperty("regex", regex);
            result.addProperty("totalMatches", allResults.size());
            result.addProperty("wasLimited", allResults.size() >= MAX_RESULTS);

            log.info("Search completed: found {} matches for pattern '{}'", allResults.size(), regex);
            return result;

        } catch (Exception e) {
            log.error("Failed to decompile and search", e);
            result.addProperty("error", "Failed to decompile and search: " + e.getMessage());
            return result;
        }
    }

    /**
     * Decompile a single JAR file using CFR
     */
    private JsonObject decompileSingleJar(File jarFile) {
        JsonObject result = new JsonObject();

        try {
            // Create output directory
            File tempDir = new File(DECOMPILE_TEMP_DIR);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            String jarBaseName = jarFile.getName().replaceAll("\\.jar$", "");
            File outputDir = new File(tempDir, jarBaseName);

            // Check if already decompiled (cache)
            if (outputDir.exists() && outputDir.isDirectory() && outputDir.list().length > 0) {
                log.info("Using cached decompiled version of {}", jarFile.getName());
                result.addProperty("outputPath", outputDir.getAbsolutePath());
                result.addProperty("classCount", countJavaFiles(outputDir));
                result.addProperty("cached", true);
                return result;
            }

            // Ensure output directory is clean
            if (outputDir.exists()) {
                deleteDirectory(outputDir);
            }
            outputDir.mkdirs();

            log.info("Decompiling JAR: {} to {}", jarFile.getAbsolutePath(), outputDir.getAbsolutePath());

            // Use CFR to decompile
            decompileWithCFR(jarFile, outputDir);

            int classCount = countJavaFiles(outputDir);

            result.addProperty("jarName", jarFile.getName());
            result.addProperty("outputPath", outputDir.getAbsolutePath());
            result.addProperty("classCount", classCount);
            result.addProperty("cached", false);

            log.info("Successfully decompiled {} ({} classes)", jarFile.getName(), classCount);
            return result;

        } catch (Exception e) {
            log.error("Failed to decompile JAR: {}", jarFile.getName(), e);
            result.addProperty("error", "Failed to decompile: " + e.getMessage());
            return result;
        }
    }

    /**
     * Decompile JAR using CFR
     */
    private void decompileWithCFR(File jarFile, File outputDir) throws IOException {
        try {
            // CFR options
            Map<String, String> options = new HashMap<>();
            options.put("outputdir", outputDir.getAbsolutePath());
            options.put("caseinsensitivefs", "true");
            options.put("silent", "true");

            // Create output sink
            OutputSinkFactory outputSinkFactory = new OutputSinkFactory() {
                @Override
                public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
                    return Arrays.asList(SinkClass.STRING, SinkClass.DECOMPILED, SinkClass.DECOMPILED_MULTIVER);
                }

                @Override
                public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
                    return new Sink<T>() {
                        @Override
                        public void write(T t) {
                            // Handle output
                            if (t instanceof SinkReturns.Decompiled) {
                                SinkReturns.Decompiled decompiled = (SinkReturns.Decompiled) t;
                                String packageName = decompiled.getPackageName();
                                String className = decompiled.getClassName();
                                String javaCode = decompiled.getJava();

                                // Write to file
                                try {
                                    File packageDir = new File(outputDir, packageName.replace('.', File.separatorChar));
                                    packageDir.mkdirs();
                                    File javaFile = new File(packageDir, className + ".java");
                                    Files.writeString(javaFile.toPath(), javaCode, StandardCharsets.UTF_8);
                                } catch (IOException e) {
                                    log.error("Failed to write decompiled file: {}.{}", packageName, className, e);
                                }
                            }
                        }
                    };
                }
            };

            // Run CFR decompiler
            CfrDriver driver = new CfrDriver.Builder()
                    .withOptions(options)
                    .withOutputSink(outputSinkFactory)
                    .build();

            driver.analyse(Collections.singletonList(jarFile.getAbsolutePath()));

            log.info("CFR decompilation completed for {}", jarFile.getName());

        } catch (Exception e) {
            log.error("CFR decompilation failed", e);
            throw new IOException("Failed to decompile with CFR: " + e.getMessage(), e);
        }
    }

    /**
     * Search in decompiled Java files
     */
    private List<SearchResult> searchInDecompiledFiles(Path root, Pattern pattern, PathMatcher classMatcher, String jarName) throws IOException {
        List<SearchResult> results = new ArrayList<>();

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (results.size() >= MAX_RESULTS) {
                    return FileVisitResult.TERMINATE;
                }

                try {
                    // Only search Java files
                    if (!file.toString().endsWith(".java")) {
                        return FileVisitResult.CONTINUE;
                    }

                    // Apply class pattern filter if provided
                    if (classMatcher != null) {
                        Path relativePath = root.relativize(file);
                        String classPath = relativePath.toString().replace(".java", ".class").replace(File.separatorChar, '/');
                        if (!classMatcher.matches(Paths.get(classPath).getFileName())) {
                            return FileVisitResult.CONTINUE;
                        }
                    }

                    List<SearchResult> fileResults = searchInFile(file, pattern, root, jarName);
                    for (SearchResult searchResult : fileResults) {
                        if (results.size() >= MAX_RESULTS) {
                            break;
                        }
                        results.add(searchResult);
                    }

                } catch (Exception e) {
                    log.debug("Error searching file: {}", file, e);
                }

                return FileVisitResult.CONTINUE;
            }
        });

        return results;
    }

    /**
     * Search for pattern within a single Java file
     */
    private List<SearchResult> searchInFile(Path file, Pattern pattern, Path basePath, String jarName) throws IOException {
        List<SearchResult> results = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            String relativePath = basePath.relativize(file).toString().replace(File.separatorChar, '/');

            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines.get(lineIndex);
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    SearchResult result = new SearchResult();
                    result.jarName = jarName;
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
            log.debug("Failed to read file: {}", file, e);
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
            if (i != centerLine) {
                context.add(allLines.get(i));
            }
        }

        return context;
    }

    /**
     * Format search results for display
     */
    private String formatSearchResults(List<SearchResult> results, String regex) {
        if (results.isEmpty()) {
            return "No matches found in decompiled code.";
        }

        StringBuilder output = new StringBuilder();

        if (results.size() >= MAX_RESULTS) {
            output.append(String.format("Showing first %d of %d+ results. Use a more specific search if necessary.\n\n",
                    MAX_RESULTS, MAX_RESULTS));
        } else {
            output.append(String.format("Found %d results in decompiled code.\n\n", results.size()));
        }

        // Group by JAR and file
        Map<String, Map<String, List<SearchResult>>> groupedResults = new LinkedHashMap<>();
        for (SearchResult result : results) {
            groupedResults.computeIfAbsent(result.jarName, k -> new LinkedHashMap<>())
                         .computeIfAbsent(result.filePath, k -> new ArrayList<>())
                         .add(result);
        }

        int currentBytes = output.toString().getBytes(StandardCharsets.UTF_8).length;
        boolean wasLimited = false;

        for (Map.Entry<String, Map<String, List<SearchResult>>> jarEntry : groupedResults.entrySet()) {
            String jarHeader = "JAR: " + jarEntry.getKey() + "\n" + "=".repeat(60) + "\n";
            if (currentBytes + jarHeader.getBytes(StandardCharsets.UTF_8).length > MAX_OUTPUT_BYTES) {
                wasLimited = true;
                break;
            }
            output.append(jarHeader);
            currentBytes += jarHeader.getBytes(StandardCharsets.UTF_8).length;

            for (Map.Entry<String, List<SearchResult>> fileEntry : jarEntry.getValue().entrySet()) {
                String fileHeader = "\n" + fileEntry.getKey() + "\n" + "-".repeat(60) + "\n";
                if (currentBytes + fileHeader.getBytes(StandardCharsets.UTF_8).length > MAX_OUTPUT_BYTES) {
                    wasLimited = true;
                    break;
                }
                output.append(fileHeader);
                currentBytes += fileHeader.getBytes(StandardCharsets.UTF_8).length;

                for (SearchResult result : fileEntry.getValue()) {
                    StringBuilder resultBlock = new StringBuilder();

                    // Add before context
                    for (String contextLine : result.beforeContext) {
                        resultBlock.append("  ").append(contextLine.replaceAll("\\s+$", "")).append("\n");
                    }

                    // Add match line with indicator
                    resultBlock.append("► ").append(result.lineContent.replaceAll("\\s+$", ""))
                              .append(String.format(" [Line %d]\n", result.lineNumber));

                    // Add after context
                    for (String contextLine : result.afterContext) {
                        resultBlock.append("  ").append(contextLine.replaceAll("\\s+$", "")).append("\n");
                    }
                    resultBlock.append("\n");

                    if (currentBytes + resultBlock.toString().getBytes(StandardCharsets.UTF_8).length > MAX_OUTPUT_BYTES) {
                        wasLimited = true;
                        break;
                    }

                    output.append(resultBlock);
                    currentBytes += resultBlock.toString().getBytes(StandardCharsets.UTF_8).length;
                }

                if (wasLimited) break;
            }

            if (wasLimited) break;
        }

        if (wasLimited) {
            output.append("\n[Results truncated due to size limit. Please use a more specific search pattern.]\n");
        }

        return output.toString();
    }

    /**
     * Find JAR files in workspace matching pattern
     */
    private List<File> findJarFiles(String workspacePath, String pattern) {
        List<File> jarFiles = new ArrayList<>();
        File workspace = new File(workspacePath);

        try {
            Files.walkFileTree(workspace.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().toLowerCase().endsWith(".jar")) {
                        String fileName = file.getFileName().toString();
                        if (pattern.equals("*.jar") || fileName.contains(pattern) || matchesPattern(fileName, pattern)) {
                            jarFiles.add(file.toFile());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    // Skip common build/cache directories
                    String dirName = dir.getFileName().toString();
                    if (dirName.equals("node_modules") || dirName.equals(".git") ||
                        dirName.equals("target") || dirName.equals("build") ||
                        dirName.equals(".idea") || dirName.equals(".vscode")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("Error finding JAR files", e);
        }

        return jarFiles;
    }

    /**
     * Check if filename matches pattern
     */
    private boolean matchesPattern(String fileName, String pattern) {
        String regex = pattern.replace("*", ".*").replace("?", ".");
        return fileName.matches(regex);
    }

    /**
     * Count Java files in directory
     */
    private int countJavaFiles(File directory) {
        int count = 0;
        try {
            count = (int) Files.walk(directory.toPath())
                    .filter(p -> p.toString().endsWith(".java"))
                    .count();
        } catch (IOException e) {
            log.warn("Failed to count Java files", e);
        }
        return count;
    }

    /**
     * Delete directory recursively
     */
    private void deleteDirectory(File directory) throws IOException {
        Files.walk(directory.toPath())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    /**
     * Get human readable file size
     */
    private String getHumanReadableSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    /**
     * Search result data class
     */
    private static class SearchResult {
        String jarName;
        String filePath;
        int lineNumber;
        int column;
        String matchText;
        String lineContent;
        List<String> beforeContext = new ArrayList<>();
        List<String> afterContext = new ArrayList<>();
    }
}
