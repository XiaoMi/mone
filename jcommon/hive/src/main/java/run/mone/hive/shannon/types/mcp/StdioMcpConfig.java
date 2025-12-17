package run.mone.hive.shannon.types.mcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * Configuration for stdio-based MCP servers.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StdioMcpConfig extends McpServerConfig {

    /**
     * Command to execute (e.g., "node", "python").
     */
    private final String command;

    /**
     * Arguments to pass to the command.
     */
    private final List<String> args;

    /**
     * Environment variables for the process.
     */
    private final Map<String, String> env;

    @Builder
    @JsonCreator
    public StdioMcpConfig(
        @JsonProperty("command") String command,
        @JsonProperty("args") @Singular("arg") List<String> args,
        @JsonProperty("env") @Singular("envVar") Map<String, String> env
    ) {
        super("stdio");
        this.command = command;
        this.args = args;
        this.env = env;
    }
}
