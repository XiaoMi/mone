package com.xiaomi.youpin.docean.plugin.cat;

/**
 * @author zheng.xucn@outlook.com goodjava@qq.com
 */
public class CatContext {

    private static ThreadLocal<CatContext> context = new ThreadLocal<CatContext>() {
        @Override
        protected CatContext initialValue() {
            return new CatContext();
        }
    };

}
