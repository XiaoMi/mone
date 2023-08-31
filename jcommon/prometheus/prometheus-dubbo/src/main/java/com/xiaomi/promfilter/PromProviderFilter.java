package com.xiaomi.promfilter;

import com.xiaomi.youpin.dubbo.filter.ResultUtils;
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
@Activate(group = Constants.PROVIDER, order = -9999)
public class PromProviderFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(PromProviderFilter.class);

    public static final String SERVICE_NAME = "serviceName";

    public static final String METHOD_NAME = "methodName";

    public static final String DUBBO_INTERFACE_CALLED_COUNT = "dubboInterfaceCalledCount";

    public static final String DUBBO_PROVIDER_TIME_COST = "dubboProviderTimeCost";

    public static final String DUBBO_METHOD_CALLED_COUNT = "dubboMethodCalledCount";

    public static final String DUBBO_BIS_ERROR_COUNT = "dubboBisErrorCount";

    public static final String DUBBO_BIS_ERROR_CODE = "dubboBisErrorCode";

    public static final String DUBBO_RPC_ERROR_COUNT = "dubboRpcErrorCount";

    public Metrics m;

    public PromProviderFilter() {
        try {
            m = Metrics.getInstance();
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        }
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        logger.debug("—————-prometheus provider filter—————");

        String serviceName = invoker.getInterface().getName();
        String methodName = RpcUtils.getMethodName(invocation);
        Result res = null;
        try {
            boolean supportServerAsync = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.ASYNC_KEY, false);

            //异步调用直接返回
            if (supportServerAsync) {
                return invoker.invoke(invocation);
            }
            long start = System.currentTimeMillis();
            //打点接口名和方法名
            m.newCounter(DUBBO_INTERFACE_CALLED_COUNT, SERVICE_NAME).with(serviceName).add(1);
            m.newCounter(DUBBO_METHOD_CALLED_COUNT, SERVICE_NAME, METHOD_NAME).with(serviceName, methodName).add(1);

            res = invoker.invoke(invocation);

            long duration = System.currentTimeMillis() - start;

            logger.debug(String.format("provider: serviceName:%s,methodName:%s,cost: %d ms", serviceName, methodName, duration));
            recordTimer(m,DUBBO_PROVIDER_TIME_COST,new String[]{SERVICE_NAME,METHOD_NAME},duration,serviceName,methodName);

            int code = 0;
            code = ResultUtils.getCode(res);
            String status;
            if (res.getException() != null) {
                status = res.getException().getClass().getSimpleName();
                //对该接口方法业务错误打点：次数和错误码
                m.newCounter(DUBBO_BIS_ERROR_COUNT, SERVICE_NAME, METHOD_NAME).with(serviceName, methodName).add(1);
                m.newGauge(DUBBO_BIS_ERROR_CODE, SERVICE_NAME, METHOD_NAME).with(serviceName, methodName).set(code);
                //log记录错误
                logger.warn(String.format("serviceName:%s,methodName:%s,error:%s", serviceName, methodName, status));
            }
        } catch (RpcException e) {
            m.newCounter(DUBBO_RPC_ERROR_COUNT, SERVICE_NAME, METHOD_NAME).with(serviceName, methodName).add(1);
            logger.error("rpc error", e);
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
