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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.youpin.gateway.common.*;
import com.xiaomi.youpin.gateway.dubbo.Dubbo;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.http.Http;
import com.xiaomi.youpin.gateway.nacos.Nacos;
import com.xiaomi.youpin.gateway.redis.Redis;
import com.xiaomi.youpin.gateway.service.ScriptJarManager;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * <p>
 * 执行脚本
 */
@FilterOrder(2500)
@Component
@Slf4j
public class ScriptFilter extends RequestFilter {

    @Autowired
    private ScriptManager scriptManager;

    @Autowired
    private Redis redis;


    @Autowired
    private Dubbo dubbo;

    @Autowired
    private Nacos nacos;

    @Autowired
    private Http http;

    @Autowired
    private ScriptJarManager scriptJarManager;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (apiInfo.isAllow(Flag.ALLOW_SCRIPT)) {
            ScriptDebug scriptDebug = new ScriptDebug();
            try {
                log.debug("--->invoke script:{}", apiInfo.getId());
                ScriptInfo info = scriptManager.loadScriptInfo(apiInfo.getId());

                int type = info.getScriptType();

                if (log.isDebugEnabled()) {
                    log.info("scirpt type:{}", type);
                }

                if (type == ScriptType.Jar.ordinal()) {
                    info.setJar(true);
                }


                //jar 包形式的(比较重的项目)
                if (info.isJar()) {
                    return executeJar(context, apiInfo, request, info);
                }


                //当function执行
                if (type == ScriptType.Function.ordinal()) {
                    Map<String, Object> bindMap = Maps.newHashMap();
                    inject(context, request, scriptDebug, info, bindMap);
                    log.info("invoke method:{} {}", apiInfo.getId(), info.getMethodName());
                    Object res = scriptManager.invoke(info.getScript(), info.getMethodName(), bindMap, info.getParams().toArray());
                    return HttpResponseUtils.create(context.byteBuf(new Gson().toJson(res).getBytes()));
                }
                //前置执行
                if (type == ScriptType.Before.ordinal()) {
                    Map<String, Object> bindMap = Maps.newHashMap();
                    inject(context, request, scriptDebug, info, bindMap);
                    scriptManager.invokeBefore(info.getScript(), apiInfo, request, bindMap);
                    return invoker.doInvoker(context, apiInfo, request);
                }
                //后置执行
                if (type == ScriptType.After.ordinal()) {
                    FullHttpResponse res = invoker.doInvoker(context, apiInfo, request);
                    String content = HttpResponseUtils.getContent(res);
                    Map<String, Object> bindMap = Maps.newHashMap();
                    inject(context, request, scriptDebug, info, bindMap);
                    Object r = scriptManager.invokeAfter(info.getScript(), apiInfo, request, content, bindMap);
                    return HttpResponseUtils.create(context.byteBuf(r.toString().getBytes()));
                }
                //环绕执行
                if (type == ScriptType.Around.ordinal()) {
                    Map<String, Object> bindMap = Maps.newHashMap();
                    inject(context, request, scriptDebug, info, bindMap);
                    scriptManager.invokeBefore(info.getScript(), apiInfo, request, bindMap);
                    FullHttpResponse res = invoker.doInvoker(context, apiInfo, request);
                    String content = HttpResponseUtils.getContent(res);
                    return HttpResponseUtils.create(context.byteBuf(scriptManager.invokeAfter(info.getScript(), apiInfo, request, content, bindMap).toString().getBytes()));
                }

            } catch (Throwable ex) {
                log.warn("invoke script error:" + ex.getMessage(), ex);
                return HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, "invoke script error:" + ex.getMessage()));
            } finally {
                if (request.headers().get("Script-Debug") != null && request.headers().get("Script-Debug").equals("on")) {
                    redis.set(Keys.scriptDebugKey(String.valueOf(apiInfo.getId())), scriptDebug.getStringBuffer().toString());
                }
            }
        }

        return invoker.doInvoker(context, apiInfo, request);
    }

    private void bindTenantContext(ApiInfo apiInfo, boolean serverless, boolean dbAuthCheck) {
    }

    private FullHttpResponse executeJar(FilterContext context, ApiInfo apiInfo, FullHttpRequest request, ScriptInfo info) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("script execute id:{}", apiInfo.getId());
            }
            bindTenantContext(apiInfo, true, true);
            String key = apiInfo.getId() + "";
            return HttpResponseUtils.create(context.byteBuf("ok".getBytes()));
        } catch (Throwable ex) {
            log.error("execute script error:{} {}", apiInfo.getPath(), ex.getMessage(), ex);
            return HttpResponseUtils.create(Result.fromException(ex));
        }
    }

    private void inject(FilterContext context, FullHttpRequest request, ScriptDebug scriptDebug, ScriptInfo info, Map<String, Object> bindMap) {
        bindMap.put("sRequest", initScriptRequest(context, request));
        bindMap.put("request", request);
        bindMap.put("scriptInfo", info);
        //日志
        bindMap.put("log", log);
        //script的调试信息
        bindMap.put("scriptDebug", scriptDebug);
        //cache调用
        bindMap.put("redis", redis);
        //dubbo调用
        bindMap.put("dubbo", dubbo);
        //配置调用
        bindMap.put("nacos", nacos);
        //http调用
        bindMap.put("http", http);
    }

    private ScriptRequest initScriptRequest(FilterContext context, FullHttpRequest request) {
        ScriptRequest req = new ScriptRequest();

        try {
            req.setIp(context.getIp());
            //req.setTraceId(HttpRequestUtils.traceId(request));
            req.setUid(context.getUid());
            req.setAttachments(context.getAttachments());

            Map<String, String> headers = request.headers().entries()
                    .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1));
            req.setHeader(headers);

            if (request.method().toString().toUpperCase().equals("GET")) {
                req.setGetParam(HttpRequestUtils.getQueryParams(request.uri()));
            } else {
                req.setFormParam(HttpRequestUtils.getFormBody(request));
                req.setPostParam(new String(HttpRequestUtils.getRequestBody(request)));
            }
        } catch (Exception e) {
            log.error("initScriptRequest error... ", e);
        }

        return req;
    }
}
