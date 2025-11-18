
package run.mone.hive.mcp.hub;

import lombok.Data;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;

@Data
public class McpServer {
    private final String name;
    private final String config;
    private String status;
    private String error;
    private List<McpSchema.Tool> tools;
    private List<io.modelcontextprotocol.spec.McpSchema.Tool> toolsV2;

    private McpSchema.Implementation serverInfo;

    private io.modelcontextprotocol.spec.McpSchema.Implementation serverInfoV2;

    private ServerParameters serverParameters;

    private io.modelcontextprotocol.client.transport.ServerParameters newServerParameters;

    public McpServer(String name, String config) {
        this.name = name;
        this.config = config;
        this.status = "disconnected";
    }


    public String getName() {
        return name;
    }

    public String getConfig() {
        return config;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<McpSchema.Tool> getTools() {
        return tools;
    }

    public void setTools(List<McpSchema.Tool> tools) {
        this.tools = tools;
    }
}
