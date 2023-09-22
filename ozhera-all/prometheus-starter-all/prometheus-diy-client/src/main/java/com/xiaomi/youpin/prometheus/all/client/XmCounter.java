package com.xiaomi.youpin.prometheus.all.client;
/**
 * @author zhangxiaowei6
 */
public interface XmCounter {
     /**
      * padding label value
      *
      * @param labelValues
      * @return XMCounter
      */
     XmCounter with(String... labelValues);

     /**
      * Counter Increment
      *
      * @param delta
      */
     void add(double delta,String... labelValues);
}
