package run.mone.mimeter.engine.agent.bo.task;

import lombok.Data;

import java.io.Serializable;

@Data
public class DagTaskRps implements Serializable {
    String reportId;
    Integer linkId;
    Integer taskId;
    Integer rps;

    public DagTaskRps(String reportId, Integer linkId, Integer taskId, Integer rps) {
        this.reportId = reportId;
        this.linkId = linkId;
        this.taskId = taskId;
        this.rps = rps;
    }
}
