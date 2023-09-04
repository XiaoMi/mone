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
package com.xiaomi.mone.log.manager.job;

import cn.hutool.core.thread.ThreadUtil;
import com.xiaomi.mone.log.manager.service.impl.LogCountServiceImpl;
import com.xiaomi.mone.log.utils.DateUtils;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TailLogCountJob {
    @Resource
    private LogCountServiceImpl logCountService;

    @Value("$job_start_flag")
    public String jobStartFlag;

    public void init() {
        if (!Boolean.parseBoolean(jobStartFlag)) {
            return;
        }
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
                ThreadUtil.newNamedThreadFactory("log-tailLogCountJob", false)
        );
        long initDelay = 0;
        long intervalTime = 1;
        scheduledExecutor.scheduleAtFixedRate(() -> {
            statisticsAll();
        }, initDelay, intervalTime, TimeUnit.HOURS);
    }

    public void statisticsAll() {
        try {
            log.info("统计日志定时任务开始执行");
            String thisDay = DateUtils.getDaysAgo(1);
            if (!logCountService.isLogtailCountDone(thisDay)) {
                logCountService.collectLogCount(thisDay);
                logCountService.deleteHistoryLogCount();
            }
            logCountService.collectTopCount();
            logCountService.collectSpaceTopCount();
            logCountService.collectSpaceTrend();
            log.info("统计日志定时任务执行完毕");
        } catch (Exception e) {
            log.error("统计日志定时任务失败", e);
        }
    }
}
