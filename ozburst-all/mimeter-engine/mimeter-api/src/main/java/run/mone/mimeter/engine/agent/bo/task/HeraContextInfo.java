package run.mone.mimeter.engine.agent.bo.task;

import lombok.Data;

import java.io.Serializable;

@Data
public class HeraContextInfo implements Serializable {
    private Integer sceneId;
    private Integer serialLinkId;
    private Integer sceneApiId;
    private String taskFlag;

    public HeraContextInfo(Integer sceneId, Integer serialLinkId, Integer sceneApiId, String taskFlag) {
        this.sceneId = sceneId;
        this.serialLinkId = serialLinkId;
        this.sceneApiId = sceneApiId;
        this.taskFlag = taskFlag;
    }
}
