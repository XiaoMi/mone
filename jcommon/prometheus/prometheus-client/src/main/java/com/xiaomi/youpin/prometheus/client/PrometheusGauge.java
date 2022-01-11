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

package com.xiaomi.youpin.prometheus.client;

import io.prometheus.client.Gauge;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangxiaowei
 */
@Slf4j
public class PrometheusGauge implements XmGauge {

    public Gauge myGauge;
    public String[] labelNames;
    public String[] labelValues;

    public PrometheusGauge() {

    }

    @Override
    public void set(double delta,String ...labelValues) {
        try {
            List<String> mylist = new ArrayList<>(Arrays.asList(labelValues));
            mylist.add(Prometheus.constLabels.get(Metrics.SERVICE));
            String[] finalValue = mylist.toArray(new String[mylist.size()]);
            this.myGauge.labels(finalValue).set(delta);
        } catch (Throwable throwable) {
            //log.warn(throwable.getMessage());
        }
    }

    public PrometheusGauge(Gauge cb, String[] lns, String[] lvs) {
        this.myGauge = cb;
        this.labelNames = lns;
        this.labelValues = lvs;
    }

    @Override
    public XmGauge with(String... labelValue) {
        /*String traceId = MDC.get("tid");
        if (StringUtils.isEmpty(traceId)) {
            traceId = "no traceId";
        }*/
        try {
            if (this.labelNames.length != labelValue.length) {
                log.warn("Incorrect numbers of labels : " + myGauge.describe().get(0).name);
                return new PrometheusGauge();
            }
            return this;
        } catch (Throwable throwable) {
            log.warn(throwable.getMessage());
            return null;
        }
    }

    @Override
    public void add(double delta,String... labelValue) {

        List<String> mylist = new ArrayList<>(Arrays.asList(labelValues));
        mylist.add(Prometheus.constLabels.get(Metrics.SERVICE));
        String[] finalValue = mylist.toArray(new String[mylist.size()]);
        try {
            this.myGauge.labels(finalValue).inc(delta);
        } catch (Throwable throwable) {
            //log.warn(throwable.getMessage());
        }
    }
}
