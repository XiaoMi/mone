package com.xiaomi.data.push.schedule;

import com.xiaomi.data.push.common.PushService;
import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.TaskExample;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import com.xiaomi.data.push.service.TaskService;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 *
 * 有问题的任务,尝试恢复
 */
@Component
@Slf4j
public class TaskRecover implements PushService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskService taskService;
    private volatile boolean shutdown = false;

    @Autowired
    private ServerContext serverContext;

    public TaskRecover() {
    }

    public void schedule() {
        this.recover();
    }

    public void recover() {
        if (!this.shutdown && this.serverContext.isLeader()) {
            log.info("task recover execute");
            long now = System.currentTimeMillis();
            TaskExample example = new TaskExample();
            example.createCriteria().andStatusEqualTo(TaskStatus.Running.code).andUpdatedLessThan(now - TimeUnit.HOURS.toMillis(3L));
            List<TaskWithBLOBs> tasks = this.taskMapper.selectByExampleWithBLOBs(example);
            if (tasks.size() > 0) {
                log.info("recover task size:{}", tasks.size());
            }

            Iterator var5 = tasks.iterator();

            while (var5.hasNext()) {
                TaskWithBLOBs task = (TaskWithBLOBs) var5.next();
                this.taskService.updateTask(task.getId(), (it) -> {
                    if (it.getStatus() != TaskStatus.Running.code) {
                        return false;
                    } else {
                        it.setStatus(TaskStatus.Retry.code);
                        it.setUpdated(now);
                        it.setNextRetryTime(now + TimeUnit.SECONDS.toMinutes(5L));
                        log.info("recover task id:{}", task.getId());
                        return true;
                    }
                });
            }

        }
    }

    public void shutdown() {
        log.info("taskRecover shutdown begin");
        this.shutdown = true;
        log.info("taskRecover shutdown finish");
    }
}
