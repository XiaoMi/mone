package org.apache.dubbo.annotation;

import java.lang.annotation.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/26
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface DubboReference {

    String name() default "";

    Class<?> interfaceClass();

    String group() default "";

    boolean check() default true;

    int timeout() default 1000;

    /**
     * Cluster strategy, legal values include: failover, failfast, failsafe, failback, forking
     */
    String cluster() default "";

    String version() default "";

}
