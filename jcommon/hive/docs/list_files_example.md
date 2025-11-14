# ListFilesTool Usage Guide

This document demonstrates how to use the newly implemented `ListFilesTool` in the hive project.

## Tool Overview

`ListFilesTool` is a Java implementation of the directory listing tool, inspired by Cline's `list_files` tool. It allows you to explore file system structure by listing files and directories within specified directories, with support for both recursive and non-recursive listing modes.

## Key Features

1. **Recursive and Non-Recursive Listing**: Choose between top-level or deep directory exploration
2. **Rich File Metadata**: Provides file size, type, modification time, and extension information
3. **Smart Filtering**: Automatically filters common build directories and hidden files during recursive listing
4. **Visual Output**: Includes file type icons and human-readable formatting
5. **Safety Limits**: Prevents overwhelming output with configurable limits and depth restrictions
6. **Detailed Statistics**: Provides summary information about directory contents

## Usage Methods

### 1. Add Tool to ReactorRole

```java
ReactorRole role = new ReactorRole();
ListFilesTool listFilesTool = new ListFilesTool();
role.addTool(listFilesTool);
```

### 2. List Top-Level Directory Contents

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src");

JsonObject result = listFilesTool.execute(role, input);

if (result.has("files")) {
    JsonArray files = result.getAsJsonArray("files");
    System.out.println("Found " + files.size() + " items in src/");
    
    for (int i = 0; i < files.size(); i++) {
        JsonObject file = files.get(i).getAsJsonObject();
        String name = file.get("name").getAsString();
        String type = file.get("type").getAsString();
        System.out.println("- " + name + " (" + type + ")");
    }
}
```

### 3. Recursive Directory Exploration

```java
JsonObject input = new JsonObject();
input.addProperty("path", ".");
input.addProperty("recursive", "true");

JsonObject result = listFilesTool.execute(role, input);

if (result.has("summary")) {
    JsonObject summary = result.getAsJsonObject("summary");
    long totalFiles = summary.get("totalFiles").getAsLong();
    long totalDirs = summary.get("totalDirectories").getAsLong();
    String totalSize = summary.get("totalSizeFormatted").getAsString();
    
    System.out.println("Project contains:");
    System.out.println("- " + totalFiles + " files");
    System.out.println("- " + totalDirs + " directories");
    System.out.println("- Total size: " + totalSize);
}
```

### 4. Explore Specific Package Structure

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java/com/example");
input.addProperty("recursive", "false");

JsonObject result = listFilesTool.execute(role, input);

// Get formatted output for display
String formattedOutput = result.get("result").getAsString();
System.out.println(formattedOutput);
```

## Common Use Cases

### 1. Project Structure Analysis

```java
// Get overview of entire project
JsonObject input = new JsonObject();
input.addProperty("path", ".");
input.addProperty("recursive", "true");
JsonObject result = listFilesTool.execute(role, input);

// Analyze project structure
JsonArray files = result.getAsJsonArray("files");
Map<String, Integer> extensionCounts = new HashMap<>();

for (int i = 0; i < files.size(); i++) {
    JsonObject file = files.get(i).getAsJsonObject();
    if (file.has("extension") && !file.get("isDirectory").getAsBoolean()) {
        String ext = file.get("extension").getAsString();
        extensionCounts.put(ext, extensionCounts.getOrDefault(ext, 0) + 1);
    }
}

System.out.println("File types in project:");
extensionCounts.forEach((ext, count) -> 
    System.out.println("- ." + ext + ": " + count + " files"));
```

### 2. Source Code Exploration

```java
// Explore source directory structure
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java");
input.addProperty("recursive", "true");
JsonObject result = listFilesTool.execute(role, input);

// Find all Java classes
JsonArray files = result.getAsJsonArray("files");
List<String> javaFiles = new ArrayList<>();

for (int i = 0; i < files.size(); i++) {
    JsonObject file = files.get(i).getAsJsonObject();
    if (!file.get("isDirectory").getAsBoolean() && 
        file.has("extension") && "java".equals(file.get("extension").getAsString())) {
        javaFiles.add(file.get("path").getAsString());
    }
}

System.out.println("Java classes found:");
javaFiles.forEach(System.out::println);
```

### 3. Configuration File Discovery

```java
// Find configuration files
JsonObject input = new JsonObject();
input.addProperty("path", ".");
input.addProperty("recursive", "true");
JsonObject result = listFilesTool.execute(role, input);

JsonArray files = result.getAsJsonArray("files");
List<String> configFiles = new ArrayList<>();
Set<String> configExtensions = Set.of("properties", "yml", "yaml", "json", "xml", "conf");

for (int i = 0; i < files.size(); i++) {
    JsonObject file = files.get(i).getAsJsonObject();
    if (file.has("extension") && 
        configExtensions.contains(file.get("extension").getAsString())) {
        configFiles.add(file.get("path").getAsString());
    }
}

System.out.println("Configuration files:");
configFiles.forEach(System.out::println);
```

### 4. Build Output Analysis

```java
// Check build output directories
String[] buildPaths = {"target", "build", "dist", "out"};

for (String buildPath : buildPaths) {
    JsonObject input = new JsonObject();
    input.addProperty("path", buildPath);
    input.addProperty("recursive", "false");
    
    JsonObject result = listFilesTool.execute(role, input);
    
    if (!result.has("error")) {
        JsonObject summary = result.getAsJsonObject("summary");
        System.out.println(buildPath + "/ contains " + 
            summary.get("totalFiles").getAsLong() + " files, " +
            summary.get("totalDirectories").getAsLong() + " directories (" +
            summary.get("totalSizeFormatted").getAsString() + ")");
    }
}
```

### 5. Documentation Structure

```java
// Explore documentation
JsonObject input = new JsonObject();
input.addProperty("path", "docs");
input.addProperty("recursive", "true");
JsonObject result = listFilesTool.execute(role, input);

if (!result.has("error")) {
    JsonArray files = result.getAsJsonArray("files");
    List<String> docFiles = new ArrayList<>();
    
    for (int i = 0; i < files.size(); i++) {
        JsonObject file = files.get(i).getAsJsonObject();
        if (file.has("extension")) {
            String ext = file.get("extension").getAsString();
            if ("md".equals(ext) || "txt".equals(ext) || "rst".equals(ext)) {
                docFiles.add(file.get("path").getAsString());
            }
        }
    }
    
    System.out.println("Documentation files:");
    docFiles.forEach(System.out::println);
}
```

## Return Format

### Successful Response Structure

```json
{
  "files": [
    {
      "name": "Application.java",
      "path": "src/main/java/Application.java",
      "type": "file",
      "size": 1234,
      "lastModified": "2025-01-16 10:30:45",
      "isDirectory": false,
      "isHidden": false,
      "extension": "java"
    },
    {
      "name": "resources",
      "path": "src/main/resources",
      "type": "directory",
      "size": 0,
      "lastModified": "2025-01-16 09:15:30",
      "isDirectory": true,
      "isHidden": false
    }
  ],
  "totalCount": 2,
  "directoryPath": "src/main",
  "recursive": false,
  "wasLimited": false,
  "summary": {
    "totalFiles": 1,
    "totalDirectories": 1,
    "totalSize": 1234,
    "totalSizeFormatted": "1.2 KB"
  },
  "result": "Directory listing for: src/main\n\nDirectories:\n  📁 resources/\n\nFiles:\n  ☕ Application.java (1.2 KB)\n\nSummary: 1 directories, 1 files"
}
```

### Error Response

```json
{
  "error": "Directory not found: nonexistent"
}
```

## File Type Icons

The tool uses visual icons to represent different file types:

- **📁** Directories
- **☕** Java files (.java)
- **🟨** JavaScript files (.js, .jsx)
- **🔷** TypeScript files (.ts, .tsx)
- **🐍** Python files (.py)
- **📋** JSON files (.json)
- **📄** XML files (.xml)
- **⚙️** YAML files (.yml, .yaml)
- **📝** Markdown files (.md)
- **🌐** HTML files (.html, .htm)
- **🎨** CSS files (.css)
- **🖼️** Image files (.png, .jpg, .jpeg, .gif)
- **📕** PDF files (.pdf)
- **📦** Archive files (.zip, .rar, .tar, .gz)
- **📄** Other files

## Filtering and Limits

### Automatic Filtering

During recursive listing, the tool automatically filters:

- **Hidden files and directories** (starting with `.`) unless specifically targeting them
- **Common build directories**: `node_modules`, `target`, `build`, `dist`, `out`, `vendor`, etc.
- **Version control directories**: `.git`, `.svn`, `.hg`
- **IDE directories**: `.idea`, `.vscode`
- **Cache directories**: `__pycache__`, `.gradle`, `.m2`

### Limits and Safety

- **File count limit**: Maximum 200 files to prevent overwhelming output
- **Recursion depth limit**: Maximum 10 levels deep to prevent infinite loops
- **Timeout protection**: Prevents hanging on problematic directory structures
- **Symlink loop detection**: Tracks visited directories to prevent infinite loops

## Integration with Other Tools

### Read-List-Modify Workflow

```java
// 1. List directory contents to understand structure
ListFilesTool listTool = new ListFilesTool();
JsonObject listInput = new JsonObject();
listInput.addProperty("path", "src/main/java/com/example");
JsonObject listResult = listTool.execute(role, listInput);

// 2. Read specific files found in listing
ReadFileTool readTool = new ReadFileTool();
JsonArray files = listResult.getAsJsonArray("files");
for (int i = 0; i < files.size(); i++) {
    JsonObject file = files.get(i).getAsJsonObject();
    if (!file.get("isDirectory").getAsBoolean() && 
        "java".equals(file.get("extension").getAsString())) {
        
        JsonObject readInput = new JsonObject();
        readInput.addProperty("path", file.get("path").getAsString());
        JsonObject readResult = readTool.execute(role, readInput);
        
        // Analyze file content...
    }
}

// 3. Modify files as needed using WriteToFileTool or ReplaceInFileTool
```

### Project Analysis Workflow

```java
// Comprehensive project analysis
public void analyzeProject(String projectPath) {
    // 1. Get project overview
    JsonObject listInput = new JsonObject();
    listInput.addProperty("path", projectPath);
    listInput.addProperty("recursive", "true");
    JsonObject overview = listTool.execute(role, listInput);
    
    // 2. Analyze different directories
    String[] keyDirs = {"src", "test", "config", "docs", "scripts"};
    for (String dir : keyDirs) {
        JsonObject dirInput = new JsonObject();
        dirInput.addProperty("path", Paths.get(projectPath, dir).toString());
        JsonObject dirResult = listTool.execute(role, dirInput);
        
        if (!dirResult.has("error")) {
            analyzeDirectoryContents(dir, dirResult);
        }
    }
    
    // 3. Generate project report
    generateProjectReport(overview);
}
```

## Best Practices

### 1. Use Appropriate Recursion

```java
// For quick overview - use non-recursive
JsonObject input = new JsonObject();
input.addProperty("path", "src");
input.addProperty("recursive", "false");

// For deep analysis - use recursive
JsonObject input2 = new JsonObject();
input2.addProperty("path", ".");
input2.addProperty("recursive", "true");
```

### 2. Handle Large Directories

```java
JsonObject result = listTool.execute(role, input);
if (result.has("wasLimited") && result.get("wasLimited").getAsBoolean()) {
    System.out.println("Warning: Directory listing was limited. Consider using more specific paths.");
}
```

### 3. Error Handling

```java
JsonObject result = listTool.execute(role, input);
if (result.has("error")) {
    String error = result.get("error").getAsString();
    if (error.contains("not found")) {
        System.out.println("Directory doesn't exist, creating it...");
        // Handle missing directory
    } else if (error.contains("not readable")) {
        System.out.println("Permission denied, trying alternative approach...");
        // Handle permission issues
    }
} else {
    // Process successful result
    processDirectoryListing(result);
}
```

### 4. Efficient File Processing

```java
// Process files by type
JsonArray files = result.getAsJsonArray("files");
Map<String, List<JsonObject>> filesByType = new HashMap<>();

for (int i = 0; i < files.size(); i++) {
    JsonObject file = files.get(i).getAsJsonObject();
    String type = file.get("isDirectory").getAsBoolean() ? "directory" : 
                  file.has("extension") ? file.get("extension").getAsString() : "other";
    
    filesByType.computeIfAbsent(type, k -> new ArrayList<>()).add(file);
}

// Process each type separately
filesByType.forEach((type, fileList) -> {
    System.out.println("Processing " + fileList.size() + " " + type + " files...");
    // Handle specific file types
});
```

## Performance Considerations

- **Recursive listing** can be slow for large directory trees
- **File metadata collection** adds overhead but provides valuable information
- **Filtering** reduces output size but requires additional processing
- **Sorting** ensures consistent output but may impact performance for large directories

## Security Notes

- Tool respects file system permissions
- Prevents access to restricted paths (root, home directory)
- Handles symlinks safely to prevent infinite loops
- Filters sensitive directories by default

This tool provides comprehensive directory exploration capabilities for the hive AI assistant system, fully compatible with Cline's list_files tool specifications.
