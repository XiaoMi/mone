/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.redis.monitor;

import com.xiaomi.youpin.cat.CatPlugin;
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

    public void recordMonitorInfo(boolean catEnabled,boolean prometheusEnable,boolean isCatStart,
                                  String metricName,MetricTypes metricType,String action,String keys,boolean value){
        log.info("monitor start catEnabled={},prometheusEnable={},isCatStart={},metricName={},metricType={},action={},keys={},value={}"
                ,catEnabled,prometheusEnable,isCatStart,metricName,metricType,action,keys,value);
        if(catEnabled){
            try {
                CatPlugin cat = new CatPlugin(action, catEnabled);
                if(isCatStart){
                    cat.before(keys);
                }else{
                    cat.after(value);
                }
            } catch (Exception e) {
                log.info("RedisMonitor.CAT.recordMonitorInfo error",e);
            }
        }

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
