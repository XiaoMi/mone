package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import run.mone.hive.roles.ReactorRole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReadFileTool test class
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
class ReadFileToolTest {

    private ReadFileTool tool;
    private ReactorRole role;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        tool = new ReadFileTool();
        role = new ReactorRole(); // Assuming default constructor exists
    }

    @Test
    void testGetName() {
        assertEquals("read_file", tool.getName());
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
        assertTrue(description.contains("read the contents"));
        assertTrue(description.contains("existing file"));
    }

    @Test
    void testParameters() {
        String parameters = tool.parameters();
        assertNotNull(parameters);
        assertTrue(parameters.contains("path"));
        assertTrue(parameters.contains("required"));
    }

    @Test
    void testUsage() {
        String usage = tool.usage();
        assertNotNull(usage);
        assertTrue(usage.contains("read_file"));
        assertTrue(usage.contains("<path>"));
    }

    @Test
    void testExample() {
        String example = tool.example();
        assertNotNull(example);
        assertTrue(example.contains("Example"));
        assertTrue(example.contains("read_file"));
    }

    @Test
    void testExecuteWithMissingPath() {
        JsonObject input = new JsonObject();

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("path"));
    }

    @Test
    void testExecuteWithEmptyPath() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("path"));
    }

    @Test
    void testExecuteWithNonExistentFile() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "nonexistent.txt");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("not found"));
    }

    @Test
    void testReadSimpleTextFile() throws IOException {
        // Create test file
        Path testFile = tempDir.resolve("test.txt");
        String content = "Hello, World!\nThis is a test file.\nLine 3";
        Files.writeString(testFile, content, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals(content, result.get("result").getAsString());
        assertEquals("Text file", result.get("fileType").getAsString());
        assertFalse(result.get("wasTruncated").getAsBoolean());
        assertEquals("UTF-8", result.get("encoding").getAsString());
    }

    @Test
    void testReadJavaSourceFile() throws IOException {
        // Create Java source file
        Path javaFile = tempDir.resolve("HelloWorld.java");
        String javaContent = """
                package com.example;
                
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;
        Files.writeString(javaFile, javaContent, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", javaFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals(javaContent, result.get("result").getAsString());
        assertEquals("Java source code", result.get("fileType").getAsString());
        assertTrue(result.get("fileSize").getAsLong() > 0);
    }

    @Test
    void testReadJSONFile() throws IOException {
        // Create JSON file
        Path jsonFile = tempDir.resolve("config.json");
        String jsonContent = """
                {
                  "name": "test-app",
                  "version": "1.0.0",
                  "description": "A test application",
                  "dependencies": {
                    "express": "^4.18.0",
                    "lodash": "^4.17.21"
                  }
                }
                """;
        Files.writeString(jsonFile, jsonContent, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", jsonFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals(jsonContent, result.get("result").getAsString());
        assertEquals("JSON data", result.get("fileType").getAsString());
    }

    @Test
    void testReadEmptyFile() throws IOException {
        // Create empty file
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.writeString(emptyFile, "", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", emptyFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals("", result.get("result").getAsString());
        assertEquals(0, result.get("fileSize").getAsLong());
        assertEquals(0, result.get("contentLength").getAsInt());
    }

    @Test
    void testReadBinaryFile() throws IOException {
        // Create a simple binary file (PNG-like header)
        Path binaryFile = tempDir.resolve("test.png");
        byte[] binaryData = {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
        };
        Files.write(binaryFile, binaryData);

        JsonObject input = new JsonObject();
        input.addProperty("path", binaryFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertTrue(result.get("result").getAsString().contains("Base64 encoded"));
        assertEquals("Image file", result.get("fileType").getAsString());
        assertEquals("Base64", result.get("encoding").getAsString());
        assertTrue(result.get("isBinary").getAsBoolean());
        assertTrue(result.has("base64Content"));
        
        // Verify Base64 content
        String expectedBase64 = Base64.getEncoder().encodeToString(binaryData);
        assertEquals(expectedBase64, result.get("base64Content").getAsString());
    }

    @Test
    void testReadDirectory() throws IOException {
        // Create a directory
        Path directory = tempDir.resolve("testdir");
        Files.createDirectory(directory);

        JsonObject input = new JsonObject();
        input.addProperty("path", directory.toString());

        JsonObject result = tool.execute(role, input);

        // Verify error result
        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("directory"));
        assertTrue(result.get("error").getAsString().contains("not a file"));
    }

    @Test
    void testReadFileWithSpecialCharacters() throws IOException {
        // Create file with special characters
        Path specialFile = tempDir.resolve("special.txt");
        String specialContent = "Special characters: 中文, émojis 🚀, symbols @#$%^&*()";
        Files.writeString(specialFile, specialContent, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", specialFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals(specialContent, result.get("result").getAsString());
        assertTrue(result.get("result").getAsString().contains("中文"));
        assertTrue(result.get("result").getAsString().contains("🚀"));
    }

    @Test
    void testReadLargeTextFile() throws IOException {
        // Create a large text file
        Path largeFile = tempDir.resolve("large.txt");
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeContent.append("Line ").append(i).append(": This is a long line of text to create a large file.\n");
        }
        Files.writeString(largeFile, largeContent.toString(), StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", largeFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertTrue(result.get("fileSize").getAsLong() > 100000); // Should be quite large
        
        // Check if content was truncated
        if (result.has("wasTruncated") && result.get("wasTruncated").getAsBoolean()) {
            assertTrue(result.get("result").getAsString().contains("truncated"));
        }
    }

    @Test
    void testReadMarkdownFile() throws IOException {
        // Create Markdown file
        Path markdownFile = tempDir.resolve("README.md");
        String markdownContent = """
                # Project Title
                
                This is a sample README file.
                
                ## Features
                
                - Feature 1
                - Feature 2
                - Feature 3
                
                ## Installation
                
                ```bash
                npm install
                ```
                
                ## Usage
                
                Run the application with:
                
                ```bash
                npm start
                ```
                """;
        Files.writeString(markdownFile, markdownContent, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", markdownFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals(markdownContent, result.get("result").getAsString());
        assertEquals("Markdown document", result.get("fileType").getAsString());
    }

    @Test
    void testReadPropertiesFile() throws IOException {
        // Create properties file
        Path propsFile = tempDir.resolve("application.properties");
        String propsContent = """
                server.port=8080
                spring.datasource.url=jdbc:mysql://localhost:3306/mydb
                spring.datasource.username=root
                spring.datasource.password=password
                logging.level.com.example=DEBUG
                """;
        Files.writeString(propsFile, propsContent, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", propsFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals(propsContent, result.get("result").getAsString());
        assertEquals("Properties file", result.get("fileType").getAsString());
    }

    @Test
    void testReadXMLFile() throws IOException {
        // Create XML file
        Path xmlFile = tempDir.resolve("config.xml");
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <configuration>
                    <database>
                        <host>localhost</host>
                        <port>3306</port>
                        <name>mydb</name>
                    </database>
                    <logging>
                        <level>DEBUG</level>
                        <file>app.log</file>
                    </logging>
                </configuration>
                """;
        Files.writeString(xmlFile, xmlContent, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", xmlFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals(xmlContent, result.get("result").getAsString());
        assertEquals("XML document", result.get("fileType").getAsString());
    }

    @Test
    void testReadFileWithNoExtension() throws IOException {
        // Create file without extension
        Path noExtFile = tempDir.resolve("LICENSE");
        String licenseContent = """
                MIT License
                
                Copyright (c) 2025 Test Project
                
                Permission is hereby granted, free of charge, to any person obtaining a copy
                of this software and associated documentation files...
                """;
        Files.writeString(noExtFile, licenseContent, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", noExtFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals(licenseContent, result.get("result").getAsString());
        assertEquals("unknown", result.get("fileType").getAsString());
    }

    @Test
    void testReadMultilineFile() throws IOException {
        // Create file with various line endings and formatting
        Path multilineFile = tempDir.resolve("multiline.txt");
        String multilineContent = """
                Line 1
                
                Line 3 (after empty line)
                	Indented line with tab
                    Indented line with spaces
                
                Final line
                """;
        Files.writeString(multilineFile, multilineContent, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", multilineFile.toString());

        JsonObject result = tool.execute(role, input);

        // Verify result
        assertTrue(result.has("result"));
        assertEquals(multilineContent, result.get("result").getAsString());
        
        // Verify line structure is preserved
        String[] lines = result.get("result").getAsString().split("\n");
        assertTrue(lines.length >= 6);
        assertTrue(lines[3].startsWith("\t")); // Tab indented line
        assertTrue(lines[4].startsWith("    ")); // Space indented line
    }
}
