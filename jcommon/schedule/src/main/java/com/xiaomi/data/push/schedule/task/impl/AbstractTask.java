/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.schedule.task.impl;

import com.google.gson.Gson;
import com.xiaomi.data.push.client.Pair;
import com.xiaomi.data.push.common.TimeUtils;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.schedule.TaskCacheUpdater;
import com.xiaomi.data.push.schedule.task.*;
import com.xiaomi.data.push.service.TaskService;
import com.xiaomi.youpin.cron.CronExpression;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;
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


    protected ApplicationContext ac;


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
            task.setRetryNum(task.getRetryNum() + 1);
            if (!task.getStatus().equals(TaskStatus.Pause.code)) {
                task.setStatus(TaskStatus.Retry.code);
            }
            return true;
        }, taskContext, taskResult, TaskStatus.Retry.code);
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
                taskContext.setTaskData(task);
                return true;
            }, taskContext, taskResult, status);

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
                task.setContext(new Gson().toJson(taskContext));
                task.setResult(new Gson().toJson(taskResult));
                task.setNextRetryTime(getNextErrorRetryTime());
                task.setErrorRetryNum(task.getErrorRetryNum() + 1);
                //没被暂停的情况下
                if (!task.getStatus().equals(TaskStatus.Pause.code)) {
                    //错误次数过多(不再重试了,而是直接返回失败)
                    if (task.getErrorRetryNum() >= taskParam.getTaskDef().getErrorRetryNum()) {
                        task.setStatus(TaskStatus.Failure.code);
                    } else {
                        task.setStatus(TaskStatus.Retry.code);
                    }
                }

                task.setFailureNum(task.getFailureNum() + 1);
                task.setUpdated(now);
                taskContext.setTaskData(task);
                return true;
            }, taskContext, taskResult, TaskStatus.Failure.code);
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


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
