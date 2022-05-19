package org.apache.dubbo.annotation;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/26
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface DubboService {

    String name() default "";

    String group() default "";

    String version() default "";

    Class<?> interfaceClass();

    int timeout() default 1000;

    boolean async() default false;

}
