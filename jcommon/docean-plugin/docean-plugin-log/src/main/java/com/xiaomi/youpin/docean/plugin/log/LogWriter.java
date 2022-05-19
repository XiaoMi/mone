package com.xiaomi.youpin.docean.plugin.log;

import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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

    private int len;

    private int position;

    public LogWriter(String filePath) {
        this.filePath = filePath;
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
        return filePath + "_" + dt.getYear() + "_" + dt.getMonth().getValue() + "_" + dt.getDayOfMonth() + "_" + hour + str;
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
        int n = pos.incrementAndGet();
        if (n > 100) {
            mappedByteBuffer.force();
            pos.set(0);
        }
    }


    private void checkTime(LocalDateTime dt) {
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
}
