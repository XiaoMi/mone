package run.mone.hive.shannon.exceptions;

/**
 * Base exception for all Claude Agent SDK errors.
 */
public class ClaudeAgentException extends RuntimeException {

    public ClaudeAgentException(String message) {
        super(message);
    }

    public ClaudeAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClaudeAgentException(Throwable cause) {
        super(cause);
    }
}
