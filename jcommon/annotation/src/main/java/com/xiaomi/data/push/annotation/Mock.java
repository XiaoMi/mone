package com.xiaomi.data.push.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface Mock {

    /**
     * mock的类型
     * 分为记录和模拟数据
     *
     * @return
     */
    MockType type() default MockType.mock;


    /**
     * mock 调用的唯一key
     *
     * @return
     */
    String key();

}
