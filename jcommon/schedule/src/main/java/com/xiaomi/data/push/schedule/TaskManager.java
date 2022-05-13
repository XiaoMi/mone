package com.xiaomi.data.push.schedule;

import com.google.common.util.concurrent.*;
import com.google.gson.Gson;
import com.xiaomi.data.push.common.PushService;
import com.xiaomi.data.push.common.ScheduleVersion;
import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.Task;
import com.xiaomi.data.push.dao.model.TaskExample;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.schedule.task.*;
import com.xiaomi.data.push.schedule.task.notify.Notify;
import com.xiaomi.data.push.service.TaskService;
import com.xiaomi.youpin.cron.CronExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class TaskManager implements ApplicationContextAware, PushService {

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);
    private ThreadPoolExecutor pool;
    private ThreadPoolExecutor callbackPool;
    private ListeningExecutorService listeningExecutor;
    private SimpleTimeLimiter timeLimiter;
    private ApplicationContext ac;

    @Value("${server.debug}")
    private boolean debug;

    @Value("${michedule_group}")
    private int micheduleGroup;


    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ServerContext serverContext;

    @Autowired
    private TaskCacheUpdater taskCacheUpdater;

    public TaskManager() {
        logger.info("version:{}", new ScheduleVersion());
        this.pool = new ThreadPoolExecutor(500, 500, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(50000));
        this.callbackPool = new ThreadPoolExecutor(500, 500, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(50000));
        this.listeningExecutor = MoreExecutors.listeningDecorator(this.pool);
        this.timeLimiter = SimpleTimeLimiter.create(Executors.newCachedThreadPool());
    }

    public void schedule() {
        int poolSize = this.pool.getQueue().size();
        if (poolSize > 1000) {
            logger.warn("task manager poolSize={}", poolSize);
        }

    }

    public void shutdown() {
        logger.info("TaskManager shutdown begin");
        this.pool.shutdown();

        try {
            int size = this.pool.getQueue().size() + this.pool.getActiveCount();
            logger.info("shutdown queue size+activie size=" + size);
            this.pool.awaitTermination((long) (size * 30), TimeUnit.SECONDS);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }

        if (this.serverContext.isLeader()) {
            TaskExample example = new TaskExample();
            example.createCriteria().andStatusEqualTo(TaskStatus.Running.code);
            List<Task> tasks = this.taskMapper.selectByExample(example);
            logger.info("TaskManager shutdown running task  num:{}", tasks.size());
            tasks.forEach((it) -> {
                this.taskService.updateTask(it.getId(), (task) -> {
                    if (task.getStatus() == TaskStatus.Running.code) {
                        task.setStatus(TaskStatus.Retry.code);
                        return true;
                    } else {
                        return false;
                    }
                });
            });
        }

        logger.info("TaskManager shutdown finish");
    }

    public void submitTask(TaskParam taskParam, TaskContext taskContext, boolean run) {
        long now = System.currentTimeMillis();

        long nextRetryTime = now + TimeUnit.SECONDS.toMillis(5L);
        if (!run && 0 != taskParam.getExecuteTime()) {
            nextRetryTime = taskParam.getExecuteTime();
        } else {
            if (!StringUtils.isEmpty(taskParam.getCron())) {
                try {
                    nextRetryTime = new CronExpression(taskParam.getCron()).getNextValidTimeAfter(new Date()).getTime();
                } catch (Exception e) {
                    logger.warn("CronExpression error task :{} error:{}", taskParam, e.getMessage());
                }
            }
        }

//        if (!StringUtils.isEmpty(taskParam.getCron())) {
//            try {
//                nextRetryTime = new CronExpression(taskParam.getCron()).getNextValidTimeAfter(new Date()).getTime();
//            } catch (Exception e) {
//                logger.warn("error:{}", e.getMessage());
//            }
//        } else {
//            if (!run && 0 != taskParam.getExecuteTime()) {
//                nextRetryTime = taskParam.getExecuteTime();
//            }
//        }

        this.submitTask(taskParam, taskContext, run, nextRetryTime, false);
    }


    public void modifyTaskCache(String id, String request, String context, String status) {
        taskCacheUpdater.updateTask(Integer.valueOf(id), request, context, Integer.valueOf(status));
    }

    public void submitTask(TaskParam taskParam, TaskContext taskContext, boolean run, long nextRetryTime, boolean debug) {
        long now = System.currentTimeMillis();
        log.info("taskParam{}", taskParam.toString());
        TaskWithBLOBs tb = new TaskWithBLOBs();
        tb.setName(taskParam.getTaskDef().getName());
        tb.setVersion(0);
        tb.setErrorRetryNum(0);
        tb.setRetryNum(0);
        tb.setParams((new Gson()).toJson(taskParam));
        tb.setContext((new Gson()).toJson(taskContext));

        log.info("context{}", tb.getContext().toString());
        tb.setParentId(-1);
        tb.setCreated(now);
        tb.setCreator(taskParam.getCreator());
        tb.setAlarmUsername(taskParam.getAlarmUsername());
        tb.setRoleId(taskParam.getRoleId());
        tb.setGid(taskParam.getGid());
        if (null == tb.getGid()) {
            tb.setGid(0);
        }
        tb.setUpdated(System.currentTimeMillis());
        tb.setSuccessNum(0);
        tb.setFailureNum(0);
        tb.setScheduleGroup(micheduleGroup);
        tb.setNextRetryTime(nextRetryTime);
        tb.setType(taskParam.getTaskDef().getType());
        tb.setTimeout(taskParam.getTimeout());

        tb.setBid(taskParam.getBizId());

        if (run) {
            tb.setStatus(TaskStatus.Running.code);
            if (!debug) {
                this.taskMapper.insert(tb);
            }
            taskParam.setTaskId(tb.getId());
            this.doTask(taskParam, taskContext);
        } else {
            tb.setStatus(TaskStatus.Retry.code);
            if (!debug) {
                this.taskMapper.insert(tb);
            }
            taskParam.setTaskId(tb.getId());
        }
        logger.info("submitTask taskId:{} {}", tb.getId(), run);
    }


    private long getTimeout(final TaskParam taskParam) {
        long timeout = taskParam.getTimeout() != 0L ? taskParam.getTimeout() : taskParam.getTaskDef().getTimeOut();
        return timeout;
    }


    public void doTask(final TaskParam taskParam, final TaskContext taskContext) {
        logger.info("doTask taskId:{}", taskParam.getTaskId());
        //注入进去通知机制
        setNofity(taskParam, taskContext);
        //初始化interceptor
        initInterceptor(taskParam, taskContext);

        final ITask itask = (ITask) this.ac.getBean(taskParam.getTaskDef().getType());

        try {

            ListenableFuture<TaskResult> future = this.listeningExecutor.submit(() ->
                this.timeLimiter.callWithTimeout(() -> {
                    itask.beforeTask(taskParam, taskContext);
                    TaskResult result = itask.execute(taskParam, taskContext);
                    itask.afterTask(taskContext, result);
                    return result;
                }, this.debug ? TimeUnit.HOURS.toMillis(1L) : getTimeout(taskParam), TimeUnit.MILLISECONDS));
            Futures.addCallback(future, new FutureCallback<TaskResult>() {
                @Override
                public void onSuccess(TaskResult result) {
                    if (result.getCode() == TaskStatus.Success.code) {
                        TaskManager.logger.info("task success:{}", result);
                        itask.onSuccess(taskParam.getTaskId(), taskParam, taskContext, result);
                    } else if (result.getCode() == TaskStatus.Retry.code) {
                        TaskManager.logger.info("task retry:{}", result);
                        itask.onRetry(taskParam.getTaskId(), taskParam, taskContext, result);
                    } else if (result.getCode() == TaskStatus.Failure.code) {
                        TaskManager.logger.info("task failure:{}", result);
                        itask.onFailure(taskParam.getTaskId(), taskParam, taskContext, result);
                    }

                }

                @Override
                public void onFailure(Throwable throwable) {
                    TaskManager.logger.error("task error:" + taskParam.getTaskId() + " " + throwable.getMessage(), throwable);
                    itask.onFailure(taskParam.getTaskId(), taskParam, taskContext, TaskResult.Failure(new String[]{throwable.getMessage()}));
                }
            }, this.callbackPool);
        } catch (Throwable ex) {
            logger.error("do Task:{} error:{}", taskParam.getTaskId(), ex.getMessage());
            itask.onRetry(taskParam.getTaskId(), taskParam, taskContext, TaskResult.Retry());
        }
    }

    private void initInterceptor(TaskParam taskParam, TaskContext context) {
        String interceptor = taskParam.getParam().get(TaskContext.INTERCEPTOR);
        if (!org.apache.commons.lang.StringUtils.isEmpty(interceptor)) {
            context.put(TaskContext.INTERCEPTOR, interceptor);
        } else {
            //没有就清空,防止对参数的二次修改
            context.remove(TaskContext.INTERCEPTOR);
        }
    }

    private void setNofity(TaskParam taskParam, TaskContext taskContext) {
        if (StringUtils.isNotEmpty(taskParam.getNotify())) {
            try {
                Notify notify = (Notify) ac.getBean(taskParam.getNotify());
                taskContext.setNotify(notify);
            } catch (Throwable ex) {
                logger.warn("ex:{}", ex.getMessage());
            }
        }
    }

    public void retryTask(int taskId) {
        TaskWithBLOBs taskBlob = this.taskMapper.selectByPrimaryKey(taskId);
        retryTask(taskBlob);
    }

    public void retryTask(TaskWithBLOBs taskBlob) {
        retryTask(taskBlob, false);
    }

    public void retryTask(TaskWithBLOBs taskBlob, boolean cache) {
        Integer taskId = null;
        if (null != taskBlob) {
            taskId = taskBlob.getId();
            TaskContext context = new Gson().fromJson(taskBlob.getContext(), TaskContext.class);
            if (null == context) {
                context = new TaskContext();
            }
            context.put(TaskContext.CACHE, String.valueOf(cache));

            TaskParam taskParam = new Gson().fromJson(taskBlob.getParams(), TaskParam.class);
            taskParam.setTaskId(taskId);
            this.doTask(taskParam, context);
        } else {
            logger.error("retry task task is null taskId:{}", taskId);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    /**
     * 暂停任务
     */
    public boolean pause(int id) {
        boolean success = taskService.updateTask(id, (task) -> {
            task.setStatus(TaskStatus.Pause.code);
            task.setUpdated(System.currentTimeMillis());
            return true;

        }, 10, "pause");

        logger.info("pause taskService.updateTask with id:{} success:{}", id, success);
        return success;
    }

    /**
     * 启动任务
     */
    public boolean start(int id) {
        boolean success = taskService.updateTask(id, (task) -> {
            if (task.getStatus().equals(TaskStatus.Retry.code) || task.getStatus().equals(TaskStatus.Running.code)) {
                return false;
            }
            task.setStatus(TaskStatus.Retry.code);
            /**
             * 每次拉起任务重置失败计数
             **/
            task.setErrorRetryNum(0);
            task.setUpdated(System.currentTimeMillis());
            return true;

        }, 50, "start");

        logger.info("start taskService.updateTask with id:{} success:{}", id, success);
        return success;
    }

    /**
     * 修改参数
     *
     * @param id
     * @param params
     */
    public void modifyParam(Integer id, String params) {
        boolean res = taskService.updateTask(id, (task) -> {
            TaskParam taskParam = new Gson().fromJson(params, TaskParam.class);
            TaskParam oldTaskParams = new Gson().fromJson(task.getParams(), TaskParam.class);
            if (taskParam.getExecuteTime() != 0) {
                task.setNextRetryTime(taskParam.getExecuteTime());
            } else {
                if (!oldTaskParams.getCron().equals(taskParam.getCron())) {
                    try {
                        long nextRetryTime = new CronExpression(taskParam.getCron()).getNextValidTimeAfter(new Date()).getTime();
                        task.setNextRetryTime(nextRetryTime);
                    } catch (ParseException e) {
                        log.warn("modify cron error:{}", taskParam);
                    }
                }
            }

            /**
             * 修改参数以后把失败重试的计数重置了
             * fix:已经达到失败上限的任务修改了还需要修改TaskDef里面的失败重试上限 才能让任务重新拉起来
             * 保证一次任务失败次数上限约束只作用于一次修改中
             */
            task.setErrorRetryNum(0);
            task.setParams(params);
            task.setGid(taskParam.getGid());
            task.setAlarmUsername(taskParam.getAlarmUsername());
            task.setBid(taskParam.getBizId());
            if (task.getGid() == null) {
                task.setGid(0);
            }

            task.setUpdated(System.currentTimeMillis());
            return true;
        }, 10, "modify_param");

        logger.info("start taskService.modify with id:{} success:{}", id, res);
    }


}
