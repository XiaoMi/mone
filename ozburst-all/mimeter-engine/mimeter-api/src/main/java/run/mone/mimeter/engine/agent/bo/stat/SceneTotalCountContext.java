package run.mone.mimeter.engine.agent.bo.stat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import run.mone.mimeter.engine.agent.bo.task.DagTaskRps;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

@Data
@AllArgsConstructor
@Builder
public class SceneTotalCountContext implements Serializable {
    private String reportId;
    private Integer sceneId;

    /**
     * 场景类型，用于调用获取p99 p95
     */
    private String sceneType;

    private Integer taskId;

    private Integer agentNum;

    /**
     * linkId -> dag task id and rps
     */
    DagTaskRps dagTaskRps;

    /**
     * 可控的场景发压比例
     */
    private Integer rpsRate;

    /**
     * 关联任务数
     */
    private Integer connectTaskNum;

    private boolean lastTime;

    /**
     * 全场景发压请求次数
     */
    private LongAdder totalReq;

    /**
     * 丢失连接数
     */
    private LongAdder lossConnNum;

    /**
     * 全场景业务处理次数
     */
    private LongAdder totalTCount;

    /**
     * 全场景成功次数
     */
    private LongAdder totalSuccReq;

    /**
     * 全场景错误次数
     */
    private LongAdder totalErrReq;


    /**
     * 用于临时proms打点链路tps统计,每秒清零，消耗较大
     */
    private LongAdder tmpTpsCounter;

    /**
     * 用于临时proms打点链路tps统计,每秒清零，消耗较大
     */
    private LongAdder tmpRpsCounter;

    /**
     * 记录接口错误统计数据
     * <"s_code_errorCode",<apiId,count>> or <"cp_id_checkpointId",<apiId,count>>
     * <"s_code_404",<2342,10>>  or <"cp_id_141242",<2342,10>>
     */
    private ConcurrentMap<String, ConcurrentMap<Integer, LongAdder>> errCounterMap;

    /**
     * 记录接口Rt数据
     * <p>
     * 例：<apiId,<"rt_list",[12(ms),34,46]>>  每次调用统计
     */
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>>> apiRtMap;

    /**
     * 记录接口请求次数，成功失败数 tps
     * <apiId,<"api_req_succ",10>>
     */
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, LongAdder>> apiCountMap;

    /**
     * 记录接口平均rps
     */
    private ConcurrentHashMap<Integer, AtomicInteger> apiRpsMap;

    /**
     * 记录接口平均tps
     */
    private ConcurrentHashMap<Integer, AtomicInteger> apiTpsMap;

    public SceneTotalCountContext() {
    }
}
