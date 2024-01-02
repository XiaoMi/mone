package run.mone.mimeter.engine.agent.bo.stat;


import lombok.Data;
import run.mone.mimeter.engine.agent.bo.task.DagTaskRps;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class SceneTotalCountContextDTO implements Serializable {

    private String reportId;

    private Integer sceneId;

    private Integer taskId;

    /**
     * 场景类型，用于调用获取p99 p95
     */
    private String sceneType;

    /**
     * 使用的发压机数量
     */
    private Integer agentNum;

    /**
     * 链路实际 rps
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
     * 全场景请求次数
     */
    private long totalReq;

    /**
     * 丢失连接数
     */
    private long lossConnNum;

    /**
     * 全场景业务处理次数
     */
    private long totalTCount;

    /**
     * 全场景成功次数
     */
    private long totalSuccReq;

    /**
     * 全场景错误次数
     */
    private long totalErrReq;

    /**
     * 记录接口错误统计
     * <"s_code_errorCode",<apiId,count>> or <"cp_id_checkpointId",<apiId,count>>
     * <"s_code_404",<2342,10>>  or <"cp_id_141242",<2342,10>>
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<Integer, Long>> counterMap;


    /**
     * 记录接口Rt与Tps数据
     *
     * 例：<apiId,<"rt_list",[12(ms),34,46]>>  每次调用统计rt
     */
    private ConcurrentHashMap<Integer,ConcurrentHashMap<String, List<Integer>>> apiRtMap;

    /**
     * 记录接口请求次数，成功失败数
     * <apiId,<"api_req_succ",10>>
     */
    private ConcurrentHashMap<Integer,ConcurrentHashMap<String, Integer>> apiCountMap;


    /**
     * 记录接口平均rps
     */
    private ConcurrentHashMap<Integer, Integer> apiRpsMap;

    /**
     * 记录接口平均tps
     */
    private ConcurrentHashMap<Integer, Integer> apiTpsMap;

}
