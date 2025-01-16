package run.mone.m78.gateway.bo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginData {

    private String token;
    private String channel;
    private String userName;
    private String userId;
    private String group;
    private String robotId;
}
