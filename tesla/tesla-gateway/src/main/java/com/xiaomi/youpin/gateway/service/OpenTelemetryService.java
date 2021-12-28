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

package com.xiaomi.youpin.gateway.service;

import com.xiaomi.youpin.gateway.RouteType;
import com.xiaomi.youpin.gateway.common.GateWayVersion;
import com.xiaomi.youpin.gateway.common.NetUtils;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/22 23:50
 */
@Service
@Slf4j
public class OpenTelemetryService {

    @Value("${jaeger.host:127.0.0.1}")
    private String jaegerHost;

    @Value("${jaeger.grpc.port:14250}")
    private String jaegerGrpcPort;

    @Value("${department.env.flag:}")
    private String departmentEnvFlag;

    @Value("${env.group:}")
    private String envGroup;

    @Getter
    private Tracer tracer;

    @Autowired
    private ConfigService configService;


    public Span startSpan(ApiInfo apiInfo, String spanName, String uri) {
        if (!configService.isOpenJeager()) {
            return null;
        }
        try {
            if (null != tracer) {
                SpanBuilder builder = tracer.spanBuilder(spanName);
                if (null != apiInfo) {
                    int routeType = apiInfo.getRouteType();
                    if (routeType == RouteType.Dubbo.type() || routeType == RouteType.Native_Dubbo.type()) {
                        builder.setSpanKind(SpanKind.CLIENT);
                    }
                }else if(StringUtils.isNotEmpty(uri)){
                    builder.setAttribute("http.unknow.uri",uri);
                }else{
                    builder.setAttribute("http.apiinfo.isnull",true);
                }
                return builder.startSpan();
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
        return null;
    }


    public void initOpenTelemetry() {
        try {
            log.info("OpenTelemetry init begin");
            ManagedChannel jaegerChannel =
                    ManagedChannelBuilder.forAddress(jaegerHost, Integer.parseInt(jaegerGrpcPort)).usePlaintext().build();
            JaegerGrpcSpanExporter jaegerExporter =
                    JaegerGrpcSpanExporter.builder()
                            .setChannel(jaegerChannel)
                            .setTimeout(3, TimeUnit.SECONDS)
                            .build();
//            LoggingSpanExporter loggingSpanExporter = new LoggingSpanExporter();
            String name = "tesla-getaway" + "-" + departmentEnvFlag + "-" + this.envGroup;
            log.info("name:{}", name);

            Resource teslaResource =
                    Resource.create(
                            Attributes.of(
                                    ResourceAttributes.SERVICE_NAME, name,
                                    AttributeKey.stringKey("tesla_version"),new GateWayVersion().toString()
                            )
                    );

            SdkTracerProvider tracerProvider =
                    SdkTracerProvider.builder()
                            .addSpanProcessor(SimpleSpanProcessor.create(jaegerExporter))
                            .setResource(teslaResource)
                            .build();

            OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                    .setTracerProvider(tracerProvider)
                    .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                    .buildAndRegisterGlobal();

            tracer = openTelemetry.getTracer(name, "1.0.0");
            Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
            log.info("OpenTelemetry init finish");
        } catch (Throwable e) {
            log.error("OpenTelemetry init error:" + e.getMessage(), e);
        }
    }

}
