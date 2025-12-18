package run.mone.hive.shannon.types.mcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Configuration for SSE (Server-Sent Events) based MCP servers.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SseMcpConfig extends McpServerConfig {

    /**
     * URL of the SSE endpoint.
     */
    private final String url;

    /**
     * Optional API key for authentication.
     */
    private final String apiKey;

    @Builder
    @JsonCreator
    public SseMcpConfig(
        @JsonProperty("url") String url,
        @JsonProperty("apiKey") String apiKey
    ) {
        super("sse");
        this.url = url;
        this.apiKey = apiKey;
    }
}
