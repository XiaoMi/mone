package com.xiaomi.youpin.annotation.log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.common.perfcounter.PerfCounter;
import com.xiaomi.data.push.action.ActionContext;
import com.xiaomi.data.push.action.ActionInfo;
import com.xiaomi.data.push.common.ExceptionUtils;
import com.xiaomi.data.push.common.TraceId;
import com.xiaomi.data.push.context.ServerInfo;
import com.xiaomi.data.push.dao.model.ErrorRecordWithBLOBs;
import com.xiaomi.data.push.error.ErrorService;
import com.xiaomi.data.push.log.ActionExecuteInfo;
import com.xiaomi.data.push.log.ErrorLogger;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * <p>
 * 支持限流
 * 支持日志
 * 支持错误记录
 */
@Aspect
@Configuration
@Order(0)
public class LogAop {

    private static final Logger logger = LoggerFactory.getLogger(LogAop.class);

    @Autowired(required = false)
    private ErrorService errorService;

    @Autowired(required = false)
    private ActionContext actionContext;

    @Autowired(required = false)
    private ServerInfo serverInfo;


    @Around(value = "@annotation(log)")
    public Object log(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();


        String name = log.name();
        if (null != actionContext) {
            ActionInfo ai = actionContext.getActionInfos().get(method.toString());

            if (ai == null || !ai.isRecordLog()) {
                return joinPoint.proceed();
            }

            name = ai.getName();
        }


        if (StringUtils.isEmpty(name)) {
            String packageName = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = method.getName();
            name = packageName + "." + methodName;
        }

        if (log.usePercount()) {
            PerfCounter.inc(name + ".Count", 1);
        }

        long begin = System.currentTimeMillis();

        if (null != actionContext && !actionContext.actionMap.containsKey(name)) {
            actionContext.actionMap.putIfAbsent(name, new ActionExecuteInfo());
        }

        Object[] o = joinPoint.getArgs();
        String traceId = TraceId.getTraceId(o);
        String params = "";

        if (log.printParam()) {
            params = new Gson().toJson(o);
        }

        logger.debug("invoke method begin id:{} name:{} params:{}", traceId, name, params);

        ActionExecuteInfo executeInfo = null;
        if (null != actionContext) {
            executeInfo = actionContext.actionMap.get(name);
            if (null != executeInfo) {
                executeInfo.getExecuteNum().incrementAndGet();
            }
        }

        long useTime = 0;
        long beginTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            useTime = System.currentTimeMillis() - beginTime;
            if (log.usePercount()) {
                PerfCounter.inc(name + ".Success", 1);
            }

            String res = "";
            if (log.printResult()) {
                res = new Gson().toJson(result);
            }

            logger.debug("invoke method {} finish [success] id:{}  useTime:{} result:{}", name, traceId, useTime, res);
            if (null != executeInfo) {
                executeInfo.getSuccessNum().incrementAndGet();
            }
            return result;
        } catch (Throwable throwable) {
            useTime = System.currentTimeMillis() - beginTime;
            if (log.usePercount()) {
                PerfCounter.inc(name + ".Failure", 1);
            }
            if (null != executeInfo) {
                executeInfo.getFailureNum().incrementAndGet();
            }


            ErrorRecordWithBLOBs errorRecord = new ErrorRecordWithBLOBs();
            if (log.recordError() && null != errorService) {
                Gson g = new GsonBuilder().setPrettyPrinting().create();
                errorRecord = new ErrorRecordWithBLOBs();
                errorRecord.setUpdated(begin);
                errorRecord.setParams(g.toJson(o));
                errorRecord.setCreated(begin);
                errorRecord.setAction(name);
                errorRecord.setServerInfo(this.serverInfo.getIp());
                errorRecord.setType(1);
                errorRecord.setVersion(1);
                errorRecord.setStatus(0);
                errorRecord.setMessage(ExceptionUtils.collectExceptionMsg(traceId, throwable));
                errorService.recordError(errorRecord);
            }

            //记录错误到日志
            if (log.recordErrorLog()) {
                ErrorLogger.ins().logger.error(new Gson().toJson(errorRecord));
            }

            logger.warn("invoke method name:{} finish [failure] id:{}   error:{}", name, traceId, throwable.getMessage());
            throw throwable;
        } finally {
            if (null != executeInfo) {
                executeInfo.getRt().addAndGet(useTime);
            }

            if (log.usePercount()) {
                PerfCounter.count(name, 1, useTime);
            }
        }
    }

}
