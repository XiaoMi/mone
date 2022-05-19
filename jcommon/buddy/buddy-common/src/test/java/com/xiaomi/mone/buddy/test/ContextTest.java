package com.xiaomi.mone.buddy.test;

import agent.bo.Context;
import agent.bo.Scope;
import org.junit.Test;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/9 11:41
 */
public class ContextTest {


    @Test
    public void test() {
        Scope.start();
        System.out.println(Context.getContext().getSpan());
        System.out.println(Context.getContext().getSpan());
        Scope.close();
        Scope.close();
        System.out.println(Context.getContext().getSpan());
    }

}
