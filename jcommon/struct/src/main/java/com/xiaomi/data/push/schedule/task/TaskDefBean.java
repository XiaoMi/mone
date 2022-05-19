package com.xiaomi.data.push.schedule.task;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class TaskDefBean implements Serializable {

    private String type;

    private int retryNum;

    private int errorRetryNum;

    private long timeOut;

    private String name;

    /**
     * cron 表达式
     */
    private String cron;


    public TaskDefBean() {
    }

    public TaskDefBean(ITaskDef taskDef) {
        this.type = taskDef.type();
        this.retryNum = taskDef.retryNum();
        this.errorRetryNum = taskDef.errorRetryNum();
        this.timeOut = taskDef.timeOut();
        this.name = taskDef.name();
    }

}
