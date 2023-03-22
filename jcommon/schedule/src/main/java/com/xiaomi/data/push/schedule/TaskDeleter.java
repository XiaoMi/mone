package com.xiaomi.data.push.schedule;

import com.xiaomi.data.push.common.PushService;
import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.TaskExample;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class TaskDeleter implements PushService {

    private volatile boolean shutdown = false;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ServerContext serverContext;

    private static final long day = 2L;

    public TaskDeleter() {
    }

    public void schedule() {
        if (!this.shutdown && this.serverContext.isLeader()) {
            log.info("delete old task");
            long now = System.currentTimeMillis();
            TaskExample example = new TaskExample();
            example.createCriteria().andStatusEqualTo(TaskStatus.Success.code).andUpdatedLessThan(now - TimeUnit.DAYS.toMillis(day));
            int n = this.taskMapper.deleteByExample(example);
            log.info("delete old task size:{} day:{}", n, day);
        }
    }

    public int deleteTaskById(int id) {
        //TaskExample example = new TaskExample();
        int len = this.taskMapper.deleteByPrimaryKey(id);
        log.info("delete task id:{},len:{}",id,len);
        return len;
    }

    public void shutdown() {
        log.info("taskDeleter shutdown begin");
        this.shutdown = true;
        log.info("taskDeleter shutdown finish");
    }
}
