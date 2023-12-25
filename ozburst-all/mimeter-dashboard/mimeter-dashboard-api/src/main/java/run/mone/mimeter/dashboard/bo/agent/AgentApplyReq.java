package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AgentApplyReq implements Serializable {
    @HttpApiDocClassDefine(value = "agentIDs", required = true, description = "发压机Id列表", defaultValue = "")
    List<Integer> agentIDs;

    @HttpApiDocClassDefine(value = "applier", ignore = true)
    String applier;
}
