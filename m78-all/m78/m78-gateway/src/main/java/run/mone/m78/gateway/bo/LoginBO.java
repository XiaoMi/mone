package run.mone.m78.gateway.bo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginBO {

    private String action;
    private String requestId;
    private String sessionId;
    private String messageId;
    private String group;

    private LoginData data;
}