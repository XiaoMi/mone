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
