package com.xiaomi.youpin.prometheus.all.client;
/**
 * @author zhangxiaowei6
 */
public interface XmHistogram {
    /**
     * padding label value
     *
     * @param labelValues
     * @return XmHistogram
     */
    XmHistogram with(String... labelValues);

    /**
     * Fill the data in the bucket
     * @param delta Increment
     */
    void observe(double delta,String... labelValue);
}
