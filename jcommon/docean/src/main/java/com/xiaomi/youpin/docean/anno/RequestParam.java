package com.xiaomi.youpin.docean.anno;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {


    String value() default "";

}
