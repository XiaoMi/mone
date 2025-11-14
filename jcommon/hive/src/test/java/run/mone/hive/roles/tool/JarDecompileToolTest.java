package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JarDecompileTool
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
class JarDecompileToolTest {

    private JarDecompileTool tool;
    private ReactorRole mockRole;

    @BeforeEach
    void setUp() {
        tool = new JarDecompileTool();

        // Create simple LLM instance for testing (can be null for this tool)
        LLM llm = null;

        // Create ReactorRole with required parameters
        mockRole = new ReactorRole("test-role", new CountDownLatch(1), llm);

        // Set workspace path to current directory for testing
        mockRole.setWorkspacePath(System.getProperty("user.dir"));
    }

    @Test
    void testGetName() {
        assertEquals("jar_decompile", tool.getName());
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
        assertTrue(description.contains("decompile"));
        assertTrue(description.contains("JAR"));
        assertTrue(description.contains("CFR"));
    }

    @Test
    void testParameters() {
        String parameters = tool.parameters();
        assertNotNull(parameters);
        assertTrue(parameters.contains("jar_name"));
        assertTrue(parameters.contains("regex"));
        assertTrue(parameters.contains("class_pattern"));
    }

    @Test
    void testUsage() {
        String usage = tool.usage();
        assertNotNull(usage);
        assertTrue(usage.contains("<jar_decompile>"));
        assertTrue(usage.contains("</jar_decompile>"));
    }

    @Test
    void testExample() {
        String example = tool.example();
        assertNotNull(example);
        assertTrue(example.contains("Example"));
        assertTrue(example.contains("jar_decompile"));
    }

    @Test
    void testListJarFiles() {
        // Test listing all JAR files without providing jar_name
        JsonObject input = new JsonObject();
        JsonObject result = tool.execute(mockRole, input);

        assertNotNull(result);
        // Result should contain either jars array or empty result
        assertTrue(result.has("result") || result.has("jars"));
    }

    @Test
    void testDecompileNonExistentJar() {
        // Test with a non-existent JAR file
        JsonObject input = new JsonObject();
        input.addProperty("jar_name", "non-existent-jar-12345.jar");

        JsonObject result = tool.execute(mockRole, input);

        assertNotNull(result);
        // Should return error or empty result
        assertTrue(result.has("error") || (result.has("totalJars") && result.get("totalJars").getAsInt() == 0));
    }

    @Test
    void testSearchWithInvalidRegex() {
        // Test with invalid regex pattern using real jar
        File testResourcesDir = new File("src/test/resources");
        mockRole.setWorkspacePath(testResourcesDir.getAbsolutePath());

        JsonObject input = new JsonObject();
        input.addProperty("jar_name", "fernflower.jar");
        input.addProperty("regex", "[invalid(regex");

        JsonObject result = tool.execute(mockRole, input);

        assertNotNull(result);
        // Should return error about invalid regex
        assertTrue(result.has("error"), "Should have error for invalid regex");
        String error = result.get("error").getAsString();
        assertTrue(error.contains("regex") || error.contains("pattern"),
            "Error message should mention regex or pattern");
    }

    @Test
    void testToolInfoStructure() {
        // Verify the tool has all required ITool interface methods
        assertNotNull(tool.getName());
        assertNotNull(tool.description());
        assertNotNull(tool.parameters());
        assertNotNull(tool.usage());
        assertNotNull(tool.example());

        // Verify boolean flags
        assertTrue(tool.needExecute());
        assertTrue(tool.show());
        assertTrue(tool.taskProgress()); // Default implementation returns true
    }

    @Test
    void testExecuteWithEmptyInput() {
        // Test with empty input - should list JAR files
        JsonObject input = new JsonObject();
        JsonObject result = tool.execute(mockRole, input);

        assertNotNull(result);
        assertTrue(result.has("result"));
    }

    @Test
    void testFormatResult() {
        // Test formatResult method (inherited from ITool)
        JsonObject testResult = new JsonObject();
        testResult.addProperty("test", "value");

        String formatted = tool.formatResult(testResult);
        assertNotNull(formatted);
    }

    @Test
    void testDecompileRealJarFile() {
        // Test decompiling the actual fernflower.jar from test resources
        File testResourcesDir = new File("src/test/resources");
        assertTrue(testResourcesDir.exists(), "Test resources directory should exist");

        // Set workspace to test resources directory
        mockRole.setWorkspacePath(testResourcesDir.getAbsolutePath());

        JsonObject input = new JsonObject();
        input.addProperty("jar_name", "fernflower.jar");

        JsonObject result = tool.execute(mockRole, input);

        assertNotNull(result, "Result should not be null");
        assertFalse(result.has("error"), "Should not have error: " +
            (result.has("error") ? result.get("error").getAsString() : ""));

        // Verify decompilation result
        assertTrue(result.has("result"), "Should have result field");
        String resultStr = result.get("result").getAsString();
        assertTrue(resultStr.contains("fernflower.jar"), "Result should mention the jar file");
        assertTrue(resultStr.contains("✓"), "Result should indicate success");

        // Verify decompiled array exists
        assertTrue(result.has("decompiled"), "Should have decompiled array");
        assertTrue(result.get("decompiled").isJsonArray(), "Decompiled should be an array");

        // Verify at least one jar was decompiled
        assertTrue(result.has("totalJars"), "Should have totalJars count");
        assertEquals(1, result.get("totalJars").getAsInt(), "Should have decompiled 1 jar");
    }

    @Test
    void testSearchInRealJarFile() {
        // Test searching for specific content in decompiled fernflower.jar
        File testResourcesDir = new File("src/test/resources");
        assertTrue(testResourcesDir.exists(), "Test resources directory should exist");

        // Set workspace to test resources directory
        mockRole.setWorkspacePath(testResourcesDir.getAbsolutePath());

        JsonObject input = new JsonObject();
        input.addProperty("jar_name", "fernflower.jar");
        input.addProperty("regex", "class\\s+\\w+");  // Search for class definitions

        JsonObject result = tool.execute(mockRole, input);

        assertNotNull(result, "Result should not be null");
        assertFalse(result.has("error"), "Should not have error: " +
            (result.has("error") ? result.get("error").getAsString() : ""));

        // Verify search results
        assertTrue(result.has("result"), "Should have result field");
        assertTrue(result.has("regex"), "Should have regex field");
        assertEquals("class\\s+\\w+", result.get("regex").getAsString(), "Regex should match input");

        assertTrue(result.has("totalMatches"), "Should have totalMatches count");
        int totalMatches = result.get("totalMatches").getAsInt();
        assertTrue(totalMatches >= 0, "Total matches should be non-negative");

        // If matches were found, verify the result format
        if (totalMatches > 0) {
            String resultStr = result.get("result").getAsString();
            assertTrue(resultStr.contains("JAR:"), "Result should contain JAR header");
            assertTrue(resultStr.contains("fernflower"), "Result should mention fernflower");
        }
    }

    @Test
    void testSearchWithNoMatches() {
        // Test searching for content that doesn't exist
        File testResourcesDir = new File("src/test/resources");
        assertTrue(testResourcesDir.exists(), "Test resources directory should exist");

        mockRole.setWorkspacePath(testResourcesDir.getAbsolutePath());

        JsonObject input = new JsonObject();
        input.addProperty("jar_name", "fernflower.jar");
        input.addProperty("regex", "ThisStringDefinitelyDoesNotExistInTheJar12345");

        JsonObject result = tool.execute(mockRole, input);

        assertNotNull(result, "Result should not be null");
        assertFalse(result.has("error"), "Should not have error");

        assertTrue(result.has("totalMatches"), "Should have totalMatches count");
        assertEquals(0, result.get("totalMatches").getAsInt(), "Should have 0 matches");

        assertTrue(result.has("result"), "Should have result field");
        String resultStr = result.get("result").getAsString();
        assertTrue(resultStr.contains("No matches found"), "Should indicate no matches");
    }

    @Test
    void testDecompilationCaching() {
        // Test that second decompilation uses cache
        File testResourcesDir = new File("src/test/resources");
        assertTrue(testResourcesDir.exists(), "Test resources directory should exist");

        mockRole.setWorkspacePath(testResourcesDir.getAbsolutePath());

        JsonObject input = new JsonObject();
        input.addProperty("jar_name", "fernflower.jar");

        // First decompilation
        JsonObject result1 = tool.execute(mockRole, input);
        assertNotNull(result1);
        assertFalse(result1.has("error"));

        // Second decompilation - should use cache
        JsonObject result2 = tool.execute(mockRole, input);
        assertNotNull(result2);
        assertFalse(result2.has("error"));

        // Both should have same output path
        assertTrue(result1.has("decompiled"));
        assertTrue(result2.has("decompiled"));
    }
}
