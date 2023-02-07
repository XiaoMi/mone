package com.xiaomi.mone.grpc.common;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/9/21
 */
@Slf4j
public class GrpcReflectUtils {

    private static Gson gson = new Gson();

    public static Object invokeMethod(String serviceName,String methodName,String[] paramTypes,String[]params) {
        try {
            Class clazz = Class.forName(serviceName);
            Class[] array = Arrays.stream(paramTypes).map(it -> {
                if (it.equals("int")) {
                    return int.class;
                }
                if (it.equals("long")) {
                    return long.class;
                }
                try {
                    return Class.forName(it);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }).toArray(Class[]::new);
            Method method = clazz.getMethod(methodName, array);

            Object[] p = IntStream.range(0, params.length).mapToObj(i -> {
                String str = params[i];
                Class c = array[i];
                Object obj = gson.fromJson(str, c);
                return obj;
            }).toArray();
            return method.invoke(clazz.newInstance(), p);
        } catch (Throwable e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
