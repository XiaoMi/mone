package com.xiaomi.youpin.test.codefilter;

import run.mone.processor.anno.CodeCheck;
import run.mone.processor.anno.Data;
import run.mone.processor.anno.HelloWorld;

/**
 * @author goodjava@qq.com
 * @date 2023/2/19 21:16
 */
@Data
@CodeCheck
public class ProcessorTest {

    private int aa;

    private int bb;


    private String pw = "";

    public void run() {
        B b = new B();
        //这个应当会被查找出来,设置明文密码了
        b.setPassword(pw);
    }


    public int sum(int a, int b) {
        return a + b;
    }

    @HelloWorld
    public static void main(String[] args) {

    }

}
