package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * 文件操作工具，用于执行基本的文件操作
 * @author user
 */
@Slf4j
public class FileOperationTool implements ITool {

    @Override
    public String getName() {
        return "file_operation";
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
            一个用于执行基本文件操作的工具。
            可以用于创建、读取、写入和删除文件。
            
            **使用时机：** 当用户需要对文件系统执行基本操作时使用此工具，比如：
            - 创建新文件或目录
            - 读取文件内容
            - 写入或追加内容到文件
            - 删除文件或目录
            - 检查文件是否存在
            
            **输出：** 工具将返回操作结果，包括成功信息或错误信息。
            """;
    }

    @Override
    public String parameters() {
        return """
                - operation: (必需) 要执行的操作类型。必须是以下之一：
                  - 'create': 创建新文件或目录
                  - 'read': 读取文件内容
                  - 'write': 写入内容到文件（覆盖现有内容）
                  - 'append': 追加内容到文件
                  - 'delete': 删除文件或目录
                  - 'exists': 检查文件或目录是否存在
                  - 'list': 列出目录内容
                
                - path: (必需) 要操作的文件或目录路径
                
                - content: (针对write/append操作必需) 要写入或追加到文件的内容
                
                - is_binary: (可选，用于read/write/append) 是否以二进制模式处理文件。默认为false，设为true时，content会被当作Base64编码或返回为Base64编码
                
                - is_directory: (可选，用于create操作) 是否创建目录而非文件。默认为false
                """;
    }

    @Override
    public String usage() {
        return """
            (注意：如果您使用此工具，必须在 <file_operation> 标签内返回操作结果):
            
            示例1: 创建新文件
            <file_operation>
              <operation>create</operation>
              <path>/path/to/file.txt</path>
              <result>
                文件 /path/to/file.txt 创建成功
              </result>
            </file_operation>
            
            示例2: 读取文件内容
            <file_operation>
              <operation>read</operation>
              <path>/path/to/file.txt</path>
              <result>
                这是文件的内容...
              </result>
            </file_operation>
            
            示例3: 写入文件内容
            <file_operation>
              <operation>write</operation>
              <path>/path/to/file.txt</path>
              <content>这是要写入的新内容</content>
              <result>
                内容已成功写入文件 /path/to/file.txt
              </result>
            </file_operation>
            
            示例4: 列出目录内容
            <file_operation>
              <operation>list</operation>
              <path>/path/to/directory</path>
              <result>
                - file1.txt
                - file2.pdf
                - subdirectory/
              </result>
            </file_operation>
            """;
    }

    @Override
    public JsonObject execute(JsonObject inputJson) {
        JsonObject result = new JsonObject();
        
        try {
            // 检查必要参数
            if (!inputJson.has("operation") || StringUtils.isBlank(inputJson.get("operation").getAsString())) {
                log.error("文件操作请求缺少必需的operation参数");
                result.addProperty("error", "缺少必需参数'operation'");
                return result;
            }
            
            if (!inputJson.has("path") || StringUtils.isBlank(inputJson.get("path").getAsString())) {
                log.error("文件操作请求缺少必需的path参数");
                result.addProperty("error", "缺少必需参数'path'");
                return result;
            }
            
            String operation = inputJson.get("operation").getAsString();
            String path = inputJson.get("path").getAsString();
            boolean isBinary = inputJson.has("is_binary") && inputJson.get("is_binary").getAsBoolean();
            
            log.info("开始执行文件操作，操作类型：{}，文件路径：{}", operation, path);
            
            switch (operation.toLowerCase()) {
                case "create":
                    return createFileOrDirectory(path, inputJson);
                case "read":
                    return readFile(path, isBinary);
                case "write":
                    return writeFile(path, inputJson, false);
                case "append":
                    return writeFile(path, inputJson, true);
                case "delete":
                    return deleteFileOrDirectory(path);
                case "exists":
                    return checkFileExists(path);
                case "list":
                    return listDirectory(path);
                default:
                    log.error("不支持的文件操作：{}", operation);
                    result.addProperty("error", "不支持的操作类型：" + operation);
                    return result;
            }
        } catch (Exception e) {
            log.error("执行文件操作时发生异常", e);
            result.addProperty("error", "执行文件操作失败: " + e.getMessage());
            return result;
        }
    }
    
    private JsonObject createFileOrDirectory(String path, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            boolean isDirectory = inputJson.has("is_directory") && inputJson.get("is_directory").getAsBoolean();
            File file = new File(path);
            
            if (isDirectory) {
                boolean success = file.mkdirs();
                if (success) {
                    log.info("目录创建成功：{}", path);
                    result.addProperty("result", "目录 " + path + " 创建成功");
                } else {
                    log.error("目录创建失败：{}", path);
                    result.addProperty("error", "目录 " + path + " 创建失败");
                }
            } else {
                // 确保父目录存在
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                
                boolean success = file.createNewFile();
                if (success) {
                    log.info("文件创建成功：{}", path);
                    result.addProperty("result", "文件 " + path + " 创建成功");
                } else {
                    log.error("文件创建失败：{}", path);
                    result.addProperty("error", "文件 " + path + " 创建失败");
                }
            }
        } catch (Exception e) {
            log.error("创建文件/目录时发生异常", e);
            result.addProperty("error", "创建失败: " + e.getMessage());
        }
        return result;
    }
    
    private JsonObject readFile(String path, boolean isBinary) {
        JsonObject result = new JsonObject();
        try {
            File file = new File(path);
            
            if (!file.exists()) {
                log.error("要读取的文件不存在：{}", path);
                result.addProperty("error", "文件不存在: " + path);
                return result;
            }
            
            if (file.isDirectory()) {
                log.error("无法读取目录内容：{}", path);
                result.addProperty("error", "路径是一个目录，无法读取内容: " + path);
                return result;
            }
            
            if (isBinary) {
                // 二进制读取并转为Base64
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String base64Content = Base64.getEncoder().encodeToString(fileContent);
                result.addProperty("result", base64Content);
                log.info("成功读取二进制文件：{}，大小：{} 字节", path, fileContent.length);
            } else {
                // 文本读取
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                result.addProperty("result", content);
                log.info("成功读取文本文件：{}，长度：{} 字符", path, content.length());
            }
        } catch (Exception e) {
            log.error("读取文件时发生异常", e);
            result.addProperty("error", "读取失败: " + e.getMessage());
        }
        return result;
    }
    
    private JsonObject writeFile(String path, JsonObject inputJson, boolean append) {
        JsonObject result = new JsonObject();
        try {
            if (!inputJson.has("content")) {
                log.error("写入操作缺少必需的content参数");
                result.addProperty("error", "缺少必需参数'content'");
                return result;
            }
            
            String content = inputJson.get("content").getAsString();
            boolean isBinary = inputJson.has("is_binary") && inputJson.get("is_binary").getAsBoolean();
            
            File file = new File(path);
            
            // 确保父目录存在
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            
            if (isBinary) {
                // 解码Base64并写入
                byte[] binaryContent = Base64.getDecoder().decode(content);
                try (FileOutputStream fos = new FileOutputStream(file, append)) {
                    fos.write(binaryContent);
                }
                log.info("成功{}二进制内容到文件：{}，大小：{} 字节", 
                        append ? "追加" : "写入", path, binaryContent.length);
            } else {
                // 文本写入
                try (FileOutputStream fos = new FileOutputStream(file, append)) {
                    fos.write(content.getBytes(StandardCharsets.UTF_8));
                }
                log.info("成功{}文本内容到文件：{}，长度：{} 字符", 
                        append ? "追加" : "写入", path, content.length());
            }
            
            result.addProperty("result", "内容已成功" + (append ? "追加到" : "写入") + "文件 " + path);
        } catch (Exception e) {
            log.error("写入文件时发生异常", e);
            result.addProperty("error", "写入失败: " + e.getMessage());
        }
        return result;
    }
    
    private JsonObject deleteFileOrDirectory(String path) {
        JsonObject result = new JsonObject();
        try {
            File file = new File(path);
            
            if (!file.exists()) {
                log.warn("要删除的文件/目录不存在：{}", path);
                result.addProperty("result", "文件/目录不存在: " + path);
                return result;
            }
            
            boolean success;
            if (file.isDirectory()) {
                success = deleteDirectory(file);
                if (success) {
                    log.info("目录删除成功：{}", path);
                    result.addProperty("result", "目录 " + path + " 及其内容已成功删除");
                } else {
                    log.error("目录删除失败：{}", path);
                    result.addProperty("error", "目录 " + path + " 删除失败");
                }
            } else {
                success = file.delete();
                if (success) {
                    log.info("文件删除成功：{}", path);
                    result.addProperty("result", "文件 " + path + " 已成功删除");
                } else {
                    log.error("文件删除失败：{}", path);
                    result.addProperty("error", "文件 " + path + " 删除失败");
                }
            }
        } catch (Exception e) {
            log.error("删除文件/目录时发生异常", e);
            result.addProperty("error", "删除失败: " + e.getMessage());
        }
        return result;
    }
    
    private boolean deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return directory.delete();
    }
    
    private JsonObject checkFileExists(String path) {
        JsonObject result = new JsonObject();
        try {
            File file = new File(path);
            boolean exists = file.exists();
            result.addProperty("result", exists);
            result.addProperty("is_directory", file.isDirectory());
            result.addProperty("is_file", file.isFile());
            
            if (exists) {
                result.addProperty("size", file.length());
                result.addProperty("last_modified", file.lastModified());
                result.addProperty("can_read", file.canRead());
                result.addProperty("can_write", file.canWrite());
                result.addProperty("can_execute", file.canExecute());
            }
            
            log.info("检查文件存在：{}，结果：{}", path, exists);
        } catch (Exception e) {
            log.error("检查文件存在时发生异常", e);
            result.addProperty("error", "检查失败: " + e.getMessage());
        }
        return result;
    }
    
    private JsonObject listDirectory(String path) {
        JsonObject result = new JsonObject();
        try {
            File dir = new File(path);
            
            if (!dir.exists()) {
                log.error("要列出内容的目录不存在：{}", path);
                result.addProperty("error", "目录不存在: " + path);
                return result;
            }
            
            if (!dir.isDirectory()) {
                log.error("指定的路径不是目录：{}", path);
                result.addProperty("error", "指定的路径不是目录: " + path);
                return result;
            }
            
            File[] files = dir.listFiles();
            if (files == null) {
                log.error("无法读取目录内容：{}", path);
                result.addProperty("error", "无法读取目录内容: " + path);
                return result;
            }
            
            StringBuilder sb = new StringBuilder();
            for (File file : files) {
                sb.append(file.getName());
                if (file.isDirectory()) {
                    sb.append("/");
                }
                sb.append("\n");
            }
            
            result.addProperty("result", sb.toString());
            log.info("成功列出目录内容：{}，包含 {} 个文件/目录", path, files.length);
        } catch (Exception e) {
            log.error("列出目录内容时发生异常", e);
            result.addProperty("error", "列出目录内容失败: " + e.getMessage());
        }
        return result;
    }
}