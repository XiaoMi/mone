package com.xiaomi.mone.log.stream.job.extension;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/13 14:29
 */
public interface CompensateMsgConsume {
    /**
     * compensate mq message
     *
     * @param ak
     * @param sk
     * @param serviceUrl
     * @param topic
     */
    void consume(String ak, String sk, String serviceUrl, String topic);

    void consume();
}
