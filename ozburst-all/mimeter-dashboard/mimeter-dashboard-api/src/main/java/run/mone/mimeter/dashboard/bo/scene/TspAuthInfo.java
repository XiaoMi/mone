package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class TspAuthInfo implements Serializable {
    @HttpApiDocClassDefine(value = "enableAuth", description = "是否启用TSP验权", defaultValue = "false")
    private boolean enableAuth;
    @HttpApiDocClassDefine(value = "accessKey", description = "TSP验权的 accessKey", defaultValue = "xxxx")
    private String accessKey;
    @HttpApiDocClassDefine(value = "secretKey", description = "TSP验权的 secretKey", defaultValue = "xxxx")
    private String secretKey;

    public TspAuthInfo(boolean enableAuth) {
        this.enableAuth = enableAuth;
    }

    public TspAuthInfo() {
    }

    public TspAuthInfo(boolean enableAuth, String accessKey, String secretKey) {
        this.enableAuth = enableAuth;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
}
