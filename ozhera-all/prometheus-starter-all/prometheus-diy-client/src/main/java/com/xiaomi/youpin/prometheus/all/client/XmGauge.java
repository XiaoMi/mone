package com.xiaomi.youpin.prometheus.all.client;
/**
 * @author zhangxiaowei6
 */
public interface XmGauge {
     /**
      * padding label value
      *
      * @param labelValues
      * @return XMGauge
      */
     XmGauge with(String... labelValues);

     /**
      * Gauge
      *
      * @param delta Increment
      */
     void set(double delta,String... labelValue);

     /**
      * Gauge
      *
      * @param delta set Increment
      */
     void add(double delta,String... labelValue);
}
