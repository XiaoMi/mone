package com.xiaomi.mone.monitor.dao.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author gaoxihui
 * @date 2022/4/19 5:01 下午
 */
@Data
@ToString
public class AlarmHealthResult {

    private Integer appId;
    private Integer iamTreeId;
    private String appName;
    private String owner;
    private Integer plateFormType;
    private Integer baseAlarmNum;
    private Integer appAlarmNum;

    /**
     * 基础指标配置评分：每个配置项的查询结果数据值>0，认为获取对应配置项的分数；
     * 满分10分
     */
    private Integer cpuUseRate;//cpu使用率 2分
    private Integer cpuLoad;//cpu负载 2分
    private Integer memUseRate;//内存使用率 2分
    private Integer containerNum;//容器数量 2分
    private Integer jvmThread;//jvm线程数 1分
    private Integer jvmGc;//jvm Gc（包含gc次数、gc耗时） 1分

    /**
     * 接口类指标配置评分：每个配置项的查询结果数据值>0，认为获取对应配置项的分数
     * 满分：19分
     */
    private Integer httpServerAvailability;//httpServer可用性（包括可用性、错误数配置任一） 2分
    private Integer httpServerQps;//httpServer qps 1分
    private Integer httpServerTimeCost;//httpServer 耗时 1分
    private Integer httpClientAvailability;//httpCleint可用性（包括可用性、错误数配置任一） 1分
    private Integer httpClientQps;//httpClient qps 1分
    private Integer httpClientTimeCost;//httpClient 耗时 1分
    private Integer dubboProviderAvailability;//dubboProvider可用性（包括可用性、错误数配置任一） 2分
    private Integer dubboProviderQps;//dubboProvider Qps 1分
    private Integer dubboProviderTimeCost;//dubboProvider 耗时 1分
    private Integer dubboProviderSlowQuery;//dubboProvider 慢查询 1分
    private Integer dubboConsumerAvailability;//dubboConsumer可用性（包括可用性、错误数配置任一） 1分
    private Integer dubboConsumerQps;//dubboConsumerQps 1分
    private Integer dubboConsumerTimeCost;//dubboConsumer耗时 1分
    private Integer dubboConsumerSlowQuery;//dubboConsumer慢查询 1分
    private Integer dbAvailability;//db可用性包括可用性、错误数配置任一） 2分
    private Integer dbSlowQuery;//mysql 慢查询 1分

    private Integer basicMetricScore;
    private Integer interfaceMetricScore;
    private Integer comprehensiveScore;
}
