# ReadFileTool Usage Guide

This document demonstrates how to use the newly implemented `ReadFileTool` in the hive project.

## Tool Overview

`ReadFileTool` is a Java implementation of the file reading tool, inspired by Cline's `read_file` tool. It allows you to examine the contents of existing files at specified paths, supporting various file types including text files, source code, configuration files, and binary files.

## Key Features

1. **Text File Reading**: Reads and returns text content with proper UTF-8 encoding
2. **Binary File Support**: Handles binary files by returning Base64 encoded content
3. **File Type Detection**: Automatically detects and reports file types based on extensions
4. **Size Management**: Handles large files with appropriate truncation and warnings
5. **Comprehensive Validation**: Checks file existence, permissions, and path safety
6. **Rich Metadata**: Provides detailed file information including size, type, and encoding

## Usage Methods

### 1. Add Tool to ReactorRole

```java
ReactorRole role = new ReactorRole();
ReadFileTool readFileTool = new ReadFileTool();
role.addTool(readFileTool);
```

### 2. Read a Java Source File

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java/com/example/UserService.java");

JsonObject result = readFileTool.execute(role, input);

if (result.has("result")) {
    String fileContent = result.get("result").getAsString();
    String fileType = result.get("fileType").getAsString(); // "Java source code"
    long fileSize = result.get("fileSize").getAsLong();
    System.out.println("File content: " + fileContent);
}
```

### 3. Read Configuration Files

```java
// Read properties file
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/resources/application.properties");
JsonObject result = readFileTool.execute(role, input);

// Read JSON configuration
JsonObject input2 = new JsonObject();
input2.addProperty("path", "config/database.json");
JsonObject result2 = readFileTool.execute(role, input2);

// Read YAML configuration
JsonObject input3 = new JsonObject();
input3.addProperty("path", "config/application.yml");
JsonObject result3 = readFileTool.execute(role, input3);
```

### 4. Read Documentation Files

```java
// Read README file
JsonObject input = new JsonObject();
input.addProperty("path", "README.md");
JsonObject result = readFileTool.execute(role, input);

if (result.has("result")) {
    String readmeContent = result.get("result").getAsString();
    System.out.println("README content: " + readmeContent);
}
```

### 5. Read Binary Files

```java
// Read image file (returns Base64)
JsonObject input = new JsonObject();
input.addProperty("path", "assets/logo.png");
JsonObject result = readFileTool.execute(role, input);

if (result.has("base64Content")) {
    String base64Data = result.get("base64Content").getAsString();
    boolean isBinary = result.get("isBinary").getAsBoolean();
    String encoding = result.get("encoding").getAsString(); // "Base64"
    System.out.println("Binary file encoded as: " + encoding);
}
```

## Supported File Types

### Text Files
- **Source Code**: `.java`, `.js`, `.ts`, `.py`, `.rb`, `.php`, `.go`, `.rs`, `.c`, `.cpp`, etc.
- **Web Files**: `.html`, `.css`, `.scss`, `.jsx`, `.tsx`
- **Configuration**: `.json`, `.xml`, `.yml`, `.yaml`, `.properties`, `.conf`
- **Documentation**: `.md`, `.txt`
- **Scripts**: `.sh`, `.bat`, `.ps1`

### Binary Files
- **Images**: `.png`, `.jpg`, `.jpeg`, `.gif`, `.bmp`, `.webp`, `.ico`
- **Documents**: `.pdf`, `.doc`, `.docx`, `.xls`, `.xlsx`, `.ppt`, `.pptx`
- **Archives**: `.zip`, `.rar`, `.7z`, `.tar`, `.gz`
- **Executables**: `.exe`, `.dll`, `.so`, `.dylib`

## Return Format

### Successful Text File Read
```json
{
  "result": "file content here...",
  "fileType": "Java source code",
  "fileSize": 1234,
  "contentLength": 1234,
  "wasTruncated": false,
  "encoding": "UTF-8"
}
```

### Successful Binary File Read
```json
{
  "result": "Binary file content (Base64 encoded):\nbase64data...",
  "fileType": "Image file",
  "fileSize": 5678,
  "encoding": "Base64",
  "isBinary": true,
  "base64Content": "base64data..."
}
```

### Error Response
```json
{
  "error": "File not found: nonexistent.txt"
}
```

## Common Use Cases

### 1. Code Analysis

```java
// Analyze existing service class
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java/com/example/UserService.java");
JsonObject result = readFileTool.execute(role, input);

// The AI can now analyze the code structure, methods, dependencies, etc.
```

### 2. Configuration Review

```java
// Check database configuration
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/resources/application.properties");
JsonObject result = readFileTool.execute(role, input);

// Review current settings before making changes
```

### 3. Documentation Reading

```java
// Read project documentation
JsonObject input = new JsonObject();
input.addProperty("path", "docs/API.md");
JsonObject result = readFileTool.execute(role, input);

// Understand project structure and requirements
```

### 4. Script Examination

```java
// Examine build script
JsonObject input = new JsonObject();
input.addProperty("path", "scripts/build.sh");
JsonObject result = readFileTool.execute(role, input);

// Understand build process before modifications
```

### 5. Log File Analysis

```java
// Read application logs
JsonObject input = new JsonObject();
input.addProperty("path", "logs/application.log");
JsonObject result = readFileTool.execute(role, input);

// Analyze errors and performance issues
```

## File Size Handling

### Text Files
- **Small files** (< 1MB): Read completely
- **Large files** (> 1MB): Read with warning, may be truncated if > 100KB content
- **Very large files**: Content truncated with notification

### Binary Files
- **Supported size**: Up to 10MB
- **Larger files**: Return error with size information
- **Encoding**: Always Base64 for binary content

## Error Handling

The tool handles various error conditions:

1. **File Not Found**: Clear error message with file path
2. **Directory Instead of File**: Specific error about path type
3. **Permission Denied**: File permission error
4. **File Too Large**: Size limit exceeded for binary files
5. **IO Errors**: General file system errors

## Best Practices

### 1. Path Usage
- Use relative paths from current working directory
- Avoid absolute paths when possible
- Check path safety (no `..` or `~` traversal)

### 2. File Type Awareness
- Check `fileType` field to understand content format
- Handle binary files appropriately using `base64Content`
- Consider `wasTruncated` flag for large files

### 3. Error Handling
```java
JsonObject result = readFileTool.execute(role, input);
if (result.has("error")) {
    String error = result.get("error").getAsString();
    log.error("Failed to read file: {}", error);
    // Handle error appropriately
} else {
    String content = result.get("result").getAsString();
    // Process file content
}
```

### 4. Content Processing
```java
if (result.has("isBinary") && result.get("isBinary").getAsBoolean()) {
    // Handle binary file
    String base64Data = result.get("base64Content").getAsString();
    byte[] binaryData = Base64.getDecoder().decode(base64Data);
} else {
    // Handle text file
    String textContent = result.get("result").getAsString();
}
```

## Integration with Other Tools

### Read-Modify-Write Workflow

```java
// 1. Read existing file
ReadFileTool readTool = new ReadFileTool();
JsonObject readInput = new JsonObject();
readInput.addProperty("path", "src/main/java/Example.java");
JsonObject readResult = readTool.execute(role, readInput);

// 2. Analyze content and plan modifications
String existingContent = readResult.get("result").getAsString();

// 3. Use ReplaceInFileTool for targeted changes
ReplaceInFileTool replaceTool = new ReplaceInFileTool();
// ... perform targeted replacements

// OR use WriteToFileTool for complete rewrite
WriteToFileTool writeTool = new WriteToFileTool();
// ... write new content
```

### Configuration Management

```java
// 1. Read current configuration
JsonObject readConfig = readFileTool.execute(role, configInput);
String currentConfig = readConfig.get("result").getAsString();

// 2. Analyze and modify as needed
// 3. Write back updated configuration
```

## Security Considerations

1. **Path Validation**: Tool validates paths to prevent traversal attacks
2. **File Permissions**: Respects file system permissions
3. **Size Limits**: Prevents memory exhaustion with large files
4. **Binary Handling**: Safe Base64 encoding for binary content

## Performance Notes

- Text files are read entirely into memory
- Large files may be truncated for performance
- Binary files have size limits to prevent memory issues
- UTF-8 encoding is used for all text content

This tool provides a robust foundation for file reading operations in the hive AI assistant system, fully compatible with the Cline project's read_file tool specifications.
