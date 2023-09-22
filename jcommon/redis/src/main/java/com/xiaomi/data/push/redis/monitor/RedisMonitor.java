package com.xiaomi.data.push.redis.monitor;

import com.xiaomi.youpin.prometheus.client.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisMonitor {

    private static final String group = "Redis";
    private static final String service = "Monitor";

    private static final String LABEL_ACTION = "action";
    private static final String LABEL_KEY = "key";
    private static final String LABEL_RESULT = "result";

    private static final String RESULT_SUCCESS = "success";
    private static final String RESULT_FAILED = "failed";

    public void recordMonitorInfo(boolean catEnabled, boolean prometheusEnable, boolean isCatStart,
                                  String metricName,MetricTypes metricType,String action,String keys,boolean value){
        log.info("monitor start catEnabled={},prometheusEnable={},isCatStart={},metricName={},metricType={},action={},keys={},value={}"
                ,catEnabled,prometheusEnable,isCatStart,metricName,metricType,action,keys,value);

        if (prometheusEnable){
            try {
                switch (metricType){
                    case Counter:
                        Metrics.getInstance().newCounter(metricName,LABEL_ACTION,LABEL_KEY,LABEL_RESULT)
                                .with(action,keys == null ? "":keys,value ? RESULT_SUCCESS : RESULT_FAILED)
                                .add(1);
                        break;

                    case Gauge:
                        Metrics.getInstance().newGauge(metricName,LABEL_ACTION).with(action).set(value ? 1 : 0);
                        break;

                    default:
                       log.info("RedisMonitor.recordMonitorInfo error, param metricType:{} invalid",metricType);
                       break;
                }

            } catch (Exception e) {
                log.info("RedisMonitor.PROMETHEUS.recordMonitorInfo error",e);
            }
        }
    }

}
