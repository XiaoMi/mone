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

package com.xiaomi.youpin.gateway.netty.filter.request;

import com.google.common.collect.Sets;
import com.xiaomi.youpin.gateway.common.ByteBufUtils;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.prometheus.client.Prometheus;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * metric
 *
 * @author goodjava@qq.com
 */
@Component
@Slf4j
@FilterOrder(2000 - 100)
public class MetricFilter extends RequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(MetricFilter.class);

    @Autowired
    private ConfigService configService;

    private static final String URL = "/mtop/arch/metric";
    private static final String JVM_URL = "/mtop/arch/metric/jvm";
    private CollectorRegistry defaultRegistry = CollectorRegistry.defaultRegistry;
    private CollectorRegistry jvmRegistry = Prometheus.REGISTRY.getPrometheusRegistry();

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (URL.equals(apiInfo.getUrl())) {
            return metrics(defaultRegistry,context);
        }
        if(JVM_URL.equals(apiInfo.getUrl())){
            return metrics(jvmRegistry,context);
        }
        return invoker.doInvoker(context, apiInfo, request);
    }

    private FullHttpResponse metrics(CollectorRegistry registry,FilterContext context){
        List<String> list = new ArrayList<>();
        try {
            Field field = registry.getClass().getDeclaredField("namesToCollectors");
            field.setAccessible(true);
            Map<String, Collector> namesToCollectors = (Map<String, Collector>) field.get(registry);
            list = namesToCollectors.keySet().stream()
                    .filter(it -> !it.endsWith("created"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.info("export metrics error : ",e);
        }
        Enumeration<Collector.MetricFamilySamples> samples = registry.filteredMetricFamilySamples(Sets.newHashSet(list));
        StringWriter writer = new StringWriter();
        String str = "";
        try {
            TextFormat.write004(writer, samples);
            StringBuffer sb = writer.getBuffer();
            str = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteBuf buf = ByteBufUtils.createBuf(context, str, configService.isAllowDirectBuf());
        return HttpResponseUtils.create(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf));
    }

    @Override
    public void init() {
        this.def.setAuthor("goodjava@qq.com");
        this.def.setName("metric_filter");
        this.def.setVersion("0.0.1");
    }


}
