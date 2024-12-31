package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DomainApplyDTO implements Serializable {
    private Integer id;

    @HttpApiDocClassDefine(value = "applyUser", required = true, description = "申请人", defaultValue = "dzx")
    private String applyUser;

    @HttpApiDocClassDefine(value = "ip", required = true, description = "绑定的ip", defaultValue = "127.0.0.1")
    private String ip;

    @HttpApiDocClassDefine(value = "domain", required = true, description = "域名", defaultValue = "com.xiao.xxx")
    private String domain;

    @HttpApiDocClassDefine(value = "applyStatus", required = true, description = "申请状态", defaultValue = "0：待审核 1：审核完成 2:拒绝")
    private Integer applyStatus;

    @HttpApiDocClassDefine(value = "agentIpList", required = true, description = "绑定的机器ip列表", defaultValue = "")
    private List<String> agentIpList;

    @HttpApiDocClassDefine(value = "ctime", required = true, description = "申请时间", defaultValue = "")
    private Long ctime;
}
