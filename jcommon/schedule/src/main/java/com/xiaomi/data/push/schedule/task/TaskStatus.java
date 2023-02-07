package com.xiaomi.data.push.schedule.task;

/**
 * @author goodjava@qq.com
 */

public enum TaskStatus {

    Init(0),
    Success(1),
    Failure(2),
    Retry(3),
    Running(4),
    /**
     * 暂停状态
     */
    Pause(5)
    ;

    public int code;

    TaskStatus(int code) {
        this.code = code;
    }
}
