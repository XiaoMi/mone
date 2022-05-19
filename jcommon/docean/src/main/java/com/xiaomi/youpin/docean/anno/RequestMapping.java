package com.xiaomi.youpin.docean.anno;


import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String path() default "";

    String method() default "post";

    long timeout() default 2000;

}
