package run.mone.mcp.cursor.miapi.util;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class TypeExtractorUtil {

    private static final Set<String> javaInternalTypes = new HashSet<>(Arrays.asList(
            // 基本类型
            "byte", "short", "int", "long", "float", "double", "boolean", "char",
            "void",

            // 包装类
            "Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean", "Character", "Void",

            // 常用java.lang包中的类
            "Object", "String", "Class", "Package", "Enum", "Throwable", "Exception", "Error",
            "RuntimeException", "NullPointerException", "IllegalArgumentException",
            "Number", "Math", "System", "Thread", "Runnable", "Comparable",

            // 常用java.util包中的类
            "List", "ArrayList", "LinkedList", "Set", "HashSet", "TreeSet", "Map", "HashMap",
            "TreeMap", "Collection", "Arrays", "Collections", "Iterator", "Date", "Calendar",

            // 常用java.io包中的类
            "File", "InputStream", "OutputStream", "Reader", "Writer",

            // 其他常用JDK类
            "BigInteger", "BigDecimal", "Pattern", "Matcher", "LocalDate", "LocalDateTime"
    ));

    public static boolean isInternalType (Type type) {
        if (type.isPrimitiveType()) {
            return true;
        }
        if (type.isClassOrInterfaceType()) {
            String nameAsString = ((ClassOrInterfaceType) type).getNameAsString();
            return javaInternalTypes.contains(nameAsString);
        }

        return false;
    }

    public static String getPrimitiveSimpleName(Type type) {
        if (type.isClassOrInterfaceType()) {
            return  ((ClassOrInterfaceType) type).getNameAsString();
        }
        return type.asString();
    }

    public static String typeStr2TypeNo(String classType) {
        if (null == classType) {
            return "";
        }
        if (classType.equals("Integer") || classType.equals("int")) {
            return Type2NoEnum.INT_NO.getValue();
        } else if (classType.equals("Byte") || classType.equals("byte")) {
            return Type2NoEnum.BYTE_NO.getValue();
        } else if (classType.equals("Long") || classType.equals("long")) {
            return Type2NoEnum.LONG_NO.getValue();
        } else if (classType.equals("Double") || classType.equals("double")) {
            return Type2NoEnum.LONG_NO.getValue();
        } else if (classType.equals("Float") || classType.equals("float")) {
            return Type2NoEnum.FLOAT_NO.getValue();
        } else if (classType.equals("String")) {
            return Type2NoEnum.STRING_NO.getValue();
        } else if (classType.equals("Character") || classType.equals("char")) {
            return Type2NoEnum.STRING_NO.getValue();
        } else if (classType.equals("Short") || classType.equals("short")) {
            return Type2NoEnum.SHORT_NO.getValue();
        } else if (classType.equals("Boolean") || classType.equals("boolean")) {
            return Type2NoEnum.BOOLEAN_NO.getValue();
        } else if (classType.equals("Date")) {
            return Type2NoEnum.DATE_NO.getValue();
        } else if (classType.equals("LocalDate") || classType.equals("LocalDateTime")) {
            return Type2NoEnum.DATETIME_NO.getValue();
        } else if (classType.equals("List") || classType.equals("ArrayList")) {
            return Type2NoEnum.ARRAY_NO.getValue();
        } else {
            return Type2NoEnum.OBJ_NO.getValue();
        }
    }
}
