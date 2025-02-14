package run.mone.moner.server.bo;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author zhangxiaowei6
 * @Date 2025/1/23 11:03
 */

@Slf4j
public class McpModelSetting {

    public static String MCP_MODEL_SETTING_PATH = System.getProperty("user.home") + "/.mcp/mcp_model_settings.json";
    public static String MCP_DIR = System.getProperty("user.home") + "/.mcp";

    private static  Gson gson = new Gson();

    public static String getAllMcpModelSetting() {
        if (createFile())
            return null;
        // 读取MCP_MODEL_SETTING_PATH路径的文件内容吗，并转为String返回
        String content = gson.toJson(new McpModelSettingDTO());
        try {
            content = Files.readString(Paths.get(MCP_MODEL_SETTING_PATH));
        } catch (IOException e) {
            log.error("read mcp model setting file error", e);
            return gson.toJson(new McpModelSettingDTO());
        }
        return content;
    }



    public static McpModelSettingDTO getMcpModelSetting() {
        String content = getAllMcpModelSetting();
        return gson.fromJson(content, McpModelSettingDTO.class);
    }

    public static void saveMcpModelSetting(String content) {
        // 将content写入MCP_MODEL_SETTING_PATH路径的文件中
        try {
            Files.write(Paths.get(MCP_MODEL_SETTING_PATH), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("save mcp model setting file error", e);
        }
    }

    private static boolean createFile() {
        if (!Files.exists(Paths.get(MCP_DIR)) || !Files.exists(Paths.get(MCP_MODEL_SETTING_PATH))) {
            try {
                log.info("Creating MCP directory and file");
                Files.createDirectories(Paths.get(MCP_DIR));
                // 创建默认配置文件

                String defaultConfig = gson.toJson(new McpModelSettingDTO().buildEmpty());
                Files.write(Paths.get(MCP_MODEL_SETTING_PATH), defaultConfig.getBytes(StandardCharsets.UTF_8));

                // FIXME：
                // 刷新文件系统以识别新创建的文件
                // LocalFileSystem.getInstance().refreshAndFindFileByPath(MCP_MODEL_SETTING_PATH);

                log.info("Created and refreshed MCP file");
            } catch (IOException e) {
                log.error("create mcp dir and file error", e);
                return true;
            }
        }
        return false;
    }

}
