package com.xiaomi.mone.log.api.filter;

import com.xiaomi.mone.log.api.model.meta.FilterType;
import lombok.Getter;

/**
 * @author milog
 * @version 1.0
 * @description
 * @date 2022/2/22 15:41
 */
@Getter
public enum RateLimitStrategy {
    /**
     * permitsPerSecond 每秒限流次数
     * 对于日志采集场景，限流条数 = permitsPerSecond * batchSize
     */
    REGINAL_FAST(Common.RATE_LIMIT_CODE + 0, FilterType.REGIONAL, 100, "", 1500),
    REGINAL_MEDIUM(Common.RATE_LIMIT_CODE + 1, FilterType.REGIONAL, 100, "", 100),
    REGINAL_SLOW(Common.RATE_LIMIT_CODE + 2, FilterType.REGIONAL, 100, "", 30),
    REGINAL_NONE(Common.RATE_LIMIT_CODE + 3, FilterType.REGIONAL, 100, "", 10),

    GLOBAL_FAST(Common.RATE_LIMIT_CODE + 4, FilterType.GLOBAL, 10, "", 50),
    GLOBAL_MEDIUM(Common.RATE_LIMIT_CODE + 5, FilterType.GLOBAL, 10, "", 20),
    GLOBAL_SLOW(Common.RATE_LIMIT_CODE + 6, FilterType.GLOBAL, 10, "", 10),
    GLOBAL_NONE(Common.RATE_LIMIT_CODE + 7, FilterType.GLOBAL, 10, "", 5);

    private String code;
    private Integer order;
    private FilterType type;
    private String lifecycle;
    private Integer permitsPerSecond;


    RateLimitStrategy(String code, FilterType type, Integer order, String lifecycle, Integer permitsPerSecond) {
        this.code = code;
        this.order = order;
        this.type = type;
        this.lifecycle = lifecycle;
        this.permitsPerSecond = permitsPerSecond;
    }

    public static RateLimitStrategy getRateLimiterStrategy(String code) {
        if (code == null) {
            return null;
        }
        switch (code) {
            case Common.RATE_LIMIT_CODE + 0:
                return REGINAL_FAST;
            case Common.RATE_LIMIT_CODE + 1:
                return REGINAL_MEDIUM;
            case Common.RATE_LIMIT_CODE + 2:
                return REGINAL_SLOW;
            case Common.RATE_LIMIT_CODE + 3:
                return REGINAL_NONE;
            case Common.RATE_LIMIT_CODE + 4:
                return GLOBAL_FAST;
            case Common.RATE_LIMIT_CODE + 5:
                return GLOBAL_MEDIUM;
            case Common.RATE_LIMIT_CODE + 6:
                return GLOBAL_SLOW;
            case Common.RATE_LIMIT_CODE + 7:
                return GLOBAL_NONE;
            default:
                return null;
        }
    }
}
