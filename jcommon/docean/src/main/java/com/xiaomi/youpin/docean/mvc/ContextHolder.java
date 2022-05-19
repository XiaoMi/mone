package com.xiaomi.youpin.docean.mvc;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class ContextHolder {

    private static ThreadLocal<ContextHolder> context = new ThreadLocal<ContextHolder>() {
        @Override
        protected ContextHolder initialValue() {
            return new ContextHolder();
        }
    };

    private MvcContext mvcContext;


    public void set(MvcContext mvcContext) {
        this.mvcContext = mvcContext;
    }

    public MvcContext get() {
        return this.mvcContext;
    }


    public static ContextHolder getContext() {
        return context.get();
    }

    public void close() {
        context.remove();
    }

}
