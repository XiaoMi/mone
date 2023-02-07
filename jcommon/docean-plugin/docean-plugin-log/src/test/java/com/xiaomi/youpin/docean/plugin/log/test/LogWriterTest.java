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

package com.xiaomi.youpin.docean.plugin.log.test;

import com.xiaomi.youpin.docean.plugin.log.LogWriter;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2020/7/8
 */
public class LogWriterTest {


    @Test
    public void testWrite() {
        LogWriter logWriter = new LogWriter("/tmp/data");
        logWriter.init(1024 * 1024 * 10);
        IntStream.range(0, 1000).forEach(it -> {
            System.out.println("run:" + new Date());
            logWriter.write(LocalDateTime.now(), "record:" + (new Date().toString()) + System.lineSeparator());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        logWriter.force();
    }

    @Test
    public void testWrite2() {
        LogWriter logWriter = new LogWriter("/tmp/redolog", false);
        logWriter.init(1024 * 1024 * 10, 0);
        IntStream.range(0, 5).forEach(it -> {
            logWriter.write(LocalDateTime.now(), ("{'id':'1'}" + System.lineSeparator()).getBytes());
        });
        logWriter.force();
        System.out.println("finish");
    }

    @Test
    public void testWriteReset() {
        LogWriter logWriter = new LogWriter("/tmp/redolog", false);
        logWriter.init(1024 * 1024 * 10);
        IntStream.range(0, 100).forEach(it -> {
            logWriter.reset();
            logWriter.write(LocalDateTime.now(), ("{'id':'1'}" + System.lineSeparator()).getBytes());
        });
        logWriter.force();
        System.out.println("finish");
    }


    @Test
    public void testChannel() throws IOException {
        FileChannel fileChannel = new RandomAccessFile(new File("/tmp/data"), "rw").getChannel();
        long size = fileChannel.size();
        System.out.println(size);
    }

    @Test
    public void testTime() {
        LocalDateTime dt = LocalDateTime.now();
        System.out.println(dt.getHour());
    }
}
