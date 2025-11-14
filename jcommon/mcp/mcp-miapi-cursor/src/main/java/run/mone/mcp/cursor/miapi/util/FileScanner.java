package run.mone.mcp.cursor.miapi.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class FileScanner {

    /**
     * 扫描目录下的所有Java文件
     * @param directoryPath 目录路径
     * @return Java文件路径列表
     */
    public static List<String> scanJavaFiles(String directoryPath) {
        List<String> javaFiles = new ArrayList<>();

        try {
            Path startPath = Paths.get(directoryPath);
            if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
                log.error("目录不存在或不是有效目录: {}", directoryPath);
                return javaFiles;
            }

            try (Stream<Path> paths = Files.walk(startPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".java"))
                        .forEach(path -> javaFiles.add(path.toString()));
            }

        } catch (IOException e) {
            log.error("扫描目录失败: {}", e.getMessage());
        }

        return javaFiles;
    }

    /**
     * 扫描目录下的所有Java文件（递归）
     * @param directoryPath 目录路径
     * @param recursive 是否递归扫描子目录
     * @return Java文件路径列表
     */
    public static List<String> scanJavaFiles(String directoryPath, boolean recursive) {
        List<String> javaFiles = new ArrayList<>();

        try {
            Path startPath = Paths.get(directoryPath);
            if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
                log.error("目录不存在或不是有效目录: {}", directoryPath);
                return javaFiles;
            }

            if (recursive) {
                try (Stream<Path> paths = Files.walk(startPath)) {
                    paths.filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".java"))
                            .forEach(path -> javaFiles.add(path.toString()));
                }
            } else {
                try (Stream<Path> paths = Files.list(startPath)) {
                    paths.filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".java"))
                            .forEach(path -> javaFiles.add(path.toString()));
                }
            }

        } catch (IOException e) {
            log.error("扫描目录失败: {}", e.getMessage());
        }

        return javaFiles;
    }

    /**
     * 检查文件是否为Java文件
     * @param filePath 文件路径
     * @return 是否为Java文件
     */
    public static boolean isJavaFile(String filePath) {
        return filePath != null && filePath.endsWith(".java");
    }

    /**
     * 从文件路径获取包名
     * @param filePath 文件路径
     * @param sourceRoot 源码根目录
     * @return 包名
     */
    public static String getPackageFromPath(String filePath, String sourceRoot) {
        try {
            Path file = Paths.get(filePath);
            Path root = Paths.get(sourceRoot);

            Path relativePath = root.relativize(file);
            String packagePath = relativePath.toString().replace(File.separator, ".");

            // 移除.java扩展名
            if (packagePath.endsWith(".java")) {
                packagePath = packagePath.substring(0, packagePath.length() - 5);
            }

            return packagePath;
        } catch (Exception e) {
            log.error("获取包名失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 从文件路径获取类名
     * @param filePath 文件路径
     * @return 类名
     */
    public static String getClassNameFromPath(String filePath) {
        try {
            Path file = Paths.get(filePath);
            String fileName = file.getFileName().toString();

            if (fileName.endsWith(".java")) {
                return fileName.substring(0, fileName.length() - 5);
            }

            return fileName;
        } catch (Exception e) {
            log.error("获取类名失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 检查目录是否存在
     * @param directoryPath 目录路径
     * @return 目录是否存在
     */
    public static boolean isDirectoryExists(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            return Files.exists(path) && Files.isDirectory(path);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取目录下的所有文件（包括子目录）
     * @param directoryPath 目录路径
     * @return 所有文件路径列表
     */
    public static List<String> getAllFiles(String directoryPath) {
        List<String> allFiles = new ArrayList<>();

        try {
            Path startPath = Paths.get(directoryPath);
            if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
                log.error("目录不存在或不是有效目录: {}", directoryPath);
                return allFiles;
            }

            try (Stream<Path> paths = Files.walk(startPath)) {
                paths.filter(Files::isRegularFile)
                        .forEach(path -> allFiles.add(path.toString()));
            }

        } catch (IOException e) {
            log.error("扫描目录失败: {}", e.getMessage());
        }

        return allFiles;
    }

    public static boolean createDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists()) {
            return true;
        }

        boolean result = directory.mkdirs();
        if (!result) {
            log.error("目录创建失败: {}", directoryPath);
        }
        return result;
    }

    public static boolean deleteDirectory(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder()) // 先删除文件，再删除目录
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            return true;
        } catch (IOException e) {
            log.error("删除目录失败: {}", e.getMessage());
            return false;
        }
    }
}
