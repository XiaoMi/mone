package com.xiaomi.youpin.docean.common;

import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/3/30 17:07
 */
public class DefaultInvokeMethodCallback implements InvokeMethodCallback {

    public boolean isDubboCall(Map<String, String> attachments) {
        if (null != attachments) {
            String type = attachments.getOrDefault("call_type", "");
            if (type.equals("dubbo")) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void before(Map<String, String> map, Object[] params) {

    }

    @Override
    public void after(Map<String, String> map, Object res) {

    }

    @Override
    public Object invoke(Method method, Object obj, Object[] params) {
        try {
            if (method.getParameterTypes() == null || method.getParameterTypes().length < 1) {
                return method.invoke(obj);
            }
            return method.invoke(obj, params);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object fastInvoke(FastMethod method, Object obj, Object[] params) {
        try {
            return method.invoke(obj, params);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
