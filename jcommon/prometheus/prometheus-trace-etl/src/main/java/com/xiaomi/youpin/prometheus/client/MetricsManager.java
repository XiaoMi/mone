package com.xiaomi.youpin.prometheus.client;
/**
 * @author zhangxiaowei
 */
public interface MetricsManager {
     /**
      * 使用Counter打点
      *
      * @param metricName 指标名
      * @param labelNames 标签名
      * @return XMCounter
      */

     XmCounter newCounter(String metricName, String... labelNames);

     /**
      * 使用Gauge打点
      *
      * @param metricName 指标名
      * @param labelNames 标签名
      * @return XMGauge
      */

     XmGauge newGauge(String metricName, String... labelNames);

     /**
      * 使用Histogram打点
      *
      * @param metricName 指标名
      * @param buckets    存储桶
      * @param labelNames 标签名
      * @return XMHistogram
      */

     XmHistogram newHistogram(String metricName, double[] buckets, String... labelNames);
}
