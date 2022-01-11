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

import io.prometheus.client.Counter;
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
public class PrometheusCounter implements XmCounter {

    public String[] labelValues;

    public Counter myCounter;

    public String[] labelNames;

    public PrometheusCounter(Counter cb, String[] lns, String[] lvs) {
        this.myCounter = cb;
        this.labelNames = lns;
        this.labelValues = lvs;
    }

    public PrometheusCounter() {

    }

    @Override
    public XmCounter with(String... labelValues) {
        try {
            if (this.labelNames.length != labelValues.length) {
                log.warn("Incorrect numbers of labels : " + myCounter.describe().get(0).name + " labelName: " + this.labelNames.length + " labelValues: " + labelValues.length + "{} {}", Arrays.toString(this.labelNames), Arrays.toString(labelValues));
                return new PrometheusCounter();
            }
            return this;
        } catch (Throwable throwable) {
            log.warn(throwable.getMessage());
            return null;
        }
    }

    @Override
    public void add(double delta, String... labelValues) {

        List<String> mylist = new ArrayList<>(Arrays.asList(labelValues));
        mylist.add(Prometheus.constLabels.get(Metrics.SERVICE));
        // mylist.add(traceId);
        String[] finalValue = mylist.toArray(new String[mylist.size()]);
        try {
            this.myCounter.labels(finalValue).inc(delta);
        } catch (Throwable throwable) {
            log.warn(throwable.getMessage());
        }

    }
}
