package com.xiaomi.youpin.docean.plugin.mybatis;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Transactional {

    String type() default "";

}
