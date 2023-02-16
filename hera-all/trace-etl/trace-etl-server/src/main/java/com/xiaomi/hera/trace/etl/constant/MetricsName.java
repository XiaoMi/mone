package com.xiaomi.hera.trace.etl.constant;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/4/2 10:03 上午
 */
public class MetricsName {
    /**
     * http
     */
    public static final String HTTP_SERVER_TOTAL_COUNT = "jaeger_aopTotalMethodCount";
    public static final String HTTP_SERVER_SUCCESS_COUNT = "jaeger_aopSuccessMethodCount";
    public static final String HTTP_SERVER_SLOW_COUNT = "jaeger_httpSlowQuery";
    public static final String HTTP_SERVER_ERROR_COUNT = "jaeger_httpError";
    public static final String HTTP_SERVER_DURATION_HISTOGRAM = "jaeger_aopMethodTimeCount";

    public static final String HTTP_CLIENT_TOTAL_COUNT = "jaeger_aopClientTotalMethodCount";
    public static final String HTTP_CLIENT_SUCCESS_COUNT = "jaeger_aopClientSuccessMethodCount";
    public static final String HTTP_CLIENT_SLOW_COUNT = "jaeger_httpClientSlowQuery";
    public static final String HTTP_CLIENT_ERROR_COUNT = "jaeger_httpClientError";
    public static final String HTTP_CLIENT_DURATION_HISTOGRAM = "jaeger_aopClientMethodTimeCount";

    /**
     * dubbo
     */
//    public static final String DUBBO_SERVER_TOTAL_COUNT1 = "jaeger_dubboInterfaceCalledCount";
    public static final String DUBBO_SERVER_TOTAL_COUNT = "jaeger_dubboMethodCalledCount";
    public static final String DUBBO_SERVER_SUCCESS_COUNT = "jaeger_dubboMethodCalledSuccessCount";
    public static final String DUBBO_SERVER_SLOW_COUNT = "jaeger_dubboProviderSlowQuery";
    public static final String DUBBO_SERVER_ERROR_COUNT = "jaeger_dubboProviderError";
    public static final String DUBBO_SERVER_DURATION_HISTOGRAM = "jaeger_dubboProviderCount";

    public static final String DUBBO_CLIENT_TOTAL_COUNT = "jaeger_dubboBisTotalCount";
    public static final String DUBBO_CLIENT_SUCCESS_COUNT = "jaeger_dubboBisSuccessCount";
    public static final String DUBBO_CLIENT_SLOW_COUNT = "jaeger_dubboConsumerSlowQuery";
    public static final String DUBBO_CLIENT_ERROR_COUNT = "jaeger_dubboConsumerError";
    public static final String DUBBO_CLIENT_DURATION_HISTOGRAM = "jaeger_dubboConsumerTimeCost";

    /**
     * redis
     */
    public static final String REDIS_TOTAL_COUNT = "jaeger_RedisTotalCount";
    public static final String REDIS_SUCCESS_COUNT = "jaeger_RedisSuccessCount";
    public static final String REDIS_ERROR_COUNT = "jaeger_redisError";
    public static final String REDIS_DURATION_HISTOGRAM = "jaeger_RedisMethodTimeCost";

    /**
     * mysql
     */
    public static final String MYSQL_TOTAL_COUNT = "jaeger_sqlTotalCount";
    public static final String MYSQL_SUCCESS_COUNT = "jaeger_sqlSuccessCount";
    public static final String MYSQL_SLOW_COUNT = "jaeger_dbSlowQuery";
    public static final String MYSQL_ERROR_COUNT = "jaeger_dbError";
    public static final String MYSQL_DURATION_HISTOGRAM = "jaeger_sqlTotalTimer";

}
