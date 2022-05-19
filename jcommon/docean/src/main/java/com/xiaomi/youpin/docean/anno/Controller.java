package com.xiaomi.youpin.docean.anno;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

    String desc() default "";

    int type() default 0;
}
