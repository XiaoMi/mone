package run.mone.mcp.miapi.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatewayResponse<T> {
    private Integer code;
    private T data;
    private String message;
    private String detailMsg;
}
