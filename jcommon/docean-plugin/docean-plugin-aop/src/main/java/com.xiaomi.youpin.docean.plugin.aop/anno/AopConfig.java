package com.xiaomi.youpin.docean.plugin.aop.anno;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface AopConfig {

    Class<? extends Annotation> clazz();

}
