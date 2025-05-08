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
            一个用于文件读写操作的工具。
            可以用于创建文件并写入内容，读取文件内容，或向现有文件追加内容。
            
            **使用时机：** 当用户需要执行以下操作时使用此工具：
            - 创建新文件并写入内容
            - 读取文件内容
            - 向现有文件追加内容
            - 覆盖现有文件的内容
            
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
                
                - path: (必需) 要操作的文件路径
                
                - content: (针对write/append操作必需) 要写入或追加到文件的内容
                
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
} 