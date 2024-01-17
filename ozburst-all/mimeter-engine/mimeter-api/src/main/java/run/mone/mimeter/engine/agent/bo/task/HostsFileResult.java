package run.mone.mimeter.engine.agent.bo.task;

import lombok.Data;

import java.io.Serializable;

@Data
public class HostsFileResult implements Serializable {
    private String hostsFile;
}
