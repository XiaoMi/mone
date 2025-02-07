package run.mone.m78.service.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/20/24 18:56
 */
public class SqlTypeClassifier {

    // 定义SQL数据类型到分类的映射
    private static final Map<String, String> typeClassification = new HashMap<>();
    private static final Map<String, String> defaultClassifyType = new HashMap<>();

    static {
        // 数字类型
        typeClassification.put("TINYINT", "NUMBER");
        typeClassification.put("SMALLINT", "NUMBER");
        typeClassification.put("MEDIUMINT", "NUMBER");
        typeClassification.put("INT", "NUMBER");
        typeClassification.put("BIGINT", "NUMBER");
        typeClassification.put("FLOAT", "NUMBER");
        typeClassification.put("DOUBLE", "NUMBER");
        typeClassification.put("DECIMAL", "NUMBER");
        typeClassification.put("NUMERIC", "NUMBER");

        // 字符串类型
        typeClassification.put("CHAR", "STRING");
        typeClassification.put("VARCHAR", "STRING");
        typeClassification.put("TEXT", "STRING");
        typeClassification.put("MEDIUMTEXT", "STRING");
        typeClassification.put("LONGTEXT", "STRING");
        typeClassification.put("BINARY", "STRING");
        typeClassification.put("VARBINARY", "STRING");

        // JSON 类型（假设存在，如MySQL）
        typeClassification.put("JSON", "JSON");

        // 日期和时间类型
        typeClassification.put("DATE", "DATE");
        typeClassification.put("TIME", "DATE");
        typeClassification.put("DATETIME", "DATE");
        typeClassification.put("TIMESTAMP", "DATE");
        typeClassification.put("YEAR", "DATE");

        // 设置默认类型映射
        defaultClassifyType.put("Number", "BIGINT");
        defaultClassifyType.put("Integer", "INT");
        defaultClassifyType.put("Time", "DATETIME");
        defaultClassifyType.put("String", "TEXT");
        defaultClassifyType.put("Boolean", "BOOLEAN");
    }


    /**
     * 根据SQL数据类型名称进行分类
     *
     * @param type SQL数据类型的名称
     * @return 数据类型的分类（数字、字符串、JSON、日期）
     */
    public static String classifyType(String type) {
        String mark = extractTypePrefix(type.toUpperCase());
        return typeClassification.getOrDefault(mark, "未知");
    }

    // 处理输入的字符串, 找到从左到右的第一个括号, 并获取第一个括号前的子字符串
    public static String extractTypePrefix(String type) {
        int bracketIndex = type.indexOf('(');
        if (bracketIndex != -1) {
            return type.substring(0, bracketIndex).trim();
        }
        return type.trim();
    }

    public static String getDefaultTypeMapping(String typeMark) {
        return defaultClassifyType.getOrDefault(typeMark, typeMark);
    }
}