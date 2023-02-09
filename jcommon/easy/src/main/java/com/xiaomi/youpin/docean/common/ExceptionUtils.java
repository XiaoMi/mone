package com.xiaomi.youpin.docean.common;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * @author goodjava@qq.com
 * @date 2022/11/24 08:58
 */
@Slf4j
public class ExceptionUtils {

    public static RuntimeException getRuntimeException(Throwable ex) {
        if (ex.getCause() != null && ex.getCause() instanceof InvocationTargetException) {
            ex = ex.getCause();
        }
        if (ex instanceof InvocationTargetException) {
            InvocationTargetException ite = (InvocationTargetException) ex;
            Throwable e = ite.getTargetException();
            log.error(e.getMessage(), e);
            if (e instanceof RuntimeException) {
                return (RuntimeException) e;
            }
            return new RuntimeException(e.getMessage(), e.getCause());
        } else {
            log.error(ex.getMessage(), ex);
        }
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        }
        return new RuntimeException(ex);
    }
}
