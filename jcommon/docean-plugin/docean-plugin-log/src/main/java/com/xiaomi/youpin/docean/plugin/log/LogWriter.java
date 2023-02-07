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

package com.xiaomi.youpin.docean.plugin.log;

import com.xiaomi.youpin.docean.common.Safe;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2020/7/8
 */
public class LogWriter {

    private MappedByteBuffer mappedByteBuffer;

    private FileChannel fileChannel;

    private String filePath;

    private int size = 1024 * 1024 * 20;

    @Setter
    private int refreshLineNum = 100;

    private int len;

    private int position;

    private boolean appendDate = true;

    public LogWriter(String filePath) {
        this.filePath = filePath;
    }

    public LogWriter(String filePath, boolean appendDate) {
        this.filePath = filePath;
        this.appendDate = appendDate;
    }

    private int hour = LocalDateTime.now().getHour();

    private int minute = LocalDateTime.now().getMinute();

    private AtomicInteger pos = new AtomicInteger();

    /**
     * 0 hour
     * 1 minute
     */
    private byte type = 0;

    private AtomicBoolean exit = new AtomicBoolean(false);

    private String getPath() {
        LocalDateTime dt = LocalDateTime.now();
        String str = "";
        if (type == 1) {
            str = "_" + dt.getMinute();
        }
        if (appendDate) {
            return filePath + "_" + dt.getYear() + "_" + dt.getMonth().getValue() + "_" + dt.getDayOfMonth() + "_" + hour + str;
        } else {
            return filePath;
        }
    }

    @SneakyThrows
    public void init(int position, int size) {
        this.size = size;
        fileChannel = new RandomAccessFile(new File(getPath()), "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, position, size);
    }

    @SneakyThrows
    public void init(int size) {
        this.size = size;
        this.fileChannel = new RandomAccessFile(new File(getPath()), "rw").getChannel();
        this.position = (int) fileChannel.size();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, position, size);
    }


    public void write(LocalDateTime time, String log) {
        write(time, log.getBytes());
    }

    /**
     * 每次写会append,并不会覆盖之前老的
     *
     * @param time
     * @param log
     */
    public synchronized void write(LocalDateTime time, byte[] log) {
        if (this.exit.get()) {
            return;
        }
        checkTime(time);
        byte[] data = log;
        if (len + data.length > size) {
            len = 0;
            this.force();
            //杜绝消息过大的问题
            if (data.length > size) {
                init(position, data.length);
            } else {
                init(position, size);
            }
        }

        len += data.length;
        position += data.length;
        mappedByteBuffer.put(log);
        int n = pos.incrementAndGet();
        //控制几行日志一刷
        if (n >= this.refreshLineNum) {
            mappedByteBuffer.force();
            pos.set(0);
        }
    }


    private void checkTime(LocalDateTime dt) {
        if (!this.appendDate) {
            return;
        }
        boolean hour = type == 0;
        int n = hour ? dt.getHour() : dt.getMinute();
//        System.out.println("n:" + n);
        int v = hour ? this.hour : this.minute;
        if (v != n) {
//            System.out.println("checkTime:" + hour);
            if (hour) {
                this.hour = n;
            } else {
                this.minute = n;
            }
            this.force();
            this.len = 0;
            this.position = 0;
            this.init(this.size);
        }
    }

    @SneakyThrows
    public synchronized void force() {
        mappedByteBuffer.force();
        mappedByteBuffer.clear();
        fileChannel.truncate(this.position);
        fileChannel.close();
        this.pos.set(0);
    }

    public synchronized void force(Runnable runnable) {
        this.force();
        runnable.run();
    }

    /**
     * 重头开始写入
     */
    public synchronized void reset() {
        this.position = 0;
        this.len = 0;
        Safe.runAndLog(()-> Files.delete(Paths.get(this.filePath)));
        this.init(0, this.size);
    }
}
