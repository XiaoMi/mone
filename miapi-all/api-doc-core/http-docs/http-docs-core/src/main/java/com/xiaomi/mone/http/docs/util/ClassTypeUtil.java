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
package com.xiaomi.mone.http.docs.util;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        if ("".equals(clazz.getTypeName()) || clazz.getTypeName().startsWith("java.math") || clazz.getTypeName().startsWith("java.lang") || clazz.getTypeName().startsWith("java.util.Date") || clazz.getTypeName().startsWith("java.time.LocalDateTime") || clazz.getTypeName().startsWith("java.time.LocalDate")
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

    public static boolean isIgnoreType(Class<?> clazz) {
        return clazz.getTypeName().startsWith("javax.servlet.http.HttpServletRequest")
                || clazz.getTypeName().startsWith("javax.servlet.http.HttpServletResponse");
    }

    /**
     * Get all fields in the class.
     *
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

    public static String typeStr2TypeNo(Class<?> classType) {
        if (null == classType) {
            return "";
        }
        if (Integer.class.isAssignableFrom(classType) || int.class.isAssignableFrom(classType)) {
            return Type2NoEnum.INT_NO.getValue();
        } else if (Byte.class.isAssignableFrom(classType) || byte.class.isAssignableFrom(classType)) {
            return Type2NoEnum.BYTE_NO.getValue();
        } else if (Long.class.isAssignableFrom(classType) || long.class.isAssignableFrom(classType)) {
            return Type2NoEnum.LONG_NO.getValue();
        } else if (Double.class.isAssignableFrom(classType) || double.class.isAssignableFrom(classType)) {
            return Type2NoEnum.LONG_NO.getValue();
        } else if (Float.class.isAssignableFrom(classType) || float.class.isAssignableFrom(classType)) {
            return Type2NoEnum.FLOAT_NO.getValue();
        } else if (String.class.isAssignableFrom(classType)) {
            return Type2NoEnum.STRING_NO.getValue();
        } else if (Character.class.isAssignableFrom(classType) || char.class.isAssignableFrom(classType)) {
            return Type2NoEnum.STRING_NO.getValue();
        } else if (Short.class.isAssignableFrom(classType) || short.class.isAssignableFrom(classType)) {
            return Type2NoEnum.SHORT_NO.getValue();
        } else if (Boolean.class.isAssignableFrom(classType) || boolean.class.isAssignableFrom(classType)) {
            return Type2NoEnum.BOOLEAN_NO.getValue();
        } else if (Date.class.isAssignableFrom(classType)) {
            return Type2NoEnum.DATE_NO.getValue();
        } else if (LocalDate.class.isAssignableFrom(classType) || LocalDateTime.class.isAssignableFrom(classType)) {
            return Type2NoEnum.DATETIME_NO.getValue();
        } else if (List.class.isAssignableFrom(classType) || classType.isArray()) {
            return Type2NoEnum.ARRAY_NO.getValue();
        } else {
            return Type2NoEnum.OBJ_NO.getValue();
        }
    }
}
