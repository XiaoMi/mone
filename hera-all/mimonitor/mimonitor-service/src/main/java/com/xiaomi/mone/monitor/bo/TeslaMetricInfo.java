package com.xiaomi.mone.monitor.bo;

import lombok.Data;

/**
 * @author gaoxihui
 * @date 2022/1/10 3:51 下午
 */
@Data
public class TeslaMetricInfo{
    /**
     * 错误数调用数量指标
     */
    private String errorCallMetric;
    /**
     * 总调用次数指标
     */
    private String totalCallMetric;
    /**
     * 耗时时常指标
     */
    private String timeCostMetric;
    /**
     * 耗时次数指标
     */
    private String timeNumMetric;
    /**
     * 耗时桶指标
     */
    private String timeBucketMetric;
    /**
     * 数据采集job名称
     */
    private String jobName;


    public TeslaMetricInfo(String errorCallMetric, String totalCallMetric, String jobName) {
        this.errorCallMetric = errorCallMetric;
        this.totalCallMetric = totalCallMetric;
        this.jobName = jobName;
    }

    public TeslaMetricInfo(String timeCostMetric, String timeNumMetric, String timeBucketMetric, String jobName) {
        this.timeCostMetric = timeCostMetric;
        this.timeNumMetric = timeNumMetric;
        this.timeBucketMetric = timeBucketMetric;
        this.jobName = jobName;
    }
}
