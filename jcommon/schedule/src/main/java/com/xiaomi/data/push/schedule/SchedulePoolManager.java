package com.xiaomi.data.push.schedule;

import com.xiaomi.data.push.action.ActionContext;
import com.xiaomi.data.push.conf.Config;
import com.xiaomi.data.push.service.state.Fsm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangzhiyong
 * @date 13/06/2018
 */
@Component
public class SchedulePoolManager {

    private static final Logger logger = LoggerFactory.getLogger(SchedulePoolManager.class);

    @Autowired
    private TaskRecover taskRecover;

    @Autowired
    private TaskFinder taskFinder;

    @Autowired
    private TaskDeleter taskDeleter;


    @Autowired
    private TaskCacheUpdater taskCacheUpdater;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private ActionContext actionContext;

    @Autowired
    private Fsm fsm;

    @Autowired
    private Config config;

    @Value("${support.task}")
    private boolean supportTask;

    @Value("${support.action}")
    private boolean supportAction;


    @PostConstruct
    public void init() {
        int num = 1;
        if (supportTask) {
            num += 5;
        }
        if (supportAction) {
            num += 1;
        }
        ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(num);
        if (supportTask) {
            pool.scheduleWithFixedDelay(() -> safeRun(() -> taskRecover.schedule()), 5, 5, TimeUnit.SECONDS);
            pool.scheduleWithFixedDelay(() -> safeRun(() -> taskFinder.schedule()), 5, 2, TimeUnit.SECONDS);
            pool.scheduleWithFixedDelay(() -> safeRun(() -> taskDeleter.schedule()), 5, 30, TimeUnit.HOURS);
            pool.scheduleWithFixedDelay(() -> safeRun(() -> taskManager.schedule()), 5, 1, TimeUnit.SECONDS);
            pool.scheduleWithFixedDelay(() -> safeRun(() -> taskCacheUpdater.schedule()), 5, 1, TimeUnit.SECONDS);
        }
        pool.scheduleWithFixedDelay(() -> safeRun(() -> fsm.schedule()), 0, 4, TimeUnit.SECONDS);
        if (supportAction) {
            pool.scheduleWithFixedDelay(() -> safeRun(() -> actionContext.schedule()), 0, 10, TimeUnit.SECONDS);
        }
    }


    private void safeRun(Runnable run) {
        try {
            run.run();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
