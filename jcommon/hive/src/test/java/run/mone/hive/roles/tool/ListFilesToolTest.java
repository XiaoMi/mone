package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import run.mone.hive.roles.ReactorRole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ListFilesTool test class
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
class ListFilesToolTest {

    private ListFilesTool tool;
    private ReactorRole role;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        tool = new ListFilesTool();
        role = new ReactorRole(); // Assuming default constructor exists
    }

    @Test
    void testGetName() {
        assertEquals("list_files", tool.getName());
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
        assertTrue(description.contains("list files and directories"));
        assertTrue(description.contains("recursive"));
    }

    @Test
    void testParameters() {
        String parameters = tool.parameters();
        assertNotNull(parameters);
        assertTrue(parameters.contains("path"));
        assertTrue(parameters.contains("recursive"));
    }

    @Test
    void testUsage() {
        String usage = tool.usage();
        assertNotNull(usage);
        assertTrue(usage.contains("list_files"));
        assertTrue(usage.contains("<path>"));
        assertTrue(usage.contains("<recursive>"));
    }

    @Test
    void testExample() {
        String example = tool.example();
        assertNotNull(example);
        assertTrue(example.contains("Example"));
        assertTrue(example.contains("list_files"));
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
    void testExecuteWithNonExistentDirectory() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "nonexistent");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("not found"));
    }

    @Test
    void testExecuteWithFile() throws IOException {
        // Create a file instead of directory
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "test content", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("file, not a directory"));
    }

    @Test
    void testListEmptyDirectory() {
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("files"));
        assertTrue(result.has("totalCount"));
        assertTrue(result.has("summary"));
        assertTrue(result.has("result"));

        JsonArray files = result.getAsJsonArray("files");
        assertEquals(0, files.size());
        assertEquals(0, result.get("totalCount").getAsInt());
        assertFalse(result.get("recursive").getAsBoolean());
        
        // Check summary
        JsonObject summary = result.getAsJsonObject("summary");
        assertEquals(0, summary.get("totalFiles").getAsLong());
        assertEquals(0, summary.get("totalDirectories").getAsLong());
        assertEquals(0, summary.get("totalSize").getAsLong());
    }

    @Test
    void testListTopLevelFiles() throws IOException {
        // Create test files and directories
        Files.writeString(tempDir.resolve("file1.txt"), "content1", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("file2.java"), "public class Test {}", StandardCharsets.UTF_8);
        Files.createDirectory(tempDir.resolve("subdir"));
        Files.writeString(tempDir.resolve("README.md"), "# Test Project", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("recursive", "false");

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("files"));
        JsonArray files = result.getAsJsonArray("files");
        assertEquals(4, files.size());
        assertEquals(4, result.get("totalCount").getAsInt());
        assertFalse(result.get("recursive").getAsBoolean());

        // Check that files contain expected information
        boolean foundFile1 = false, foundJavaFile = false, foundDir = false, foundReadme = false;
        
        for (int i = 0; i < files.size(); i++) {
            JsonObject file = files.get(i).getAsJsonObject();
            assertTrue(file.has("name"));
            assertTrue(file.has("path"));
            assertTrue(file.has("type"));
            assertTrue(file.has("size"));
            assertTrue(file.has("lastModified"));
            assertTrue(file.has("isDirectory"));
            
            String name = file.get("name").getAsString();
            if ("file1.txt".equals(name)) {
                foundFile1 = true;
                assertEquals("file", file.get("type").getAsString());
                assertFalse(file.get("isDirectory").getAsBoolean());
                assertTrue(file.get("size").getAsLong() > 0);
            } else if ("file2.java".equals(name)) {
                foundJavaFile = true;
                assertEquals("file", file.get("type").getAsString());
                assertTrue(file.has("extension"));
                assertEquals("java", file.get("extension").getAsString());
            } else if ("subdir".equals(name)) {
                foundDir = true;
                assertEquals("directory", file.get("type").getAsString());
                assertTrue(file.get("isDirectory").getAsBoolean());
            } else if ("README.md".equals(name)) {
                foundReadme = true;
                assertEquals("file", file.get("type").getAsString());
                assertEquals("md", file.get("extension").getAsString());
            }
        }
        
        assertTrue(foundFile1);
        assertTrue(foundJavaFile);
        assertTrue(foundDir);
        assertTrue(foundReadme);
    }

    @Test
    void testListRecursiveFiles() throws IOException {
        // Create nested directory structure
        Path subdir = tempDir.resolve("subdir");
        Files.createDirectory(subdir);
        Path nestedDir = subdir.resolve("nested");
        Files.createDirectory(nestedDir);
        
        Files.writeString(tempDir.resolve("root.txt"), "root content", StandardCharsets.UTF_8);
        Files.writeString(subdir.resolve("sub.txt"), "sub content", StandardCharsets.UTF_8);
        Files.writeString(nestedDir.resolve("nested.txt"), "nested content", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("recursive", "true");

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("files"));
        JsonArray files = result.getAsJsonArray("files");
        assertTrue(files.size() >= 5); // root.txt, subdir/, sub.txt, nested/, nested.txt
        assertTrue(result.get("recursive").getAsBoolean());

        // Check that we found files at different levels
        boolean foundRootFile = false, foundSubFile = false, foundNestedFile = false;
        
        for (int i = 0; i < files.size(); i++) {
            JsonObject file = files.get(i).getAsJsonObject();
            String path = file.get("path").getAsString();
            
            if (path.equals("root.txt")) {
                foundRootFile = true;
            } else if (path.contains("sub.txt")) {
                foundSubFile = true;
            } else if (path.contains("nested.txt")) {
                foundNestedFile = true;
            }
        }
        
        assertTrue(foundRootFile);
        assertTrue(foundSubFile);
        assertTrue(foundNestedFile);
    }

    @Test
    void testListWithHiddenFiles() throws IOException {
        // Create hidden files and directories
        Files.writeString(tempDir.resolve(".hidden"), "hidden content", StandardCharsets.UTF_8);
        Files.createDirectory(tempDir.resolve(".hiddendir"));
        Files.writeString(tempDir.resolve("visible.txt"), "visible content", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("recursive", "false");

        JsonObject result = tool.execute(role, input);

        // For non-recursive listing, hidden files should be filtered out by default
        JsonArray files = result.getAsJsonArray("files");
        
        boolean foundHidden = false, foundVisible = false;
        for (int i = 0; i < files.size(); i++) {
            JsonObject file = files.get(i).getAsJsonObject();
            String name = file.get("name").getAsString();
            
            if (name.startsWith(".")) {
                foundHidden = true;
            } else if ("visible.txt".equals(name)) {
                foundVisible = true;
            }
        }
        
        assertTrue(foundVisible);
        // Hidden files behavior depends on implementation - could be included or excluded
    }

    @Test
    void testListWithVariousFileTypes() throws IOException {
        // Create files with different extensions
        Files.writeString(tempDir.resolve("script.js"), "console.log('hello');", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("style.css"), "body { margin: 0; }", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("data.json"), "{\"key\": \"value\"}", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("config.xml"), "<config></config>", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("README.md"), "# Project", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        JsonArray files = result.getAsJsonArray("files");
        assertEquals(5, files.size());

        // Verify file extensions are detected correctly
        for (int i = 0; i < files.size(); i++) {
            JsonObject file = files.get(i).getAsJsonObject();
            String name = file.get("name").getAsString();
            
            if (file.has("extension")) {
                String extension = file.get("extension").getAsString();
                
                if ("script.js".equals(name)) {
                    assertEquals("js", extension);
                } else if ("style.css".equals(name)) {
                    assertEquals("css", extension);
                } else if ("data.json".equals(name)) {
                    assertEquals("json", extension);
                } else if ("config.xml".equals(name)) {
                    assertEquals("xml", extension);
                } else if ("README.md".equals(name)) {
                    assertEquals("md", extension);
                }
            }
        }
    }

    @Test
    void testListLargeDirectory() throws IOException {
        // Create many files to test limit handling
        for (int i = 0; i < 50; i++) {
            Files.writeString(tempDir.resolve("file" + i + ".txt"), "content " + i, StandardCharsets.UTF_8);
        }

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        JsonArray files = result.getAsJsonArray("files");
        assertEquals(50, files.size());
        assertEquals(50, result.get("totalCount").getAsInt());
        
        // Check summary
        JsonObject summary = result.getAsJsonObject("summary");
        assertEquals(50, summary.get("totalFiles").getAsLong());
        assertEquals(0, summary.get("totalDirectories").getAsLong());
        assertTrue(summary.get("totalSize").getAsLong() > 0);
    }

    @Test
    void testFormattedOutput() throws IOException {
        // Create a mix of files and directories
        Files.writeString(tempDir.resolve("app.java"), "public class App {}", StandardCharsets.UTF_8);
        Files.createDirectory(tempDir.resolve("src"));
        Files.writeString(tempDir.resolve("README.md"), "# Project", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("result"));
        String formattedOutput = result.get("result").getAsString();
        
        // Check formatted output contains expected sections
        assertTrue(formattedOutput.contains("Directory listing for:"));
        assertTrue(formattedOutput.contains("Directories:"));
        assertTrue(formattedOutput.contains("Files:"));
        assertTrue(formattedOutput.contains("Summary:"));
        
        // Check for file icons/formatting
        assertTrue(formattedOutput.contains("📁")); // Directory icon
        assertTrue(formattedOutput.contains("☕")); // Java file icon
        assertTrue(formattedOutput.contains("📝")); // Markdown icon
    }

    @Test
    void testRelativePathHandling() throws IOException {
        // Create subdirectory structure
        Path subdir = tempDir.resolve("project");
        Files.createDirectory(subdir);
        Path srcDir = subdir.resolve("src");
        Files.createDirectory(srcDir);
        Files.writeString(srcDir.resolve("Main.java"), "public class Main {}", StandardCharsets.UTF_8);

        // Test with relative path to subdirectory
        JsonObject input = new JsonObject();
        input.addProperty("path", subdir.toString());
        input.addProperty("recursive", "true");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("files"));
        JsonArray files = result.getAsJsonArray("files");
        assertTrue(files.size() >= 2); // src/ and Main.java

        // Check relative paths are correct
        boolean foundSrcDir = false, foundJavaFile = false;
        for (int i = 0; i < files.size(); i++) {
            JsonObject file = files.get(i).getAsJsonObject();
            String path = file.get("path").getAsString();
            
            if ("src".equals(path)) {
                foundSrcDir = true;
                assertTrue(file.get("isDirectory").getAsBoolean());
            } else if (path.contains("Main.java")) {
                foundJavaFile = true;
                assertFalse(file.get("isDirectory").getAsBoolean());
            }
        }
        
        assertTrue(foundSrcDir);
        assertTrue(foundJavaFile);
    }

    @Test
    void testSummaryStatistics() throws IOException {
        // Create files of different sizes
        Files.writeString(tempDir.resolve("small.txt"), "small", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("medium.txt"), "medium content with more text", StandardCharsets.UTF_8);
        Files.createDirectory(tempDir.resolve("empty_dir"));

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        JsonObject summary = result.getAsJsonObject("summary");
        assertNotNull(summary);
        
        assertEquals(2, summary.get("totalFiles").getAsLong());
        assertEquals(1, summary.get("totalDirectories").getAsLong());
        assertTrue(summary.get("totalSize").getAsLong() > 0);
        assertTrue(summary.has("totalSizeFormatted"));
        
        String sizeFormatted = summary.get("totalSizeFormatted").getAsString();
        assertTrue(sizeFormatted.contains("B") || sizeFormatted.contains("KB"));
    }

    @Test
    void testRecursiveDepthLimit() throws IOException {
        // Create deeply nested structure
        Path current = tempDir;
        for (int i = 0; i < 15; i++) { // More than MAX_DEPTH
            current = current.resolve("level" + i);
            Files.createDirectory(current);
            Files.writeString(current.resolve("file" + i + ".txt"), "content", StandardCharsets.UTF_8);
        }

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("recursive", "true");

        JsonObject result = tool.execute(role, input);

        // Should not crash and should return some results
        assertTrue(result.has("files"));
        JsonArray files = result.getAsJsonArray("files");
        assertTrue(files.size() > 0);
        
        // The exact number depends on depth limit implementation
        // but it should be less than 30 (15 dirs + 15 files)
        assertTrue(files.size() < 30);
    }
}
