package com.xiaomi.youpin.docean.mvc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author: wtt
 * @date: 2022/5/13 13:12
 * @description:
 */
public class ExceptionUtil {
    private ExceptionUtil() {
    }

    /**
     * Unwrapping InvocationTargetException and UndeclaredThrowableException exceptions to obtain the underlying wrapped exception.
     *
     * @param wrapped Abnormal packaging
     * @return Abnormal packaging of disassembled components.
     */
    public static Throwable unwrapThrowable(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }
}
