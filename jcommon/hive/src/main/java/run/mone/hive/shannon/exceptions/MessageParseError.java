package run.mone.hive.shannon.exceptions;

/**
 * Exception thrown when there is an error parsing a message from the Claude Code CLI.
 */
public class MessageParseError extends ClaudeAgentException {

    private final String rawMessage;

    public MessageParseError(String message, String rawMessage) {
        super(message);
        this.rawMessage = rawMessage;
    }

    public MessageParseError(String message, String rawMessage, Throwable cause) {
        super(message, cause);
        this.rawMessage = rawMessage;
    }

    public String getRawMessage() {
        return rawMessage;
    }
}
