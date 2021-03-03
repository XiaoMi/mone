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

import com.google.gson.Gson;
import com.xiaomi.youpin.gateway.cache.TrafficRecordingCache;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpRequestUtils;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingSourceTypeEnum;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingStrategyEnum;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfig;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.HttpTraffic;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.Traffic;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingStrategyEnum.*;

/**
 * @author dp@qq.com
 * <p>
 * 流量录制
 */
@Slf4j
@Component
@FilterOrder(1000 + 2)
public class TrafficRecordingFilter extends RequestFilter {

    @Autowired
    private TrafficRecordingCache cache;

    @Autowired
    private DefaultMQProducer defaultMQProducer;


    @Autowired
    private ConfigService configService;

    @Value("${recording.traffic.topic}")
    private String topic;


    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        //是否要校验token
        if (needRecordTraffic(apiInfo, context, request)) {
            log.info("TrafficRecordingFilter record:{} {}", apiInfo.getId(), apiInfo.getUrl());
            long invokeBeginTime = System.currentTimeMillis();
            FullHttpResponse res = invoker.doInvoker(context, apiInfo, request);
            long invokeEndTime = System.currentTimeMillis();

            sendMessage(adapterTraffic(context, apiInfo, request, invokeBeginTime, invokeEndTime, res));
            return res;
        }
        return invoker.doInvoker(context, apiInfo, request);
    }

    private boolean needRecordTraffic(ApiInfo apiInfo, FilterContext context, FullHttpRequest request) {

        if (!configService.isOpenTrafficRecord()) {
            return false;
        }


        String url = apiInfo.getUrl();
        RecordingConfig recordingConfig = cache.get(url);
        if (recordingConfig != null && recordingConfig.getStatus() == 1) {
            //分析录制策略
            int strategy = recordingConfig.getRecordingStrategy();
            switch (strategy) {
                case PERCENTAGE_CODE: {
                    return needRecordingByStrategyPercentage(recordingConfig);
                }
                case UID_CODE: {
                    return needRecordingByStrategyUid(recordingConfig, context.getUid());
                }
                case HEADER_CODE: {
                    return needRecordingByStrategyHeader(recordingConfig, request);
                }
                case PARAM_CODE: {

                }
                default: {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean needRecordingByStrategyPercentage(RecordingConfig recordingConfig) {
        int v = new Random().nextInt(101);
        if (v < recordingConfig.getPercentage()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean needRecordingByStrategyUid(RecordingConfig recordingConfig, String uid) {
        if (StringUtils.isEmpty(uid)) {
            return false;
        }
        if (recordingConfig.getUid() == Integer.parseInt(uid)) {
            return true;
        }
        return false;
    }

    private boolean needRecordingByStrategyHeader(RecordingConfig recordingConfig, FullHttpRequest request) {
        Map<String, String> recordingHeaders = recordingConfig.getHeaders();
        if (recordingHeaders == null || recordingHeaders.size() == 0) {
            return true;
        }

        for (Map.Entry<String, String> entry : recordingHeaders.entrySet()) {
            String realValue = request.headers().get(entry.getKey(), "");
            if (realValue.equals(entry.getValue())) {
                return false;
            }
        }

        return true;
    }


    private Traffic adapterTraffic(FilterContext context, ApiInfo apiInfo, FullHttpRequest request, long invokeBeginTime, long invokeEndTime, FullHttpResponse res) {
        Traffic traffic = new Traffic();
        try {
            String httpMethod = request.method().toString().toUpperCase();
            Map<String, String> headers = request.headers().entries()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1));
            HttpTraffic httpTraffic = new HttpTraffic();
            httpTraffic.setHttpMethod(httpMethod);
            httpTraffic.setOriginHeaders(headers);
            httpTraffic.setHost(request.headers().get("Host"));
            httpTraffic.setUrl(apiInfo.getUrl());
            if (HttpMethod.POST.name().equals(httpMethod)) {
                httpTraffic.setOrginBody(new String(HttpRequestUtils.getRequestBody(request)));
            } else if (HttpMethod.GET.name().equals(httpMethod)) {
                httpTraffic.setOriginQueryString(HttpRequestUtils.getQueryString(request));
            }

            traffic.setHttpTraffic(httpTraffic);
            traffic.setInvokeBeginTime(invokeBeginTime);
            traffic.setInvokeEndTime(invokeEndTime);
            traffic.setRecordingConfigId(cache.get(apiInfo.getUrl()).getId());
            traffic.setSourceType(RecordingSourceTypeEnum.GATEWAY.getCode());
            traffic.setResponse(HttpResponseUtils.getContent(res));
            traffic.setTraceId(context.getTraceId());
            if (StringUtils.isNotEmpty(context.getUid())) {
                traffic.setUid(Long.parseLong(context.getUid()));
            }
            traffic.setSaveDays(cache.get(apiInfo.getUrl()).getSaveDays());
        } catch (Exception e) {
            log.warn("TrafficRecordingFilter.adapterTraffic error: {}", e.getMessage(), e);
        }

        return traffic;
    }

    private void sendMessage(Traffic traffic) {
        try {
            defaultMQProducer.send(new Message(
                    topic,
                    "",
                    new Gson().toJson(traffic).getBytes()
            ));
        } catch (Exception e) {
            log.warn("TrafficRecordingFilter.sendMessage, send message to mq error:" + e.getMessage());
        }
    }
}
