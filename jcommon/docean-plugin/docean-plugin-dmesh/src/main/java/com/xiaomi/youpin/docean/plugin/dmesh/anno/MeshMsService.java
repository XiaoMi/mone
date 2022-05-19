package com.xiaomi.youpin.docean.plugin.dmesh.anno;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MeshMsService {

    String name() default "";

    String group() default "";

    String version() default "";

    Class<?> interfaceClass();

    int timeout() default 1000;

    String app() default "";

}
