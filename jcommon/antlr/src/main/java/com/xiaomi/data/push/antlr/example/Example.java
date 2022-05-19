package com.xiaomi.data.push.antlr.example;

import lombok.Data;
import lombok.Getter;

/**
 * @author goodjava@qq.com
 * @date 3/7/21
 */
@Data
public class Example<T> {

    //    int a;
//
    @Getter
    protected int b;

//    public int c;
    //
//    private A aa;


//    static public final String zz = "abc";


//    public void test() {
////        System.out.println(aa.sum(11, 22));
//    }


    public static final int sum(int a, int b) {
        return a + b;
    }

    public void test1() {
    }
//
//
//    private void e() {
//
//    }
//
//    protected void c() {
//
//    }
//
//
//    void f() {
//
//    }

}
