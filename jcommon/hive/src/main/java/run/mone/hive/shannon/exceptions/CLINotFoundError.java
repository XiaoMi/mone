package run.mone.hive.shannon.exceptions;

/**
 * Exception thrown when the Claude Code CLI binary cannot be found.
 */
public class CLINotFoundError extends ClaudeAgentException {

    private static final String DEFAULT_MESSAGE =
        "Claude Code CLI not found. Please install it using: npm install -g @anthropic-ai/claude-code";

    public CLINotFoundError() {
        super(DEFAULT_MESSAGE);
    }

    public CLINotFoundError(String customMessage) {
        super(customMessage);
    }

    public CLINotFoundError(String message, Throwable cause) {
        super(message, cause);
    }
}
