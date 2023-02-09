package com.xiaomi.data.push.schedule.task.notify;

/**
 * @author goodjava@qq.com
 * 通知
 */
public interface Notify {

    /**
     * 通知
     *
     * @param type
     * @param message
     */
    void notify(String type, String message);

    void notify(String type, String message, int shardingKey);
}
