package com.xiaomi.promfilter;

import com.xiaomi.youpin.prometheus.client.Metrics;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.support.RpcUtils;
/**
 * @Description TODO
 * @Author zhenxing.dong
 * @Date 2021/6/16 12:04
 */
@Activate(group = Constants.CONSUMER, order = -9999)
public class PromConsumerFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(PromConsumerFilter.class);

    public static final String SERVICE_NAME = "serviceName";

    public static final String METHOD_NAME = "methodName";

    public static final String DUBBO_CONSUMER_TIME_COST = "dubboConsumerTimeCost";

    public static final String DUBBO_BIS_ERROR_COUNT = "dubboBisErrorCount";

    public static final String DUBBO_BIS_SUCCESS_COUNT = "dubboBisSuccessCount";

    public static final String DUBBO_ERROR_RPC_CALL_TIME = "dubboErrorRpcCallTime";

    public static final String DUBBO_BIS_TOTAL_COUNT = "dubboBisTotalCount";

    public Metrics m;

    public PromConsumerFilter() {
        try {
            m = Metrics.getInstance();
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        }
    }


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        logger.debug("—————-prometheus consumer filter—————");
        String application = invocation.getAttachment(Constants.APPLICATION_KEY, "");
        String serviceName = invoker.getInterface().getName();
        String methodName = RpcUtils.getMethodName(invocation);
        Result res = null;
        long start = System.currentTimeMillis();
        m.newCounter(DUBBO_BIS_TOTAL_COUNT, SERVICE_NAME, METHOD_NAME).with(serviceName, methodName).add(1);
        try {
            boolean isAsync = RpcUtils.isAsync(invoker.getUrl(), invocation);

            //异步直接返回
            if (isAsync) {
                return invoker.invoke(invocation);
            }
            res = invoker.invoke(invocation);

            long duration = System.currentTimeMillis() - start;

            //记录调用时长
            logger.debug(String.format("consumer:%s call serviceName:%s,methodName:%s,cost: %d ms", application, serviceName, methodName, duration));
            recordTimer(m,DUBBO_CONSUMER_TIME_COST,new String[]{SERVICE_NAME,METHOD_NAME},duration,serviceName,methodName);
            String status = "success";
            if (res.getException() != null) {
                m.newCounter(DUBBO_BIS_ERROR_COUNT, SERVICE_NAME, METHOD_NAME).with(serviceName, methodName).add(1);
                //记录错误
                logger.warn(String.format("serviceName:%s,methodName:%s,error:%s", serviceName, methodName, status));
            }
            //记录成功
            m.newCounter(DUBBO_BIS_SUCCESS_COUNT,SERVICE_NAME,METHOD_NAME).with(serviceName,methodName).add(1);
        } catch (RpcException e) {
            long duration = System.currentTimeMillis() - start;
            String result = "error";
            if (e.isTimeout()) {
                result = "timeoutError";
            }
            if (e.isBiz()) {
                result = "bisError";
            }
            if (e.isNetwork()) {
                result = "networkError";
            }
            if (e.isSerialization()) {
                result = "serializationError";
            }
            m.newGauge(DUBBO_ERROR_RPC_CALL_TIME, SERVICE_NAME, METHOD_NAME).with(serviceName, methodName).set(duration);
            logger.warn(String.format("app:%s,serviceName:%s,methodName:%s,error:%s",application, serviceName, methodName, result));
        }
        return res;
    }

    private void recordTimer(Metrics m,String metricName, String[] labelsName,long value,String... labelsValue) {
        try {
            m.newHistogram(metricName, null,labelsName).with(labelsValue).observe(value);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
}
