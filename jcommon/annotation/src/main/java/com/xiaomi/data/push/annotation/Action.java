package com.xiaomi.data.push.annotation;

import java.lang.annotation.*;

/**
 * 记录action的行为
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface Action {

    int version() default 1;

    String name() default "";

    String desc() default "";

}
