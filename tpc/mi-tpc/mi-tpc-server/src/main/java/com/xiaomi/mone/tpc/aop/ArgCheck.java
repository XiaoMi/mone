package com.xiaomi.mone.tpc.aop;

import java.lang.annotation.*;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 9:38
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ArgCheck {

    boolean needUser() default true;
    boolean allowArgUser() default false;
}
