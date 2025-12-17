package run.mone.hive.shannon.exceptions;

/**
 * Exception thrown when there is an error connecting to or communicating with the Claude Code CLI.
 */
public class CLIConnectionError extends ClaudeAgentException {

    public CLIConnectionError(String message) {
        super(message);
    }

    public CLIConnectionError(String message, Throwable cause) {
        super(message, cause);
    }
}
