package com.xiaomi.mone.log.manager.service.bind;

import java.lang.annotation.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/23 15:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Processor {
    /**
     * Alias name of Processor class
     *
     * @return
     */
    String value() default "";

    /**
     * Whether is the default Processor
     */
    boolean isDefault() default false;

    /**
     * Order priority of Processor class
     */
    int order() default 0;

    int ORDER_HIGHEST = Integer.MIN_VALUE;

    int ORDER_LOWEST = Integer.MAX_VALUE;
}
