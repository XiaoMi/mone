
package run.mone.mcp.idea.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdeaFunctionTest {

    private IdeaFunction ideaFunction;

    @BeforeEach
    void setUp() {
        ideaFunction = new IdeaFunction();
        ideaFunction.setIdeaPort(30000);
    }

    @Test
    void testCloseEditors() {
        // Assuming closeEditors returns a boolean indicating success
        String result = ideaFunction.closeAllEditors();
        System.out.println(result);
    }

    @Test
    void testGetContent() {
        String content = ideaFunction.getCurrentEditorContent();
        assertNotNull(content, "Content should not be null");
        // Add more specific assertions based on expected content
    }
}
