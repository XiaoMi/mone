package com.xiaomi.youpin.tesla.ip.util;

import lombok.extern.slf4j.Slf4j;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.service.AthenaCodeService;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class FixCodeUtils {

    private static final Map<String, String> BASIC_CLASSES = new HashMap<>();

    private static final Pattern BO_PATTERN = Pattern.compile("$.\\.bo\\.");

    static {
        // 初始化 Java 基础类集合
        BASIC_CLASSES.put("ArrayList", "java.util.ArrayList");
        BASIC_CLASSES.put("List", "java.util.List");
        BASIC_CLASSES.put("Map", "java.util.Map");
        BASIC_CLASSES.put("HashMap", "java.util.HashMap");
        BASIC_CLASSES.put("Serializable", "java.io.Serializable");
    }

    public static boolean isBO(String fqcn) {
        Matcher matcher = BO_PATTERN.matcher(fqcn);
        return matcher.matches();
    }

    public static String addDataAnnotationForClass(String code, AthenaClassInfo athenaClassInfo) {
        //检查import里有没有lombok.Data, 没有则添加import lombok.Data;
        if (athenaClassInfo.getImports() != null
                && !athenaClassInfo.getImports().contains("lombok.Data")) {
            code = addImportStatements(code, Arrays.asList("import lombok.Data;"));
        }

        //检查annotations里有没有Data, 没有则添加@Data
        if (athenaClassInfo.getAnnoList() != null
                && !athenaClassInfo.getAnnoList().contains("Data")) {
            code = addAnnotationStatements(code, Arrays.asList("@Data"));
        }

        return code;
    }

    public static String addBasicImportForClass(String code, AthenaClassInfo athenaClassInfo) {
        List<String> existImports = (athenaClassInfo.getImports() == null) ? new ArrayList<>() : athenaClassInfo.getImports();
        List<String> needImports = analyzeBasicClassesUsed(code);

        List<String> importStatements = needImports.stream().filter(it -> !existImports.contains(it)).map(it -> {
            return "import " + it + ";";
        }).collect(Collectors.toList());

        return addImportStatements(code, importStatements);
    }

    private static List<String> analyzeBasicClassesUsed(String code) {
        return BASIC_CLASSES.entrySet().stream().filter(it -> code.contains(it.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    private static String addImportStatements(String code, List<String> importStatements) {
        if (importStatements == null || importStatements.size() == 0) {
            return code;
        }

        int packageIndex = code.indexOf("package ");
        int importIndex = code.indexOf("import ");
        String tmpImport = importStatements.stream()
                .map(importStatement -> importStatement + "\n")
                .collect(Collectors.joining());
        // 如果已经存在 import 语句，则将 import 语句插入到第一个 import 语句之前
        if (importIndex != -1) {
            return code.substring(0, importIndex) + tmpImport + code.substring(importIndex);
        } else if (packageIndex != -1) {
            // 如果不存在 import 语句但存在 package 语句，则将 import 语句插入到 package 语句的下一行
            int lineEndIndex = code.indexOf(';', packageIndex);
            return code.substring(0, lineEndIndex + 1) + "\n" + tmpImport + code.substring(lineEndIndex + 1);
        } else {
            // 如果都不存在，则将 import 语句插入到最前面
            return tmpImport + code;
        }
    }

    private static String addAnnotationStatements(String code, List<String> annotations) {
        if (annotations == null || annotations.size() == 0) {
            return code;
        }

        String tmpAnnotation = annotations.stream()
                .map(importStatement -> importStatement + "\n")
                .collect(Collectors.joining());
        // 在类定义上方添加注解
        int index = code.indexOf("public class");
        if (index != -1) {
            code = code.substring(0, index) + tmpAnnotation + code.substring(index);
        }
        return code;
    }

}
