package com.xiaomi.youpin.prometheus.client;
/**
 * @author zhangxiaowei
 */
public interface XmGauge {
     /**
      * 填充label的value
      *
      * @param labelValues 标签值
      * @return XMGauge
      */
     XmGauge with(String... labelValues);

     /**
      * Gauge指标设置标量值
      *
      * @param delta 设置增量
      */
     void set(double delta,String... labelValue);

     /**
      * Gauge指标增加定量值
      *
      * @param delta 设置增量
      */
     void add(double delta,String... labelValue);

     /**
      * Gauge指标增加定量值
      *
      * @param delta 设置增量
      */
     void add(String service, double delta,String... labelValue);


}
