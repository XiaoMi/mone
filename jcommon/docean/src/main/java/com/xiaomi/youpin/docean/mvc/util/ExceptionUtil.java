package com.xiaomi.youpin.docean.mvc.util;

import com.xiaomi.youpin.docean.exception.DoceanException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author: wtt
 * @date: 2022/5/13 13:12
 * @description:
 */
public class ExceptionUtil {
    private ExceptionUtil() {
        // Prevent Instantiation
    }

    /**
     * 拆解InvocationTargetException和UndeclaredThrowableException异常的包装，从而得到被包装的真正异常
     *
     * @param wrapped 包装后的异常
     * @return 拆解出的被包装异常
     */
    public static Throwable unwrapThrowable(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else if (unwrapped instanceof DoceanException) {
                unwrapped = unwrapped.getCause();
            }else{
                return unwrapped;
            }
        }
    }
}
