/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.file.server.service;

import com.google.common.base.Stopwatch;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class DownloadListener implements ChannelProgressiveFutureListener {

    private Stopwatch stopwatch = Stopwatch.createStarted();

    private int num = 0;

    private String id;

    private long fileLength;

    public DownloadListener(String id, long fileLength) {
        this.id = id;
        this.fileLength = fileLength;
    }

    @Override
    public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
        int value = (int) (progress * 1.0f / total * 100);
        if (value - num >= 25) {
            num = value;
            log.info("download:{} {}/100", this.id, num);
        }
    }

    @Override
    public void operationComplete(ChannelProgressiveFuture future) {
        long time = stopwatch.elapsed(TimeUnit.SECONDS);
        if (time <=0) {
            time = 1;
        }
        try {
            if (fileLength > 0 && fileLength / time > 0) {
                try {
                    long speed = fileLength / time / 1024;
                    log.info("download:{} finish use time:{} speed:{}kb  size:{}", this.id, time, speed, fileLength / 1024 + "kb");
                } catch (Throwable ex) {
                    //ignore
                }
            } else {
                log.info("download:{} finish size:{}", this.id, fileLength / 1024 + "kb");
            }
        } catch (Throwable ex) {
            //ignore
        }
    }
}
