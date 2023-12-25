package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class TenantForAgentReq implements Serializable {
    @HttpApiDocClassDefine(value = "agentID", required = true, description = "发压机Id", defaultValue = "66")
    Integer agentID;
    @HttpApiDocClassDefine(value = "tenant", required = true, description = "租户id", defaultValue = "xx/yy")
    String tenant;
    @HttpApiDocClassDefine(value = "tenantCn", required = true, description = "租户id 中文路径", defaultValue = ".../效能组")
    String tenantCn;
}
