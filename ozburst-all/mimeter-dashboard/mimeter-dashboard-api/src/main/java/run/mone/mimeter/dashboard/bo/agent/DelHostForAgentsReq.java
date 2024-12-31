package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DelHostForAgentsReq implements Serializable {
    @HttpApiDocClassDefine(value = "agentIps", required = true, description = "发压机Ip列表", defaultValue = "127.0.0.1")
    List<String> agentIps;
    @HttpApiDocClassDefine(value = "domain", required = true, description = "域名", defaultValue = "com.test.xxx")
    String domain;
}
