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

package com.xiaomi.data.push.cache;

import com.caucho.hessian.io.HessianProtocolException;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.xiaomi.data.push.action.ActionContext;
import com.xiaomi.data.push.action.ActionInfo;
import com.xiaomi.data.push.annotation.Cache;
import com.xiaomi.data.push.annotation.CacheType;
import com.xiaomi.data.push.common.TraceId;
import com.xiaomi.data.push.hessian.HessianUtils;
import com.xiaomi.data.push.redis.Redis;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 */
@Aspect
@Configuration
@Order(2)
public class CacheAop {


    private static final Logger logger = LoggerFactory.getLogger(CacheAop.class);

    @Autowired
    private Redis redis;

    @Autowired
    private ActionContext actionContext;


    private com.google.common.cache.Cache<String, Object> memCache = null;


    public CacheAop() {
        memCache = CacheBuilder.newBuilder()
                .recordStats()
                .expireAfterWrite(1000, TimeUnit.MILLISECONDS)
                .maximumSize(20000)
                .build();


        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                logger.info("cache status:{}", memCache.stats().toString());
            } catch (Exception ex) {
                //ignore
            }

        }, 0, 30, TimeUnit.SECONDS);
    }

    @Around(value = "@annotation(cache)")
    public Object cache(ProceedingJoinPoint joinPoint, Cache cache) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodStr = method.toString();

        ActionInfo ai = actionContext.getActionInfos().get(methodStr);
        if (ai == null || !ai.isCache()) {
            return joinPoint.proceed();
        }

        String packageName = joinPoint.getSignature().getDeclaringTypeName();

        Object[] o = joinPoint.getArgs();
        String traceId = TraceId.getTraceId(o);

        String[] indexs = cache.paramIndex();

        String paramKey = "";


        if (indexs.length == 1 && indexs[0].equals("")) {
            if (o.length > 0) {
                Gson gson = new Gson();
                paramKey = Stream.of(o).map(it -> gson.toJson(it)).collect(Collectors.joining("__"));
            }
        } else {
            Gson gson = new Gson();
            paramKey = Stream.of(indexs).mapToInt(it -> Integer.parseInt(it)).mapToObj(it -> {
                String v = gson.toJson(o[it]);
                return v;
            }).collect(Collectors.joining("__"));
        }

        Class returnType = method.getReturnType();
        String key = cache.key();
        if (cache.key().equals("")) {
            key = packageName + "_" + method.getName();
        }

        key = key + "|$%|" + paramKey;

        try {
            if (cache.cacheType().equals(CacheType.Mem)) {
                Object v = memCache.getIfPresent(key);
                if (null != v) {
                    logger.info("cache_aop traceId:{} finish get from cache(mem) key:{}", traceId, key);
                    return v;
                }
            } else if (cache.cacheType().equals(CacheType.Redis)) {
                byte[] value = redis.getBytes(key);
                if (null != value) {
                    logger.info("cache_aop traceId:{} finish get from cache(reids) key:{}", traceId, key);
                    return HessianUtils.read(value);
                }
            }

            Object result = joinPoint.proceed();

            if (cache.cacheType().equals(CacheType.Mem)) {
                if (null != key && null != result) {
                    memCache.put(key, result);
                }
            } else if (cache.cacheType().equals(CacheType.Redis)) {
                if (null != key && null != result) {
                    redis.set(key, HessianUtils.write(result), cache.time());
                }
            }

            return result;
        } catch (HessianProtocolException e) {
            logger.warn("cache_aop finish key:{} error:{}", key, e.getMessage());
            if (null != key && cache.cacheType().equals(CacheType.Redis)) {
                redis.del(key);
            }
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.warn("cache_aop finish key:{} error:{}", key, throwable.getMessage());
            throw throwable;
        }
    }

}
