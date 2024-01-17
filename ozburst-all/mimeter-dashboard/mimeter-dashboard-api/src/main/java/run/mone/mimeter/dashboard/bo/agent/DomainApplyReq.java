package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DomainApplyReq implements Serializable {
    @HttpApiDocClassDefine(value = "agentIPs", required = true, description = "发压机Ip列表", defaultValue = "")
    List<String> agentIPs;

    @HttpApiDocClassDefine(value = "applier",ignore = true)
    String applier;

    @HttpApiDocClassDefine(value = "domain",required = true,description = "要绑定的域名")
    String domain;

    @HttpApiDocClassDefine(value = "ip",required = true,description = "要绑定的ip",defaultValue = "127.0.0.1")
    String ip;

}
