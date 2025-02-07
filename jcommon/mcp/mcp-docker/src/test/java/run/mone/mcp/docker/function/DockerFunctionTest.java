
package run.mone.mcp.docker.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DockerFunctionTest {

    private DockerFunction dockerFunction;

    @BeforeEach
    void setUp() {
        dockerFunction = new DockerFunction();
    }

    @Test
    void testListContainers() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("operation", "listContainers");

        McpSchema.CallToolResult result = dockerFunction.apply(arguments);

        assertNotNull(result);
    }
}
