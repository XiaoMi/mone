package run.mone.mimeter.engine.agent.bo.hosts;

import lombok.Data;

import java.io.Serializable;

@Data
public class HostBo implements Serializable {
    /**
     * 绑定的ip
     */
    private String ip;

    /**
     * 绑定的域名
     */
    private String domain;
}
