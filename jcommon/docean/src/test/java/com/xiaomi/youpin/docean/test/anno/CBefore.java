package com.xiaomi.youpin.docean.test.anno;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CBefore {

    String name() default "name";
}
