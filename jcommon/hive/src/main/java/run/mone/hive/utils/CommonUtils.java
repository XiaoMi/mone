package run.mone.hive.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Common utility methods
 */
@Slf4j
public class CommonUtils {

    /**
     * Get class name without package
     */
    public static String anyToName(Class<?> clazz) {
        if (clazz == null) {
            return "";
        }
        return clazz.getSimpleName();
    }

    /**
     * Convert collection to string with delimiter
     */
    public static String collectionToString(Collection<?> collection, String delimiter) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        return collection.stream()
            .map(Object::toString)
            .collect(Collectors.joining(delimiter));
    }

    /**
     * Convert map to string with delimiters
     */
    public static String mapToString(Map<?, ?> map, String keyValueDelimiter, String pairDelimiter) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        return map.entrySet().stream()
            .map(e -> e.getKey() + keyValueDelimiter + e.getValue())
            .collect(Collectors.joining(pairDelimiter));
    }

    /**
     * Split string into list
     */
    public static List<String> splitToList(String str, String delimiter) {
        if (str == null || str.isEmpty()) {
            return List.of();
        }
        return List.of(str.split(delimiter));
    }

    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Get stack trace as string
     */
    public static String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        return String.join("\n", throwable.getStackTrace().toString());
    }

    public static String removeComments(String code) {
        return "";
    }
}