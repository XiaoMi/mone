package com.xiaomi.youpin.docean.test;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * @date 2022/11/14 17:05
 */
public class StreamTest {


    @Test
    public void testPeek() {
        List<String> list = Stream.of("a", "b").peek(it -> {
            System.out.println(it);
        }).collect(Collectors.toList());
        System.out.println(list);
    }
}
