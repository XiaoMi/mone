package run.mone.mimeter.dashboard.bo.agent;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AgentDTO implements Serializable {

    private Integer id;

    private String serverName;

    private String ip;

    private Integer port;

    private Integer cpu;

    private Long mem;

    private Integer useCpu;

    private Long useMem;

    private String hostname;

    private String clientDesc;

    private Long ctime;

    private Long utime;

    private Boolean enable;

    private String tenant;

    private String tenantCn;

    private List<DomainConf> domainConfs;
}
