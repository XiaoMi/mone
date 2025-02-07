
package run.mone.mcp.writer.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.writer.service.WriterService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WriterFunctionTest {

    @Autowired
    private WriterFunction writerFunction;

    @Autowired
    private WriterService writerService;

    @BeforeEach
    void setUp() {
        // Any setup code if needed
    }

    @Test
    void testWriteNewArticle() {
        // Prepare test data
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("operation", "writeNewArticle");
        arguments.put("topic", "The benefits of regular exercise");

        // Call the function
        McpSchema.CallToolResult result = writerFunction.apply(arguments);

        // Assertions
        assertNotNull(result);
    }
}
