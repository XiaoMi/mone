package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

/**
 * 文件操作工具，用于文件的读取和写入操作
 * @author goodjava@qq.com
 */
@Slf4j
public class FileTool implements ITool {

    public static final String name = "file";

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
            一个用于文件和文件夹操作的工具。
            可以用于创建文件并写入内容，读取文件内容，向现有文件追加内容，
            以及列出文件夹内容，创建文件夹，删除文件或文件夹，重命名文件或文件夹。
            
            **使用时机：** 当用户需要执行以下操作时使用此工具：
            - 创建新文件并写入内容
            - 读取文件内容
            - 向现有文件追加内容
            - 覆盖现有文件的内容
            - 列出文件夹中的所有文件和子文件夹
            - 创建新文件夹
            - 删除文件或文件夹
            - 重命名文件或文件夹
            
            **输出：** 工具将返回操作结果，包括成功信息、文件内容或错误信息。
            """;
    }

    @Override
    public String parameters() {
        return """
                - operation: (必需) 要执行的操作。必须是以下之一：
                  - 'read': 读取文件内容
                  - 'write': 写入内容到文件（覆盖现有内容）
                  - 'append': 追加内容到文件
                  - 'list': 列出文件夹中的所有文件和子文件夹
                  - 'mkdir': 创建文件夹
                  - 'delete': 删除文件或文件夹
                  - 'rename': 重命名文件或文件夹
                
                - path: (必需) 要操作的文件或文件夹路径
                
                - content: (针对write/append操作必需) 要写入或追加到文件的内容
                
                - new_path: (针对rename操作必需) 重命名后的新路径
                
                - is_binary: (可选) 是否以二进制模式处理内容。默认为false，设为true时：
                  - 读取时：内容将以Base64编码返回
                  - 写入时：content应为Base64编码的字符串
                """;
    }

    @Override
    public String usage() {
        return """
            (注意：如果您使用此工具，必须在 <file> 标签内返回操作结果):
            
            示例1: 读取文件内容
            <file>
              <operation>read</operation>
              <path>/path/to/file.txt</path>
              <r>
                这是文件的内容...
              </r>
            </file>
            
            示例2: 创建或覆盖文件
            <file>
              <operation>write</operation>
              <path>/path/to/file.txt</path>
              <content>这是要写入的新内容</content>
              <r>
                内容已成功写入文件 /path/to/file.txt
              </r>
            </file>
            
            示例3: 追加内容到文件
            <file>
              <operation>append</operation>
              <path>/path/to/file.txt</path>
              <content>这是要追加的内容</content>
              <r>
                内容已成功追加到文件 /path/to/file.txt
              </r>
            </file>
            
            示例4: 列出文件夹内容
            <file>
              <operation>list</operation>
              <path>/path/to/folder</path>
              <r>
                [文件和文件夹列表]
              </r>
            </file>
            
            示例5: 创建文件夹
            <file>
              <operation>mkdir</operation>
              <path>/path/to/new_folder</path>
              <r>
                文件夹已成功创建: /path/to/new_folder
              </r>
            </file>
            
            示例6: 删除文件或文件夹
            <file>
              <operation>delete</operation>
              <path>/path/to/file_or_folder</path>
              <r>
                已成功删除: /path/to/file_or_folder
              </r>
            </file>
            
            示例7: 重命名文件或文件夹
            <file>
              <operation>rename</operation>
              <path>/path/to/old_name</path>
              <new_path>/path/to/new_name</path>
              <r>
                重命名成功: /path/to/old_name -> /path/to/new_name
              </r>
            </file>
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
            
            switch (operation.toLowerCase()) {
                case "read":
                    return readFile(path, isBinary);
                case "write":
                    if (!inputJson.has("content")) {
                        log.error("写入操作缺少必需的content参数");
                        result.addProperty("error", "写入操作缺少必需参数'content'");
                        return result;
                    }
                    String writeContent = inputJson.get("content").getAsString();
                    return writeToFile(path, writeContent, false, isBinary);
                case "append":
                    if (!inputJson.has("content")) {
                        log.error("追加操作缺少必需的content参数");
                        result.addProperty("error", "追加操作缺少必需参数'content'");
                        return result;
                    }
                    String appendContent = inputJson.get("content").getAsString();
                    return writeToFile(path, appendContent, true, isBinary);
                case "list":
                    return listDirectory(path);
                case "mkdir":
                    return createDirectory(path);
                case "delete":
                    return deleteFileOrDirectory(path);
                case "rename":
                    if (!inputJson.has("new_path") || StringUtils.isBlank(inputJson.get("new_path").getAsString())) {
                        log.error("重命名操作缺少必需的new_path参数");
                        result.addProperty("error", "重命名操作缺少必需参数'new_path'");
                        return result;
                    }
                    String newPath = inputJson.get("new_path").getAsString();
                    return renameFileOrDirectory(path, newPath);
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
    
    /**
     * 读取文件内容
     * 
     * @param path 文件路径
     * @param isBinary 是否以二进制模式读取
     * @return 包含读取结果的JsonObject
     */
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
        } catch (IOException e) {
            log.error("读取文件时发生异常", e);
            result.addProperty("error", "读取失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 写入或追加内容到文件
     * 
     * @param path 文件路径
     * @param content 要写入的内容
     * @param append 是否追加模式
     * @param isBinary 是否以二进制模式写入
     * @return 包含写入结果的JsonObject
     */
    private JsonObject writeToFile(String path, String content, boolean append, boolean isBinary) {
        JsonObject result = new JsonObject();
        File file = new File(path);
        
        try {
            // 确保父目录存在
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            
            // 根据是否为二进制决定写入方式
            if (isBinary) {
                // 解码Base64内容
                byte[] binaryContent = Base64.getDecoder().decode(content);
                
                try (FileOutputStream fos = new FileOutputStream(file, append)) {
                    fos.write(binaryContent);
                }
                
                log.info("成功写入二进制文件：{}，大小：{} 字节", path, binaryContent.length);
                result.addProperty("result", (append ? "内容已成功追加到" : "内容已成功写入") + "文件 " + path);
            } else {
                // 文本写入
                Path filePath = Paths.get(path);
                
                if (append) {
                    Files.write(filePath, content.getBytes(StandardCharsets.UTF_8), 
                            Files.exists(filePath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
                } else {
                    Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
                }
                
                log.info("成功写入文本文件：{}，长度：{} 字符", path, content.length());
                result.addProperty("result", (append ? "内容已成功追加到" : "内容已成功写入") + "文件 " + path);
            }
        } catch (IOException e) {
            log.error("写入文件时发生异常", e);
            result.addProperty("error", "写入失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 列出目录中的所有文件和子目录
     * 
     * @param path 目录路径
     * @return 包含目录内容的JsonObject
     */
    private JsonObject listDirectory(String path) {
        JsonObject result = new JsonObject();
        try {
            File directory = new File(path);
            
            if (!directory.exists()) {
                log.error("要列出内容的目录不存在：{}", path);
                result.addProperty("error", "目录不存在: " + path);
                return result;
            }
            
            if (!directory.isDirectory()) {
                log.error("指定的路径不是目录：{}", path);
                result.addProperty("error", "指定的路径不是目录: " + path);
                return result;
            }
            
            File[] files = directory.listFiles();
            JsonArray fileList = new JsonArray();
            
            if (files != null) {
                for (File file : files) {
                    JsonObject fileInfo = new JsonObject();
                    fileInfo.addProperty("name", file.getName());
                    fileInfo.addProperty("path", file.getPath());
                    fileInfo.addProperty("isDirectory", file.isDirectory());
                    fileInfo.addProperty("size", file.length());
                    fileInfo.addProperty("lastModified", file.lastModified());
                    fileList.add(fileInfo);
                }
            }
            
            result.add("result", fileList);
            log.info("成功列出目录内容：{}，共 {} 个项目", path, fileList.size());
            
        } catch (Exception e) {
            log.error("列出目录内容时发生异常", e);
            result.addProperty("error", "列出目录内容失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 创建目录
     * 
     * @param path 要创建的目录路径
     * @return 包含创建结果的JsonObject
     */
    private JsonObject createDirectory(String path) {
        JsonObject result = new JsonObject();
        try {
            File directory = new File(path);
            
            if (directory.exists()) {
                if (directory.isDirectory()) {
                    log.info("目录已存在：{}", path);
                    result.addProperty("result", "目录已存在: " + path);
                } else {
                    log.error("无法创建目录，路径已存在且为文件：{}", path);
                    result.addProperty("error", "无法创建目录，路径已存在且为文件: " + path);
                }
                return result;
            }
            
            boolean success = directory.mkdirs();
            if (success) {
                log.info("成功创建目录：{}", path);
                result.addProperty("result", "文件夹已成功创建: " + path);
            } else {
                log.error("创建目录失败：{}", path);
                result.addProperty("error", "创建目录失败: " + path);
            }
            
        } catch (Exception e) {
            log.error("创建目录时发生异常", e);
            result.addProperty("error", "创建目录失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 删除文件或目录
     * 
     * @param path 要删除的文件或目录路径
     * @return 包含删除结果的JsonObject
     */
    private JsonObject deleteFileOrDirectory(String path) {
        JsonObject result = new JsonObject();
        try {
            File fileOrDir = new File(path);
            
            if (!fileOrDir.exists()) {
                log.error("要删除的文件或目录不存在：{}", path);
                result.addProperty("error", "要删除的文件或目录不存在: " + path);
                return result;
            }
            
            boolean success = false;
            if (fileOrDir.isDirectory()) {
                success = deleteDirectory(fileOrDir);
            } else {
                success = fileOrDir.delete();
            }
            
            if (success) {
                log.info("成功删除{}：{}", fileOrDir.isDirectory() ? "目录" : "文件", path);
                result.addProperty("result", "已成功删除: " + path);
            } else {
                log.error("删除{}失败：{}", fileOrDir.isDirectory() ? "目录" : "文件", path);
                result.addProperty("error", "删除失败: " + path);
            }
            
        } catch (Exception e) {
            log.error("删除文件或目录时发生异常", e);
            result.addProperty("error", "删除失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 递归删除目录及其内容
     * 
     * @param directory 要删除的目录
     * @return 是否删除成功
     */
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
    
    /**
     * 重命名文件或目录
     * 
     * @param oldPath 原文件或目录路径
     * @param newPath 新文件或目录路径
     * @return 包含重命名结果的JsonObject
     */
    private JsonObject renameFileOrDirectory(String oldPath, String newPath) {
        JsonObject result = new JsonObject();
        try {
            File oldFile = new File(oldPath);
            File newFile = new File(newPath);
            
            if (!oldFile.exists()) {
                log.error("要重命名的文件或目录不存在：{}", oldPath);
                result.addProperty("error", "要重命名的文件或目录不存在: " + oldPath);
                return result;
            }
            
            if (newFile.exists()) {
                log.error("目标路径已存在，无法重命名：{}", newPath);
                result.addProperty("error", "目标路径已存在，无法重命名: " + newPath);
                return result;
            }
            
            boolean success = oldFile.renameTo(newFile);
            if (success) {
                log.info("成功重命名：{} -> {}", oldPath, newPath);
                result.addProperty("result", "重命名成功: " + oldPath + " -> " + newPath);
            } else {
                log.error("重命名失败：{} -> {}", oldPath, newPath);
                result.addProperty("error", "重命名失败: " + oldPath + " -> " + newPath);
            }
            
        } catch (Exception e) {
            log.error("重命名文件或目录时发生异常", e);
            result.addProperty("error", "重命名失败: " + e.getMessage());
        }
        
        return result;
    }
} 