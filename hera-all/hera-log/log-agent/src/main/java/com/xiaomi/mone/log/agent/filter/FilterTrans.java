/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.filter;

import com.xiaomi.mone.log.api.filter.Common;
import com.xiaomi.mone.log.api.filter.RateLimitStrategy;
import com.xiaomi.mone.log.api.model.meta.FilterConf;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import com.xiaomi.mone.log.api.model.meta.FilterName;

import java.util.Map;

/**
 * @author milog
 */
public class FilterTrans {
    public static FilterConf filterConfTrans(FilterDefine define) {
        if (define == null) {
            return null;
        }
        if (define.getCode() != null) {
            if (define.getCode().startsWith(Common.RATE_LIMIT_CODE)) {
                RateLimitStrategy rateLimiterStrategy = RateLimitStrategy.getRateLimiterStrategy(define.getCode());
                if (rateLimiterStrategy != null) {
                    return consFilterConf(FilterName.RATELIMITER, rateLimiterStrategy, define.getArgs());
                }
            }
        }
        return null;
    }

    private static FilterConf consFilterConf(FilterName name, RateLimitStrategy strategy, Map<String, String> args) {
        return new FilterConf(strategy.getCode(), name, strategy.getType(), strategy.getOrder(), strategy.getLifecycle(), args);
    }
}
