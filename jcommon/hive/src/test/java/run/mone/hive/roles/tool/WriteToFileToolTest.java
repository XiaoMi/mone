package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WriteToFileTool测试类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
class WriteToFileToolTest {

    private WriteToFileTool tool;
    private ReactorRole role;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        tool = new WriteToFileTool();
        role = new ReactorRole("test",null,new LLM(LLMConfig.builder().build())); // 假设有默认构造函数
    }

    @Test
    void testGetName() {
        assertEquals("write_to_file", tool.getName());
    }

    @Test
    void testNeedExecute() {
        assertTrue(tool.needExecute());
    }

    @Test
    void testShow() {
        assertTrue(tool.show());
    }

    @Test
    void testDescription() {
        String description = tool.description();
        assertNotNull(description);
        assertTrue(description.contains("写入"));
        assertTrue(description.contains("文件"));
    }

    @Test
    void testParameters() {
        String parameters = tool.parameters();
        assertNotNull(parameters);
        assertTrue(parameters.contains("path"));
        assertTrue(parameters.contains("content"));
    }

    @Test
    void testUsage() {
        String usage = tool.usage();
        assertNotNull(usage);
        assertTrue(usage.contains("write_to_file"));
        assertTrue(usage.contains("<path>"));
        assertTrue(usage.contains("<content>"));
    }

    @Test
    void testExample() {
        String example = tool.example();
        assertNotNull(example);
        assertTrue(example.contains("示例"));
    }

    @Test
    void testExecuteWithMissingPath() {
        JsonObject input = new JsonObject();
        input.addProperty("content", "test content");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("path"));
    }

    @Test
    void testExecuteWithMissingContent() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "test.txt");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("content"));
    }

    @Test
    void testCreateNewFile() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        String content = "Hello, World!\nThis is a test file.";

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", content);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        assertTrue(result.get("result").getAsString().contains("创建"));
        assertEquals("创建", result.get("operation").getAsString());
        assertEquals(testFile.toString(), result.get("path").getAsString());
        assertFalse(result.get("fileExisted").getAsBoolean());
        
        // 验证文件内容
        assertTrue(Files.exists(testFile));
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertEquals(content, fileContent);
    }

    @Test
    void testOverwriteExistingFile() throws IOException {
        Path testFile = tempDir.resolve("existing.txt");
        String originalContent = "Original content";
        String newContent = "New content that replaces the original";

        // 创建原始文件
        Files.writeString(testFile, originalContent, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", newContent);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        assertTrue(result.get("result").getAsString().contains("覆盖"));
        assertEquals("覆盖", result.get("operation").getAsString());
        assertTrue(result.get("fileExisted").getAsBoolean());
        
        // 验证文件内容被完全替换
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertEquals(newContent, fileContent);
        assertNotEquals(originalContent, fileContent);
    }

    @Test
    void testCreateFileWithDirectories() throws IOException {
        Path testFile = tempDir.resolve("deep/nested/directory/test.txt");
        String content = "Content in nested directory";

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", content);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        assertTrue(result.get("result").getAsString().contains("创建"));
        
        // 验证目录和文件都被创建
        assertTrue(Files.exists(testFile));
        assertTrue(Files.exists(testFile.getParent()));
        
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertEquals(content, fileContent);
    }

    @Test
    void testWriteJavaFile() throws IOException {
        Path testFile = tempDir.resolve("HelloWorld.java");
        String javaContent = """
                package com.example;
                
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", javaContent);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        
        // 验证Java文件内容
        assertTrue(Files.exists(testFile));
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertTrue(fileContent.contains("package com.example"));
        assertTrue(fileContent.contains("public class HelloWorld"));
        assertTrue(fileContent.contains("Hello, World!"));
    }

    @Test
    void testWriteJSONFile() throws IOException {
        Path testFile = tempDir.resolve("config.json");
        String jsonContent = """
                {
                  "apiEndpoint": "https://api.example.com",
                  "theme": {
                    "primaryColor": "#007bff",
                    "secondaryColor": "#6c757d"
                  },
                  "features": {
                    "darkMode": true,
                    "notifications": false
                  }
                }
                """;

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", jsonContent);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        
        // 验证JSON文件内容
        assertTrue(Files.exists(testFile));
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertTrue(fileContent.contains("apiEndpoint"));
        assertTrue(fileContent.contains("primaryColor"));
        assertTrue(fileContent.contains("darkMode"));
    }

    @Test
    void testWriteEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        String content = "";

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", content);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        assertEquals(0, result.get("contentLength").getAsInt());
        
        // 验证空文件
        assertTrue(Files.exists(testFile));
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertEquals("", fileContent);
    }

    @Test
    void testWriteFileWithMarkdownCodeBlocks() throws IOException {
        Path testFile = tempDir.resolve("test.java");
        
        // 模拟从markdown代码块中提取的内容（带有```标记）
        String contentWithCodeBlocks = """
                ```java
                public class Test {
                    public void method() {
                        System.out.println("test");
                    }
                }
                ```
                """;

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", contentWithCodeBlocks);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        
        // 验证markdown代码块标记被移除
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertFalse(fileContent.contains("```java"));
        assertFalse(fileContent.contains("```"));
        assertTrue(fileContent.contains("public class Test"));
        assertTrue(fileContent.contains("System.out.println"));
    }

    @Test
    void testWriteToExistingDirectory() throws IOException {
        Path existingDir = tempDir.resolve("existing_directory");
        Files.createDirectory(existingDir);

        JsonObject input = new JsonObject();
        input.addProperty("path", existingDir.toString());
        input.addProperty("content", "some content");

        JsonObject result = tool.execute(role, input);

        // 验证返回错误
        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("目录"));
    }

    @Test
    void testWriteMultiLineFile() throws IOException {
        Path testFile = tempDir.resolve("multiline.txt");
        String content = """
                Line 1
                Line 2
                Line 3
                
                Line 5 (after empty line)
                Last line
                """;

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", content);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        
        // 验证多行内容
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        String[] lines = fileContent.split("\n");
        assertTrue(lines.length >= 5);
        assertEquals("Line 1", lines[0]);
        assertEquals("Line 2", lines[1]);
        assertEquals("Last line", lines[lines.length - 1]);
    }

    @Test
    void testWriteFileWithSpecialCharacters() throws IOException {
        Path testFile = tempDir.resolve("special_chars.txt");
        String content = "特殊字符测试：中文、émojis 🚀、符号 @#$%^&*()";

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", content);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        
        // 验证特殊字符被正确保存
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertEquals(content, fileContent);
        assertTrue(fileContent.contains("中文"));
        assertTrue(fileContent.contains("🚀"));
        assertTrue(fileContent.contains("@#$%"));
    }

    @Test
    void testContentPreprocessing() throws IOException {
        WriteToFileTool tool = new WriteToFileTool();
        
        // 使用反射测试私有方法（如果需要的话）
        // 或者通过公共接口间接测试预处理功能
        
        Path testFile = tempDir.resolve("preprocessed.txt");
        String contentWithCodeBlocks = "```\ntest content\n```";

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("content", contentWithCodeBlocks);

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("result"));
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertEquals("test content", fileContent);
    }
}
