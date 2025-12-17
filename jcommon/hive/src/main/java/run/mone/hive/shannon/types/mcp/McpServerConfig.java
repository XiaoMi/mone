package run.mone.hive.shannon.types.mcp;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for MCP server configurations.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = StdioMcpConfig.class, name = "stdio"),
    @JsonSubTypes.Type(value = SseMcpConfig.class, name = "sse"),
    @JsonSubTypes.Type(value = SdkMcpConfig.class, name = "sdk")
})
public abstract class McpServerConfig {

    private final String type;

    protected McpServerConfig(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isStdio() {
        return this instanceof StdioMcpConfig;
    }

    public boolean isSse() {
        return this instanceof SseMcpConfig;
    }

    public boolean isSdk() {
        return this instanceof SdkMcpConfig;
    }

    public StdioMcpConfig asStdio() {
        return (StdioMcpConfig) this;
    }

    public SseMcpConfig asSse() {
        return (SseMcpConfig) this;
    }

    public SdkMcpConfig asSdk() {
        return (SdkMcpConfig) this;
    }
}
