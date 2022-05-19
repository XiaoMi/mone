package com.xiaomi.mone.buddy.agent.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/7 23:41
 */
public class Context {

    @Getter
    @Setter
    private Span span;

    private static final ThreadLocal<Context> tr = new ThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            return new Context();
        }
    };

    public static Context getContext() {
        return tr.get();
    }


    public static void removeContext() {
        tr.remove();
    }






}
