package com.xiaomi.data.push.schedule.task;

/**
 * Created by zhangzhiyong on 10/06/2018.
 * 任务拦截器,任何一个任务都可以添加
 */
public interface TaskInterceptor {

    void beforeTask(TaskParam param, TaskContext context);

    void afterTask(TaskResult result);

    /**
     * 任务成功
     */
    void onSuccess(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult);

    /**
     * 任务失败
     */
    void onFailure(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult);


}
