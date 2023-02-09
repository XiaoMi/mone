package com.xiaomi.data.push.service;

import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.TaskExample;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.schedule.TaskCacheUpdater;
import com.xiaomi.data.push.schedule.TaskManager;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskManager taskManager;


    @Autowired(required = false)
    private RpcClient rpcClient;


    @Autowired
    private ServerContext serverContext;


    @Autowired
    private TaskCacheUpdater taskCacheUpdater;

    public TaskService() {
    }

    public int insertTask(int parentId, String params, String context, String name, TaskStatus status, int group) {
        TaskWithBLOBs task = new TaskWithBLOBs();
        task.setParams(params);
        task.setContext(context);
        task.setParentId(parentId);
        task.setVersion(0);
        task.setNextRetryTime(0L);
        task.setErrorRetryNum(0);
        task.setFailureNum(0);
        task.setSuccessNum(0);
        task.setRetryNum(0);
        long now = System.currentTimeMillis();
        task.setCreated(now);
        task.setUpdated(now);
        task.setName(name);
        task.setScheduleGroup(group);
        task.setStatus(status.code);
        this.taskMapper.insert(task);
        return task.getId();
    }


    public int batchUpdateStatus(int status, long now, int micheduleGroup) {
        TaskWithBLOBs blob = new TaskWithBLOBs();
        TaskExample taskExample = new TaskExample();
        taskExample.createCriteria()
                .andScheduleGroupEqualTo(micheduleGroup)
                .andStatusEqualTo(TaskStatus.Retry.code).andNextRetryTimeLessThan(now);
        blob.setStatus(status);
        blob.setUpdated(now);
        return this.taskMapper.updateByExampleSelective(blob, taskExample);
    }


    private static final int maxUpdateNum = 3;


    public boolean updateTask(int id, Function<TaskWithBLOBs, Boolean> function) {
        return updateTask(id, function, null, null, null, -1, "");
    }

    public boolean updateTask(int id, Function<TaskWithBLOBs, Boolean> function, int retryNum, String source) {
        return updateTask(id, function, null, null, null, retryNum, source);
    }


    public boolean updateTask(int id, Function<TaskWithBLOBs, Boolean> function, TaskContext context, TaskResult result, Integer status, int retryNum, String source) {
        int n = 0;
        if (retryNum == -1) {
            retryNum = maxUpdateNum;
        }

        do {
            try {
                TaskWithBLOBs task = this.taskMapper.selectByPrimaryKey(id);
                if (null == task) {
                    logger.error("task is null id:{}", id);
                    return false;
                }

                boolean b = function.apply(task);
                if (!b) {
                    return false;
                }

                int oldVersion = task.getVersion();

                if (null == task.getGid()) {
                    task.setGid(0);
                }

                task.setVersion(task.getVersion() + 1);
                task.setUpdated(System.currentTimeMillis());
                TaskExample taskExample = new TaskExample();
                taskExample.createCriteria().andIdEqualTo(id).andVersionEqualTo(oldVersion);
                int i = this.taskMapper.updateByExampleSelective(task, taskExample);
                if (i > 0) {
                    return true;
                }
                logger.info("update task fail id:{} version:{}", id, oldVersion);

                try {
                    TimeUnit.MILLISECONDS.sleep((new Random()).nextInt(200));
                } catch (InterruptedException var10) {
                }
            } catch (Throwable ex) {
                logger.warn("updateTask id:{} error:{} source:{}", id, ex.getMessage(), source);
            }

            ++n;
        } while (n < retryNum);

        logger.warn("updateTask failure n>={} taskId:{} source:{}", retryNum, id, source);
        return false;
    }

    public TaskWithBLOBs findTask(int id) {
        return this.taskMapper.selectByPrimaryKey(id);
    }
}
