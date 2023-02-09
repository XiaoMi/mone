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

package com.xiaomi.youpin.gateway.handler;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.redis.Redis;
import com.youpin.xiaomi.tesla.bo.ScriptInfo;
import com.youpin.xiaomi.tesla.bo.ScriptType;
import com.xiaomi.youpin.gateway.cache.ApiRouteCache;
import com.xiaomi.youpin.gateway.common.Keys;
import com.xiaomi.youpin.gateway.common.ScriptManager;
import com.xiaomi.youpin.gateway.context.GatewayServerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */

@Slf4j
@Service
public class HandlerManager {

    @Autowired
    private ApiRouteCache cache;

    @Autowired
    private Redis redis;

    @Autowired
    private ScriptManager scriptManager;


    @Autowired
    private GatewayServerContext serverContext;

    private ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    public HandlerManager() {
        pool.scheduleAtFixedRate(() -> {
            try {
                schedule();
            } catch (Throwable ex) {
                log.warn("error:" + ex.getMessage(), ex);
            }

        }, 0, 10, TimeUnit.SECONDS);
    }

    public void schedule() {
        if (null == cache) {
            return;
        }
        cache.forEach((id) -> {
            Gson gson = new Gson();
            String scriptInfoStr = redis.get(Keys.scriptKey(String.valueOf(id)));
            if (null != scriptInfoStr) {
                ScriptInfo info = gson.fromJson(scriptInfoStr, ScriptInfo.class);
                int type = info.getScriptType();
                if (type == ScriptType.Function.ordinal() && ("__schedule__").equals(info.getMethodName())) {
                    Map<String, Object> bindMap = Maps.newHashMap();
                    bindMap.put("request", null);
                    bindMap.put("scriptInfo", info);
                    Object res = scriptManager.invoke(info.getScript(), info.getMethodName(), bindMap, info.getParams().toArray());
                    TextWebSocketFrame frame = new TextWebSocketFrame(new Gson().toJson(res));
                    GatewayServerContext.group.writeAndFlush(frame);
                }
            } else {
                log.warn("HandlerManager id:{}", id);
            }
            return null;
        });
    }
}
