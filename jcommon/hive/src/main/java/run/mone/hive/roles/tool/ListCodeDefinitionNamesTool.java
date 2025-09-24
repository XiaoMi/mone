package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Code definition extraction tool for analyzing source code structure
 * 
 * This tool lists definition names (classes, functions, methods, etc.) used in source code files 
 * at the top level of the specified directory. It provides insights into the codebase structure 
 * and important constructs, encapsulating high-level concepts and relationships that are crucial 
 * for understanding the overall architecture.
 * 
 * Use this tool when you need to:
 * - Understand the structure and organization of a codebase
 * - Get an overview of classes, functions, and methods in a directory
 * - Analyze code architecture and design patterns
 * - Identify key components and their relationships
 * - Prepare for code refactoring or documentation
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ListCodeDefinitionNamesTool implements ITool {

    public static final String name = "list_code_definition_names";

    // Supported file extensions for code analysis
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
        "java", "js", "jsx", "ts", "tsx", "py", "go", "rs", "cpp", "hpp", 
        "c", "h", "cs", "rb", "php", "swift", "kt", "scala", "groovy"
    );

    // Maximum number of files to analyze to prevent overwhelming output
    private static final int MAX_FILES_TO_ANALYZE = 50;

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
                Request to list definition names (classes, functions, methods, etc.) used in source code files 
                at the top level of the specified directory. This tool provides insights into the codebase structure 
                and important constructs, encapsulating high-level concepts and relationships that are crucial for 
                understanding the overall architecture.
                
                **When to use this tool:**
                - Understand the structure and organization of a codebase
                - Get an overview of classes, functions, and methods in a directory
                - Analyze code architecture and design patterns
                - Identify key components and their relationships
                - Prepare for code refactoring or documentation
                - Explore unfamiliar codebases to understand their structure
                
                **What this tool extracts:**
                - Class declarations and their names
                - Method and function definitions
                - Interface declarations
                - Enum definitions
                - Constructor definitions
                - Abstract class declarations
                - Important variable declarations (constants, static fields)
                
                **Supported languages:**
                Java, JavaScript, TypeScript, Python, Go, Rust, C/C++, C#, Ruby, PHP, Swift, Kotlin, Scala, Groovy
                """;
    }

    @Override
    public String parameters() {
        return """
                - path: (required) The path of the directory (relative to the current working directory) to list top level source code definitions for.
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
            <list_code_definition_names>
            <path>Directory path here</path>
            %s
            </list_code_definition_names>
            """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: Analyze main source directory
                <list_code_definition_names>
                <path>src/main/java</path>
                </list_code_definition_names>
                
                Example 2: Analyze specific package
                <list_code_definition_names>
                <path>src/main/java/com/example/service</path>
                </list_code_definition_names>
                
                Example 3: Analyze JavaScript components
                <list_code_definition_names>
                <path>src/components</path>
                </list_code_definition_names>
                
                Example 4: Analyze Python modules
                <list_code_definition_names>
                <path>src/python</path>
                </list_code_definition_names>
                
                Example 5: Analyze entire project source
                <list_code_definition_names>
                <path>src</path>
                </list_code_definition_names>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // Validate required parameters
            if (!inputJson.has("path") || StringUtils.isBlank(inputJson.get("path").getAsString())) {
                log.error("list_code_definition_names operation missing required path parameter");
                result.addProperty("error", "Missing required parameter 'path'");
                return result;
            }

            String path = inputJson.get("path").getAsString();
            return performCodeDefinitionAnalysis(path);

        } catch (Exception e) {
            log.error("Exception occurred while executing list_code_definition_names operation", e);
            result.addProperty("error", "Failed to execute list_code_definition_names operation: " + e.getMessage());
            return result;
        }
    }

    /**
     * Perform the code definition analysis operation
     */
    private JsonObject performCodeDefinitionAnalysis(String path) {
        JsonObject result = new JsonObject();

        try {
            Path dirPath = Paths.get(path);
            File directory = dirPath.toFile();

            // Check if directory exists
            if (!directory.exists()) {
                log.error("Directory not found: {}", path);
                result.addProperty("error", "This directory does not exist or you do not have permission to access it.");
                return result;
            }

            // Check if path points to a file instead of directory
            if (!directory.isDirectory()) {
                log.error("Path points to a file, not a directory: {}", path);
                result.addProperty("error", "Path points to a file, not a directory: " + path + 
                                           ". This tool analyzes directories containing source code files.");
                return result;
            }

            // Check directory permissions
            if (!directory.canRead()) {
                log.error("Directory is not readable: {}", path);
                result.addProperty("error", "Directory is not readable: " + path);
                return result;
            }

            // Get all source code files at top level
            List<Path> sourceFiles = getSourceCodeFiles(dirPath);

            if (sourceFiles.isEmpty()) {
                result.addProperty("result", "No source code definitions found.");
                result.addProperty("totalFiles", 0);
                result.addProperty("directoryPath", path);
                return result;
            }

            // Analyze files and extract definitions
            StringBuilder analysisResult = new StringBuilder();
            JsonArray filesAnalyzed = new JsonArray();
            int totalDefinitions = 0;

            for (Path sourceFile : sourceFiles.subList(0, Math.min(sourceFiles.size(), MAX_FILES_TO_ANALYZE))) {
                String relativePath = dirPath.relativize(sourceFile).toString().replace('\\', '/');
                
                try {
                    List<CodeDefinition> definitions = analyzeSourceFile(sourceFile);
                    
                    if (!definitions.isEmpty()) {
                        analysisResult.append(relativePath).append("\n");
                        
                        JsonObject fileInfo = new JsonObject();
                        fileInfo.addProperty("path", relativePath);
                        fileInfo.addProperty("definitionCount", definitions.size());
                        
                        JsonArray definitionArray = new JsonArray();
                        
                        analysisResult.append("|----\n");
                        for (CodeDefinition def : definitions) {
                            analysisResult.append("│").append(def.getLine()).append("\n");
                            
                            JsonObject defObj = new JsonObject();
                            defObj.addProperty("type", def.getType());
                            defObj.addProperty("name", def.getName());
                            defObj.addProperty("line", def.getLine());
                            defObj.addProperty("lineNumber", def.getLineNumber());
                            definitionArray.add(defObj);
                        }
                        analysisResult.append("|----\n\n");
                        
                        fileInfo.add("definitions", definitionArray);
                        filesAnalyzed.add(fileInfo);
                        totalDefinitions += definitions.size();
                    }
                } catch (Exception e) {
                    log.warn("Failed to analyze file: {}, error: {}", sourceFile, e.getMessage());
                }
            }

            // Build result
            String finalResult = analysisResult.length() > 0 ? analysisResult.toString().trim() : "No source code definitions found.";
            
            result.addProperty("result", finalResult);
            result.addProperty("totalFiles", sourceFiles.size());
            result.addProperty("analyzedFiles", Math.min(sourceFiles.size(), MAX_FILES_TO_ANALYZE));
            result.addProperty("totalDefinitions", totalDefinitions);
            result.addProperty("directoryPath", path);
            result.add("filesAnalyzed", filesAnalyzed);

            // Add summary statistics
            JsonObject summary = new JsonObject();
            summary.addProperty("sourceFilesFound", sourceFiles.size());
            summary.addProperty("filesAnalyzed", Math.min(sourceFiles.size(), MAX_FILES_TO_ANALYZE));
            summary.addProperty("totalDefinitions", totalDefinitions);
            summary.addProperty("wasLimited", sourceFiles.size() > MAX_FILES_TO_ANALYZE);
            result.add("summary", summary);

            log.info("Successfully analyzed {} source files in directory: {}, found {} definitions", 
                    Math.min(sourceFiles.size(), MAX_FILES_TO_ANALYZE), path, totalDefinitions);

            return result;

        } catch (IOException e) {
            log.error("IO exception while analyzing directory: {}", path, e);
            result.addProperty("error", "Failed to analyze directory: " + e.getMessage());
        } catch (Exception e) {
            log.error("Exception while analyzing directory: {}", path, e);
            result.addProperty("error", "Error analyzing directory: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get all source code files from the directory (top-level only)
     */
    private List<Path> getSourceCodeFiles(Path dirPath) throws IOException {
        List<Path> sourceFiles = new ArrayList<>();
        
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.filter(Files::isRegularFile)
                  .filter(this::isSourceCodeFile)
                  .sorted()
                  .forEach(sourceFiles::add);
        }
        
        return sourceFiles;
    }

    /**
     * Check if file is a source code file based on extension
     */
    private boolean isSourceCodeFile(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
            return SUPPORTED_EXTENSIONS.contains(extension);
        }
        
        return false;
    }

    /**
     * Analyze a single source file and extract code definitions
     */
    private List<CodeDefinition> analyzeSourceFile(Path filePath) throws IOException {
        String content = Files.readString(filePath, StandardCharsets.UTF_8);
        String extension = getFileExtension(filePath);
        
        return switch (extension.toLowerCase()) {
            case "java" -> analyzeJavaFile(content);
            case "js", "jsx" -> analyzeJavaScriptFile(content);
            case "ts", "tsx" -> analyzeTypeScriptFile(content);
            case "py" -> analyzePythonFile(content);
            case "go" -> analyzeGoFile(content);
            case "rs" -> analyzeRustFile(content);
            case "cpp", "hpp", "c", "h" -> analyzeCppFile(content);
            case "cs" -> analyzeCSharpFile(content);
            case "rb" -> analyzeRubyFile(content);
            case "php" -> analyzePhpFile(content);
            case "swift" -> analyzeSwiftFile(content);
            case "kt" -> analyzeKotlinFile(content);
            case "scala" -> analyzeScalaFile(content);
            case "groovy" -> analyzeGroovyFile(content);
            default -> new ArrayList<>();
        };
    }

    /**
     * Analyze Java source code
     */
    private List<CodeDefinition> analyzeJavaFile(String content) {
        List<CodeDefinition> definitions = new ArrayList<>();
        String[] lines = content.split("\n");
        
        // Patterns for Java constructs
        Pattern classPattern = Pattern.compile("^\\s*(?:public|private|protected)?\\s*(?:static)?\\s*(?:final)?\\s*(?:abstract)?\\s*class\\s+(\\w+)");
        Pattern interfacePattern = Pattern.compile("^\\s*(?:public|private|protected)?\\s*interface\\s+(\\w+)");
        Pattern enumPattern = Pattern.compile("^\\s*(?:public|private|protected)?\\s*enum\\s+(\\w+)");
        Pattern methodPattern = Pattern.compile("^\\s*(?:public|private|protected)?\\s*(?:static)?\\s*(?:final)?\\s*(?:abstract)?\\s*(?:\\w+(?:<[^>]*>)?\\s+)?(\\w+)\\s*\\([^)]*\\)\\s*(?:throws\\s+[\\w\\s,]+)?\\s*[{;]");
        Pattern constructorPattern = Pattern.compile("^\\s*(?:public|private|protected)?\\s*(\\w+)\\s*\\([^)]*\\)\\s*(?:throws\\s+[\\w\\s,]+)?\\s*\\{");
        Pattern fieldPattern = Pattern.compile("^\\s*(?:public|private|protected)?\\s*(?:static)?\\s*(?:final)?\\s*\\w+(?:<[^>]*>)?\\s+(\\w+)\\s*[=;]");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // Skip comments and empty lines
            if (line.trim().startsWith("//") || line.trim().startsWith("/*") || line.trim().isEmpty()) {
                continue;
            }
            
            Matcher classMatcher = classPattern.matcher(line);
            if (classMatcher.find()) {
                definitions.add(new CodeDefinition("class", classMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher interfaceMatcher = interfacePattern.matcher(line);
            if (interfaceMatcher.find()) {
                definitions.add(new CodeDefinition("interface", interfaceMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher enumMatcher = enumPattern.matcher(line);
            if (enumMatcher.find()) {
                definitions.add(new CodeDefinition("enum", enumMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher methodMatcher = methodPattern.matcher(line);
            if (methodMatcher.find() && !line.contains("class") && !line.contains("interface")) {
                String methodName = methodMatcher.group(1);
                if (!methodName.equals("class") && !methodName.equals("interface")) {
                    definitions.add(new CodeDefinition("method", methodName, line.trim(), i + 1));
                }
                continue;
            }
            
            Matcher constructorMatcher = constructorPattern.matcher(line);
            if (constructorMatcher.find()) {
                definitions.add(new CodeDefinition("constructor", constructorMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            // Only capture static final fields (constants)
            if (line.contains("static") && line.contains("final")) {
                Matcher fieldMatcher = fieldPattern.matcher(line);
                if (fieldMatcher.find()) {
                    definitions.add(new CodeDefinition("constant", fieldMatcher.group(1), line.trim(), i + 1));
                }
            }
        }
        
        return definitions;
    }

    /**
     * Analyze JavaScript source code
     */
    private List<CodeDefinition> analyzeJavaScriptFile(String content) {
        List<CodeDefinition> definitions = new ArrayList<>();
        String[] lines = content.split("\n");
        
        // Patterns for JavaScript constructs
        Pattern functionPattern = Pattern.compile("^\\s*(?:export\\s+)?(?:async\\s+)?function\\s+(\\w+)\\s*\\(");
        Pattern arrowFunctionPattern = Pattern.compile("^\\s*(?:export\\s+)?(?:const|let|var)\\s+(\\w+)\\s*=\\s*(?:async\\s+)?\\([^)]*\\)\\s*=>");
        Pattern classPattern = Pattern.compile("^\\s*(?:export\\s+)?(?:default\\s+)?class\\s+(\\w+)");
        Pattern methodPattern = Pattern.compile("^\\s*(\\w+)\\s*\\([^)]*\\)\\s*\\{");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // Skip comments and empty lines
            if (line.trim().startsWith("//") || line.trim().startsWith("/*") || line.trim().isEmpty()) {
                continue;
            }
            
            Matcher functionMatcher = functionPattern.matcher(line);
            if (functionMatcher.find()) {
                definitions.add(new CodeDefinition("function", functionMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher arrowMatcher = arrowFunctionPattern.matcher(line);
            if (arrowMatcher.find()) {
                definitions.add(new CodeDefinition("function", arrowMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher classMatcher = classPattern.matcher(line);
            if (classMatcher.find()) {
                definitions.add(new CodeDefinition("class", classMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher methodMatcher = methodPattern.matcher(line);
            if (methodMatcher.find() && !line.contains("function") && !line.contains("class")) {
                definitions.add(new CodeDefinition("method", methodMatcher.group(1), line.trim(), i + 1));
            }
        }
        
        return definitions;
    }

    /**
     * Analyze TypeScript source code (similar to JavaScript with additional type annotations)
     */
    private List<CodeDefinition> analyzeTypeScriptFile(String content) {
        List<CodeDefinition> definitions = new ArrayList<>();
        String[] lines = content.split("\n");
        
        // Patterns for TypeScript constructs
        Pattern functionPattern = Pattern.compile("^\\s*(?:export\\s+)?(?:async\\s+)?function\\s+(\\w+)\\s*[<(]");
        Pattern arrowFunctionPattern = Pattern.compile("^\\s*(?:export\\s+)?(?:const|let|var)\\s+(\\w+)\\s*[:=]\\s*(?:async\\s+)?\\([^)]*\\)\\s*=>");
        Pattern classPattern = Pattern.compile("^\\s*(?:export\\s+)?(?:default\\s+)?(?:abstract\\s+)?class\\s+(\\w+)");
        Pattern interfacePattern = Pattern.compile("^\\s*(?:export\\s+)?interface\\s+(\\w+)");
        Pattern typePattern = Pattern.compile("^\\s*(?:export\\s+)?type\\s+(\\w+)\\s*=");
        Pattern enumPattern = Pattern.compile("^\\s*(?:export\\s+)?enum\\s+(\\w+)");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // Skip comments and empty lines
            if (line.trim().startsWith("//") || line.trim().startsWith("/*") || line.trim().isEmpty()) {
                continue;
            }
            
            Matcher functionMatcher = functionPattern.matcher(line);
            if (functionMatcher.find()) {
                definitions.add(new CodeDefinition("function", functionMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher arrowMatcher = arrowFunctionPattern.matcher(line);
            if (arrowMatcher.find()) {
                definitions.add(new CodeDefinition("function", arrowMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher classMatcher = classPattern.matcher(line);
            if (classMatcher.find()) {
                definitions.add(new CodeDefinition("class", classMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher interfaceMatcher = interfacePattern.matcher(line);
            if (interfaceMatcher.find()) {
                definitions.add(new CodeDefinition("interface", interfaceMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher typeMatcher = typePattern.matcher(line);
            if (typeMatcher.find()) {
                definitions.add(new CodeDefinition("type", typeMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher enumMatcher = enumPattern.matcher(line);
            if (enumMatcher.find()) {
                definitions.add(new CodeDefinition("enum", enumMatcher.group(1), line.trim(), i + 1));
            }
        }
        
        return definitions;
    }

    /**
     * Analyze Python source code
     */
    private List<CodeDefinition> analyzePythonFile(String content) {
        List<CodeDefinition> definitions = new ArrayList<>();
        String[] lines = content.split("\n");
        
        // Patterns for Python constructs
        Pattern classPattern = Pattern.compile("^\\s*class\\s+(\\w+)\\s*[:(]");
        Pattern functionPattern = Pattern.compile("^\\s*(?:async\\s+)?def\\s+(\\w+)\\s*\\(");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // Skip comments and empty lines
            if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                continue;
            }
            
            Matcher classMatcher = classPattern.matcher(line);
            if (classMatcher.find()) {
                definitions.add(new CodeDefinition("class", classMatcher.group(1), line.trim(), i + 1));
                continue;
            }
            
            Matcher functionMatcher = functionPattern.matcher(line);
            if (functionMatcher.find()) {
                String funcName = functionMatcher.group(1);
                String type = funcName.startsWith("__") && funcName.endsWith("__") ? "method" : "function";
                definitions.add(new CodeDefinition(type, funcName, line.trim(), i + 1));
            }
        }
        
        return definitions;
    }

    /**
     * Analyze other language files with basic patterns
     */
    private List<CodeDefinition> analyzeGoFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "function", Pattern.compile("^\\s*func\\s+(\\w+)\\s*\\("),
            "type", Pattern.compile("^\\s*type\\s+(\\w+)\\s+(?:struct|interface)"),
            "const", Pattern.compile("^\\s*const\\s+(\\w+)")
        ));
    }

    private List<CodeDefinition> analyzeRustFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "function", Pattern.compile("^\\s*(?:pub\\s+)?fn\\s+(\\w+)\\s*[<(]"),
            "struct", Pattern.compile("^\\s*(?:pub\\s+)?struct\\s+(\\w+)"),
            "enum", Pattern.compile("^\\s*(?:pub\\s+)?enum\\s+(\\w+)"),
            "trait", Pattern.compile("^\\s*(?:pub\\s+)?trait\\s+(\\w+)")
        ));
    }

    private List<CodeDefinition> analyzeCppFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "class", Pattern.compile("^\\s*class\\s+(\\w+)"),
            "struct", Pattern.compile("^\\s*struct\\s+(\\w+)"),
            "function", Pattern.compile("^\\s*(?:\\w+\\s+)*?(\\w+)\\s*\\([^)]*\\)\\s*\\{")
        ));
    }

    private List<CodeDefinition> analyzeCSharpFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "class", Pattern.compile("^\\s*(?:public|private|protected)?\\s*(?:static)?\\s*class\\s+(\\w+)"),
            "interface", Pattern.compile("^\\s*(?:public|private|protected)?\\s*interface\\s+(\\w+)"),
            "method", Pattern.compile("^\\s*(?:public|private|protected)?\\s*(?:static)?\\s*(?:\\w+\\s+)?(\\w+)\\s*\\([^)]*\\)"),
            "enum", Pattern.compile("^\\s*(?:public|private|protected)?\\s*enum\\s+(\\w+)")
        ));
    }

    private List<CodeDefinition> analyzeRubyFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "class", Pattern.compile("^\\s*class\\s+(\\w+)"),
            "module", Pattern.compile("^\\s*module\\s+(\\w+)"),
            "method", Pattern.compile("^\\s*def\\s+(\\w+)")
        ));
    }

    private List<CodeDefinition> analyzePhpFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "class", Pattern.compile("^\\s*(?:abstract\\s+)?class\\s+(\\w+)"),
            "interface", Pattern.compile("^\\s*interface\\s+(\\w+)"),
            "function", Pattern.compile("^\\s*(?:public|private|protected)?\\s*function\\s+(\\w+)\\s*\\(")
        ));
    }

    private List<CodeDefinition> analyzeSwiftFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "class", Pattern.compile("^\\s*(?:public\\s+)?class\\s+(\\w+)"),
            "struct", Pattern.compile("^\\s*(?:public\\s+)?struct\\s+(\\w+)"),
            "function", Pattern.compile("^\\s*(?:public\\s+)?func\\s+(\\w+)\\s*[<(]"),
            "enum", Pattern.compile("^\\s*(?:public\\s+)?enum\\s+(\\w+)")
        ));
    }

    private List<CodeDefinition> analyzeKotlinFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "class", Pattern.compile("^\\s*(?:open\\s+|abstract\\s+)?class\\s+(\\w+)"),
            "interface", Pattern.compile("^\\s*interface\\s+(\\w+)"),
            "function", Pattern.compile("^\\s*(?:fun\\s+)?(\\w+)\\s*\\([^)]*\\)"),
            "object", Pattern.compile("^\\s*object\\s+(\\w+)")
        ));
    }

    private List<CodeDefinition> analyzeScalaFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "class", Pattern.compile("^\\s*(?:abstract\\s+)?class\\s+(\\w+)"),
            "object", Pattern.compile("^\\s*object\\s+(\\w+)"),
            "trait", Pattern.compile("^\\s*trait\\s+(\\w+)"),
            "def", Pattern.compile("^\\s*def\\s+(\\w+)")
        ));
    }

    private List<CodeDefinition> analyzeGroovyFile(String content) {
        return analyzeWithPatterns(content, Map.of(
            "class", Pattern.compile("^\\s*(?:abstract\\s+)?class\\s+(\\w+)"),
            "interface", Pattern.compile("^\\s*interface\\s+(\\w+)"),
            "def", Pattern.compile("^\\s*def\\s+(\\w+)\\s*\\(")
        ));
    }

    /**
     * Generic pattern-based analysis for various languages
     */
    private List<CodeDefinition> analyzeWithPatterns(String content, Map<String, Pattern> patterns) {
        List<CodeDefinition> definitions = new ArrayList<>();
        String[] lines = content.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // Skip comments and empty lines
            if (line.trim().startsWith("//") || line.trim().startsWith("#") || 
                line.trim().startsWith("/*") || line.trim().isEmpty()) {
                continue;
            }
            
            for (Map.Entry<String, Pattern> entry : patterns.entrySet()) {
                Matcher matcher = entry.getValue().matcher(line);
                if (matcher.find()) {
                    definitions.add(new CodeDefinition(entry.getKey(), matcher.group(1), line.trim(), i + 1));
                    break; // Only match first pattern per line
                }
            }
        }
        
        return definitions;
    }

    /**
     * Get file extension from path
     */
    private String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        
        return "";
    }

    /**
     * Code definition data class
     */
    private static class CodeDefinition {
        private final String type;
        private final String name;
        private final String line;
        private final int lineNumber;

        public CodeDefinition(String type, String name, String line, int lineNumber) {
            this.type = type;
            this.name = name;
            this.line = line;
            this.lineNumber = lineNumber;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getLine() {
            return line;
        }

        public int getLineNumber() {
            return lineNumber;
        }
    }
}
