package com.xiaomi.youpin.docean.aop.anno;


import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Before {

    Class anno();

}
