package com.xiaomi.mone.monitor.service.prometheus;

/**
 * @author gaoxihui
 * @date 2021/7/22 4:42 下午
 */
public class PromQL {

    // dubbo
    public static final String DUBBO_BIS_ERROR_COUNT = "dubboBisErrorCount";
    public static final String DUBBO_ERROR_RPC_CALL_TIME = "dubboErrorRpcCallTime";
    public static final String DUBBO_CONSUMER_TIME_COST = "dubboConsumerTimeCost";

    // 时间记录aop
    public static final String AOP_ERROR_METHOD_COUNT = "aopErrorMethodCount";
    public static final String AOP_METHOD_TIME_COUNT = "aopMethodTimeCount";

    // sql
    public static final String SQL_ERROR_COUNT = "sqlErrorCount";
    public static final String SQL_TIME_OUT_COUNT = "sqlTimeOutCount";

    // redis
    public static final String REDIS_FAILED_COUNT = "RedisFailedCount"; //失败方法调用
    public static final String REDIS_SLOW_QUERY = "RedisSlowQuery";

}
