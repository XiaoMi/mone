# SearchFilesTool Usage Guide

This document demonstrates how to use the newly implemented `SearchFilesTool` in the hive project.

## Tool Overview

`SearchFilesTool` is a Java implementation of the file content search tool, inspired by Cline's `search_files` tool. It performs regex searches across files in a specified directory, providing context-rich results that help you understand and analyze code patterns, find specific content, and explore codebases efficiently.

## Key Features

1. **Powerful Regex Search**: Full Java regex syntax support for complex pattern matching
2. **Recursive Directory Search**: Automatically searches through subdirectories
3. **File Type Filtering**: Use glob patterns to filter specific file types
4. **Context-Rich Results**: Shows lines before and after matches for better understanding
5. **Binary File Detection**: Automatically skips binary files to avoid noise
6. **Smart Directory Filtering**: Ignores common build/cache directories
7. **Performance Optimized**: Limits results and output size to prevent overwhelming responses
8. **Detailed Metadata**: Provides line numbers, column positions, and match details

## Supported Search Patterns

### Basic Text Search
```regex
TODO:
FIXME:
BUG:
```

### Function/Method Patterns
```regex
# Java methods
public\s+\w+\s+\w+\s*\(

# JavaScript functions
function\s+\w+\s*\(

# Python functions
def\s+\w+\s*\(
```

### Class/Interface Patterns
```regex
# Java classes
public\s+class\s+\w+

# Interface definitions
interface\s+\w+

# TypeScript types
type\s+\w+\s*=
```

### Configuration Patterns
```regex
# Spring properties
spring\.datasource\.|server\.port

# Environment variables
\$\{[^}]+\}

# API endpoints
@RequestMapping|@GetMapping|@PostMapping
```

## Usage Methods

### 1. Add Tool to ReactorRole

```java
ReactorRole role = new ReactorRole();
SearchFilesTool searchTool = new SearchFilesTool();
role.addTool(searchTool);
```

### 2. Basic Text Search

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src");
input.addProperty("regex", "TODO:");

JsonObject result = searchTool.execute(role, input);

if (result.has("result") && !result.has("error")) {
    String searchResults = result.get("result").getAsString();
    System.out.println("Search results:");
    System.out.println(searchResults);
    
    int totalMatches = result.get("totalMatches").getAsInt();
    System.out.println("Total matches found: " + totalMatches);
}
```

### 3. Search with File Pattern Filtering

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java");
input.addProperty("regex", "@RequestMapping|@GetMapping|@PostMapping");
input.addProperty("file_pattern", "*.java");

JsonObject result = searchTool.execute(role, input);

// Process detailed results
if (result.has("detailedResults")) {
    JsonArray detailedResults = result.getAsJsonArray("detailedResults");
    
    for (int i = 0; i < detailedResults.size(); i++) {
        JsonObject fileResult = detailedResults.get(i).getAsJsonObject();
        String fileName = fileResult.get("file").getAsString();
        int matchCount = fileResult.get("matches").getAsInt();
        
        System.out.println(fileName + " has " + matchCount + " API endpoints:");
        
        JsonArray matchDetails = fileResult.getAsJsonArray("matchDetails");
        for (int j = 0; j < matchDetails.size(); j++) {
            JsonObject match = matchDetails.get(j).getAsJsonObject();
            int lineNumber = match.get("line").getAsInt();
            String matchText = match.get("matchText").getAsString();
            String lineContent = match.get("lineContent").getAsString();
            
            System.out.println("  Line " + lineNumber + ": " + lineContent.trim());
        }
    }
}
```

### 4. Complex Regex Pattern Search

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src");
input.addProperty("regex", "(?i)\\b(password|secret|key|token)\\s*[=:]");

JsonObject result = searchTool.execute(role, input);

// Analyze security-sensitive patterns
if (result.has("summary")) {
    JsonObject summary = result.getAsJsonObject("summary");
    int filesWithMatches = summary.get("filesWithMatches").getAsInt();
    int totalMatches = summary.get("totalMatches").getAsInt();
    
    System.out.println("Security Analysis:");
    System.out.println("Files with sensitive patterns: " + filesWithMatches);
    System.out.println("Total sensitive patterns found: " + totalMatches);
}
```

## Common Use Cases

### 1. Code Quality Analysis

```java
public void analyzeCodeQuality(String projectPath) {
    SearchFilesTool searchTool = new SearchFilesTool();
    
    // Search for TODO comments
    JsonObject todoSearch = new JsonObject();
    todoSearch.addProperty("path", projectPath);
    todoSearch.addProperty("regex", "TODO:|FIXME:|BUG:|HACK:");
    JsonObject todoResults = searchTool.execute(role, todoSearch);
    
    // Search for deprecated usage
    JsonObject deprecatedSearch = new JsonObject();
    deprecatedSearch.addProperty("path", projectPath);
    deprecatedSearch.addProperty("regex", "@Deprecated|@deprecated");
    JsonObject deprecatedResults = searchTool.execute(role, deprecatedSearch);
    
    // Search for exception handling
    JsonObject exceptionSearch = new JsonObject();
    exceptionSearch.addProperty("path", projectPath);
    exceptionSearch.addProperty("regex", "catch\\s*\\([^)]*Exception[^)]*\\)");
    exceptionSearch.addProperty("file_pattern", "*.java");
    JsonObject exceptionResults = searchTool.execute(role, exceptionSearch);
    
    System.out.println("Code Quality Report:");
    System.out.println("TODO items: " + todoResults.get("totalMatches").getAsInt());
    System.out.println("Deprecated usage: " + deprecatedResults.get("totalMatches").getAsInt());
    System.out.println("Exception handlers: " + exceptionResults.get("totalMatches").getAsInt());
}
```

### 2. API Endpoint Discovery

```java
public List<String> findApiEndpoints(String controllerPath) {
    JsonObject input = new JsonObject();
    input.addProperty("path", controllerPath);
    input.addProperty("regex", "@(Get|Post|Put|Delete|Patch)Mapping\\s*\\([^)]*\\)");
    input.addProperty("file_pattern", "*Controller.java");
    
    JsonObject result = searchTool.execute(role, input);
    List<String> endpoints = new ArrayList<>();
    
    if (result.has("detailedResults")) {
        JsonArray detailedResults = result.getAsJsonArray("detailedResults");
        
        for (int i = 0; i < detailedResults.size(); i++) {
            JsonObject fileResult = detailedResults.get(i).getAsJsonObject();
            String fileName = fileResult.get("file").getAsString();
            
            JsonArray matchDetails = fileResult.getAsJsonArray("matchDetails");
            for (int j = 0; j < matchDetails.size(); j++) {
                JsonObject match = matchDetails.get(j).getAsJsonObject();
                String endpoint = fileName + ":" + match.get("line").getAsInt() + 
                               " - " + match.get("matchText").getAsString();
                endpoints.add(endpoint);
            }
        }
    }
    
    return endpoints;
}
```

### 3. Configuration Analysis

```java
public void analyzeConfiguration(String configPath) {
    SearchFilesTool searchTool = new SearchFilesTool();
    
    // Find database configurations
    JsonObject dbSearch = new JsonObject();
    dbSearch.addProperty("path", configPath);
    dbSearch.addProperty("regex", "spring\\.datasource\\.|jdbc\\.|hibernate\\.");
    dbSearch.addProperty("file_pattern", "*.properties");
    JsonObject dbResults = searchTool.execute(role, dbSearch);
    
    // Find server configurations
    JsonObject serverSearch = new JsonObject();
    serverSearch.addProperty("path", configPath);
    serverSearch.addProperty("regex", "server\\.|management\\.|logging\\.");
    serverSearch.addProperty("file_pattern", "*.properties");
    JsonObject serverResults = searchTool.execute(role, serverSearch);
    
    System.out.println("Configuration Analysis:");
    System.out.println("Database settings: " + dbResults.get("totalMatches").getAsInt());
    System.out.println("Server settings: " + serverResults.get("totalMatches").getAsInt());
    
    // Show actual configuration values
    displayConfigurationResults(dbResults, "Database Configuration");
    displayConfigurationResults(serverResults, "Server Configuration");
}

private void displayConfigurationResults(JsonObject results, String category) {
    System.out.println("\n" + category + ":");
    if (results.has("detailedResults")) {
        JsonArray detailedResults = results.getAsJsonArray("detailedResults");
        
        for (int i = 0; i < detailedResults.size(); i++) {
            JsonObject fileResult = detailedResults.get(i).getAsJsonObject();
            String fileName = fileResult.get("file").getAsString();
            
            System.out.println("  " + fileName + ":");
            JsonArray matchDetails = fileResult.getAsJsonArray("matchDetails");
            for (int j = 0; j < matchDetails.size(); j++) {
                JsonObject match = matchDetails.get(j).getAsJsonObject();
                String lineContent = match.get("lineContent").getAsString();
                System.out.println("    " + lineContent.trim());
            }
        }
    }
}
```

### 4. Security Pattern Detection

```java
public void performSecurityAudit(String sourcePath) {
    SearchFilesTool searchTool = new SearchFilesTool();
    
    // Search for hardcoded secrets
    JsonObject secretSearch = new JsonObject();
    secretSearch.addProperty("path", sourcePath);
    secretSearch.addProperty("regex", "(?i)(password|secret|key|token)\\s*[=:]\\s*[\"'][^\"']{3,}[\"']");
    JsonObject secretResults = searchTool.execute(role, secretSearch);
    
    // Search for SQL injection vulnerabilities
    JsonObject sqlSearch = new JsonObject();
    sqlSearch.addProperty("path", sourcePath);
    sqlSearch.addProperty("regex", "\"\\s*\\+\\s*\\w+\\s*\\+\\s*\"|String\\.format.*%s.*%s");
    sqlSearch.addProperty("file_pattern", "*.java");
    JsonObject sqlResults = searchTool.execute(role, sqlSearch);
    
    // Search for unsafe deserialization
    JsonObject deserializeSearch = new JsonObject();
    deserializeSearch.addProperty("path", sourcePath);
    deserializeSearch.addProperty("regex", "ObjectInputStream|readObject|XMLDecoder");
    deserializeSearch.addProperty("file_pattern", "*.java");
    JsonObject deserializeResults = searchTool.execute(role, deserializeSearch);
    
    System.out.println("Security Audit Results:");
    System.out.println("Potential hardcoded secrets: " + secretResults.get("totalMatches").getAsInt());
    System.out.println("Potential SQL injection points: " + sqlResults.get("totalMatches").getAsInt());
    System.out.println("Unsafe deserialization usage: " + deserializeResults.get("totalMatches").getAsInt());
    
    // Show details for critical findings
    if (secretResults.get("totalMatches").getAsInt() > 0) {
        System.out.println("\n⚠️ CRITICAL: Hardcoded secrets found!");
        System.out.println(secretResults.get("result").getAsString());
    }
}
```

### 5. Dependency Analysis

```java
public void analyzeDependencies(String sourcePath) {
    SearchFilesTool searchTool = new SearchFilesTool();
    
    // Find import statements
    JsonObject importSearch = new JsonObject();
    importSearch.addProperty("path", sourcePath);
    importSearch.addProperty("regex", "^import\\s+[^;]+;");
    importSearch.addProperty("file_pattern", "*.java");
    JsonObject importResults = searchTool.execute(role, importSearch);
    
    // Find specific framework usage
    JsonObject springSearch = new JsonObject();
    springSearch.addProperty("path", sourcePath);
    springSearch.addProperty("regex", "@(Component|Service|Repository|Controller|RestController|Autowired)");
    springSearch.addProperty("file_pattern", "*.java");
    JsonObject springResults = searchTool.execute(role, springSearch);
    
    // Analyze import patterns
    Map<String, Integer> packageUsage = analyzeImportPatterns(importResults);
    
    System.out.println("Dependency Analysis:");
    System.out.println("Total imports: " + importResults.get("totalMatches").getAsInt());
    System.out.println("Spring annotations: " + springResults.get("totalMatches").getAsInt());
    
    System.out.println("\nTop package usage:");
    packageUsage.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .limit(10)
        .forEach(entry -> System.out.println("  " + entry.getKey() + ": " + entry.getValue()));
}

private Map<String, Integer> analyzeImportPatterns(JsonObject importResults) {
    Map<String, Integer> packageUsage = new HashMap<>();
    
    if (importResults.has("detailedResults")) {
        JsonArray detailedResults = importResults.getAsJsonArray("detailedResults");
        
        for (int i = 0; i < detailedResults.size(); i++) {
            JsonObject fileResult = detailedResults.get(i).getAsJsonObject();
            JsonArray matchDetails = fileResult.getAsJsonArray("matchDetails");
            
            for (int j = 0; j < matchDetails.size(); j++) {
                JsonObject match = matchDetails.get(j).getAsJsonObject();
                String lineContent = match.get("lineContent").getAsString();
                
                // Extract package name from import statement
                String packageName = extractPackageName(lineContent);
                if (packageName != null) {
                    packageUsage.put(packageName, packageUsage.getOrDefault(packageName, 0) + 1);
                }
            }
        }
    }
    
    return packageUsage;
}

private String extractPackageName(String importLine) {
    // Extract base package from import statement
    // e.g., "import java.util.List;" -> "java.util"
    String[] parts = importLine.replace("import ", "").replace(";", "").split("\\.");
    if (parts.length >= 2) {
        return parts[0] + "." + parts[1];
    }
    return null;
}
```

### 6. Test Coverage Analysis

```java
public void analyzeTestCoverage(String sourcePath, String testPath) {
    SearchFilesTool searchTool = new SearchFilesTool();
    
    // Find test methods
    JsonObject testSearch = new JsonObject();
    testSearch.addProperty("path", testPath);
    testSearch.addProperty("regex", "@Test|@ParameterizedTest|@RepeatedTest");
    testSearch.addProperty("file_pattern", "*Test.java");
    JsonObject testResults = searchTool.execute(role, testSearch);
    
    // Find assertion statements
    JsonObject assertSearch = new JsonObject();
    assertSearch.addProperty("path", testPath);
    assertSearch.addProperty("regex", "assert(True|False|Equals|NotNull|Null|That)");
    assertSearch.addProperty("file_pattern", "*Test.java");
    JsonObject assertResults = searchTool.execute(role, assertSearch);
    
    // Find mock usage
    JsonObject mockSearch = new JsonObject();
    mockSearch.addProperty("path", testPath);
    mockSearch.addProperty("regex", "@Mock|@MockBean|Mockito\\.|when\\(|verify\\(");
    mockSearch.addProperty("file_pattern", "*Test.java");
    JsonObject mockResults = searchTool.execute(role, mockSearch);
    
    System.out.println("Test Coverage Analysis:");
    System.out.println("Test methods: " + testResults.get("totalMatches").getAsInt());
    System.out.println("Assertions: " + assertResults.get("totalMatches").getAsInt());
    System.out.println("Mock usage: " + mockResults.get("totalMatches").getAsInt());
    
    // Calculate coverage metrics
    JsonObject summary = testResults.getAsJsonObject("summary");
    int testFiles = summary.get("filesWithMatches").getAsInt();
    int testMethods = testResults.get("totalMatches").getAsInt();
    
    System.out.println("Test files: " + testFiles);
    System.out.println("Average tests per file: " + (testFiles > 0 ? testMethods / testFiles : 0));
}
```

## Return Format

### Successful Response Structure

```json
{
  "result": "file1.java\n│----\n│// Line before match\n│public class TestClass {\n│// Line after match\n│----\n\nfile2.java\n│----\n│private void testMethod() {\n│----\n",
  "searchPath": "src/main/java",
  "regex": "public\\s+class\\s+\\w+",
  "filePattern": "*.java",
  "totalMatches": 2,
  "wasLimited": false,
  "detailedResults": [
    {
      "file": "file1.java",
      "matches": 1,
      "matchDetails": [
        {
          "line": 5,
          "column": 1,
          "matchText": "public class TestClass",
          "lineContent": "public class TestClass {"
        }
      ]
    }
  ],
  "summary": {
    "filesSearched": 10,
    "filesWithMatches": 2,
    "totalMatches": 2,
    "wasLimited": false
  }
}
```

### Error Response

```json
{
  "error": "Invalid regex pattern: Unclosed character class near index 15"
}
```

## Advanced Search Patterns

### 1. Method Signature Patterns

```java
// Find all public methods
input.addProperty("regex", "public\\s+\\w+\\s+\\w+\\s*\\([^)]*\\)");

// Find getter methods
input.addProperty("regex", "public\\s+\\w+\\s+get\\w+\\s*\\(\\s*\\)");

// Find setter methods
input.addProperty("regex", "public\\s+void\\s+set\\w+\\s*\\([^)]+\\)");

// Find main methods
input.addProperty("regex", "public\\s+static\\s+void\\s+main\\s*\\(String\\[\\]");
```

### 2. Annotation Patterns

```java
// Spring annotations
input.addProperty("regex", "@(Component|Service|Repository|Controller|RestController)");

// JPA annotations
input.addProperty("regex", "@(Entity|Table|Column|Id|GeneratedValue)");

// Validation annotations
input.addProperty("regex", "@(NotNull|NotEmpty|NotBlank|Valid|Size|Min|Max)");

// Test annotations
input.addProperty("regex", "@(Test|BeforeEach|AfterEach|ParameterizedTest)");
```

### 3. Configuration Patterns

```java
// Environment variables
input.addProperty("regex", "\\$\\{[^}]+\\}");

// Property placeholders
input.addProperty("regex", "@Value\\s*\\([^)]+\\)");

// Configuration properties
input.addProperty("regex", "spring\\.(datasource|jpa|security)\\.");

// Logging configurations
input.addProperty("regex", "logging\\.(level|pattern|file)\\.");
```

## Integration with Other Tools

### Combined Analysis Workflow

```java
// 1. List directory to understand structure
ListFilesTool listTool = new ListFilesTool();
JsonObject listInput = new JsonObject();
listInput.addProperty("path", "src/main/java");
JsonObject listResult = listTool.execute(role, listInput);

// 2. Search for specific patterns in the directory
SearchFilesTool searchTool = new SearchFilesTool();
JsonObject searchInput = new JsonObject();
searchInput.addProperty("path", "src/main/java");
searchInput.addProperty("regex", "@RestController");
JsonObject searchResult = searchTool.execute(role, searchInput);

// 3. Read specific files found in search for detailed analysis
ReadFileTool readTool = new ReadFileTool();
if (searchResult.has("detailedResults")) {
    JsonArray detailedResults = searchResult.getAsJsonArray("detailedResults");
    
    for (int i = 0; i < detailedResults.size(); i++) {
        JsonObject fileResult = detailedResults.get(i).getAsJsonObject();
        String filePath = "src/main/java/" + fileResult.get("file").getAsString();
        
        JsonObject readInput = new JsonObject();
        readInput.addProperty("path", filePath);
        JsonObject readResult = readTool.execute(role, readInput);
        
        // Perform detailed analysis on controller files
        analyzeControllerFile(readResult, fileResult);
    }
}
```

## Performance Considerations

- **Result Limits**: Search is limited to 300 matches to prevent overwhelming output
- **File Size Limits**: Files larger than 1MB are skipped to maintain performance
- **Output Size Limits**: Results are truncated at 0.25MB to prevent memory issues
- **Binary Detection**: Automatically skips binary files to avoid noise
- **Directory Filtering**: Ignores common build/cache directories for efficiency

## Best Practices

### 1. Use Specific Patterns

```java
// Good: Specific pattern
input.addProperty("regex", "@RequestMapping\\s*\\([^)]*value\\s*=\\s*[\"'][^\"']+[\"']");

// Less optimal: Too broad
input.addProperty("regex", "RequestMapping");
```

### 2. Combine with File Filtering

```java
// Good: Filter by file type
input.addProperty("file_pattern", "*.java");

// Less optimal: Search all files
// (no file_pattern specified)
```

### 3. Use Word Boundaries

```java
// Good: Exact word match
input.addProperty("regex", "\\bTODO\\b");

// Less optimal: Partial matches
input.addProperty("regex", "TODO");
```

### 4. Handle Case Sensitivity

```java
// Case-insensitive search
input.addProperty("regex", "(?i)error|exception|fail");

// Case-sensitive search
input.addProperty("regex", "Error|Exception|Fail");
```

This tool provides powerful file content search capabilities for the hive AI assistant system, fully compatible with Cline's search_files tool specifications while offering enhanced Java regex support and comprehensive file filtering options.
