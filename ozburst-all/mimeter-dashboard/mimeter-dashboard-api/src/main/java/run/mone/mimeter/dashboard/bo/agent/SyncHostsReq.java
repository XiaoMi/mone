package run.mone.mimeter.dashboard.bo.agent;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SyncHostsReq implements Serializable {
    private List<AgentHostsConf> agentHostsConfList;
}
