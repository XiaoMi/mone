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

import com.xiaomi.youpin.gateway.TeslaConstants;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.GateWayVersion;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.exception.ApiNotFoundException;
import com.xiaomi.youpin.gateway.exception.ApiOfflineException;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.xiaomi.youpin.qps.QpsAop;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.xiaomi.youpin.gateway.common.AccessLog.logAccess;
import static com.xiaomi.youpin.gateway.common.Const.*;

/**
 * @author goodjava@qq.com
 *
 */
@Slf4j
@Component
@FilterOrder(Integer.MIN_VALUE + 10)
public class HeadFilter extends RequestFilter {

    @Autowired
    private QpsAop qpsAop;

    private String version = new GateWayVersion().toString();

    @Autowired
    private ConfigService configService;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        FullHttpResponse fullHttpResponse = null;
        long startTs = System.currentTimeMillis();

        try {
            doBefore(context, invoker, apiInfo, request);
            log.debug("head filter id:{} url:{}", apiInfo.getId(), apiInfo.getUrl());
            fullHttpResponse = doFilter1(context, invoker, apiInfo, request);
        } catch (ApiNotFoundException e) {
            fullHttpResponse = HttpResponseUtils.create(Result.fail(GeneralCodes.NotFound, HttpResponseStatus.NOT_FOUND.reasonPhrase(), "apiInfo is null"));
        } catch (ApiOfflineException e) {
            fullHttpResponse = HttpResponseUtils.create(Result.fail(GeneralCodes.METHOD_NOT_ALLOWED, HttpResponseStatus.METHOD_NOT_ALLOWED.reasonPhrase(), "api is offline"));
        } finally {
            String accessLogEnabled = System.getProperty("access.log.enabled", "false");

            if (this.configService.isAllowAccessLog() || "true".equals(accessLogEnabled)) {
                logAccess(request, fullHttpResponse, startTs, apiInfo, context);
            }
            //收尾工作，对response进行一些修饰操作
            doAfter(context, invoker, apiInfo, fullHttpResponse);
        }

        return fullHttpResponse;
    }

    private void doBefore(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        qpsAop.incr(String.valueOf(Optional.ofNullable(apiInfo).isPresent() ? apiInfo.getId() : "null"));
        // copy traceId
        String traceId = context.getTraceId();
        String spanId = context.getSpanId();
        String traceParent = "00-" + traceId + "-" + spanId + "-01";
        RpcContext.getContext().setAttachment("_trace_id_", traceId);
        RpcContext.getContext().setAttachment("traceparent", traceParent);

        request.headers().set("X-trace-id", traceId);
        request.headers().set("traceparent", traceParent);
        MDC.put(MDC_TRACE_ID, context.getTraceId());
        MDC.put(MDC_TRACE_PARENT, traceParent);

        if (null == apiInfo) {
            throw new ApiNotFoundException("apiInfo is null");
        } else {
            //服务下线了
            if (apiInfo.isAllow(Flag.OFF_LINE)) {
                throw new ApiOfflineException(String.format("api:%s is offline", apiInfo.getId()));
            }
        }
    }

    private FullHttpResponse doFilter1(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        return invoker.doInvoker(context, apiInfo, request);
    }

    private void doAfter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpResponse res) {
        MDC.remove(MDC_TRACE_ID);
        MDC.remove(MDC_TRACE_PARENT);
        if (null == res) {
            return;
        }
        res.headers().set(TeslaConstants.TeslaVersion, version);
        res.headers().set(TeslaConstants.TraceId, context.getTraceId());
    }
}
