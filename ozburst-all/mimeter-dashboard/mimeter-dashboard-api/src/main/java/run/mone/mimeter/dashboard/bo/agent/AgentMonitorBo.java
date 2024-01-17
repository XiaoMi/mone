package run.mone.mimeter.dashboard.bo.agent;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AgentMonitorBo implements Serializable {
    private Long fromTime;
    private Long toTime;
    private List<AgentMonitorInfo> agentMonitorInfos;
}
