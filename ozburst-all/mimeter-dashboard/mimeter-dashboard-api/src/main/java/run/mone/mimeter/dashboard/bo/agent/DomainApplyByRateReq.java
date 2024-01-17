package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DomainApplyByRateReq implements Serializable {
    @HttpApiDocClassDefine(value = "agentIPs", required = true, description = "发压机Ip列表", defaultValue = "")
    List<String> agentIPs;

    @HttpApiDocClassDefine(value = "applier",ignore = true)
    String applier;

    @HttpApiDocClassDefine(value = "domain",required = true,description = "要绑定的域名")
    String domain;

    @HttpApiDocClassDefine(value = "domainIpAndRates",required = true,description = "ip和比例")
    List<DomainIpAndRate> domainIpAndRates;

}
