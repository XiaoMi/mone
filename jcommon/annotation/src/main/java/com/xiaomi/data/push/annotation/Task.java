package com.xiaomi.data.push.annotation;

import java.lang.annotation.*;

/**
 * Created by zhangzhiyong on 14/06/2018.
 * 加入此注解的话,异常会被拦截并返回TaskFailure结果(不用再取手动处理异常)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface Task {

    //任务的名称
    String name();

    //任务的开发版本
    String version() default "0.0.1";
}
