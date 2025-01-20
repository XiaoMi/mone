
package run.mone.hive.mcp.hub;

import com.fasterxml.jackson.databind.ObjectMapper;
import run.mone.hive.mcp.client.transport.ServerParameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class McpSettings {
    private Map<String, ServerParameters> mcpServers;

    public static McpSettings fromFile(Path path) throws IOException {
        String content = new String(Files.readAllBytes(path));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content, McpSettings.class);
    }

    public Map<String, ServerParameters> getMcpServers() {
        return mcpServers;
    }

    public void setMcpServers(Map<String, ServerParameters> mcpServers) {
        this.mcpServers = mcpServers;
    }
}
