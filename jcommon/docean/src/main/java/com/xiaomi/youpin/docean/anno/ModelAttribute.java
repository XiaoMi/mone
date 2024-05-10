package com.xiaomi.youpin.docean.anno;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 * @date 2024/4/23 10:55
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelAttribute {

    String value() default "";

}
