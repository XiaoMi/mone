package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

@Data
public class TspAuthInfoDTO {
    private boolean enableAuth;
    private String accessKey;
    private String secretKey;

    public TspAuthInfoDTO(boolean enableAuth, String accessKey, String secretKey) {
        this.enableAuth = enableAuth;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
}
