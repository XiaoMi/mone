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
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gateway.common.*;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import com.youpin.xiaomi.tesla.bo.ScriptInfo;
import com.youpin.xiaomi.tesla.bo.ScriptType;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * <p>
 * 执行脚本
 */
@FilterOrder(2500)
@Component
public class ScriptFilter extends RequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ScriptFilter.class);

    @Autowired
    private ScriptManager scriptManager;

    @Autowired
    private Redis redis;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (apiInfo.isAllow(Flag.ALLOW_SCRIPT)) {
            ScriptDebug scriptDebug = new ScriptDebug();
            try {
                Gson gson = new Gson();
                String scriptInfoStr = redis.get(Keys.scriptKey(String.valueOf(apiInfo.getId())));
                ScriptInfo info = gson.fromJson(scriptInfoStr, ScriptInfo.class);
                int type = info.getScriptType();
                //当脚本来执行
                if (type == ScriptType.Function.ordinal()) {
                    Map<String, Object> bindMap = Maps.newHashMap();
                    bindMap.put("request", request);
                    bindMap.put("scriptInfo", info);
                    bindMap.put("log", logger);
                    bindMap.put("scriptDebug", scriptDebug);
                    Object res = scriptManager.invoke(info.getScript(), info.getMethodName(), bindMap, info.getParams().toArray());
                    return HttpResponseUtils.create(context.byteBuf(new Gson().toJson(res).getBytes()));
                }
                //前置执行
                if (type == ScriptType.Before.ordinal()) {
                    Map<String, Object> bindMap = Maps.newHashMap();
                    bindMap.put("log", logger);
                    bindMap.put("scriptDebug", scriptDebug);
                    scriptManager.invokeBefore(info.getScript(), apiInfo, request, bindMap);
                    return invoker.doInvoker(context, apiInfo, request);
                }
                //后置执行
                if (type == ScriptType.After.ordinal()) {
                    FullHttpResponse res = invoker.doInvoker(context, apiInfo, request);
                    String content = HttpResponseUtils.getContent(res);
                    Map<String, Object> bindMap = Maps.newHashMap();
                    bindMap.put("log", logger);
                    bindMap.put("scriptDebug", scriptDebug);
                    Object r = scriptManager.invokeAfter(info.getScript(), apiInfo, request, content, bindMap);
                    return HttpResponseUtils.create(context.byteBuf(r.toString().getBytes()));
                }
                //环绕执行
                if (type == ScriptType.Around.ordinal()) {
                    Map<String, Object> bindMap = Maps.newHashMap();
                    bindMap.put("log", logger);
                    bindMap.put("scriptDebug", scriptDebug);
                    scriptManager.invokeBefore(scriptInfoStr, apiInfo, request, bindMap);
                    FullHttpResponse res = invoker.doInvoker(context, apiInfo, request);
                    String content = HttpResponseUtils.getContent(res);
                    return HttpResponseUtils.create(context.byteBuf(scriptManager.invokeAfter(info.getScript(), apiInfo, request, content, bindMap).toString().getBytes()));
                }

            } catch (Throwable ex) {
                logger.warn("invoke script error:" + ex.getMessage(), ex);
                return HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, "invoke script error:" + ex.getMessage()));
            } finally {
                if (request.headers().get("Script-Debug") != null && request.headers().get("Script-Debug").equals("on")) {
                    redis.set(Keys.scriptDebugKey(String.valueOf(apiInfo.getId())), scriptDebug.getStringBuffer().toString());
                }
            }
        }
        return invoker.doInvoker(context, apiInfo, request);
    }
}
