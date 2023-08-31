package com.xiaomi.mione.prometheus.redis.monitor;

import com.xiaomi.youpin.prometheus.client.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Created by gaoxihui on 2021-06-21
 * RedisMonitor
 */
@Slf4j
@Component
public class RedisMonitor {

    private static final String METRIC_TOTAL_COUNT = "RedisTotalCount";
    private static final String METRIC_SUCCESS_COUNT = "RedisSuccessCount";
    private static final String METRIC_FAILED_COUNT = "RedisFailedCount";
    private static final String METRIC_METHOD = "RedisAccessMethod";
    private static final String METRIC_SLOW_QUERY = "RedisSlowQuery";

    private static final String METRIC_METHOD_TIME_COST = "RedisMethodTimeCost";


    private static final String LABEL_METHOD = "method";
    private static final String LABEL_ACTION = "action";
    private static final String LABEL_KEYS = "key";
    private static final String LABEL_HOST = "host";
    private static final String LABEL_PORT = "port";
    private static final String LABEL_DB = "db";

    private Long slowQueryTime = 100l;

    public RedisMonitor(){

    }

    public RedisMonitor(Long slowQueryTime){
        if(slowQueryTime!=null){
            this.slowQueryTime = slowQueryTime;
        }

    }

    public void recordMonitorInfo(boolean catEnabled, boolean prometheusEnable, boolean isCatStart,
                                  String metricName, MetricTypes metricType, String action, String keys,Long startTime, boolean value,AttachInfo attachInfo) {
        log.info("monitor start catEnabled={},prometheusEnable={},isCatStart={},metricName={},metricType={},action={},keys={},value={}"
                , catEnabled, prometheusEnable, isCatStart, metricName, metricType, action, keys, value);
        StopWatch sw = new StopWatch();
        if (prometheusEnable) {
            try {
                switch (metricType) {
                    case Counter:

                        String host = attachInfo == null ? "" : attachInfo.getHostName();
                        String port = attachInfo == null ? "" : String.valueOf(attachInfo.getPort());
                        String db = attachInfo == null ? "" : String.valueOf(attachInfo.getDbIndex());

                        //totalCount
                        Metrics.getInstance().newCounter(METRIC_TOTAL_COUNT,LABEL_METHOD,LABEL_KEYS,LABEL_HOST,LABEL_PORT,LABEL_DB).with(action,keys,host,port,db).add(1);

                        //success or fail count
                        if(value){
                            Metrics.getInstance().newCounter(METRIC_SUCCESS_COUNT,LABEL_METHOD,LABEL_KEYS,LABEL_HOST,LABEL_PORT,LABEL_DB).with(action,keys,host,port,db).add(1);
                        }else{
                            Metrics.getInstance().newCounter(METRIC_FAILED_COUNT,LABEL_METHOD,LABEL_KEYS,LABEL_HOST,LABEL_PORT,LABEL_DB).with(action,keys,host,port,db).add(1);
                        }

                        //count for every method
                        Metrics.getInstance().newCounter(METRIC_METHOD,LABEL_METHOD,LABEL_KEYS,LABEL_HOST,LABEL_PORT,LABEL_DB).with(action,keys,host,port,db).add(1);

                        //count slow query times
                        long costTime = System.currentTimeMillis() - startTime;
                        if(costTime > slowQueryTime){
                            Metrics.getInstance().newCounter(METRIC_SLOW_QUERY,LABEL_METHOD,LABEL_KEYS,LABEL_HOST,LABEL_PORT,LABEL_DB).with(action,keys,host,port,db).add(1);
                        }

                        Metrics.getInstance().newHistogram(METRIC_METHOD_TIME_COST, new double[] {.01, .05, 1 ,5, 10, 15, 20,50,100,500,1000,2000,5000},
                                LABEL_METHOD,LABEL_KEYS,LABEL_HOST,LABEL_PORT,LABEL_DB).with(action,keys,host,port,db).observe(costTime);

                        break;

                    case Gauge:
                        Metrics.getInstance().newGauge(metricName, LABEL_ACTION).with(action).set(value ? 1 : 0);
                        break;

                    default:
                        log.info("RedisMonitor.recordMonitorInfo error, param metricType:{} invalid", metricType);
                        break;
                }

            } catch (Exception e) {
                log.info("RedisMonitor.PROMETHEUS.recordMonitorInfo error", e);
            }
        }
    }

}
