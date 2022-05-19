package com.xiaomi.mione.prometheus.metrics;

import java.lang.annotation.*;

/**
 * @author wodiwudi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface MetricsTime {

    String name() default "";

    /**
     * 打印结果
     *
     * @return boolean
     */
    boolean printResult() default true;
}
