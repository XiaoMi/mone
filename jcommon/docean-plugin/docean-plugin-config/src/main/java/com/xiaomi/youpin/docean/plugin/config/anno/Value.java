package com.xiaomi.youpin.docean.plugin.config.anno;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Value {

    String value();

    String defaultValue() default "";

}
