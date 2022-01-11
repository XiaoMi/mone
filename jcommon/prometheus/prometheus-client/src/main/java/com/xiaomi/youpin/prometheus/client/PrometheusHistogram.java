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

import io.prometheus.client.Histogram;
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
public class PrometheusHistogram implements XmHistogram {

    public Histogram myHistogram;
    public String[] labelNames;
    public String[] labelValues;

    public PrometheusHistogram(Histogram cb, String[] lns, String[] lvs) {
        this.myHistogram = cb;
        this.labelNames = lns;
        this.labelValues = lvs;
    }

    public PrometheusHistogram() {

    }

    @Override
    public XmHistogram with(String... labelValues) {
        /*String traceId = MDC.get("tid");
        if (StringUtils.isEmpty(traceId)) {
            traceId = "no traceId";
        }*/
        try {
            if (this.labelNames.length != labelValues.length) {
                log.warn("Incorrect numbers of labels : " + myHistogram.describe().get(0).name + " labelName: " + this.labelNames.length + " labelValues: " + this.labelValues.length);
                return new PrometheusHistogram();
            }
            return this;
        } catch (Throwable throwable) {
            log.warn(throwable.getMessage());
            return null;
        }
    }

    @Override
    public void observe(double delta,String... labelValue) {
        try {
            List<String> mylist = new ArrayList<>(Arrays.asList(labelValue));
            mylist.add(Prometheus.constLabels.get(Metrics.SERVICE));
            String[] finalValue = mylist.toArray(new String[mylist.size()]);
            this.myHistogram.labels(finalValue).observe(delta);
        } catch (Throwable throwable) {
            log.warn(throwable.getMessage());
        }
    }
}
