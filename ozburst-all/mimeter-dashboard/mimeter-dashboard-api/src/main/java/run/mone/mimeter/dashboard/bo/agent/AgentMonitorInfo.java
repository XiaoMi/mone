package run.mone.mimeter.dashboard.bo.agent;

import lombok.Data;

import java.io.Serializable;

@Data
public class AgentMonitorInfo implements Serializable {
    private String podIp;
    private String nodeIp;
}
