package com.xiaomi.youpin.docean.plugin.dmesh.anno;

import java.lang.annotation.*;


/**
 * @author goodjava@qq.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface MeshReference {

    String name() default "";

    Class<?> interfaceClass();

    String group() default "";

    String version() default "";

    boolean check() default true;

    int timeout() default 1000;

    String app() default "";

    /**
     * 对面也是mesh服务(经过网格的)
     * @return
     */
    boolean mesh() default true;

    /**
     * 是否是远程调用
     * @return
     */
    boolean remote() default false;

}
