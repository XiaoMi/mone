package run.mone.mimeter.dashboard.bo.statistics;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiStatistics implements Serializable {

    private int apiId;

    private String apiName;

    /**
     * 接口类型 0:http 1:dubbo
     */
    private int apiType;

    private String uri;

    private String method;

    private String dubboServiceName;

    private String dubboMethodName;

    private String dubboGroup;

    private String dubboVersion;

    private int serialId;

    /**
     * 该接口总请求次数
     */
    private int reqTotal;

    /**
     * 业务事务处理总数
     */
    private int tansTotal;

    /**
     * 该接口总请求成功次数
     */
    private int reqSucc;

    /**
     * 该接口总请求失败次数
     */
    private int reqFail;

    /**
     * 该接口请求成功率
     */
    private String succRate;

    /**
     * 接口平均rt
     */
    private int avgRt;

    /**
     * 接口最大rt
     */
    private int maxRt;


    /**
     * p99 rt
     */
    private int p99Rt;


    /**
     * p95rt
     */
    private int p95Rt;

    /**
     * 接口平均tps
     */
    private int avgTps;

    /**
     * 接口最大tps
     */
    private int maxTps;

    /**
     * 接口最大rps
     */
    private int maxRps;


    /**
     * 接口平均rps
     */
    private int avgRps;

}
