package com.xiaomi.data.push.schedule.task;

/**
 * @author goodjava@qq.com
 */
public interface ITaskDef {

    String type();

    int retryNum();

    int errorRetryNum();

    long timeOut();

    String name();

}
