package com.xiaomi.hera.trace.etl.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ExecutorUtil {

    public static BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(30000);
    public static BlockingQueue<Runnable> consumerDelayMsgQueue = new ArrayBlockingQueue<>(30000);
    public static BlockingQueue<Runnable> rocksThreadQueue = new ArrayBlockingQueue<>(2);
    public static final int ROCKSDB_DEAL_MESSAGE_CORE = 20;
    private final static ThreadPoolExecutor errorESthreadPoolExecutor;
    private final static ThreadPoolExecutor consumerDelayMsgthreadPoolExecutor;
    private final static ThreadPoolExecutor rocksDBThreaPool;

    static{

        errorESthreadPoolExecutor = new ThreadPoolExecutor(2, 5,
                0L, TimeUnit.MILLISECONDS,
                queue);
        consumerDelayMsgthreadPoolExecutor = new ThreadPoolExecutor(ROCKSDB_DEAL_MESSAGE_CORE, ROCKSDB_DEAL_MESSAGE_CORE,
                0L, TimeUnit.MILLISECONDS,
                consumerDelayMsgQueue);
        rocksDBThreaPool = new ThreadPoolExecutor(2, 2,
                0L, TimeUnit.MILLISECONDS,
                rocksThreadQueue);
    }

    public static void submit(Runnable runnable){
        try {
            errorESthreadPoolExecutor.submit(runnable);
        }catch(Exception e){
            log.error("提交错误es任务失败：",e);
        }
    }

    public static void submitDelayMessage(Runnable runnable){
        try {
            consumerDelayMsgthreadPoolExecutor.submit(runnable);
        }catch(Exception e){
            log.error("提交延迟消息任务失败：",e);
        }
    }

    public static void submitRocksDBRead(Runnable runnable){
        try {
            rocksDBThreaPool.submit(runnable);
        }catch(Exception e){
            log.error("提交rocksdb读取任务失败：",e);
        }
    }
}
