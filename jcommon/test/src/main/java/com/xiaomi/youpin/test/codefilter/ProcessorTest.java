package com.xiaomi.youpin.test.codefilter;

import run.mone.processor.anno.Data;
import run.mone.processor.anno.HelloWorld;

/**
 * @author goodjava@qq.com
 * @date 2023/2/19 21:16
 */
@Data
public class ProcessorTest {

    private int aa;

    private int bb;


    public int sum(int a, int b) {
        return a + b;
    }

    @HelloWorld
    public static void main(String[] args) {

    }

}
