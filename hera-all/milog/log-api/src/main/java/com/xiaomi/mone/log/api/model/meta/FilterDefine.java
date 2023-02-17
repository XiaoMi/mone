package com.xiaomi.mone.log.api.model.meta;

import com.xiaomi.mone.log.api.enums.RateLimitEnum;
import com.xiaomi.mone.log.api.filter.Common;
import com.xiaomi.mone.log.api.filter.RateLimitStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于manage到agent 之间 filter 参数传输
 *
 * @author milog
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterDefine {
    /**
     * 和FilterConf中的code一一对应
     */
    private String code;

    private Map<String, String> args;

    public static FilterDefine Of(String code, Map<String, String> args) {
        return new FilterDefine(code, args);
    }

    public static FilterDefine consFilterDefine(RateLimitStrategy strategy) {
        return FilterDefine.Of(strategy.getCode(), new HashMap<String, String>() {{
            put(Common.PERMITS_PER_SECOND, String.valueOf(strategy.getPermitsPerSecond()));
        }});
    }

    public static FilterDefine consRateLimitFilterDefine(String rateLimit) {
        if (rateLimit == null) {
            return null;
        }
        RateLimitEnum rateLimitEnum = RateLimitEnum.queryByRateLimit(rateLimit);
        switch (rateLimitEnum) {
            case RATE_LIMIT_FAST:
                return FilterDefine.consFilterDefine(RateLimitStrategy.REGINAL_FAST);
            case RATE_LIMIT_MEDIUM:
                return FilterDefine.consFilterDefine(RateLimitStrategy.REGINAL_MEDIUM);
            case RATE_LIMIT_SLOW:
                return FilterDefine.consFilterDefine(RateLimitStrategy.REGINAL_SLOW);
            case RATE_LIMIT_NONE:
                return FilterDefine.consFilterDefine(RateLimitStrategy.REGINAL_NONE);
            default:
                return null;
        }
    }

}