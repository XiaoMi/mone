package com.xiaomi.mone.monitor.service.aop.action;

import com.xiaomi.mone.monitor.bo.HeraReqInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 一个参数类型
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/13 15:59
 */
@Slf4j
public abstract class HeraRequestMappingActionArg0<RESULT> implements HeraRequestMappingAction{

    @Autowired
    protected com.xiaomi.mone.monitor.dao.HeraOperLogDao HeraOperLogDao;

    /**
     *
     * @param args 请求参数
     * @param heraReqInfo hera收集参数
     */
    @Override
    public void beforeAction(Object[] args, HeraReqInfo heraReqInfo) {
        if (args != null && args.length != 0) {
            return;
        }
        try {
            beforeAction(heraReqInfo);
        } catch (Throwable e) {
            log.info("操作日志执行前异常;heraReqInfo={}", heraReqInfo, e);
        }
    }

    public abstract void beforeAction(HeraReqInfo heraReqInfo);

    /**
     *
     * @param args 请求参数
     * @param heraReqInfo hera收集参数
     * @param result 执行结果
     */
    @Override
    public void afterAction(Object[] args, HeraReqInfo heraReqInfo, Object result) {
        if (args != null && args.length != 0) {
            return;
        }
        try {
            afterAction(heraReqInfo, (RESULT)result);
        } catch (Throwable e) {
            log.info("操作日志执行后异常;heraReqInfo={}", heraReqInfo, e);
        }
    }

    public abstract void afterAction(HeraReqInfo heraReqInfo, RESULT result);
}
