package com.xiaomi.youpin.docean.plugin.sentinel;

import com.alibaba.csp.sentinel.EntryType;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @author lyc@qq.com
 * @date 2020/6/26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sentinel {

    String value() default "";

    EntryType entryType() default EntryType.OUT;

    int resourceType() default 0;

    String blockHandler() default "";

    Class<?>[] blockHandlerClass() default {};

    String fallback() default "";

    String defaultFallback() default "";

    Class<?>[] fallbackClass() default {};

    Class<? extends Throwable>[] exceptionsToTrace() default {Throwable.class};

    Class<? extends Throwable>[] exceptionsToIgnore() default {};
}
