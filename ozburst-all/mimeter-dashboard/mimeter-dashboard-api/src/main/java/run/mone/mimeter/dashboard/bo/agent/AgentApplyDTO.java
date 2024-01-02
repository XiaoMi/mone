package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class AgentApplyDTO implements Serializable {
    private Integer id;

    @HttpApiDocClassDefine(value = "applyUser", required = true, description = "申请人", defaultValue = "dzx")
    private String applyUser;

    @HttpApiDocClassDefine(value = "applyOrgId", required = true, description = "申请组织id", defaultValue = "DSGKBW")
    private String applyOrgId;

    @HttpApiDocClassDefine(value = "applyOrgName", required = true, description = "申请组织中文名", defaultValue = "研发效能组")
    private String applyOrgName;

    @HttpApiDocClassDefine(value = "agentIp", required = true, description = "压测机ip", defaultValue = "127.0.0.1")
    private String agentIp;

    @HttpApiDocClassDefine(value = "agentHostname", required = true, description = "主机名", defaultValue = "xdwqd")
    private String agentHostname;

    @HttpApiDocClassDefine(value = "applyStatus", required = true, description = "申请状态", defaultValue = "0：待审核 1：审核完成 2:拒绝")
    private Integer applyStatus;

    @HttpApiDocClassDefine(value = "ctime", required = true, description = "申请时间", defaultValue = "")
    private Long ctime;
}
