package run.mone.mimeter.agent.manager.bo;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;

import java.io.Serializable;

/**
 * 入库的agent信息
 */
@Data
public class AgentInfo implements Serializable {

    @Id
    private Integer id;

    @Column(value = "server_name")
    private String serverName;
    @Column
    private String ip;

    @Column
    private String hostname;

    @Column
    private int port;

    @Column
    private int cpu;

    @Column
    private long mem;

    @Column(value = "use_cpu")
    private int useCpu;

    @Column(value = "use_mem")
    private long useMem;

    @Column
    private Long utime;

    @Column
    private Long ctime;

    @Column(value = "client_desc")
    private String clientDesc;

    @Column(value = "node_ip")
    private String nodeIp;

    @Column
    private boolean enable;

}
