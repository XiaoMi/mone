package run.mone.mimeter.dashboard.bo.agent;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AgentHostsConf implements Serializable {
    private String agentIp;
    private List<DomainConf> domainConfs;
}
