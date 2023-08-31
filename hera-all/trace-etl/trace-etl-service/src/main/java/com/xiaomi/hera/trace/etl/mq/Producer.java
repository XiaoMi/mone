package com.xiaomi.hera.trace.etl.mq;

import java.util.List;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/10/17 3:06 下午
 */
public interface Producer<T> {

    /**
     * send one
     * @param message
     */
    void send(T message);
    /**
     * send batch
     * @param messages
     */
    void send(List<T> messages);
}
