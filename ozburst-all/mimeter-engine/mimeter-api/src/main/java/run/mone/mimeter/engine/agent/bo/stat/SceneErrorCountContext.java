package run.mone.mimeter.engine.agent.bo.stat;


import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

@Data
public class SceneErrorCountContext implements Serializable {
    private String reportId;
    private Integer sceneId;

    private Integer taskId;

    private Integer agentNum;

    /**
     * 关联任务数
     */
    private Integer connectTaskNum;

    private boolean lastTime;

    private LongAdder tmpCount;
    /**
     * 全场景请求次数
     */
    private LongAdder totalReq;

    /**
     * 全场景错误次数
     */
    private LongAdder totalErrReq;

    /**
     * <"s_code_errorCode",<apiId,count>> or <"cp_id_checkpointId",<apiId,count>>
     * <"s_code_404",<2342,10>>  or <"cp_id_141242",<2342,10>>
     */
    private ConcurrentMap<String, ConcurrentMap<Integer, LongAdder>> counterMap;

    public SceneErrorCountContext(String reportId, Integer sceneId, Integer taskId, Integer agentNum, Integer connectTaskNum, boolean lastTime, LongAdder totalReq, LongAdder totalErrReq, ConcurrentMap<String, ConcurrentMap<Integer, LongAdder>> counterMap) {
        this.reportId = reportId;
        this.sceneId = sceneId;
        this.taskId = taskId;
        this.agentNum = agentNum;
        this.connectTaskNum = connectTaskNum;
        this.lastTime = lastTime;
        this.totalReq = totalReq;
        this.totalErrReq = totalErrReq;
        this.counterMap = counterMap;
    }
}
