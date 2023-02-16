package com.xiaomi.mone.log.api.enums;

import com.xiaomi.mone.log.api.filter.Common;
import com.xiaomi.mone.log.api.filter.RateLimitStrategy;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/2/22 15:09
 */
@Getter
public enum RateLimitEnum {

    RATE_LIMIT_FAST("FAST"),
    RATE_LIMIT_MEDIUM("MEDIUM"),
    RATE_LIMIT_SLOW("SLOW"),
    RATE_LIMIT_NONE("NONE");

    private final String rateLimit;

    RateLimitEnum(String rateLimit) {
        this.rateLimit = rateLimit;
    }

    public static RateLimitEnum queryByRateLimit(String rateLimit) {
        return Arrays.stream(RateLimitEnum.values()).filter(rateLimitEnum -> rateLimit.equals(rateLimitEnum.getRateLimit())).findFirst().orElse(null);
    }


    public static String consTailRate(List<FilterDefine> defines) {
        // 默认 MEDIUM 速度采集
        if (defines == null) {
            return RATE_LIMIT_MEDIUM.getRateLimit();
        }
        for (FilterDefine define : defines) {
            if (define != null && define.getCode() != null && define.getCode().startsWith(Common.RATE_LIMIT_CODE)) {
                if (define.getCode().equals(RateLimitStrategy.REGINAL_FAST.getCode())) {
                    return RATE_LIMIT_FAST.getRateLimit();
                } else if (define.getCode().equals(RateLimitStrategy.REGINAL_MEDIUM.getCode())) {
                    return RATE_LIMIT_MEDIUM.getRateLimit();
                } else if (define.getCode().equals(RateLimitStrategy.REGINAL_SLOW.getCode())) {
                    return RATE_LIMIT_SLOW.getRateLimit();
                } else if (define.getCode().equals(RateLimitStrategy.REGINAL_NONE.getCode())) {
                    return RATE_LIMIT_NONE.getRateLimit();
                }
            }
        }
        return RATE_LIMIT_MEDIUM.getRateLimit();
    }

}
