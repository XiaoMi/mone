package com.xiaomi.mone.monitor.service.aop.action;

import com.xiaomi.mone.monitor.bo.HeraReqInfo;

/**
 * 通用参数类型
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/13 15:59
 */
public interface HeraRequestMappingAction {
    /**
     * 数据类型
     */
    int DATA_TYPE_STRATEGY = 1;
    int DATA_TYPE_RULE = 2;
    int DATA_ALERT_GROUP = 3;

    /**
     * 日志类型
     */
    int LOG_TYPE_PARENT = 0;

    /**
     *
     * @param args 请求参数
     * @param heraReqInfo hera收集参数
     */
    void beforeAction(Object[] args, HeraReqInfo heraReqInfo);

    /**
     *
     * @param args 请求参数
     * @param heraReqInfo hera收集参数
     * @param result 执行结果
     */
    void afterAction(Object[] args, HeraReqInfo heraReqInfo, Object result);
}
