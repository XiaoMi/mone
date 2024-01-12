package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HostForAgentReq implements Serializable {
    @HttpApiDocClassDefine(value = "agentIp", required = true, description = "发压机Ip", defaultValue = "127.0.0.1")
    String agentIp;
    @HttpApiDocClassDefine(value = "domain", required = true, description = "域名", defaultValue = "com.test.xxx")
    String domain;
    @HttpApiDocClassDefine(value = "ip", required = true, description = "绑定的ip", defaultValue = "127.0.0.1")
    String ip;
}
