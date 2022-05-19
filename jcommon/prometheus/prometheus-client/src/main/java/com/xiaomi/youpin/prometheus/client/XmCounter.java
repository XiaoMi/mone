package com.xiaomi.youpin.prometheus.client;
/**
 * @author zhangxiaowei
 */
public interface XmCounter {
     /**
      * 填充label的value
      *
      * @param labelValues 标签值
      * @return XMCounter
      */
     XmCounter with(String... labelValues);

     /**
      * Counter指标增加定量值
      *
      * @param delta 增加delta的数值
      */
     void add(double delta,String... labelValues);
}
