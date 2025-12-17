package run.mone.hive.shannon.control;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Control message for bidirectional communication with Claude Code CLI.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ControlMessage {

    /**
     * Message type: "control_request" or "control_response".
     */
    @JsonProperty("type")
    private String type;

    /**
     * Request/response ID for correlation.
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Control action: "interrupt", "set_permission_mode", "set_model", "rewind_files", etc.
     */
    @JsonProperty("action")
    private String action;

    /**
     * Parameters for the action.
     */
    @JsonProperty("params")
    private Map<String, Object> params;

    /**
     * Response status: "success" or "error".
     */
    @JsonProperty("status")
    private String status;

    /**
     * Response data.
     */
    @JsonProperty("data")
    private Object data;

    /**
     * Error message (if status is "error").
     */
    @JsonProperty("error")
    private String error;

    /**
     * Create a control request.
     */
    public static ControlMessage request(Long id, String action, Map<String, Object> params) {
        return ControlMessage.builder()
            .type("control_request")
            .id(id)
            .action(action)
            .params(params)
            .build();
    }

    /**
     * Create a successful control response.
     */
    public static ControlMessage success(Long id, Object data) {
        return ControlMessage.builder()
            .type("control_response")
            .id(id)
            .status("success")
            .data(data)
            .build();
    }

    /**
     * Create an error control response.
     */
    public static ControlMessage error(Long id, String error) {
        return ControlMessage.builder()
            .type("control_response")
            .id(id)
            .status("error")
            .error(error)
            .build();
    }

    /**
     * Check if this is a request.
     */
    public boolean isRequest() {
        return "control_request".equals(type);
    }

    /**
     * Check if this is a response.
     */
    public boolean isResponse() {
        return "control_response".equals(type);
    }

    /**
     * Check if this response indicates success.
     */
    public boolean isSuccess() {
        return "success".equals(status);
    }

    /**
     * Check if this response indicates an error.
     */
    public boolean isError() {
        return "error".equals(status);
    }
}
