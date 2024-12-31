package run.mone.mimeter.dashboard.bo;

import lombok.Data;
import run.mone.mimeter.dashboard.bo.task.DagTaskRps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SubmitTaskRes implements Serializable {
    private String reportId;
    private TaskResult taskResult;

    /**
     * 链路id与生成的dag 任务id映射
     */
    private Map<Integer, DagTaskRps> linkTaskIdMap;
    private List<String> agentIpList;

    public SubmitTaskRes(String reportId, TaskResult taskResult, Map<Integer, DagTaskRps> linkTaskIdMap, List<String> agentIpList) {
        this.reportId = reportId;
        this.taskResult = taskResult;
        this.linkTaskIdMap = linkTaskIdMap;
        this.agentIpList = agentIpList;
    }
}
