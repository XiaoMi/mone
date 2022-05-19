package com.xiaomi.youpin.docean.anno;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    String name() default "";

    String desc() default "";

    int type() default 2;

    int order() default 0;
}
