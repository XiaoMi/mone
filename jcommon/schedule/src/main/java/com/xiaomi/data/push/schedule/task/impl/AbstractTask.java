package com.xiaomi.data.push.schedule.task.impl;

import com.google.gson.Gson;
import com.xiaomi.data.push.client.Pair;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.common.TimeUtils;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.dto.AlertEventDto;
import com.xiaomi.data.push.schedule.TaskCacheUpdater;
import com.xiaomi.data.push.schedule.task.*;
import com.xiaomi.data.push.service.FalconService;
import com.xiaomi.data.push.service.FeiShuCommonService;
import com.xiaomi.data.push.service.TaskService;
import com.xiaomi.youpin.cron.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangzhiyong
 * @date 29/05/2018
 */
public abstract class AbstractTask implements ITask, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTask.class);

    @Autowired
    private TaskService taskService;


    @Autowired
    private TaskCacheUpdater taskCacheUpdater;

    @Autowired
    private TaskMapper taskMapper;

    @Value("${server.type}")
    private String serverType;
    @Value("${task.detail.url}")
    private String TASK_DETAIL_URL;

    @Autowired
    private FeiShuCommonService feiShuService;
    @Autowired
    private FalconService falconService;

    protected ApplicationContext ac;

    private ExecutorService msgPool = Executors.newCachedThreadPool();


    @Override
    public void beforeTask(TaskParam param, TaskContext context) {
        TaskInterceptor taskInterceptor = null;
        //任务都可以指定拦截器
        String interceptor = context.get("interceptor");
        if (!StringUtils.isEmpty(interceptor)) {
            taskInterceptor = (TaskInterceptor) ac.getBean(interceptor);
        }
        if (taskInterceptor != null) {
            taskInterceptor.beforeTask(param, context);
        }
    }


    @Override
    public void afterTask(TaskContext context, TaskResult result) {
        TaskInterceptor taskInterceptor = null;
        //任务都可以指定拦截器
        String interceptor = context.get(TaskContext.INTERCEPTOR);
        if (!StringUtils.isEmpty(interceptor)) {
            taskInterceptor = (TaskInterceptor) ac.getBean(interceptor);
        }
        if (taskInterceptor != null) {
            taskInterceptor.afterTask(result);
        }
    }


    //下一次执行任务的时间
    public Long getNextRetryTime() {
        return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
    }

    //出现错误后下次执行任务的时间
    public Long getNextErrorRetryTime() {
        return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
    }


    @Override
    public void onRetry(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        logger.info("task onRetry id:{}", id);
        if (taskContext.getString("update").equals("false")) {
            return;
        }
        taskService.updateTask(id, task -> {
            task.setContext(new Gson().toJson(taskContext));
            task.setResult(new Gson().toJson(taskResult));
            task.setNextRetryTime(getNextRetryTime());
            //重置错误的重试次数(只要有一个周期正常了,错误次数就被重置)
            task.setErrorRetryNum(0);
            task.setRetryNum(task.getRetryNum() + 1);
            if (!task.getStatus().equals(TaskStatus.Pause.code)) {
                task.setStatus(TaskStatus.Retry.code);
            }
            return true;
        }, taskContext, taskResult, TaskStatus.Retry.code, 5, "");
    }

    @Override
    public void onSuccess(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        logger.info("task on success id:{}", id);
        //不需要更新到数据库
        if (!taskContext.getString(TaskContext.UPDATE).equals(TaskContext.FALSE)) {
            Pair<TaskStatus, Long> pair = getStatusAndNextRetryTime(taskParam, taskContext);

            int status = pair.getKey().code;

            boolean res = taskService.updateTask(id, task -> {
                long now = System.currentTimeMillis();
                task.setContext(new Gson().toJson(taskContext));
                task.setResult(new Gson().toJson(taskResult));
                task.setNextRetryTime(pair.getValue());
                setStatus(pair, task);
                if (null == task.getSuccessNum()) {
                    task.setSuccessNum(0);
                }
                if (null == task.getFailureNum()) {
                    task.setFailureNum(0);
                }
                task.setSuccessNum(task.getSuccessNum() + 1);
                task.setUpdated(now);
                //重试的错误数被强制设置为0
                task.setErrorRetryNum(0);
                taskContext.setTaskData(task);
                return true;
            }, taskContext, taskResult, status, 50, "");

            //没有更新成功,开启强制更新
            if (!res) {
                try {
                    logger.info("put task on cache id:{}", id);
                    TaskWithBLOBs task = this.taskMapper.selectByPrimaryKey(id);
                    setStatus(pair, task);
                    task.setSuccessNum(task.getSuccessNum() + 1);
                    task.setNextRetryTime(pair.getValue());
                    taskCacheUpdater.putTask(task);
                } catch (Throwable ex) {
                    logger.error(ex.getMessage());
                }
            }
        }

        invokeInterceptor(id, taskParam, taskContext, taskResult);
    }

    private void setStatus(Pair<TaskStatus, Long> pair, TaskWithBLOBs task) {
        //没被暂停的情况下
        if (!task.getStatus().equals(TaskStatus.Pause.code)) {
            task.setStatus(pair.getKey().code);
        }
    }

    private void invokeInterceptor(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        if (!StringUtils.isEmpty(taskContext.get(TaskContext.INTERCEPTOR))) {
            String interceptor = taskContext.get(TaskContext.INTERCEPTOR);
            try {
                TaskInterceptor ti = (TaskInterceptor) ac.getBean(interceptor);
                ti.onSuccess(id, taskParam, taskContext, taskResult);
            } catch (Throwable ex) {
                logger.warn("task:{} on success invoke interceptor ex:{}", id, ex.getMessage());
            }
        }
    }

    private Pair<TaskStatus, Long> getStatusAndNextRetryTime(TaskParam taskParam, TaskContext taskContext) {
        TaskStatus status = TaskStatus.Success;
        long nextRetryTime = 0L;
        //周期执行的
        if (null != taskParam.getCron() && !taskParam.getCron().equals("") && !taskContext.isOnce()) {
            status = TaskStatus.Retry;
            try {
                //计算出下次要执行的时间
                nextRetryTime = new CronExpression(taskParam.getCron()).getNextValidTimeAfter(new Date()).getTime();
            } catch (ParseException e) {
                logger.warn("parse cron:{} ex:{}", taskParam.getCron(), e.getMessage());
            }
        }
        nextRetryTime += taskContext.getDelay();
        taskContext.setDelay(0L);

        return Pair.of(status, nextRetryTime);
    }

    @Override
    public void onFailure(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        logger.info("task on failure id:{}", id);
        if (!taskContext.getString(TaskContext.UPDATE).equals("false")) {
            taskService.updateTask(id, task -> {
                long now = System.currentTimeMillis();
                boolean b = TimeUtils.moreThanOneHour(task.getUpdated());
                if (b) {
                    task.setSuccessNum(0);
                    task.setFailureNum(0);
                }
                Pair<TaskStatus, Long> pair = getStatusAndNextRetryTime(taskParam, taskContext);
                task.setContext(new Gson().toJson(taskContext));
                task.setResult(new Gson().toJson(taskResult));
                //即使失败也不再拉起重试(dubboTask任务可配置自身的重试、httpTask不重试
                task.setNextRetryTime(pair.getValue());
                task.setErrorRetryNum(task.getErrorRetryNum() + 1);
                boolean pause = false;
                //没被暂停的情况下
                if (!task.getStatus().equals(TaskStatus.Pause.code)) {
                    //错误次数过多(若未设置 忽略失败，则不再重试了,而是直接返回失败)
                    if (task.getErrorRetryNum() >= taskParam.getTaskDef().getErrorRetryNum()) {
                        pause = true;
                        if (!task.getIgnoreError()){
                            task.setStatus(TaskStatus.Failure.code);
                        }
                    } else {
                        task.setStatus(TaskStatus.Retry.code);
                    }
                }
                alarm(id, taskParam, taskResult, pause);
                task.setFailureNum(task.getFailureNum() + 1);
                task.setUpdated(now);
                taskContext.setTaskData(task);
                return true;
            }, taskContext, taskResult, TaskStatus.Failure.code, 50, "");

        }

        if (!StringUtils.isEmpty(taskContext.get(TaskContext.INTERCEPTOR))) {
            String interceptor = taskContext.get(TaskContext.INTERCEPTOR);
            try {
                TaskInterceptor ti = (TaskInterceptor) ac.getBean(interceptor);
                ti.onFailure(id, taskParam, taskContext, taskResult);
            } catch (Throwable ex) {
                logger.warn("task:{} on failure invoke interceptor ex:{}", id, ex.getMessage());
            }
        }
    }

    /**
     * 任务异常暂停通知
     * @param id
     * @param taskParam
     * @param taskResult
     */
    private void alarm(int id, TaskParam taskParam, TaskResult taskResult, boolean pauseFlag){
        msgPool.execute(() -> {
            SafeRun.run(()->{
                logger.info("AbstractTask.alarm id:{}任务已暂停，超过失败重试次数{}，本次调用结果:{},alarmGroup:{},pauseFlag:{}", id, taskParam.getTaskDef().getErrorRetryNum(), taskResult, taskParam.getAlarmGroup(),pauseFlag);
                String title = (serverType.equals("c3") || serverType.equals("c4") || serverType.equals("online"))?
                        "【online-mischedule告警】":"【st-mischedule告警】";
                String desc = pauseFlag?"id:" + id + "任务已暂停，超过失败重试次数" + taskParam.getTaskDef().getErrorRetryNum():"id:" + id + "任务执行失败";
                String timestamp = TimeUtils.getCurrentTime();
                if (!StringUtils.isEmpty(taskParam.getAlarmGroup())){
                    AlertEventDto.Meta meta = AlertEventDto.Meta.builder()
                            .errorRetryNum(taskParam.getTaskDef().getErrorRetryNum())
                            .id(id)
                            .title(title)
                            .url(TASK_DETAIL_URL + taskParam.getTaskId())
                            .summary(desc)
                            .timestamp(timestamp)
                            .taskResult(new Gson().toJson(taskResult)).build();
                    falconService.alert(taskParam, meta);
                    return;
                }
                String alarmUsername = StringUtils.isEmpty(taskParam.getAlarmUsername())?taskParam.getCreator():taskParam.getAlarmUsername();
                feiShuService.batchSendMsg(alarmUsername,
                        title + timestamp + "，" + desc + "，本次调用结果:" + new Gson().toJson(taskResult));
            });
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
