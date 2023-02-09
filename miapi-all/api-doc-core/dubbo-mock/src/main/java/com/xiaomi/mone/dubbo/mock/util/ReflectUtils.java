/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.dubbo.mock.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ReflectUtils
 */
public final class ReflectUtils {

    /**
     * void.
     */
    public static final char JVM_VOID = 'V';

    /**
     * boolean(Z).
     */
    public static final char JVM_BOOLEAN = 'Z';

    /**
     * byte(B).
     */
    public static final char JVM_BYTE = 'B';

    /**
     * char(C).
     */
    public static final char JVM_CHAR = 'C';

    /**
     * double(D).
     */
    public static final char JVM_DOUBLE = 'D';

    /**
     * float(F).
     */
    public static final char JVM_FLOAT = 'F';

    /**
     * int(I).
     */
    public static final char JVM_INT = 'I';

    /**
     * long(J).
     */
    public static final char JVM_LONG = 'J';

    /**
     * short(S).
     */
    public static final char JVM_SHORT = 'S';


    private static final ConcurrentMap<String, Class<?>> NAME_CLASS_CACHE = new ConcurrentHashMap<String, Class<?>>();


    private ReflectUtils() {
    }


    public static Class<?> forName(String name) {
        try {
            return name2class(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Not found class " + name + ", cause: " + e.getMessage(), e);
        }
    }


    /**
     * name to class.
     * "boolean" => boolean.class
     * "java.util.Map[][]" => java.util.Map[][].class
     */
    public static Class<?> name2class(String name) throws ClassNotFoundException {
        return name2class(ClassHelper.getClassLoader(), name);
    }

    /**
     * name to class.
     * "boolean" => boolean.class
     * "java.util.Map[][]" => java.util.Map[][].class
     *
     */
    private static Class<?> name2class(ClassLoader cl, String name) throws ClassNotFoundException {
        int c = 0, index = name.indexOf('[');
        if (index > 0) {
            c = (name.length() - index) / 2;
            name = name.substring(0, index);
        }
        if (c > 0) {
            StringBuilder sb = new StringBuilder();
            while (c-- > 0) {
                sb.append("[");
            }

            switch (name) {
                case "void":
                    sb.append(JVM_VOID);
                    break;
                case "boolean":
                    sb.append(JVM_BOOLEAN);
                    break;
                case "byte":
                    sb.append(JVM_BYTE);
                    break;
                case "char":
                    sb.append(JVM_CHAR);
                    break;
                case "double":
                    sb.append(JVM_DOUBLE);
                    break;
                case "float":
                    sb.append(JVM_FLOAT);
                    break;
                case "int":
                    sb.append(JVM_INT);
                    break;
                case "long":
                    sb.append(JVM_LONG);
                    break;
                case "short":
                    sb.append(JVM_SHORT);
                    break;
                default:
                    // "java.lang.Object" ==> "Ljava.lang.Object;"
                    sb.append('L').append(name).append(';');
                    break;
            }
            name = sb.toString();
        } else {
            if ("void".equals(name)) {
                return void.class;
            }
            if ("boolean".equals(name)) {
                return boolean.class;
            }
            if ("byte".equals(name)) {
                return byte.class;
            }
            if ("char".equals(name)) {
                return char.class;
            }
            if ("double".equals(name)) {
                return double.class;
            }
            if ("float".equals(name)) {
                return float.class;
            }
            if ("int".equals(name)) {
                return int.class;
            }
            if ("long".equals(name)) {
                return long.class;
            }
            if ("short".equals(name)) {
                return short.class;
            }
        }

        if (cl == null) {
            cl = ClassHelper.getClassLoader();
        }
        Class<?> clazz = NAME_CLASS_CACHE.get(name);
        if (clazz == null) {
            clazz = Class.forName(name, true, cl);
            NAME_CLASS_CACHE.put(name, clazz);
        }
        return clazz;
    }

}