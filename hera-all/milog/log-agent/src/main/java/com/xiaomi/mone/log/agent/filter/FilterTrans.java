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
