package com.xiaomi.mione.prometheus.metrics;
import com.google.gson.Gson;
import com.xiaomi.youpin.prometheus.client.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import  org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * @author wodiwudi
 */
@Aspect
@Slf4j
@Configuration
public class MetricsAop {

    private static final Logger logger = LoggerFactory.getLogger(MetricsAop.class);

    public static final String AOP_TOTAL_METHOD_COUNT = "aopTotalMethodCount";

    public static final String AOP_SUCCESS_METHOD_COUNT = "aopSuccessMethodCount";

    public static final String AOP_ERROR_METHOD_COUNT = "aopErrorMethodCount";

    public static final String AOP_METHOD_TIME_COUNT = "aopMethodTimeCount";

    @Around(value = "@annotation(metricsTime)")
    public Object metricsTime(ProceedingJoinPoint joinPoint, MetricsTime metricsTime) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String name = metricsTime.name();
        if ( "".equals(name)) {
            name = method.getName();
        }
        // 记录访问量
        recordCounter(AOP_TOTAL_METHOD_COUNT,new String[]{"methodName"},name);
        long begin = System.currentTimeMillis();
        long time = 0;
        try {
            Object result;
            result = joinPoint.proceed();
            time = System.currentTimeMillis() - begin;
            String res = "";
            if (metricsTime.printResult()) {
                res = new Gson().toJson(result);
            }
            // 记录成功
            recordCounter(AOP_SUCCESS_METHOD_COUNT,new String[]{"methodName"},name);
            logger.debug("invoke method {} finish [success]   useTime:{} result:{}", name, time, res);
            return result;
        }catch (Throwable throwable) {
            time = System.currentTimeMillis() - begin;
            logger.warn("invoke method name:{} finish [failure]  error:{}", name, throwable.getMessage());
            // 记录失败
            recordCounter(AOP_ERROR_METHOD_COUNT,new String[]{"methodName"},name);
            throw throwable;
        }finally {
            // 记录耗时
            recordTimer(AOP_METHOD_TIME_COUNT,new String[]{"methodName"},time,name);
        }

    }

    private void recordTimer(String metricName, String[] labelsName,double value,String... labelsValue) {
        try {
            //用来记录p99等数据
            Metrics.getInstance().newHistogram(metricName, null,labelsName).with(labelsValue).observe(value);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void recordCounter(String metricName, String[] labelsName,String... labelsValue) {
        try {
            //用来计算qps等信息
            Metrics.getInstance().newCounter(metricName, labelsName).with(labelsValue).add(1);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
