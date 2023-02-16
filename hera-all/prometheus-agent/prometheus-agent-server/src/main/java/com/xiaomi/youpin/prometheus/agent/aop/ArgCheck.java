package com.xiaomi.youpin.prometheus.agent.aop;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ArgCheck {

}
