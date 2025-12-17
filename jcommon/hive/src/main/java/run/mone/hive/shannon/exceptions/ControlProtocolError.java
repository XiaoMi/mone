package run.mone.hive.shannon.exceptions;

/**
 * Exception thrown when there is an error in the control protocol communication.
 */
public class ControlProtocolError extends ClaudeAgentException {

    private final String requestType;

    public ControlProtocolError(String requestType, String message) {
        super("Control protocol error for " + requestType + ": " + message);
        this.requestType = requestType;
    }

    public ControlProtocolError(String requestType, String message, Throwable cause) {
        super("Control protocol error for " + requestType + ": " + message, cause);
        this.requestType = requestType;
    }

    public String getRequestType() {
        return requestType;
    }
}
