package com.xiaomi.mone.file;

import com.google.common.base.Stopwatch;
import okio.BufferedSource;
import okio.Okio;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2023/9/21 15:25
 */
public class RandomAccessFileTest {


    @Test
    public void test1() throws IOException {
        AtomicInteger ai = new AtomicInteger();
        MoneRandomAccessFile mra = new MoneRandomAccessFile("/tmp/data", "r", 1024 * 4);
        Stopwatch sw = Stopwatch.createStarted();
        while (true) {
            String line = mra.getNextLine();
//            System.out.println(line);
            ai.incrementAndGet();
            if (null == line) {
                break;
            }
        }
        System.out.println("use time:" + sw.elapsed(TimeUnit.MILLISECONDS));
        System.out.println(ai.get());
    }


    @Test
    public void test2() throws IOException {
        RandomAccessFile mra = new RandomAccessFile("/tmp/data", "r");
        Stopwatch sw = Stopwatch.createStarted();
        while (true) {
            String line = mra.readLine();
            System.out.println(line);
            if (null == line) {
                break;
            }
        }
        System.out.println("use time:" + sw.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void test3() throws IOException {
        AtomicInteger ai = new AtomicInteger();
        BufferedSource bufferedSource = Okio.buffer(Okio.source(new File("/tmp/data")));
        Stopwatch sw = Stopwatch.createStarted();
        while (true) {
            String line = bufferedSource.readUtf8Line();
//            System.out.println(line);
            ai.incrementAndGet();
            if (null == line) {
                break;
            }
        }
        System.out.println("use time:" + sw.elapsed(TimeUnit.MILLISECONDS));
        System.out.println(ai.get());
    }

}
