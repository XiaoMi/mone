/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.dubbo.docs.utils;


import java.lang.reflect.*;
import java.util.*;
/**
 * Java class tool class, special for Dubbo doc.
 */
public class ClassTypeUtil {
    /**
     * Check if it is a basic data type.
     *
     * @param clazz
     * @return boolean
     */
    public static boolean isBaseType(Class<?> clazz) {
        if (Objects.isNull(clazz.getTypeName()) ||
                "".equals(clazz.getTypeName()) ||
                clazz.getTypeName().startsWith("java.math") ||
                clazz.getTypeName().startsWith("java.lang") ||
                clazz.getTypeName().startsWith("java.util.Date") ||
                clazz.getTypeName().startsWith("java.time.LocalDateTime") ||
                clazz.getTypeName().startsWith("java.time.LocalDate")||
                clazz.getTypeName().startsWith("java.sql")
        ) {
            return true;
        }
        try {
            if (clazz.isPrimitive()) {
                return true;
            }
            return ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all fields in the class.
     *
     * @param fieldList fieldList
     * @param classz    classz
     * @return java.util.List<java.lang.reflect.Field>
     */
    public static List<Field> getAllFields(List<Field> fieldList, Class<?> classz) {
        if (classz == null) {
            return fieldList;
        }
        if (fieldList == null) {
            fieldList = new ArrayList<>(Arrays.asList(classz.getDeclaredFields()));
        } else {
            fieldList.addAll(Arrays.asList(classz.getDeclaredFields()));
        }
        return getAllFields(fieldList, classz.getSuperclass());
    }
}
