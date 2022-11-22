package com.xiaomi.youpin.gwdash.annotation;

import com.xiaomi.youpin.gwdash.common.OnPropertyNotCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 在@Configuration修饰的类如果使用该注解，配@Param name作为key在Environment中的value为空或 和 @Param value 的值不相等时才会进行加载该配置类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Conditional(OnPropertyNotCondition.class)
public @interface ConditionalOnPropertyNotEq {

    /**
     * 不等于该value时才会进行加载
     * @return
     */
    boolean value();

    /**
     * 配置的key值
     * @return
     */
    String name();
}
