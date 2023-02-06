package com.xiaomi.data.push.schedule.task;

/**
 * @author goodjava@qq.com
 */
public interface ITask {

    void beforeTask(TaskParam var1, TaskContext var2);

    TaskResult execute(TaskParam var1, TaskContext var2);

    void afterTask(TaskContext var1, TaskResult var2);

    void onRetry(int var1, TaskParam var2, TaskContext var3, TaskResult var4);

    void onSuccess(int var1, TaskParam var2, TaskContext var3, TaskResult var4);

    void onFailure(int var1, TaskParam var2, TaskContext var3, TaskResult var4);
}
