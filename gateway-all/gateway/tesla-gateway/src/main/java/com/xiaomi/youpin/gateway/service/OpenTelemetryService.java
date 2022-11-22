///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gateway.service;
//
//import com.xiaomi.youpin.gateway.RouteType;
//import com.xiaomi.youpin.gateway.common.GateWayVersion;
//import com.youpin.xiaomi.tesla.bo.ApiInfo;
//import io.netty.handler.codec.http.FullHttpRequest;
//import io.opentelemetry.api.OpenTelemetry;
//import io.opentelemetry.api.common.AttributeKey;
//import io.opentelemetry.api.common.Attributes;
//import io.opentelemetry.api.trace.Span;
//import io.opentelemetry.api.trace.SpanBuilder;
//import io.opentelemetry.api.trace.SpanKind;
//import io.opentelemetry.api.trace.Tracer;
//import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
//import io.opentelemetry.context.Context;
//import io.opentelemetry.context.propagation.ContextPropagators;
//import io.opentelemetry.context.propagation.TextMapGetter;
//import io.opentelemetry.exporter.logging.Log4j2SpanExporter;
//import io.opentelemetry.sdk.OpenTelemetrySdk;
//import io.opentelemetry.sdk.resources.Resource;
//import io.opentelemetry.sdk.trace.SdkTracerProvider;
//import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
//import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.jetbrains.annotations.Nullable;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
///**
// * @Author goodjava@qq.com
// * @Date 2021/8/22 23:50
// */
//@Service
//@Slf4j
//public class OpenTelemetryService {
//
//    @Value("${department.env.flag:}")
//    private String departmentEnvFlag;
//
//    @Value("${env.group:}")
//    private String envGroup;
//
//    @Value("${log.exporter.isAsycn}")
//    private String isAsycn;
//
//    /**
//     * 日志切分策略。每多长时间切分日志（单位：分钟）
//     */
//    @Value("${log.exporter.logInterval}")
//    private String logInterval;
//
//    /**
//     * 日志删除策略。删除多长时间之前的日志文件
//     * "PT20S" -- parses as "20 seconds"
//     * "PT15M"     -- parses as "15 minutes" (where a minute is 60 seconds)
//     * "PT10H"     -- parses as "10 hours" (where an hour is 3600 seconds)
//     * "P2D"       -- parses as "2 days" (where a day is 24 hours or 86400 seconds)
//     * "P2DT3H4M"  -- parses as "2 days, 3 hours and 4 minutes"
//     */
//    @Value("${log.exporter.logDeleteAge}")
//    private String logDeleteAge;
//
//    @Getter
//    private Tracer tracer;
//
//    private ContextPropagators propagators;
//
//    private TextMapGetter<FullHttpRequest> httpRequestExtractAdapter = new HttpRequestExtractAdapter();
//
//    @Autowired
//    private ConfigService configService;
//
//
//    public Span startSpan(ApiInfo apiInfo, String spanName, String uri) {
//        if (!configService.isOpenJeager()) {
//            return null;
//        }
//        try {
//            if (null != tracer) {
//                SpanBuilder builder = tracer.spanBuilder(spanName);
//                completeSpan(builder,apiInfo,uri);
//                return builder.startSpan();
//            }
//        } catch (Throwable ex) {
//            log.error(ex.getMessage());
//        }
//        return null;
//    }
//
//    public Span startSpan(ApiInfo apiinfo,String spanName, String uri,FullHttpRequest request){
//        if (!configService.isOpenJeager()) {
//            return null;
//        }
//        try {
//            Context parent = Context.current();
//            if (Span.fromContextOrNull(parent) != null) {
//                parent = Context.root();
//            }
//            Context parentContext = propagators.getTextMapPropagator().extract(parent, request, httpRequestExtractAdapter);
//            SpanBuilder spanBuilder = tracer.spanBuilder(spanName);
//            completeSpan(spanBuilder, apiinfo, uri);
//            spanBuilder.setParent(parentContext);
//            return spanBuilder.startSpan();
//        } catch (Throwable ex) {
//            log.error(ex.getMessage());
//        }
//        return null;
//    }
//
//    private void completeSpan(SpanBuilder builder,ApiInfo apiInfo,String uri){
//        if (null != apiInfo) {
//            int routeType = apiInfo.getRouteType();
//            if (routeType == RouteType.Dubbo.type() || routeType == RouteType.Native_Dubbo.type()) {
//                builder.setSpanKind(SpanKind.CLIENT);
//            }
//        }else if(StringUtils.isNotEmpty(uri)){
//            builder.setAttribute("http.unknow.uri",uri);
//        }else{
//            builder.setAttribute("http.apiinfo.isnull",true);
//        }
//    }
//
//    public void initOpenTelemetry() {
//        try {
//            log.info("OpenTelemetry init begin");
//
//            // log4j exporter
//            Log4j2SpanExporter log4j2SpanExporter = new Log4j2SpanExporter("",isAsycn,logInterval,logDeleteAge);
//
//            String name = "tesla-getaway" + "-" + departmentEnvFlag + "-" + this.envGroup;
//            log.info("name:{}", name);
//
//            Resource teslaResource =
//                    Resource.create(
//                            Attributes.of(
//                                    ResourceAttributes.SERVICE_NAME, name,
//                                    AttributeKey.stringKey("tesla_version"),new GateWayVersion().toString()
//                            )
//                    );
//
//            SdkTracerProvider tracerProvider =
//                    SdkTracerProvider.builder()
//                            .addSpanProcessor(SimpleSpanProcessor.create(log4j2SpanExporter))
//                            .setResource(teslaResource)
//                            .build();
//
//            OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
//                    .setTracerProvider(tracerProvider)
//                    .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
//                    .buildAndRegisterGlobal();
//
//            tracer = openTelemetry.getTracer(name, "1.0.0");
//            propagators = openTelemetry.getPropagators();
//            Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
//            log.info("OpenTelemetry init finish");
//        } catch (Throwable e) {
//            log.error("OpenTelemetry init error:" + e.getMessage(), e);
//        }
//    }
//
//    class HttpRequestExtractAdapter implements TextMapGetter<FullHttpRequest>{
//
//        @Override
//        public Iterable<String> keys(FullHttpRequest fullHttpRequest) {
//            return fullHttpRequest.headers().names();
//        }
//
//        @Nullable
//        @Override
//        public String get(@Nullable FullHttpRequest fullHttpRequest, String key) {
//            return fullHttpRequest.headers().get(key);
//        }
//    }
//}
