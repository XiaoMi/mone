package run.mone.agentx.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * MCP配置文件工具类
 * 用于处理MCP配置文件的读写操作
 */
@Slf4j
public class McpConfigUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 更新MCP配置文件
     * 如果文件存在，则读取现有内容并添加新的配置项
     * 如果文件不存在，则创建新文件并写入完整配置
     *
     * @param filePath 配置文件路径
     * @param serverName 服务器名称，如 "chat-mcp"
     * @param serverConfig 服务器配置
     * @return 是否更新成功
     */
    public static boolean updateMcpConfig(String filePath, String serverName, Map<String, Object> serverConfig) {
        try {
            File configFile = new File(filePath);
            JsonObject configJson;

            // 检查文件是否存在
            if (configFile.exists()) {
                // 读取现有配置
                String content = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                configJson = GSON.fromJson(content, JsonObject.class);
                
                // 如果mcpServers不存在，则创建
                if (!configJson.has("mcpServers")) {
                    configJson.add("mcpServers", new JsonObject());
                }
                
                // 获取mcpServers对象
                JsonObject mcpServers = configJson.getAsJsonObject("mcpServers");
                
                // 添加或更新服务器配置
                mcpServers.add(serverName, GSON.toJsonTree(serverConfig));
            } else {
                // 创建新配置
                configJson = new JsonObject();
                JsonObject mcpServers = new JsonObject();
                mcpServers.add(serverName, GSON.toJsonTree(serverConfig));
                configJson.add("mcpServers", mcpServers);
            }
            
            // 写入文件
            FileUtils.writeStringToFile(configFile, GSON.toJson(configJson), StandardCharsets.UTF_8);
            log.info("MCP配置文件已更新: {}", filePath);
            return true;
        } catch (IOException e) {
            log.error("更新MCP配置文件失败: {}", filePath, e);
            return false;
        }
    }
}