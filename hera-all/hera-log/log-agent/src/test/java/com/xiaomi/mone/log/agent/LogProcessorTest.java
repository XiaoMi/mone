/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/29 10:35
 */
@Slf4j
public class LogProcessorTest {

    @Test
    public void test() {
        CompletableFuture<Void> reFreshFuture = CompletableFuture.runAsync(() -> {
        });
        CompletableFuture<Void> stopChannelFuture = CompletableFuture.runAsync(() -> {
        });
        CompletableFuture.allOf(reFreshFuture, stopChannelFuture).join();
        log.info("config change success");
    }

    @Test
    public void testFile() {
        String defaultMonitorPath = "/home/work/log/";
        long size = FileUtils.listFiles(new File(defaultMonitorPath), null, true).size();
        log.info("result:{}", size);
    }

    /**
     * 停止不了的
     *
     * @throws IOException
     */
    @Test
    public void testComplete() throws IOException {
        String defaultMonitorPath = "/home/work/log/";
        int result = 0;
        CompletableFuture<Integer> fileSizeFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(30);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("testes");
                    return FileUtils.listFiles(new File(defaultMonitorPath), null, true).size();
                });
        try {
            result = fileSizeFuture.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.info("getDefaultFileSize error", e);
        }
        log.info("result:{}", result);
        fileSizeFuture.complete(1);
        System.in.read();
    }
}
