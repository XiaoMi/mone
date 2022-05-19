package com.xiaomi.data.push.task;

import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class Task {

    private Runnable runnable;

    private long delay;

    public Task(Runnable runnable, long delay) {
        this.runnable = runnable;
        this.delay = delay;
    }
}
