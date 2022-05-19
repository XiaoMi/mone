package com.xiaomi.youpin.annotation.log;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface Log {


    String name() default "";

    /**
     * 记录错误信息
     *
     * @return
     */
    boolean recordError() default false;

    /**
     * 记录错误到日志
     *
     * @return
     */
    boolean recordErrorLog() default false;

    /**
     * 打印结果
     *
     * @return
     */
    boolean printResult() default true;

    boolean printParam() default true;

    /**
     * 是否启用限流
     *
     * @return
     */
    boolean useSemphore() default false;

    /**
     * 是否使用falcon
     *
     * @return
     */
    boolean usePercount() default false;

    /**
     * 是否使用cat
     *
     * @return
     */
    boolean useCat() default true;

}
