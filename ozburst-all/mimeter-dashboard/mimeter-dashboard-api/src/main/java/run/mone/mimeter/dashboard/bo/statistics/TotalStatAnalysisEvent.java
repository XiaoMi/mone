package run.mone.mimeter.dashboard.bo.statistics;


import lombok.Data;
import run.mone.mimeter.dashboard.bo.task.DagTaskRps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class TotalStatAnalysisEvent implements Serializable {

    /**
     * 全场景总请求次数
     */
    private int totalReq;

    /**
     * 丢失连接数
     */
    private int lossConnNum;

    /**
     * 全场景业务处理次数
     */
    private int totalTCount;

    /**
     * 全场景成功次数
     */
    private int totalSuccReq;

    /**
     * 全场景总错误请求次数
     */
    private int totalErrReq;

    /**
     * 全场景成功率
     */
    private String totalSuccRate;

    /**
     * 总错误占比
     */
    private String totalErrRate;

    /**
     * 全场景平均rt
     */
    private int avgRt;

    /**
     * 全场景最大rt
     */
    private int maxRt;

    /**
     * 全场景平均tps
     */
    private int avgTps;

    /**
     * 全场景最大rps
     */
    private int maxRps;

    /**
     * 全场景平均rps
     */
    private int avgRps;

    /**
     * 全场景最大tps
     */
    private int maxTps;

    /**
     * 是否结束
     */
    private boolean finish;

    /**
     * 全场景发压比例
     */
    private Integer rpsRate;

    /**
     * 各错误类型统计分析
     */
    private List<ErrorTypeAnalysis> errorTypeAnalyses;

    /**
     * 接口统计数据
     */
    private List<ApiStatistics> apiStatisticsList;

    /**
     * 链路id与dag任务id、rps映射
     */
    private Map<Integer, DagTaskRps> linkToDagTaskRpsMap;
}
