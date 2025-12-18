package run.mone.hive.shannon;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import run.mone.hive.shannon.types.hooks.HookEvent;
import run.mone.hive.shannon.types.hooks.HookMatcher;
import run.mone.hive.shannon.types.mcp.McpServerConfig;
import run.mone.hive.shannon.types.permissions.PermissionMode;
import run.mone.hive.shannon.types.permissions.ToolPermissionCallback;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Configuration options for Claude Agent SDK.
 * This is an MVP version with ~20 essential fields (not all 67 from Python SDK).
 */
@Data
@Builder(toBuilder = true)
public class ClaudeAgentOptions {

    // ========== Model Configuration ==========

    /**
     * Model to use: "sonnet", "opus", or "haiku".
     */
    private String model;

    /**
     * API key for Claude API (if not using environment variable).
     */
    private String apiKey;

    /**
     * System prompt to use for the conversation.
     */
    private String systemPrompt;

    // ========== Tools ==========

    /**
     * List of tools to enable (e.g., "Bash", "Edit", "Read").
     */
    @Singular
    private List<String> tools;

    /**
     * List of tools to disable.
     */
    @Singular
    private List<String> disabledTools;

    // ========== MCP Servers ==========

    /**
     * MCP server configurations.
     * Key: server name, Value: server config (stdio, SSE, or SDK).
     */
    @Singular("mcpServer")
    private Map<String, McpServerConfig> mcpServers;

    // ========== Permissions ==========

    /**
     * Permission mode for tool usage.
     */
    @Builder.Default
    private PermissionMode permissionMode = PermissionMode.AUTO;

    /**
     * Callback for determining tool permissions dynamically.
     */
    private ToolPermissionCallback canUseTool;

    /**
     * Directories allowed for file operations.
     */
    @Singular
    private List<Path> allowedDirectories;

    // ========== Hooks ==========

    /**
     * Hook configurations.
     * Key: hook event, Value: list of hook matchers.
     */
    @Singular("hook")
    private Map<HookEvent, List<HookMatcher>> hooks;

    // ========== Session ==========

    /**
     * Current working directory for the CLI process.
     */
    private Path cwd;

    /**
     * Environment variables for the CLI process.
     */
    @Singular("envVar")
    private Map<String, String> env;

    /**
     * Session ID for resuming conversations.
     */
    private String sessionId;

    // ========== Budget & Limits ==========

    /**
     * Maximum budget in USD.
     */
    private Double maxBudget;

    /**
     * Maximum number of execution rounds.
     */
    private Integer maxRounds;

    // ========== Streaming & Output ==========

    /**
     * Enable streaming mode.
     */
    @Builder.Default
    private boolean stream = true;

    /**
     * Path to Claude Code CLI binary (null = auto-detect).
     */
    private String cliPath;

    // ========== Advanced ==========

    /**
     * Additional settings as key-value pairs.
     */
    @Singular("setting")
    private Map<String, Object> settings;

    /**
     * Validate the configuration.
     *
     * @throws IllegalArgumentException if configuration is invalid
     */
    public void validate() {
        if (canUseTool != null && permissionMode == PermissionMode.PROMPT) {
            throw new IllegalArgumentException(
                "canUseTool callback cannot be used with PROMPT permission mode");
        }

        if (mcpServers != null) {
            for (Map.Entry<String, McpServerConfig> entry : mcpServers.entrySet()) {
                if (entry.getKey() == null || entry.getKey().isEmpty()) {
                    throw new IllegalArgumentException("MCP server name cannot be empty");
                }
                if (entry.getValue() == null) {
                    throw new IllegalArgumentException("MCP server config cannot be null");
                }
            }
        }

        if (maxBudget != null && maxBudget < 0) {
            throw new IllegalArgumentException("maxBudget cannot be negative");
        }

        if (maxRounds != null && maxRounds < 1) {
            throw new IllegalArgumentException("maxRounds must be at least 1");
        }
    }

    /**
     * Create a default configuration.
     */
    public static ClaudeAgentOptions defaults() {
        return ClaudeAgentOptions.builder()
            .model("sonnet")
            .permissionMode(PermissionMode.AUTO)
            .stream(true)
            .build();
    }

    /**
     * Create a configuration for quick queries with minimal permissions.
     */
    public static ClaudeAgentOptions quickQuery() {
        return ClaudeAgentOptions.builder()
            .model("sonnet")
            .permissionMode(PermissionMode.DENY)
            .stream(false)
            .build();
    }

    /**
     * Create a configuration with full tool access.
     */
    public static ClaudeAgentOptions fullAccess() {
        return ClaudeAgentOptions.builder()
            .model("sonnet")
            .tool("Bash")
            .tool("Edit")
            .tool("Read")
            .tool("Write")
            .tool("Glob")
            .tool("Grep")
            .permissionMode(PermissionMode.ALLOW)
            .stream(true)
            .build();
    }
}
