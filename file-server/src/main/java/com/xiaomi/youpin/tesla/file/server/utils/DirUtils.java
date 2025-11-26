package com.xiaomi.youpin.tesla.file.server.utils;

import com.xiaomi.youpin.tesla.file.server.common.Cons;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.regex.Pattern;

public class DirUtils {

    // Regex pattern to validate directory path - allows alphanumeric, hyphens, and forward slashes
    private static final Pattern VALID_DIRECTORY_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-/]+$");

    public static boolean isValidDirectoryPath(String path) {

        // Check for common directory traversal patterns
        if (path.contains("..") || path.contains("./") || path.contains("/.")) {
            return false;
        }

        // Check if path starts or ends with slash
        if (path.startsWith("/") || path.endsWith("/")) {
            return false;
        }

        // Check if path contains consecutive slashes
        if (path.contains("//")) {
            return false;
        }

        return VALID_DIRECTORY_PATTERN.matcher(path).matches();
    }

    public static String filePath(String userKey, String directoryPath, String name) {
        return dirPath(userKey, directoryPath) + File.separator + name;
    }

    public static String dirPath(String userKey, String directoryPath) {
        // Construct the base path for this user
        String basePath = Cons.DATAPATH + File.separator + userKey;

        // If directory path is provided, append it to the base path
        String targetPath = basePath;
        if (StringUtils.isNotEmpty(directoryPath)) {
            targetPath = basePath + File.separator + directoryPath;
        }

        return targetPath;
    }
}
