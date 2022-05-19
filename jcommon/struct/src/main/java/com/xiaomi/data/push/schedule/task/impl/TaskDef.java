package com.xiaomi.data.push.schedule.task.impl;

import com.xiaomi.data.push.schedule.task.ITaskDef;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangzhiyong on 29/05/2018.
 */
public enum TaskDef implements ITaskDef {

    DemoTask("demoTask", 10, 3, TimeUnit.SECONDS.toMillis(10)),
    HttpTask("httpTask", 10, 3, TimeUnit.MILLISECONDS.toMillis(300)),
    CompileTask("compileTask", 10, 3,TimeUnit.SECONDS.toMillis(10))
    ;

    //任务的类型
    public String type;
    //正常情况下重试次数
    public int retryNum;
    //失败后重试的次数
    public int errorRetryNum;
    //任务的执行超时时间
    public long timeOut;

    TaskDef(String type, int retryNum, int errorRetryNum, long timeOut) {
        this.type = type;
        this.retryNum = retryNum;
        this.errorRetryNum = errorRetryNum;
        this.timeOut = timeOut;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public int retryNum() {
        return retryNum;
    }

    @Override
    public int errorRetryNum() {
        return errorRetryNum;
    }

    @Override
    public long timeOut() {
        return timeOut;
    }
}
