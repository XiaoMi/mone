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

import com.dianping.cat.Cat;
import com.dianping.cat.message.Trace;
import com.dianping.cat.message.Transaction;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpRequestUtils;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.netty.filter.CodeParser;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.jcommon.log.LogContext;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 * @author 许铮
 * @author 丁雯
 * 解析code,并记录到cat
 */
@Slf4j
@Component
@FilterOrder(Integer.MIN_VALUE)
public class CodeFilter extends RequestFilter {

    @Autowired
    private ConfigService configService;

    @Value("${server.type}")
    private String serverType;

    private static final String SERVER_TYPE_STAGING = "staging";

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        long now = System.currentTimeMillis();
        Transaction t = Cat.newTransaction("tesla", apiInfo == null ? "api_null" : apiInfo.getUrl());
        t.addChild(Cat.newTrace("uri", request.uri()));
        FullHttpResponse response = null;
        try {
            t.setStatus(Transaction.SUCCESS);
            response = invoker.doInvoker(context, apiInfo, request);

            t.addChild(Cat.newTrace("trace_id", HttpRequestUtils.traceId(request)));
            t.addChild(Cat.newTrace("param", context.getAttachments().get("param")));

            if (response.status().equals(HttpResponseStatus.FOUND)) {
                return response;
            }

            if (null == apiInfo ||
                    response == null ||
                    response.content() == null ||
                    !response.content().isReadable()) {
                t.setStatus("failed");
                return response;
            }
            if (configService.isNeedParseCode()) {
                boolean bcode = parseCode(response, apiInfo.getUrl(), t);
                if (!bcode || serverType.equals(SERVER_TYPE_STAGING)) {
                    String data = HttpResponseUtils.getContent(response);
                    t.addChild(Cat.newTrace("result", data));
                }
            }
            t.addChild(Cat.newTrace("use_time", String.valueOf(now - context.getBeginTime())));
            t.addChild(Cat.newTrace("h_use_time", String.valueOf(now - context.getHbegin())));
            return response;
        } catch (Throwable e) {
            t.setStatus(e);
            Cat.logError(e);
            if (null == response) {
                response = HttpResponseUtils.create(Result.fromException(e));
            }
            return response;
        } finally {
            t.complete();
        }
    }


    private boolean parseCode(FullHttpResponse response, String url, Transaction t) {
        try {
            ByteBuf buf = response.content().duplicate();
            long begin = System.currentTimeMillis();
            int code = CodeParser.parseCode(buf);
            Trace trace = Cat.newTrace("code", "" + code);
            trace.setTimestamp(System.currentTimeMillis() - begin);
            t.addChild(trace);
            if (String.valueOf(code).startsWith("5")) {
                t.setStatus("failed");
                log.error("invoke {} error code:{}", url, code,
                        LogContext.builder().code(String.valueOf(code)).errorSource(url).build());
                return false;
            }
            return true;
        } catch (Exception e) {
            t.setStatus(e);
            log.error("failed to parse response code, err: {}", e.getMessage());
        }
        return true;
    }


    @Override
    public void init() {
        this.def.setName("code_filter");
        this.def.setAuthor("goodjava@qq.com;许铮;丁雯");
        this.def.setVersion("0.0.2:2020-06-20");
        super.init();
    }
}
