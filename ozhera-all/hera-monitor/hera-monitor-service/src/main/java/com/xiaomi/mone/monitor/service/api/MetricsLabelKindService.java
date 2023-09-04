package com.xiaomi.mone.monitor.service.api;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 2:31 PM
 */
public interface MetricsLabelKindService {

    /**
     * kind=3是dubbo类型
     * @param alert
     * @return
     */
    boolean dubboType(String alert);

    /**
     * kind=1或2是http类型
     * @param alert
     * @return
     */
    boolean httpType(String alert);
}
