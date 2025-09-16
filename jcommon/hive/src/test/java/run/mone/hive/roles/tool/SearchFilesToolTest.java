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
 * SearchFilesTool test class
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
class SearchFilesToolTest {

    private SearchFilesTool tool;
    private ReactorRole role;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        tool = new SearchFilesTool();
        role = new ReactorRole(); // Assuming default constructor exists
    }

    @Test
    void testGetName() {
        assertEquals("search_files", tool.getName());
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
        assertTrue(description.contains("regex search"));
        assertTrue(description.contains("context-rich results"));
        assertTrue(description.contains("multiple files"));
    }

    @Test
    void testParameters() {
        String parameters = tool.parameters();
        assertNotNull(parameters);
        assertTrue(parameters.contains("path"));
        assertTrue(parameters.contains("regex"));
        assertTrue(parameters.contains("file_pattern"));
    }

    @Test
    void testUsage() {
        String usage = tool.usage();
        assertNotNull(usage);
        assertTrue(usage.contains("search_files"));
        assertTrue(usage.contains("<path>"));
        assertTrue(usage.contains("<regex>"));
    }

    @Test
    void testExample() {
        String example = tool.example();
        assertNotNull(example);
        assertTrue(example.contains("Example"));
        assertTrue(example.contains("search_files"));
    }

    @Test
    void testExecuteWithMissingPath() {
        JsonObject input = new JsonObject();
        input.addProperty("regex", "test");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("path"));
    }

    @Test
    void testExecuteWithMissingRegex() {
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("regex"));
    }

    @Test
    void testExecuteWithEmptyPath() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "");
        input.addProperty("regex", "test");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("path"));
    }

    @Test
    void testExecuteWithNonExistentDirectory() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "nonexistent");
        input.addProperty("regex", "test");

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
        input.addProperty("regex", "test");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("file, not a directory"));
    }

    @Test
    void testSearchEmptyDirectory() {
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "test");

        JsonObject result = tool.execute(role, input);

        // Verify successful result with no matches
        assertTrue(result.has("result"));
        assertTrue(result.has("totalMatches"));
        assertTrue(result.has("summary"));

        assertEquals("No matches found.", result.get("result").getAsString());
        assertEquals(0, result.get("totalMatches").getAsInt());
    }

    @Test
    void testSimpleTextSearch() throws IOException {
        // Create test files
        Files.writeString(tempDir.resolve("file1.txt"), 
            "This is a test file.\nIt contains some test content.\nEnd of file.", 
            StandardCharsets.UTF_8);
        
        Files.writeString(tempDir.resolve("file2.txt"), 
            "Another file here.\nNo matching content in this one.\nJust regular text.", 
            StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "test");

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("result"));
        assertTrue(result.has("totalMatches"));
        assertTrue(result.has("detailedResults"));

        assertFalse(result.get("result").getAsString().equals("No matches found."));
        assertTrue(result.get("totalMatches").getAsInt() > 0);

        String resultContent = result.get("result").getAsString();
        assertTrue(resultContent.contains("file1.txt"));
        assertTrue(resultContent.contains("│----"));
        assertTrue(resultContent.contains("test"));

        // Check detailed results
        JsonArray detailedResults = result.getAsJsonArray("detailedResults");
        assertTrue(detailedResults.size() > 0);

        JsonObject fileResult = detailedResults.get(0).getAsJsonObject();
        assertEquals("file1.txt", fileResult.get("file").getAsString());
        assertTrue(fileResult.get("matches").getAsInt() > 0);
        assertTrue(fileResult.has("matchDetails"));
    }

    @Test
    void testRegexPatternSearch() throws IOException {
        // Create Java file with various patterns
        String javaCode = """
            package com.example;
            
            public class TestClass {
                public void testMethod() {
                    System.out.println("Hello World");
                }
                
                private String getName() {
                    return "test";
                }
                
                public static void main(String[] args) {
                    // TODO: Implement main logic
                    new TestClass().testMethod();
                }
            }
            """;

        Files.writeString(tempDir.resolve("TestClass.java"), javaCode, StandardCharsets.UTF_8);

        // Search for method definitions
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "public\\s+\\w+\\s+\\w+\\s*\\(");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("result"));
        assertTrue(result.get("totalMatches").getAsInt() > 0);

        String resultContent = result.get("result").getAsString();
        assertTrue(resultContent.contains("TestClass.java"));
        assertTrue(resultContent.contains("public void testMethod"));
        assertTrue(resultContent.contains("public static void main"));
    }

    @Test
    void testFilePatternFiltering() throws IOException {
        // Create files with different extensions
        Files.writeString(tempDir.resolve("test.java"), 
            "public class Test { /* TODO: implement */ }", 
            StandardCharsets.UTF_8);
        
        Files.writeString(tempDir.resolve("test.js"), 
            "function test() { // TODO: implement }", 
            StandardCharsets.UTF_8);
        
        Files.writeString(tempDir.resolve("test.txt"), 
            "This is a TODO item in text file.", 
            StandardCharsets.UTF_8);

        // Search only in Java files
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "TODO:");
        input.addProperty("file_pattern", "*.java");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("result"));
        assertTrue(result.get("totalMatches").getAsInt() > 0);

        String resultContent = result.get("result").getAsString();
        assertTrue(resultContent.contains("test.java"));
        assertFalse(resultContent.contains("test.js"));
        assertFalse(resultContent.contains("test.txt"));

        // Check file pattern is recorded
        assertTrue(result.has("filePattern"));
        assertEquals("*.java", result.get("filePattern").getAsString());
    }

    @Test
    void testMultipleMatches() throws IOException {
        String content = """
            First TODO: Fix this bug
            Some other content here
            Second TODO: Add more tests
            More content
            Third TODO: Refactor this method
            """;

        Files.writeString(tempDir.resolve("todos.txt"), content, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "TODO:");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("result"));
        assertEquals(3, result.get("totalMatches").getAsInt());

        JsonArray detailedResults = result.getAsJsonArray("detailedResults");
        assertEquals(1, detailedResults.size()); // One file

        JsonObject fileResult = detailedResults.get(0).getAsJsonObject();
        assertEquals("todos.txt", fileResult.get("file").getAsString());
        assertEquals(3, fileResult.get("matches").getAsInt());

        JsonArray matchDetails = fileResult.getAsJsonArray("matchDetails");
        assertEquals(3, matchDetails.size());

        // Check line numbers are correct
        JsonObject match1 = matchDetails.get(0).getAsJsonObject();
        JsonObject match2 = matchDetails.get(1).getAsJsonObject();
        JsonObject match3 = matchDetails.get(2).getAsJsonObject();

        assertEquals(1, match1.get("line").getAsInt());
        assertEquals(3, match2.get("line").getAsInt());
        assertEquals(5, match3.get("line").getAsInt());
    }

    @Test
    void testContextLines() throws IOException {
        String content = """
            Line 1
            Line 2 - before context
            Line 3 - MATCH HERE
            Line 4 - after context
            Line 5
            """;

        Files.writeString(tempDir.resolve("context.txt"), content, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "MATCH HERE");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("result"));
        String resultContent = result.get("result").getAsString();

        // Should contain the match line and context
        assertTrue(resultContent.contains("Line 2 - before context"));
        assertTrue(resultContent.contains("Line 3 - MATCH HERE"));
        assertTrue(resultContent.contains("Line 4 - after context"));
        
        // Should not contain lines too far away
        assertFalse(resultContent.contains("Line 1"));
        assertFalse(resultContent.contains("Line 5"));
    }

    @Test
    void testRecursiveSearch() throws IOException {
        // Create nested directory structure
        Path subDir = tempDir.resolve("subdir");
        Files.createDirectory(subDir);
        Path nestedDir = subDir.resolve("nested");
        Files.createDirectory(nestedDir);

        Files.writeString(tempDir.resolve("root.txt"), "Root file with SEARCH_TERM", StandardCharsets.UTF_8);
        Files.writeString(subDir.resolve("sub.txt"), "Sub file with SEARCH_TERM", StandardCharsets.UTF_8);
        Files.writeString(nestedDir.resolve("nested.txt"), "Nested file with SEARCH_TERM", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "SEARCH_TERM");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("result"));
        assertEquals(3, result.get("totalMatches").getAsInt());

        String resultContent = result.get("result").getAsString();
        assertTrue(resultContent.contains("root.txt"));
        assertTrue(resultContent.contains("subdir/sub.txt"));
        assertTrue(resultContent.contains("subdir/nested/nested.txt"));
    }

    @Test
    void testIgnoreDirectories() throws IOException {
        // Create directories that should be ignored
        Path nodeModules = tempDir.resolve("node_modules");
        Files.createDirectory(nodeModules);
        Path gitDir = tempDir.resolve(".git");
        Files.createDirectory(gitDir);
        Path target = tempDir.resolve("target");
        Files.createDirectory(target);

        // Create files in ignored directories
        Files.writeString(nodeModules.resolve("package.txt"), "SEARCH_TERM in node_modules", StandardCharsets.UTF_8);
        Files.writeString(gitDir.resolve("config.txt"), "SEARCH_TERM in .git", StandardCharsets.UTF_8);
        Files.writeString(target.resolve("output.txt"), "SEARCH_TERM in target", StandardCharsets.UTF_8);

        // Create file in main directory
        Files.writeString(tempDir.resolve("main.txt"), "SEARCH_TERM in main", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "SEARCH_TERM");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("result"));
        assertEquals(1, result.get("totalMatches").getAsInt()); // Only main.txt should be found

        String resultContent = result.get("result").getAsString();
        assertTrue(resultContent.contains("main.txt"));
        assertFalse(resultContent.contains("node_modules"));
        assertFalse(resultContent.contains(".git"));
        assertFalse(resultContent.contains("target"));
    }

    @Test
    void testBinaryFileSkipping() throws IOException {
        // Create a binary-like file (with non-printable characters)
        byte[] binaryContent = new byte[100];
        for (int i = 0; i < binaryContent.length; i++) {
            binaryContent[i] = (byte) (i % 256);
        }
        Files.write(tempDir.resolve("binary.dat"), binaryContent);

        // Create a text file
        Files.writeString(tempDir.resolve("text.txt"), "SEARCH_TERM in text file", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "SEARCH_TERM");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("result"));
        assertEquals(1, result.get("totalMatches").getAsInt());

        String resultContent = result.get("result").getAsString();
        assertTrue(resultContent.contains("text.txt"));
        assertFalse(resultContent.contains("binary.dat"));
    }

    @Test
    void testInvalidRegex() {
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "[unclosed bracket");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("Invalid regex pattern"));
    }

    @Test
    void testInvalidFilePattern() throws IOException {
        Files.writeString(tempDir.resolve("test.txt"), "content", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "content");
        input.addProperty("file_pattern", "[invalid glob");

        JsonObject result = tool.execute(role, input);

        // Should still work, just ignore invalid pattern
        assertTrue(result.has("result"));
        // Should find the match since invalid pattern is ignored
        assertTrue(result.get("totalMatches").getAsInt() > 0);
    }

    @Test
    void testSummaryStatistics() throws IOException {
        Files.writeString(tempDir.resolve("file1.txt"), "test content", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("file2.txt"), "more test content", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("file3.txt"), "no match here", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "test");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("summary"));
        JsonObject summary = result.getAsJsonObject("summary");

        assertTrue(summary.get("filesSearched").getAsInt() >= 3);
        assertEquals(2, summary.get("filesWithMatches").getAsInt());
        assertEquals(2, summary.get("totalMatches").getAsInt());
        assertFalse(summary.get("wasLimited").getAsBoolean());
    }

    @Test
    void testCaseSensitiveSearch() throws IOException {
        Files.writeString(tempDir.resolve("case.txt"), 
            "This has Test and test and TEST", 
            StandardCharsets.UTF_8);

        // Search for lowercase "test"
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "test");

        JsonObject result = tool.execute(role, input);

        assertEquals(1, result.get("totalMatches").getAsInt()); // Only lowercase "test"
    }

    @Test
    void testCaseInsensitiveSearch() throws IOException {
        Files.writeString(tempDir.resolve("case.txt"), 
            "This has Test and test and TEST", 
            StandardCharsets.UTF_8);

        // Case-insensitive search using regex flag
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "(?i)test");

        JsonObject result = tool.execute(role, input);

        assertEquals(3, result.get("totalMatches").getAsInt()); // All three variants
    }

    @Test
    void testWordBoundarySearch() throws IOException {
        Files.writeString(tempDir.resolve("words.txt"), 
            "test testing tested contest", 
            StandardCharsets.UTF_8);

        // Search for whole word "test" only
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "\\btest\\b");

        JsonObject result = tool.execute(role, input);

        assertEquals(1, result.get("totalMatches").getAsInt()); // Only exact "test"
    }

    @Test
    void testMultilineContent() throws IOException {
        String multilineContent = """
            function example() {
                if (condition) {
                    return true;
                }
                return false;
            }
            """;

        Files.writeString(tempDir.resolve("multiline.js"), multilineContent, StandardCharsets.UTF_8);

        // Search for function pattern
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "function\\s+\\w+\\s*\\(");

        JsonObject result = tool.execute(role, input);

        assertEquals(1, result.get("totalMatches").getAsInt());
        assertTrue(result.get("result").getAsString().contains("function example()"));
    }

    @Test
    void testEmptyFileHandling() throws IOException {
        Files.writeString(tempDir.resolve("empty.txt"), "", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("content.txt"), "some content", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "content");

        JsonObject result = tool.execute(role, input);

        assertEquals(1, result.get("totalMatches").getAsInt());
        assertTrue(result.get("result").getAsString().contains("content.txt"));
    }

    @Test
    void testResultFormat() throws IOException {
        Files.writeString(tempDir.resolve("format.txt"), 
            "Line before\nMATCH_LINE with pattern\nLine after", 
            StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());
        input.addProperty("regex", "MATCH_LINE");

        JsonObject result = tool.execute(role, input);

        // Check result format matches expected structure
        assertTrue(result.has("result"));
        assertTrue(result.has("searchPath"));
        assertTrue(result.has("regex"));
        assertTrue(result.has("totalMatches"));
        assertTrue(result.has("wasLimited"));
        assertTrue(result.has("detailedResults"));
        assertTrue(result.has("summary"));

        String resultContent = result.get("result").getAsString();
        
        // Should contain file name and separators
        assertTrue(resultContent.contains("format.txt"));
        assertTrue(resultContent.contains("│----"));
        assertTrue(resultContent.contains("│Line before"));
        assertTrue(resultContent.contains("│MATCH_LINE with pattern"));
        assertTrue(resultContent.contains("│Line after"));
    }
}
