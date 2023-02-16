package com.xiaomi.hera.trace.etl.util.pool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/4/28 3:40 下午
 */
@Component
@Slf4j
public class AsyncNotify {

    private ThreadPoolExecutor pool;

    private static final int QUEUE_SIZE = 30;

    @PostConstruct
    public void init(){
        BlockingQueue queue = new ArrayBlockingQueue(QUEUE_SIZE);
        pool = new ThreadPoolExecutor(2,2,1, TimeUnit.MINUTES,queue);
    }

    public void submit(Runnable runnable){
        pool.submit(runnable);
    }
}
