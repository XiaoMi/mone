package run.mone.mcp.cursor.miapi.parser;

import lombok.extern.slf4j.Slf4j;
import run.mone.mcp.cursor.miapi.model.ParameterInfo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 返回类型字段解析器
 * 用于解析返回类型的字段信息
 */
@Slf4j
public class ReturnTypeFieldParser {
    
    /**
     * 解析返回类型的字段信息
     * @param returnType 返回类型
     * @return 字段信息列表
     */
    public static List<ParameterInfo> parseReturnFields(Class<?> returnType) {
        List<ParameterInfo> fields = new ArrayList<>();
        
        if (returnType == null || isBasicType(returnType)) {
            return fields;
        }
        
        try {
            // 处理泛型类型
            if (returnType.isAssignableFrom(List.class) || 
                returnType.isAssignableFrom(java.util.Collection.class)) {
                // 对于List类型，解析其泛型参数
                return fields; // List类型暂时不解析内部字段
            }
            
            // 处理自定义类
            if (isCustomClass(returnType)) {
                parseClassFields(returnType, fields, "");
            }
            
        } catch (Exception e) {
            log.error("解析返回类型字段失败: {}", e.getMessage());
        }
        
        return fields;
    }
    
    /**
     * 解析类的字段
     */
    private static void parseClassFields(Class<?> clazz, List<ParameterInfo> fields, String prefix) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        
        // 解析当前类的字段
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (shouldIncludeField(field)) {
                ParameterInfo fieldInfo = createFieldInfo(field, prefix);
                fields.add(fieldInfo);
                
                // 如果是自定义类，递归解析其字段
                if (isCustomClass(field.getType())) {
                    parseClassFields(field.getType(), fields, prefix + field.getName() + ".");
                }
            }
        }
        
        // 解析父类的字段
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            parseClassFields(clazz.getSuperclass(), fields, prefix);
        }
    }
    
    /**
     * 创建字段信息
     */
    private static ParameterInfo createFieldInfo(Field field, String prefix) {
        ParameterInfo fieldInfo = new ParameterInfo();
        fieldInfo.setName(prefix + field.getName());
        fieldInfo.setType(getFieldTypeName(field));
        fieldInfo.setDescription("字段: " + field.getName());
        fieldInfo.setRequired(false); // 返回字段默认非必填
        fieldInfo.setPosition("return");
        
        // 设置泛型类型信息
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                StringBuilder genericType = new StringBuilder();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    if (i > 0) genericType.append(", ");
                    genericType.append(actualTypeArguments[i].getTypeName());
                }
                fieldInfo.setGenericType(genericType.toString());
            }
        }
        
        return fieldInfo;
    }
    
    /**
     * 获取字段类型名称
     */
    private static String getFieldTypeName(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            StringBuilder typeName = new StringBuilder();
            typeName.append(parameterizedType.getRawType().getTypeName());
            typeName.append("<");
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                if (i > 0) typeName.append(", ");
                typeName.append(actualTypeArguments[i].getTypeName());
            }
            typeName.append(">");
            return typeName.toString();
        } else {
            return field.getType().getSimpleName();
        }
    }
    
    /**
     * 判断是否应该包含该字段
     */
    private static boolean shouldIncludeField(Field field) {
        // 排除静态字段
        if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        
        // 排除final字段（常量）
        if (java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
            return false;
        }
        
        // 排除transient字段
        if (java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 判断是否是基本类型
     */
    private static boolean isBasicType(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz == String.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Double.class ||
               clazz == Float.class ||
               clazz == Boolean.class ||
               clazz == Character.class ||
               clazz == Byte.class ||
               clazz == Short.class ||
               clazz == java.util.Date.class ||
               clazz == java.time.LocalDateTime.class ||
               clazz == java.time.LocalDate.class ||
               clazz == java.math.BigDecimal.class ||
               clazz == java.math.BigInteger.class;
    }
    
    /**
     * 判断是否是自定义类
     */
    private static boolean isCustomClass(Class<?> clazz) {
        if (clazz == null || clazz.isPrimitive() || clazz.isArray()) {
            return false;
        }
        
        // 排除Java标准库类
        String packageName = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
        if (packageName.startsWith("java.") || 
            packageName.startsWith("javax.") ||
            packageName.startsWith("sun.") ||
            packageName.startsWith("com.sun.") ||
            packageName.startsWith("org.springframework.") ||
            packageName.startsWith("com.fasterxml.jackson.")) {
            return false;
        }
        
        return true;
    }
}
