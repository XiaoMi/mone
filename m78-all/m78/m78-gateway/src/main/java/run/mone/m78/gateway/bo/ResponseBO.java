package run.mone.m78.gateway.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseBO implements Serializable {
    private int code;
    private String msg;
    private String originalAction;
    private ResponseBodyBO data;
    private String requestId;
    private String gatewayType;
}
