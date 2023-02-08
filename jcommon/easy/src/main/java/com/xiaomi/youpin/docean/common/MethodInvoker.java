/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.xiaomi.youpin.docean.adapter.DoubleDefaultAdapter;
import com.xiaomi.youpin.docean.adapter.IntegerDefaultAdapter;
import com.xiaomi.youpin.docean.adapter.LongDefaultAdapter;
import com.xiaomi.youpin.docean.exception.DoceanException;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * @date 2022/4/18
 */
@Slf4j
public class MethodInvoker {

    private ConcurrentHashMap<String, FastMethod> methodCache = new ConcurrentHashMap<>();


    public Object invokeMethod(Object obj, Class clazz, String methodName, Object[] params) {
        try {
            Optional<Method> optional = getMethod(clazz, methodName, params.length);
            if (optional.isPresent()) {
                optional.get().setAccessible(true);
                //没有可以匹配上的方法
                if (optional.get().getParameterCount() != params.length) {
                    return null;
                }

                if (params.length == 0) {
                    return optional.get().invoke(obj);
                }
                return optional.get().invoke(obj, params);
            }

        } catch (Throwable ex) {
            log.error("invokeMethod error:{} class:{} method:{}", ex.getMessage(), clazz.getName(), methodName);
        }
        return null;
    }


    public Object invokeFastMethod(Object obj, Class clazz, String methodName, Object[] params) {
        try {
            String key = clazz + "_" + methodName;
            FastMethod m = methodCache.get(key);
            if (null != m) {
                return m.invoke(obj, params);
            }
            Optional<Method> optional = getMethod(clazz, methodName);
            if (optional.isPresent()) {
                FastClass fastClass = FastClass.create(clazz);
                FastMethod method = fastClass.getMethod(optional.get());
                methodCache.putIfAbsent(key, method);
                return method.invoke(obj, params);
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }


    public Object invokeMethod(Object obj, Method method, Object[] params) {
        try {
            if (params.length == 0) {
                return method.invoke(obj);
            }
            return method.invoke(obj, params);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            throw new DoceanException(ex);
        }
    }

    public Object invokeFastMethod(Object obj, Method method, Object[] params) {
        try {
            String key = obj.getClass() + "_" + method.getName();
            FastMethod m = methodCache.get(key);
            if (null != m) {
                return m.invoke(obj, params);
            }
            FastClass fc = FastClass.create(obj.getClass());
            FastMethod fm = fc.getMethod(method);
            methodCache.putIfAbsent(key, fm);
            return fm.invoke(obj, params);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            throw new DoceanException(ex);
        }
    }

    public Object invokeMethod(Object obj, String methodName, Object[] params) {
        try {
            Method method = obj.getClass().getMethod(methodName, Arrays.stream(params).map(it -> it.getClass()).toArray(Class[]::new));
            if (params.length == 0) {
                return method.invoke(obj);
            }
            return method.invoke(obj, params);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            throw new DoceanException(ex);
        }
    }

    public static Optional<Method> getMethod(Class clazz, String methodName) {
        return Arrays.stream(clazz.getMethods()).filter(it -> it.getName().equals(methodName)).findAny();
    }

    /**
     * 查找方法 通过 方法名和参数数量
     *
     * @param clazz
     * @param methodName
     * @param paramNum
     * @return
     */
    public static Optional<Method> getMethod(Class clazz, String methodName, int paramNum) {
        return Arrays.stream(clazz.getMethods()).filter(it -> it.getName().equals(methodName) && it.getParameterCount() == paramNum).findAny();
    }

    public Object[] getMethodParams(Object obj, String methodName, JsonElement params) {
        Method method = getMethod(obj.getClass(), methodName).get();
        return getMethodParams(method, params);
    }


    public Object[] getMethodParams(Method method, JsonElement params) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, new IntegerDefaultAdapter())
                .registerTypeAdapter(Double.class, new DoubleDefaultAdapter())
                .registerTypeAdapter(Long.class, new LongDefaultAdapter())
                .create();
        Class<?>[] types = method.getParameterTypes();
        if (types.length == 0) {
            return new Object[]{};
        }
        //一个参数,不需要用参数列表
        if (params.isJsonObject()) {
            return Stream.of(gson.fromJson(gson.toJson(params), types[0])).toArray();
        }
        //参数列表
        if (params.isJsonArray()) {
            JsonArray array = params.getAsJsonArray();
            return IntStream.range(0, types.length).mapToObj(i -> gson.fromJson(gson.toJson(array.get(i)), types[i])).collect(Collectors.toList()).toArray();
        }
        throw new DoceanException();
    }


    public static Class classForName(String name) {
        if (name.equals("com.xiaomi.youpin.docean.Mvc$LazyHolder")) {
            return null;
        }
        try {
            return Class.forName(name);
        } catch (Throwable e) {
            log.warn("classForName:{} error:{}", name, e.getMessage());
        }
        return null;
    }

    public static Class classForName(String name, ClassLoader classLoader) {
        if (name.equals("com.xiaomi.youpin.docean.Mvc$LazyHolder")) {
            return null;
        }
        try {
            if (null == classLoader) {
                return Class.forName(name);
            } else {
                return Class.forName(name, true, classLoader);
            }
        } catch (Throwable e) {
            log.warn("classForName:{} error:{}", name, e.getMessage());
        }
        return null;
    }


    public static Object getInstance(Class clazz) {
        try {
            if (clazz.isInterface()) {
                return null;
            }
            Constructor constructor = clazz.getConstructor();
            if (null != constructor) {
                return clazz.newInstance();
            }
        } catch (InstantiationException e) {
            log.error(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static Optional<Annotation> getAnno(Class<?> clazz, List<Class<? extends Annotation>> list) {
        Annotation[] anns = clazz.getAnnotations();
        return Arrays.stream(anns).filter(a -> list.stream().filter(it -> a.annotationType().equals(it)).findAny().isPresent()).findAny();
    }

    public Object invokeMethod(MethodReq req, Object obj, BiFunction<Class[], byte[][], Object[]> fun) {
        return invokeMethod(req.getMethodName(), obj, req.getParamTypes(), req.getByteParams(), fun, true);
    }

    public Object invokeMethod(String methodName, Object obj, String[] types, byte[][] paramArray, BiFunction<Class[], byte[][], Object[]> fun, boolean fast) {
        try {
            if (types.length > 0) {
                Class[] clazzArray = Arrays.stream(types).map(i -> {
                    if (i.equals("int")) {
                        return int.class;
                    }
                    if (i.equals("long")) {
                        return long.class;
                    }
                    try {
                        return Class.forName(i);
                    } catch (ClassNotFoundException e) {
                        throw new DoceanException("class forName error:" + e.getMessage());
                    }
                }).toArray(Class[]::new);

                Object[] params = fun.apply(clazzArray, paramArray);

                if (true) {
                    return invokeFastMethod(obj, obj.getClass(), methodName, params);
                }

                Method method = obj.getClass().getMethod(methodName, clazzArray);
                return method.invoke(obj, params);
            } else {
                Method method = obj.getClass().getMethod(methodName);
                return method.invoke(obj);
            }
        } catch (Throwable ex) {
            if (ex instanceof InvocationTargetException) {
                InvocationTargetException ite = (InvocationTargetException) ex;
                Throwable e = ite.getTargetException();
                log.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e.getCause());
            } else {
                log.error(ex.getMessage(), ex);
            }
            throw new RuntimeException(ex);
        }
    }

    public void clear() {
        methodCache.clear();
    }

}
