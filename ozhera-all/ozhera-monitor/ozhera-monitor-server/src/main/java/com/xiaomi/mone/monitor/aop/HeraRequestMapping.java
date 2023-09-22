package com.xiaomi.mone.monitor.aop;


import com.xiaomi.mone.monitor.bo.InterfaceNameEnum;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/13 14:17
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
public @interface HeraRequestMapping {

    String name() default "";

    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};

    RequestMethod[] method() default {};

    String[] params() default {};

    String[] headers() default {};

    String[] consumes() default {};

    String[] produces() default {};

    InterfaceNameEnum interfaceName();

    Class<?> actionClass();

}
