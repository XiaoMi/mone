package com.xiaomi.data.push.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface Cache {

    /**
     * 放入redis中得key,不设置则自动取类名+方法名称
     * 自己设定key的时候,自己要保证这是唯一的
     *
     * @return
     */
    String key() default "";

    /**
     * 从参数数组中取参数的索引
     *
     * @return
     */
    String[] paramIndex() default "";


    /**
     * 缓存时间 毫秒
     *
     * @return
     */
    int time() default 3000;


    /**
     * 缓存类型s
     * @return
     */
    CacheType cacheType() default CacheType.Mem;
}
