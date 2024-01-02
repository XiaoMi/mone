package run.mone.mimeter.engine.agent.bo.stat;


import lombok.Data;
import run.mone.mimeter.engine.agent.bo.task.DagTaskRps;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
@Data
public class TotalCounterStatistic implements Serializable {

    private AtomicInteger finishAgentNum;

    /**
     * 可控的场景发压比例
     */
    private volatile int rpsRate;

    /**
     * 全场景总请求次数
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
     * 全场景总错误请求次数
     */
    private LongAdder totalErrReq;

    /**
     * 全场景平均rt
     */
    private volatile int avgRt;

    /**
     * 全场景最大rt
     */
    private volatile int maxRt;

    /**
     * 全场景平均tps
     */
    private volatile int avgTps;

    /**
     * 全场景最大tps
     */
    private volatile int maxTps;

    /**
     * 全场景最大rps
     */
    private volatile int maxRps;

    /**
     * 全场景平均rps
     */
    private volatile int avgRps;

    /**
     * 错误数据统计
     * <"s_code_errorCode",<apiId,count>> or <"cp_id_checkpointId",<apiId,count>>
     * <"s_code_404",<2342,10>>  or <"cp_id_141242",<2342,10>>
     */
    private ConcurrentMap<String, ConcurrentMap<Integer, LongAdder>> counterMap;

    /**
     * 记录接口Rt与Tps数据
     * <p>
     * 例：<apiId,<"avg_rt",12(ms)>>
     * <apiId,<"max_rt",121(ms)>>
     * <apiId,<"avg_tps",121(次)>>
     * <apiId,<"max_tps",1210(次)>>
     *  <apiId,<"req_succ",1210(次)>>
     *  <apiId,<"req_fail",1210(次)>>
     *   <apiId,<"avg_rps",121(次)>>
     */
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, AtomicInteger>> apiRtAndTpsMap;

    /**
     * 场景下每个链路的实时Rps<linkId,{...}>
     */
    private ConcurrentHashMap<Integer, DagTaskRps> reportLinkRps;

    /**
     * 记录接口请求次数，成功失败数
     * <apiId,<"api_req_succ",10>>
     */
    private ConcurrentHashMap<Integer,ConcurrentHashMap<String,LongAdder>> apiCountMap;

    public TotalCounterStatistic(AtomicInteger finishAgentNum, LongAdder totalReq,
                                 LongAdder lossConnNum,
                                 LongAdder totalTCount,LongAdder totalSuccReq, LongAdder totalErrReq,
                                 ConcurrentMap<String, ConcurrentMap<Integer, LongAdder>> counterMap,
                                 ConcurrentHashMap<Integer, ConcurrentHashMap<String, AtomicInteger>> apiRtAndTpsMap,
                                 ConcurrentHashMap<Integer,ConcurrentHashMap<String,LongAdder>> apiCountMap,
                                 ConcurrentHashMap<Integer, DagTaskRps> reportLinkRps
    ) {
        this.finishAgentNum = finishAgentNum;
        this.totalReq = totalReq;
        this.lossConnNum = lossConnNum;
        this.totalTCount = totalTCount;
        this.totalSuccReq = totalSuccReq;
        this.totalErrReq = totalErrReq;
        this.counterMap = counterMap;
        this.apiRtAndTpsMap = apiRtAndTpsMap;
        this.apiCountMap = apiCountMap;
        this.reportLinkRps = reportLinkRps;
    }
}
