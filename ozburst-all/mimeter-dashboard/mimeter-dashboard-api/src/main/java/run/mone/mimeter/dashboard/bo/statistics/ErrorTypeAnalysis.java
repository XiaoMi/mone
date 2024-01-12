package run.mone.mimeter.dashboard.bo.statistics;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ErrorTypeAnalysis implements Serializable {
    /**
     * 0：错误码 or 1：检查点
     */
    private int errorType;

    /**
     * http错误状态码
     */
    private int errorCode;
    /**
     * 该错误占比
     */
    private String errRate;

    /**
     * 各接口该错误次数
     */
    private Map<Integer, Integer> errInApis;

    /**
     * 错误次数最多的api id
     */
    private Integer mostErrApi;

    /**
     * 错误次数最多的接口名
     */
    private String mostErrApiName;

    /**
     * 检查点错误类型的 规则id
     */
    private Integer checkPointId;

    /**
     * 检查点错误类型的类型
     */
    private String checkPointDesc;

}
