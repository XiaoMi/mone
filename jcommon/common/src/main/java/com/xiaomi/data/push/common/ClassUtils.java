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

package com.xiaomi.data.push.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangzhiyong
 * @date 09/06/2018
 */
public abstract class ClassUtils {

    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * spring 环境中查询匹配的method
     *
     * @param ac
     * @param annotation
     * @return
     */
    public static List<Method> findMethodWithAnnotation(ApplicationContext ac, Class<? extends Annotation> annotation) {
        List<Method> list = Arrays.stream(ac.getBeanDefinitionNames()).map(n -> ac.getBean(n))
                .map(obj -> {
                    if (AopUtils.isAopProxy(obj)) {
                        Class<?> objClz = AopUtils.getTargetClass(obj);
                        return objClz;
                    }
                    return obj.getClass();
                })
                .flatMap(o -> Arrays.stream(o.getMethods()))
                .filter(m -> m.getAnnotation(annotation) != null)
                .collect(Collectors.toList());
        return list;
    }


    public static String getMethodStr(ProceedingJoinPoint joinPoint) {
        if (joinPoint instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String methodStr = method.toString();
            return methodStr;
        }
        return "";
    }


}
