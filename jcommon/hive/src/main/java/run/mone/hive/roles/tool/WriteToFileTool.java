package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件写入工具，用于创建新文件或覆盖现有文件的内容
 * 
 * 此工具会：
 * 1. 如果文件存在，将用提供的内容覆盖它
 * 2. 如果文件不存在，将创建它
 * 3. 自动创建写入文件所需的任何目录
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class WriteToFileTool implements ITool {

    public static final String name = "write_to_file";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                用于将内容写入指定路径的文件的工具。如果文件存在，将用提供的内容覆盖它。
                如果文件不存在，将创建它。此工具会自动创建写入文件所需的任何目录。
                
                **使用时机：**
                - 创建新的源代码文件
                - 创建配置文件
                - 创建文档文件
                - 完全重写现有文件的内容
                - 创建脚本文件
                - 生成任何类型的文本文件
                
                **重要说明：**
                - 此工具会完全覆盖现有文件的内容
                - 如果只需要修改文件的部分内容，请使用replace_in_file工具
                - 必须提供文件的完整预期内容，不能有任何截断或省略
                - 必须包含文件的所有部分，即使它们没有被修改
                
                **输出：** 工具将返回操作结果，包括成功信息或错误信息。
                """;
    }

    @Override
    public String parameters() {
        return """
                - path: (必需) 要写入的文件路径（相对于当前工作目录）
                - content: (必需) 要写入文件的内容。必须始终提供文件的完整预期内容，
                           不能有任何截断或省略。必须包含文件的所有部分，即使它们没有被修改。
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
            <task_progress>
            任务进度清单（可选）
            </task_progress>
            """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
            <write_to_file>
            <path>文件路径</path>
            <content>
            您的文件内容
            </content>
            %s
            </write_to_file>
            """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例1: 创建一个简单的Java类文件
                <write_to_file>
                <path>src/main/java/com/example/HelloWorld.java</path>
                <content>
                package com.example;
                
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                </content>
                </write_to_file>
                
                示例2: 创建一个配置文件
                <write_to_file>
                <path>config/application.properties</path>
                <content>
                server.port=8080
                spring.datasource.url=jdbc:mysql://localhost:3306/mydb
                spring.datasource.username=root
                spring.datasource.password=password
                </content>
                </write_to_file>
                
                示例3: 创建一个HTML文件
                <write_to_file>
                <path>index.html</path>
                <content>
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>My Website</title>
                </head>
                <body>
                    <h1>Welcome to My Website</h1>
                    <p>This is a sample HTML page.</p>
                </body>
                </html>
                </content>
                </write_to_file>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 检查必要参数
            if (!inputJson.has("path") || StringUtils.isBlank(inputJson.get("path").getAsString())) {
                log.error("write_to_file操作缺少必需的path参数");
                result.addProperty("error", "缺少必需参数'path'");
                return result;
            }

            if (!inputJson.has("content")) {
                log.error("write_to_file操作缺少必需的content参数");
                result.addProperty("error", "缺少必需参数'content'");
                return result;
            }

            String path = inputJson.get("path").getAsString();
            String content = inputJson.get("content").getAsString();

            return performWriteToFile(path, content);

        } catch (Exception e) {
            log.error("执行write_to_file操作时发生异常", e);
            result.addProperty("error", "执行write_to_file操作失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 执行文件写入操作
     */
    private JsonObject performWriteToFile(String path, String content) {
        JsonObject result = new JsonObject();

        try {
            Path filePath = Paths.get(path);
            File file = filePath.toFile();
            
            // 检查路径是否指向一个目录
            if (file.exists() && file.isDirectory()) {
                log.error("指定的路径是一个目录，无法写入文件：{}", path);
                result.addProperty("error", "指定的路径是一个目录，无法写入文件: " + path);
                return result;
            }

            boolean fileExisted = file.exists();
            
            // 自动创建必要的父目录
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                if (!dirCreated) {
                    log.error("无法创建父目录：{}", parentDir.getAbsolutePath());
                    result.addProperty("error", "无法创建父目录: " + parentDir.getAbsolutePath());
                    return result;
                }
                log.info("成功创建父目录：{}", parentDir.getAbsolutePath());
            }

            // 处理内容预处理（移除可能的代码块标记等）
            String processedContent = preprocessContent(content);
            
            // 写入文件内容
            Files.writeString(filePath, processedContent, StandardCharsets.UTF_8, 
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            String operation = fileExisted ? "覆盖" : "创建";
            log.info("成功{}文件：{}，内容长度：{} 字符", operation, path, processedContent.length());
            
            result.addProperty("result", 
                String.format("文件已成功%s: %s", operation, path));
            result.addProperty("operation", operation);
            result.addProperty("path", path);
            result.addProperty("contentLength", processedContent.length());
            result.addProperty("fileExisted", fileExisted);

        } catch (IOException e) {
            log.error("写入文件时发生IO异常：{}", path, e);
            result.addProperty("error", "文件写入失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("写入文件时发生异常：{}", path, e);
            result.addProperty("error", "文件写入失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 预处理文件内容
     * 移除可能的markdown代码块标记等
     */
    private String preprocessContent(String content) {
        if (content == null) {
            return "";
        }
        
        String processed = content;
        
        // 移除开头的markdown代码块标记（如果存在）
        if (processed.startsWith("```")) {
            String[] lines = processed.split("\n");
            if (lines.length > 1) {
                // 跳过第一行（```language）
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < lines.length; i++) {
                    if (i > 1) {
                        sb.append("\n");
                    }
                    sb.append(lines[i]);
                }
                processed = sb.toString();
            }
        }
        
        // 移除结尾的markdown代码块标记（如果存在）
        if (processed.endsWith("```")) {
            String[] lines = processed.split("\n");
            if (lines.length > 1) {
                // 跳过最后一行（```）
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < lines.length - 1; i++) {
                    if (i > 0) {
                        sb.append("\n");
                    }
                    sb.append(lines[i]);
                }
                processed = sb.toString();
            }
        }
        
        // 移除末尾多余的换行符（保留一个换行符，如果原本就有的话）
        processed = processed.replaceAll("\\n+$", "");
        
        return processed;
    }

    /**
     * 验证文件路径的安全性
     * 防止路径遍历攻击等安全问题
     */
    private boolean isPathSafe(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        
        // 检查路径遍历攻击
        if (path.contains("..") || path.contains("~")) {
            return false;
        }
        
        // 检查绝对路径（在某些情况下可能不安全）
        if (path.startsWith("/") || (path.length() > 1 && path.charAt(1) == ':')) {
            log.warn("检测到绝对路径，建议使用相对路径：{}", path);
        }
        
        return true;
    }

    /**
     * 获取文件的MIME类型（用于日志记录）
     */
    private String getFileType(String path) {
        if (path == null) {
            return "unknown";
        }
        
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith(".java")) return "java";
        if (lowerPath.endsWith(".js") || lowerPath.endsWith(".ts")) return "javascript/typescript";
        if (lowerPath.endsWith(".html") || lowerPath.endsWith(".htm")) return "html";
        if (lowerPath.endsWith(".css")) return "css";
        if (lowerPath.endsWith(".json")) return "json";
        if (lowerPath.endsWith(".xml")) return "xml";
        if (lowerPath.endsWith(".yml") || lowerPath.endsWith(".yaml")) return "yaml";
        if (lowerPath.endsWith(".properties")) return "properties";
        if (lowerPath.endsWith(".md")) return "markdown";
        if (lowerPath.endsWith(".txt")) return "text";
        
        return "unknown";
    }
}
