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

import com.alibaba.nacos.client.naming.NacosNamingService;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.xiaomi.youpin.gateway.RouteType;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.common.TeslaSafeRun;
import com.xiaomi.youpin.gateway.exception.GatewayException;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.netty.filter.CodeParser;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.prometheus.client.Metrics;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/15 11:53
 */
@Component
@Slf4j
@FilterOrder(Integer.MIN_VALUE + 1000)
public class PrometheusFilter extends RequestFilter {

    @Value(value = "${department.env.flag}")
    private String departmentEnvFlag;

    @Autowired
    private ConfigService configService;

    @Autowired
    private NacosNamingService nacosNamingService;

    public static final String PROVIDER_FLAG = "providers:";

    private ConcurrentHashMap<String, String> interfaceApplicationMap = new ConcurrentHashMap<>(1024);

    private String ip = "";

    @Value("${machine.group}")
    private String defaultMachineGroup = "";


    @PostConstruct
    public void initFilter() {
        try {
            //初始化
            Metrics.getInstance().init(defaultMachineGroup,"tesla",true);
        } catch (Throwable ex) {
            log.warn(ex.getMessage());
        }
        TeslaSafeRun.runEx(() -> ip = null != System.getenv("host.ip") ? System.getenv("host.ip") : Inet4Address.getLocalHost().getHostAddress());
    }

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (!configService.isUsePrometheusFilter()) {
            return invoker.doInvoker(context, apiInfo, request);
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        String uri = request.uri();
        log.debug("PrometheusFilter:{}", uri);
        if (StringUtils.isEmpty(uri) || StringUtils.countOccurrencesOf(uri, "/") < 3) {
            log.info("PrometheusFilter uri empty");
            return invoker.doInvoker(context, apiInfo, request);
        }
        String[] str = uri.split("/", 4);
        String group = str[2];
        String lastUri = URI.create(str[3]).getPath();
        String applicationName = getApplicationName(apiInfo);
        if (null == departmentEnvFlag) {
            departmentEnvFlag = "";
        }
        if (applicationName != null && !"".equals(applicationName)) {
            applicationName = StringUtils.uncapitalize(applicationName);
        }
        applicationName = departmentEnvFlag + "_" + applicationName;
        if (StringUtils.isEmpty(lastUri) || StringUtils.isEmpty(group) || StringUtils.isEmpty(applicationName)) {
            log.info("params is empty url:{} ,group:{} , serviceName:{}",lastUri,group,applicationName);
        }
        recordCounter("TotalCounter", lastUri, applicationName, group);
        try {
            FullHttpResponse response = invoker.doInvoker(context, apiInfo, request);
            if (null == apiInfo ||
                    response == null ||
                    response.content() == null ||
                    !response.content().isReadable()) {
                log.info("ErrorCounterPrometheus");
                recordCounter("ErrorCounter", lastUri, applicationName, group);
                return response;
            }
            if (configService.isNeedParseCode()) {
                parseCode(context, response, lastUri, applicationName, group);
            }
            recordTimer("use_time", new String[]{"url", "group", "serviceName"}, stopwatch.elapsed(TimeUnit.MILLISECONDS), lastUri, group, applicationName);
            return response;
        } catch (Throwable ex) {
            log.error("PrometheusFilter Throwable", ex);
            throw new GatewayException(ex);
        }
    }

    private void parseCode(FilterContext context, FullHttpResponse response, String url, String applicationName, String group) {
        try {
            ByteBuf buf = response.content().duplicate();
            int code = CodeParser.parseCode(buf);
            if (String.valueOf(code).startsWith("5")) {
                final String traceId = context.getTraceId();
                TeslaSafeRun.run(() -> log.info(Joiner.on(",").join(new Object[]{"$%^", url, departmentEnvFlag + "_tesla", traceId, group, ip, System.currentTimeMillis(),departmentEnvFlag})));
                recordCode("errCode", new String[]{"code", "url", "serviceName", "group"}, String.valueOf(code), url, applicationName, group);
                String data = HttpResponseUtils.getContent(response);
                context.addBusErrorEvent("business error event", code, data);
            }
        } catch (Exception e) {
            log.error("failed to parse response code, err: {}", e.getMessage());
        }
    }


    private void recordCounter(String metricName, String uri, String applicationName, String group) {
        try {
            //用来计算qps等信息
            Metrics.getInstance().newCounter(metricName, "url", "serviceName", "group").with(uri, applicationName, group).add(1,uri, applicationName, group);
        } catch (Exception e) {
            log.warn(e.getMessage());
            log.error("PrometheusFilter recordCounter", e);
        }
    }

    private void recordTimer(String metricName, String[] labelsName, long value, String... labelsValue) {
        try {
            //耗时记录
            double[] buckets = new double[]{10.0, 100.0, 200.0, 300.0, 400.0, 600.0, 800.0, 1000.0, 2000.0, 3000.0};
            Metrics.getInstance().newHistogram(metricName, buckets, labelsName).with(labelsValue).observe(value,labelsValue);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void recordCode(String metricName, String[] labelsName, String... labelsValue) {
        try {
            Metrics.getInstance().newCounter(metricName, labelsName).with(labelsValue).add(1,labelsValue);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private String getApplicationName(ApiInfo apiInfo) {
        try {
            if (apiInfo.getRouteType().equals(RouteType.Http.type())) {
                return apiInfo.getApplication();
            }
            //若业务填了application，直接返回
            if ((apiInfo.getApplication() != null && !"".equals(apiInfo.getApplication()))) {
                return apiInfo.getApplication();
            }
            if (interfaceApplicationMap.get(apiInfo.getDubboApiInfo().getServiceName()) != null) {
                return interfaceApplicationMap.get(apiInfo.getDubboApiInfo().getServiceName());
            } else {
                return "appName";
            }
        } catch (Throwable e) {
            log.error("PrometheusFilter ApplicationName throw:", e);
            return "appName";
        }
    }
}
