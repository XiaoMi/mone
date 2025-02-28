
package run.mone.mcp.gateway.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApiFunctionTest {

    @Autowired
    private ApiFunction apiFunction;


    @BeforeEach
    void setUp() {
        // Any setup code if needed
    }

    @Test
    void testDetailByUrl() {
        // Prepare test data
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("operation", "detailByUrl");
        arguments.put("env", "staging");
        arguments.put("url", "/mtop/test/ceshiyaya1");

        // Call the function
        McpSchema.CallToolResult result = apiFunction.apply(arguments);

        // Assertions
        assertNotNull(result);
    }

    @Test
    void testListApiInfo() {
        // Prepare test data
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("operation", "listApiInfo");
        arguments.put("env", "staging");
        arguments.put("keyword", "auditReject");

        // Call the function
        McpSchema.CallToolResult result = apiFunction.apply(arguments);

        // Assertions
        assertNotNull(result);
    }
}
