package com.xiaomi.data.push.common;

/**
 *
 * @author zhangzhiyong
 * @date 15/06/2018
 */
public interface Service {

    /**
     * 服务初始化
     */
    default void init() {

    }

    /**
     * 服务启动
     */
    default void start() {

    }

    /**
     * 服务停止
     */
    default void stop() {

    }

    /**
     * 服务关闭(kill)
     */
    default void shutdown() {

    }

    /**
     * 定期执行
     */
    default void schedule() {

    }
}
