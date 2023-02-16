package com.xiaomi.mone.log.agent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/29 10:35
 */
@Slf4j
public class LogProcessorTest {

    @Test
    public void test(){
        CompletableFuture<Void> reFreshFuture = CompletableFuture.runAsync(() -> {
        });
        CompletableFuture<Void> stopChannelFuture = CompletableFuture.runAsync(() -> {
        });
        CompletableFuture.allOf(reFreshFuture, stopChannelFuture).join();
        log.info("config change success");
    }
}
