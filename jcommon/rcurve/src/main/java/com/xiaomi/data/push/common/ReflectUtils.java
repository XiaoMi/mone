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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.xiaomi.data.push.uds.po.UdsCommand;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/8/21
 */
@Slf4j
public abstract class ReflectUtils {

    private static Gson gson = new Gson();

    public static Object invokeMethod(UdsCommand req, Object obj) {

        return invokeMethod(req.getMethodName(), obj, req.getParamTypes(), req.getSerializeType() == 0 ?
                        Arrays.stream(req.getParams()).map(it -> it.getBytes()).toArray(byte[][]::new) :
                        req.getByteParams(),
                req.getSerializeType());
    }

    public static Object invokeMethod(String methodName, Object obj, String[] types, String[] paramArray) {
        return invokeMethod(methodName, obj, types, Arrays.stream(paramArray).map(it -> it.getBytes()).toArray(byte[][]::new), 0);
    }

    public static Object invokeMethod(String methodName, Object obj, String[] types, byte[][] paramArray, int type) {
        try {
            if (types.length > 0) {
                Class[] clazzArray = Arrays.stream(types).map(i -> {
                    try {
                        return Class.forName(i);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("class forName error:" + e.getMessage());
                    }
                }).toArray(Class[]::new);
                Method method = obj.getClass().getMethod(methodName, clazzArray);
                Object[] params = null;
                if (type == 0) {
                    params = convert(clazzArray, paramArray);
                }
                if (type == 1) {
                    params = convertMsgpack(clazzArray, paramArray);
                }
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

    /**
     * json 转换
     *
     * @param classes
     * @param params
     * @return
     */
    public static Object[] convert(Class[] classes, byte[][] params) {
        Object[] res = IntStream.range(0, classes.length).mapToObj(i -> gson.fromJson(new String(params[i]), classes[i])).toArray();
        return res;
    }


    public static Object[] convertMsgpack(Class[] classes, byte[][] params) {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        Object[] res = IntStream.range(0, classes.length).mapToObj(i -> {
            try {
                return objectMapper.readValue(params[i], classes[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).toArray();
        return res;
    }

}
