package com.xiaomi.youpin.prometheus.all.client;
/**
 * @author zhangxiaowei6
 */
public interface XmHistogram {
    /**
     * 填充label的value值
     *
     * @param labelValues 标签值
     * @return XmHistogram
     */
    XmHistogram with(String... labelValues);

    /**
     * 填入桶中的数据
     * @param delta 设置增量
     */
    void observe(double delta,String... labelValue);
}
