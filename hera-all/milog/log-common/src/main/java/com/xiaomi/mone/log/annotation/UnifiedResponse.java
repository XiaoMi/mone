package com.xiaomi.mone.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wtt
 * @version 1.0
 * @description 统一包装响应
 * @date 2022/5/10 11:33
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UnifiedResponse {
}
