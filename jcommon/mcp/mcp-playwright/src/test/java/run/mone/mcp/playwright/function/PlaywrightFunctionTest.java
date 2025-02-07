
package run.mone.mcp.playwright.function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.playwright.Bootstrap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Bootstrap.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PlaywrightFunctionTest {

    private PlaywrightFunction playwrightFunction;

    @BeforeEach
    void setUp() {
        playwrightFunction = new PlaywrightFunction();
    }

    @AfterEach
    void tearDown() {
        playwrightFunction.close();
    }

    @Test
    void testLoadPage() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("action", "load_page");
        arguments.put("url", "https://www.baidu.com");

        McpSchema.CallToolResult result = playwrightFunction.apply(arguments);

        assertFalse(result.isError());
    }

    @Test
    void testTakeScreenshot() {
        // First, load a page
        Map<String, Object> loadArguments = new HashMap<>();
        loadArguments.put("action", "load_page");
        loadArguments.put("url", "https://www.baidu.com");
        playwrightFunction.apply(loadArguments);

        // Then, take a screenshot
        Map<String, Object> screenshotArguments = new HashMap<>();
        screenshotArguments.put("action", "take_screenshot");

        McpSchema.CallToolResult result = playwrightFunction.apply(screenshotArguments);

        assertFalse(result.isError());

    }

    @Test
    void testInvalidAction() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("action", "invalid_action");
        McpSchema.CallToolResult result = playwrightFunction.apply(arguments);
        assertTrue(result.isError());
    }
}
