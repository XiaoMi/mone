package run.mone.hive.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件操作工具类
 */
public class FileUtils {

    /**
     * 读取Markdown文件内容
     *
     * @param filePath Markdown文件路径
     * @return 文件内容字符串
     * @throws IOException 如果文件不存在或读取失败
     */
    public static String readMarkdownFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    /**
     * 写入Markdown文件内容
     *
     * @param filePath Markdown文件路径
     * @param content 要写入的内容
     * @throws IOException 如果写入失败
     */
    public static void writeMarkdownFile(String filePath, String content) throws IOException {
        // 确保目录存在
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        
        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }
}
