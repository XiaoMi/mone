
package run.mone.hive.mcp.hub;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class McpHubTest {

    private McpHub mcpHub;
    private Path settingsPath;

    @BeforeEach
    void setUp() throws Exception {
        // Create a temporary file for settings
        settingsPath = Files.createTempFile("mcp_settings", ".json");

        // Write some initial settings to the file
        String initialSettings = """
                {
                "redis-mcp": {
                "command": "java",
                "args": [
                "-jar",
                "/Users/zhangzhiyong/IdeaProjects/open/mone/jcommon/mcp/mcp-redis/target/app.jar"
                ]
                }
                }
                """;
        Files.write(settingsPath, initialSettings.getBytes());

        // Create McpHub instance
        mcpHub = new McpHub(settingsPath);

    }

    @SneakyThrows
    @Test
    void testCallTool() {
        // Prepare test data
        String serverName = "redis-mcp";
        String toolName = "redisOperation";
        Map<String, Object> toolArguments = new HashMap<>();
        toolArguments.put("operation", "set");
        toolArguments.put("key", "name");
        toolArguments.put("value", "n");

        TimeUnit.SECONDS.sleep(6);

        // Call the method under test
        McpSchema.CallToolResult result = mcpHub.callTool(serverName, toolName, toolArguments);

        // Assert the result
        assertNotNull(result);
        mcpHub.dispose();
    }

    @Test
    void test() {
        List<McpServer> servers = mcpHub.getServers();
        System.out.println(servers);
        mcpHub.dispose();
    }


    @SneakyThrows
    @Test
    public void test2() {
        TimeUnit.SECONDS.sleep(6);
        McpSchema.ListToolsResult tools = mcpHub.getConnections().get("redis-mcp").getClient().listTools();
        System.out.println(tools);
    }

}
