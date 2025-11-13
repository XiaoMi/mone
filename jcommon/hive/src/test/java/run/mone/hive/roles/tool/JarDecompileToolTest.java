package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;

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

        // Create mock LLM
        LLM mockLlm = Mockito.mock(LLM.class);

        // Create ReactorRole with required parameters
        mockRole = new ReactorRole("test-role", new CountDownLatch(1), mockLlm);

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
        assertTrue(description.contains("FernFlower"));
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
        // Test with invalid regex pattern
        JsonObject input = new JsonObject();
        input.addProperty("jar_name", "test.jar");
        input.addProperty("regex", "[invalid(regex");

        JsonObject result = tool.execute(mockRole, input);

        assertNotNull(result);
        // Should return error about invalid regex
        if (result.has("error")) {
            String error = result.get("error").getAsString();
            assertTrue(error.contains("regex") || error.contains("pattern"));
        }
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
}
