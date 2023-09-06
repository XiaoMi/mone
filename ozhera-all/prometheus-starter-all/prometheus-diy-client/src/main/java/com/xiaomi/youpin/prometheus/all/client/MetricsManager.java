package com.xiaomi.youpin.prometheus.all.client;
/**
 * @author zhangxiaowei6
 */
public interface MetricsManager {
     /**
      * use Counter
      *
      * @param metricName
      * @param labelNames
      * @return XMCounter
      */

     XmCounter newCounter(String metricName, String... labelNames);

     /**
      * use Gauge
      *
      * @param metricName
      * @param labelNames
      * @return XMGauge
      */

     XmGauge newGauge(String metricName, String... labelNames);

     /**
      * use Histogram
      *
      * @param metricName
      * @param buckets
      * @param labelNames
      * @return XMHistogram
      */

     XmHistogram newHistogram(String metricName, double[] buckets, String... labelNames);
}
