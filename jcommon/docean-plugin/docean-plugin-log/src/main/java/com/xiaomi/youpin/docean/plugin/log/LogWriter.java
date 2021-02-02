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

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author goodjava@qq.com
 * @date 2020/7/8
 */
public class LogWriter {

    private MappedByteBuffer mappedByteBuffer;

    private FileChannel fileChannel;

    private String filePath;

    private int size = 1024 * 1024 * 20;

    private int len;

    private int position;

    public LogWriter(String filePath) {
        this.filePath = filePath;
    }

    private int hour = LocalDateTime.now().getHour();

    private AtomicBoolean exit = new AtomicBoolean(false);

    private String getPath() {
        LocalDateTime dt = LocalDateTime.now();
        return filePath + "_" + dt.getYear() + "_" + dt.getMonth().getValue() + "_" + dt.getDayOfMonth() + "_" + hour;
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


    public synchronized void write(LocalDateTime time, String log) {
        if (this.exit.get()) {
            return;
        }
        checkTime(time);
        byte[] data = log.getBytes();
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
        mappedByteBuffer.put(log.getBytes());
    }


    private void checkTime(LocalDateTime dt) {
        int hour = dt.getHour();
        if (this.hour != hour) {
            this.hour = hour;
            this.force();

            this.len = 0;
            this.position = 0;

            this.init(this.size);
        }
    }

    @SneakyThrows
    public synchronized void force() {
        this.exit.set(true);
        mappedByteBuffer.force();
        mappedByteBuffer.clear();
        fileChannel.truncate(this.position);
        fileChannel.close();
    }
}
