package com.xiaomi.mone.log.manager.job;

import com.xiaomi.mone.log.api.enums.ProjectSourceEnum;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wtt
 * @version 1.0
 * @description 获取mione所有的机器列表定时统计上下线
 * @date 2022/9/19 15:45
 */
public class OpentelneltryMachineJob {

    @Resource
    private LogTailServiceImpl logTailService;

    @Value("$job_start_flag")
    public String jobStartFlag;

    public void init() {
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        long initDelay = 0;
        long intervalTime = 1;
        if (Boolean.parseBoolean(jobStartFlag)) {
            scheduledExecutor.scheduleAtFixedRate(() -> {
                CompletableFuture<Void> chinaFuture = CompletableFuture.runAsync(() -> {
                    logTailService.casOttMachines(ProjectSourceEnum.ONE_SOURCE.getSource());
                });
                CompletableFuture<Void> youpinFuture = CompletableFuture.runAsync(() -> {
                    logTailService.casOttMachines(ProjectSourceEnum.TWO_SOURCE.getSource());
                });
                CompletableFuture.allOf(chinaFuture, youpinFuture).join();
            }, initDelay, intervalTime, TimeUnit.MINUTES);
        }
    }
}
