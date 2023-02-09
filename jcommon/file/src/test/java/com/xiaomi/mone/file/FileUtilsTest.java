package com.xiaomi.mone.file;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * @author goodjava@qq.com
 */
public class FileUtilsTest {


    @Test
    public void testReadFileLines() throws IOException {
        long pointer = 1000;
        for (; ; ) {
            ReadResult res = FileUtils.readFile("/tmp/a", pointer, 1);
            res.getLines().forEach(System.out::println);
            pointer = res.getPointer();
            if (res.isOver()) {
                break;
            }
        }
    }

    @Test
    public void testReadFileLines2() throws IOException {
        long pointer = 1;
        ReadResult res = FileUtils.readFile("/tmp/log/rcurve.log", pointer, 12, "ServiceManager destory");
        res.getLines().forEach(System.out::println);
        System.out.println(res.isOver());
    }

    @Test
    public void testReadFileLines21() throws IOException {
        long pointer = 1;
        ReadResult res = FileUtils.readFile("/tmp/log/rcurve.log", pointer, 12, "26.*ServiceManager");
        res.getLines().forEach(System.out::println);
        System.out.println(res.isOver());
    }

    @Test
    public void testReadFileLines22() throws IOException {
        long pointer = 1;
        for (; ; ) {
            ReadResult res = FileUtils.readFile("/tmp/log/rcurve.log", pointer, 1, "ServiceManager");
            res.getLines().forEach(System.out::println);
            pointer = res.getPointer();
            if (res.isOver()) {
                System.out.println("over");
                break;
            }
        }
    }

    @Test
    public void testList() throws IOException {
        FileUtils.list("/tmp").forEach(it -> {
            System.out.println(it.isFile() + "-->" + it.getName());
        });
    }
}
