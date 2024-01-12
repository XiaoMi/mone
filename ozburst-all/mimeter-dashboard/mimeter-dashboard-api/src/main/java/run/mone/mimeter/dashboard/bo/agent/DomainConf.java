package run.mone.mimeter.dashboard.bo.agent;

import lombok.Data;

import java.io.Serializable;
@Data
public class DomainConf implements Serializable {
    private String domain;
    private String ip;
}
