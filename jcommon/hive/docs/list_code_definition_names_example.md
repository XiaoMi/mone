# ListCodeDefinitionNamesTool Usage Guide

This document demonstrates how to use the newly implemented `ListCodeDefinitionNamesTool` in the hive project.

## Tool Overview

`ListCodeDefinitionNamesTool` is a Java implementation of the code analysis tool, inspired by Cline's `list_code_definition_names` tool. It analyzes source code files to extract definition names (classes, functions, methods, etc.) and provides insights into the codebase structure and important constructs.

## Key Features

1. **Multi-Language Support**: Analyzes 14+ programming languages including Java, JavaScript, TypeScript, Python, Go, Rust, C/C++, C#, Ruby, PHP, Swift, Kotlin, Scala, and Groovy
2. **Definition Extraction**: Identifies classes, interfaces, functions, methods, enums, types, and constants
3. **Structured Output**: Provides both human-readable and machine-parseable results
4. **Performance Optimized**: Limits analysis to prevent overwhelming output
5. **Top-Level Analysis**: Focuses on files in the specified directory (non-recursive)
6. **Detailed Metadata**: Includes line numbers, definition types, and file information

## Supported Languages and Constructs

### Java
- **Classes**: `public class ClassName`
- **Interfaces**: `public interface InterfaceName`  
- **Enums**: `public enum EnumName`
- **Methods**: `public void methodName()`
- **Constructors**: `public ClassName()`
- **Constants**: `static final` fields

### JavaScript/TypeScript
- **Classes**: `class ClassName`, `export class ClassName`
- **Functions**: `function functionName()`, `export function functionName()`
- **Arrow Functions**: `const functionName = () => {}`
- **Interfaces** (TS): `interface InterfaceName`
- **Types** (TS): `type TypeName = ...`
- **Enums** (TS): `enum EnumName`

### Python
- **Classes**: `class ClassName:`
- **Functions**: `def function_name():`, `async def function_name():`
- **Methods**: Instance and static methods within classes

### Go
- **Functions**: `func functionName()`
- **Types**: `type TypeName struct`, `type InterfaceName interface`
- **Constants**: `const ConstantName`

### Other Languages
- **Rust**: Functions, structs, enums, traits
- **C/C++**: Classes, structs, functions
- **C#**: Classes, interfaces, methods, enums
- **Ruby**: Classes, modules, methods
- **PHP**: Classes, interfaces, functions
- **Swift**: Classes, structs, functions, enums
- **Kotlin**: Classes, interfaces, functions, objects
- **Scala**: Classes, objects, traits, methods
- **Groovy**: Classes, interfaces, methods

## Usage Methods

### 1. Add Tool to ReactorRole

```java
ReactorRole role = new ReactorRole();
ListCodeDefinitionNamesTool codeTool = new ListCodeDefinitionNamesTool();
role.addTool(codeTool);
```

### 2. Analyze Java Source Directory

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java");

JsonObject result = codeTool.execute(role, input);

if (result.has("result") && !result.has("error")) {
    String definitions = result.get("result").getAsString();
    System.out.println("Code definitions found:");
    System.out.println(definitions);
    
    // Get summary statistics
    JsonObject summary = result.getAsJsonObject("summary");
    System.out.println("Files analyzed: " + summary.get("filesAnalyzed").getAsInt());
    System.out.println("Total definitions: " + summary.get("totalDefinitions").getAsInt());
}
```

### 3. Analyze Specific Package

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java/com/example/service");

JsonObject result = codeTool.execute(role, input);

// Process detailed file analysis
if (result.has("filesAnalyzed")) {
    JsonArray filesAnalyzed = result.getAsJsonArray("filesAnalyzed");
    
    for (int i = 0; i < filesAnalyzed.size(); i++) {
        JsonObject fileInfo = filesAnalyzed.get(i).getAsJsonObject();
        String fileName = fileInfo.get("path").getAsString();
        int defCount = fileInfo.get("definitionCount").getAsInt();
        
        System.out.println(fileName + " contains " + defCount + " definitions:");
        
        JsonArray definitions = fileInfo.getAsJsonArray("definitions");
        for (int j = 0; j < definitions.size(); j++) {
            JsonObject def = definitions.get(j).getAsJsonObject();
            String type = def.get("type").getAsString();
            String name = def.get("name").getAsString();
            int lineNumber = def.get("lineNumber").getAsInt();
            
            System.out.println("  - " + type + ": " + name + " (line " + lineNumber + ")");
        }
    }
}
```

### 4. Analyze JavaScript/TypeScript Project

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/components");

JsonObject result = codeTool.execute(role, input);

// Filter for specific definition types
if (result.has("filesAnalyzed")) {
    JsonArray filesAnalyzed = result.getAsJsonArray("filesAnalyzed");
    
    for (int i = 0; i < filesAnalyzed.size(); i++) {
        JsonObject fileInfo = filesAnalyzed.get(i).getAsJsonObject();
        JsonArray definitions = fileInfo.getAsJsonArray("definitions");
        
        List<String> classes = new ArrayList<>();
        List<String> functions = new ArrayList<>();
        
        for (int j = 0; j < definitions.size(); j++) {
            JsonObject def = definitions.get(j).getAsJsonObject();
            String type = def.get("type").getAsString();
            String name = def.get("name").getAsString();
            
            if ("class".equals(type)) {
                classes.add(name);
            } else if ("function".equals(type)) {
                functions.add(name);
            }
        }
        
        System.out.println(fileInfo.get("path").getAsString() + ":");
        System.out.println("  Classes: " + String.join(", ", classes));
        System.out.println("  Functions: " + String.join(", ", functions));
    }
}
```

## Common Use Cases

### 1. Project Architecture Analysis

```java
public void analyzeProjectArchitecture(String projectPath) {
    ListCodeDefinitionNamesTool codeTool = new ListCodeDefinitionNamesTool();
    
    // Analyze different layers
    String[] layers = {"controller", "service", "repository", "entity", "dto"};
    
    for (String layer : layers) {
        JsonObject input = new JsonObject();
        input.addProperty("path", projectPath + "/" + layer);
        
        JsonObject result = codeTool.execute(role, input);
        
        if (!result.has("error")) {
            JsonObject summary = result.getAsJsonObject("summary");
            System.out.println(layer.toUpperCase() + " Layer:");
            System.out.println("  Files: " + summary.get("sourceFilesFound").getAsInt());
            System.out.println("  Definitions: " + summary.get("totalDefinitions").getAsInt());
            
            // Analyze definition types
            analyzeDefinitionTypes(result);
        }
    }
}

private void analyzeDefinitionTypes(JsonObject result) {
    Map<String, Integer> typeCount = new HashMap<>();
    
    if (result.has("filesAnalyzed")) {
        JsonArray filesAnalyzed = result.getAsJsonArray("filesAnalyzed");
        
        for (int i = 0; i < filesAnalyzed.size(); i++) {
            JsonObject fileInfo = filesAnalyzed.get(i).getAsJsonObject();
            JsonArray definitions = fileInfo.getAsJsonArray("definitions");
            
            for (int j = 0; j < definitions.size(); j++) {
                JsonObject def = definitions.get(j).getAsJsonObject();
                String type = def.get("type").getAsString();
                typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            }
        }
    }
    
    typeCount.forEach((type, count) -> 
        System.out.println("  " + type + ": " + count));
}
```

### 2. API Endpoint Discovery

```java
public List<String> findControllerEndpoints(String controllerPath) {
    JsonObject input = new JsonObject();
    input.addProperty("path", controllerPath);
    
    JsonObject result = codeTool.execute(role, input);
    List<String> endpoints = new ArrayList<>();
    
    if (result.has("filesAnalyzed")) {
        JsonArray filesAnalyzed = result.getAsJsonArray("filesAnalyzed");
        
        for (int i = 0; i < filesAnalyzed.size(); i++) {
            JsonObject fileInfo = filesAnalyzed.get(i).getAsJsonObject();
            String fileName = fileInfo.get("path").getAsString();
            
            // Only analyze controller files
            if (fileName.toLowerCase().contains("controller")) {
                JsonArray definitions = fileInfo.getAsJsonArray("definitions");
                
                for (int j = 0; j < definitions.size(); j++) {
                    JsonObject def = definitions.get(j).getAsJsonObject();
                    if ("method".equals(def.get("type").getAsString())) {
                        String methodName = def.get("name").getAsString();
                        endpoints.add(fileName + ":" + methodName);
                    }
                }
            }
        }
    }
    
    return endpoints;
}
```

### 3. Code Complexity Assessment

```java
public void assessCodeComplexity(String sourcePath) {
    JsonObject input = new JsonObject();
    input.addProperty("path", sourcePath);
    
    JsonObject result = codeTool.execute(role, input);
    
    if (!result.has("error")) {
        JsonArray filesAnalyzed = result.getAsJsonArray("filesAnalyzed");
        
        System.out.println("Code Complexity Assessment:");
        System.out.println("===========================");
        
        for (int i = 0; i < filesAnalyzed.size(); i++) {
            JsonObject fileInfo = filesAnalyzed.get(i).getAsJsonObject();
            String fileName = fileInfo.get("path").getAsString();
            int defCount = fileInfo.get("definitionCount").getAsInt();
            
            String complexity;
            if (defCount > 20) {
                complexity = "High";
            } else if (defCount > 10) {
                complexity = "Medium";
            } else {
                complexity = "Low";
            }
            
            System.out.println(fileName + ": " + defCount + " definitions (" + complexity + " complexity)");
        }
    }
}
```

### 4. Interface and Implementation Mapping

```java
public void mapInterfacesToImplementations(String sourcePath) {
    JsonObject input = new JsonObject();
    input.addProperty("path", sourcePath);
    
    JsonObject result = codeTool.execute(role, input);
    
    Set<String> interfaces = new HashSet<>();
    Set<String> classes = new HashSet<>();
    
    if (result.has("filesAnalyzed")) {
        JsonArray filesAnalyzed = result.getAsJsonArray("filesAnalyzed");
        
        // Collect interfaces and classes
        for (int i = 0; i < filesAnalyzed.size(); i++) {
            JsonObject fileInfo = filesAnalyzed.get(i).getAsJsonObject();
            JsonArray definitions = fileInfo.getAsJsonArray("definitions");
            
            for (int j = 0; j < definitions.size(); j++) {
                JsonObject def = definitions.get(j).getAsJsonObject();
                String type = def.get("type").getAsString();
                String name = def.get("name").getAsString();
                
                if ("interface".equals(type)) {
                    interfaces.add(name);
                } else if ("class".equals(type)) {
                    classes.add(name);
                }
            }
        }
    }
    
    System.out.println("Interfaces found: " + interfaces.size());
    interfaces.forEach(iface -> System.out.println("  - " + iface));
    
    System.out.println("Classes found: " + classes.size());
    classes.forEach(cls -> System.out.println("  - " + cls));
    
    // Find potential implementations (classes ending with interface name + "Impl")
    System.out.println("\nPotential Implementations:");
    for (String iface : interfaces) {
        for (String cls : classes) {
            if (cls.toLowerCase().contains(iface.toLowerCase()) && 
                (cls.endsWith("Impl") || cls.endsWith("Implementation"))) {
                System.out.println("  " + iface + " -> " + cls);
            }
        }
    }
}
```

### 5. Test Coverage Analysis

```java
public void analyzeTestCoverage(String srcPath, String testPath) {
    // Analyze source code
    JsonObject srcInput = new JsonObject();
    srcInput.addProperty("path", srcPath);
    JsonObject srcResult = codeTool.execute(role, srcInput);
    
    // Analyze test code
    JsonObject testInput = new JsonObject();
    testInput.addProperty("path", testPath);
    JsonObject testResult = codeTool.execute(role, testInput);
    
    Set<String> sourceClasses = extractClassNames(srcResult);
    Set<String> testClasses = extractClassNames(testResult);
    
    System.out.println("Test Coverage Analysis:");
    System.out.println("======================");
    System.out.println("Source classes: " + sourceClasses.size());
    System.out.println("Test classes: " + testClasses.size());
    
    // Find classes without tests
    Set<String> untestedClasses = new HashSet<>(sourceClasses);
    for (String testClass : testClasses) {
        String baseClassName = testClass.replace("Test", "").replace("Spec", "");
        untestedClasses.remove(baseClassName);
    }
    
    if (!untestedClasses.isEmpty()) {
        System.out.println("\nClasses without tests:");
        untestedClasses.forEach(cls -> System.out.println("  - " + cls));
    } else {
        System.out.println("\nAll classes have corresponding test files!");
    }
}

private Set<String> extractClassNames(JsonObject result) {
    Set<String> classNames = new HashSet<>();
    
    if (result.has("filesAnalyzed")) {
        JsonArray filesAnalyzed = result.getAsJsonArray("filesAnalyzed");
        
        for (int i = 0; i < filesAnalyzed.size(); i++) {
            JsonObject fileInfo = filesAnalyzed.get(i).getAsJsonObject();
            JsonArray definitions = fileInfo.getAsJsonArray("definitions");
            
            for (int j = 0; j < definitions.size(); j++) {
                JsonObject def = definitions.get(j).getAsJsonObject();
                if ("class".equals(def.get("type").getAsString())) {
                    classNames.add(def.get("name").getAsString());
                }
            }
        }
    }
    
    return classNames;
}
```

## Return Format

### Successful Response Structure

```json
{
  "result": "UserService.java\n|----\n│public class UserService {\n│    public UserService() {\n│    public String getName() {\n│    public void setName(String name) {\n|----\n\nUserController.java\n|----\n│public class UserController {\n│    public ResponseEntity<User> getUser(@PathVariable Long id) {\n|----\n",
  "totalFiles": 2,
  "analyzedFiles": 2,
  "totalDefinitions": 5,
  "directoryPath": "src/main/java/com/example",
  "filesAnalyzed": [
    {
      "path": "UserService.java",
      "definitionCount": 3,
      "definitions": [
        {
          "type": "class",
          "name": "UserService",
          "line": "public class UserService {",
          "lineNumber": 5
        },
        {
          "type": "constructor",
          "name": "UserService",
          "line": "    public UserService() {",
          "lineNumber": 8
        },
        {
          "type": "method",
          "name": "getName",
          "line": "    public String getName() {",
          "lineNumber": 12
        }
      ]
    }
  ],
  "summary": {
    "sourceFilesFound": 2,
    "filesAnalyzed": 2,
    "totalDefinitions": 5,
    "wasLimited": false
  }
}
```

### Error Response

```json
{
  "error": "This directory does not exist or you do not have permission to access it."
}
```

## Integration with Other Tools

### Combined Analysis Workflow

```java
// 1. List directory contents to understand structure
ListFilesTool listTool = new ListFilesTool();
JsonObject listInput = new JsonObject();
listInput.addProperty("path", "src/main/java");
JsonObject listResult = listTool.execute(role, listInput);

// 2. Analyze code definitions in the directory
ListCodeDefinitionNamesTool codeTool = new ListCodeDefinitionNamesTool();
JsonObject codeInput = new JsonObject();
codeInput.addProperty("path", "src/main/java");
JsonObject codeResult = codeTool.execute(role, codeInput);

// 3. Read specific files for detailed analysis
ReadFileTool readTool = new ReadFileTool();
if (codeResult.has("filesAnalyzed")) {
    JsonArray filesAnalyzed = codeResult.getAsJsonArray("filesAnalyzed");
    
    for (int i = 0; i < filesAnalyzed.size(); i++) {
        JsonObject fileInfo = filesAnalyzed.get(i).getAsJsonObject();
        String filePath = "src/main/java/" + fileInfo.get("path").getAsString();
        
        JsonObject readInput = new JsonObject();
        readInput.addProperty("path", filePath);
        JsonObject readResult = readTool.execute(role, readInput);
        
        // Perform detailed analysis on file content
        analyzeFileContent(readResult, fileInfo);
    }
}
```

## Performance Considerations

- **File Limit**: Analysis is limited to 50 files to prevent overwhelming output
- **Top-Level Only**: Only analyzes files directly in the specified directory (non-recursive)
- **Regex-Based**: Uses efficient regular expression patterns for fast parsing
- **Memory Efficient**: Processes files one at a time without loading entire directory into memory

## Limitations

- **Regex-Based Parsing**: May miss complex or unconventional syntax patterns
- **No Semantic Analysis**: Does not understand code semantics, only syntactic patterns
- **Top-Level Only**: Does not recursively analyze subdirectories
- **Language-Specific**: Accuracy varies by programming language and coding style

## Best Practices

### 1. Use on Focused Directories

```java
// Good: Analyze specific package
input.addProperty("path", "src/main/java/com/example/service");

// Less optimal: Analyze entire source tree (may hit file limits)
input.addProperty("path", "src");
```

### 2. Combine with File Listing

```java
// First understand directory structure
JsonObject listResult = listTool.execute(role, listInput);

// Then analyze code definitions
JsonObject codeResult = codeTool.execute(role, codeInput);
```

### 3. Handle Large Projects

```java
// For large projects, analyze by layers or modules
String[] modules = {"user", "order", "payment", "notification"};

for (String module : modules) {
    JsonObject input = new JsonObject();
    input.addProperty("path", "src/main/java/com/example/" + module);
    JsonObject result = codeTool.execute(role, input);
    
    processModuleAnalysis(module, result);
}
```

### 4. Error Handling

```java
JsonObject result = codeTool.execute(role, input);

if (result.has("error")) {
    String error = result.get("error").getAsString();
    if (error.contains("does not exist")) {
        System.out.println("Directory not found, skipping analysis");
    } else if (error.contains("not a directory")) {
        System.out.println("Path points to a file, not a directory");
    } else {
        System.out.println("Analysis failed: " + error);
    }
} else {
    processSuccessfulAnalysis(result);
}
```

This tool provides comprehensive code structure analysis capabilities for the hive AI assistant system, fully compatible with Cline's list_code_definition_names tool specifications while offering enhanced multi-language support and detailed metadata extraction.
