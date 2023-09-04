package com.xiaomi.hera.trace.etl.es.util.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerPool {
    public static final ThreadPoolExecutor CONSUMER_POOL;
    public static final BlockingQueue CONSUMER_QUEUE = new ArrayBlockingQueue(30000);
    private static AtomicInteger threadNumber = new AtomicInteger(1);
    public static final int CONSUMER_QUEUE_THRESHOLD = 3000;
    static {
        CONSUMER_POOL = new ThreadPoolExecutor(10,10,1, TimeUnit.MINUTES,CONSUMER_QUEUE, r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            thread.setName("consumer-" + threadNumber.getAndIncrement());
            return thread;
        });
    }

}
