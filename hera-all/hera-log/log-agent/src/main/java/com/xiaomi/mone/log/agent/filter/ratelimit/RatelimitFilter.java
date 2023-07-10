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
package com.xiaomi.mone.log.agent.filter.ratelimit;

import com.google.common.util.concurrent.RateLimiter;
import com.xiaomi.mone.log.agent.filter.Invoker;
import com.xiaomi.mone.log.agent.filter.MilogFilter;
import com.xiaomi.mone.log.api.filter.Common;
import com.xiaomi.mone.log.api.model.meta.FilterConf;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 限制log agent 发送日志速率
 */
@Slf4j
public class RatelimitFilter implements MilogFilter {
    private RateLimiter rateLimiter;

    @Override
    public void doFilter(Invoker invoker) {
        this.rateLimiter.acquire();
        if (invoker != null) {
            invoker.doInvoker();
        } else {
            log.error("do filter but next invoker is null");
        }
    }

    @Override
    public boolean init(FilterConf conf) {
        Map<String, String> args = conf.getArgs();
        if (args != null) {
            String ppsStr = args.get(Common.PERMITS_PER_SECOND);
            try {
                int pps = Integer.parseInt(ppsStr);
                this.rateLimiter = RateLimiter.create(pps);
                return true;
            } catch (Exception e) {
                log.error("init rateLimit err PERMITSPERSECOND:{},FilterConf:{}", ppsStr, conf, e);
            }
        } else {
            log.warn("wrong rateLimit args FilterConf:{}", conf);
        }
        return false;
    }
}
