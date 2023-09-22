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
package com.xiaomi.mone.log.api.model.meta;

import com.xiaomi.mone.log.api.enums.RateLimitEnum;
import com.xiaomi.mone.log.api.filter.Common;
import com.xiaomi.mone.log.api.filter.RateLimitStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to transfer filter parameters between manage and agent
 *
 * @author milog
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterDefine implements Serializable {
    /**
     * One-to-one correspondence with the code in Filter Conf
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