package com.xiaomi.mone.log.stream.job;

public interface SinkJob {

    boolean start() throws Exception;

    void shutdown() throws Exception;
}
