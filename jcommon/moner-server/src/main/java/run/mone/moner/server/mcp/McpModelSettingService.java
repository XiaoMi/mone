package run.mone.moner.server.mcp;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import run.mone.moner.server.bo.McpModelSettingDTO;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author zhangxiaowei6
 * @Date 2025/1/23 11:03
 */

@Slf4j
@Service
public class McpModelSettingService {

    private final McpConfig mcpConfig;
    private static final Gson gson = new Gson();

    public McpModelSettingService(McpConfig mcpConfig) {
        this.mcpConfig = mcpConfig;
    }

    public String getAllMcpModelSetting(String from) {
        FromType fromType = FromType.fromString(from);
        if (createFile(fromType)) {
            return null;
        }
        
        try {
            String content = Files.readString(Paths.get(mcpConfig.getMcpModelPath(fromType)));
            return content;
        } catch (IOException e) {
            log.error("read mcp model setting file error for " + fromType, e);
            return gson.toJson(new McpModelSettingDTO().buildEmpty());
        }
    }

    public McpModelSettingDTO getMcpModelSetting(String from) {
        String content = getAllMcpModelSetting(from);
        return gson.fromJson(content, McpModelSettingDTO.class);
    }

    public void saveMcpModelSetting(String from, String content) {
        FromType fromType = FromType.fromString(from);
        try {
            Files.write(Paths.get(mcpConfig.getMcpModelPath(fromType)), 
                content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("save mcp model setting file error for " + fromType, e);
        }
    }

    public void openModelFileSettings(String from) {
        log.info("begin openModelFileSettings");
        FromType fromType = FromType.fromString(from);
        // 确保目录和文件存在
        if (createFile(fromType)) {
            log.error("Failed to create MCP model file");
            return;
        }

        try {
            String mcpModelPath = mcpConfig.getMcpModelPath(fromType);
            File file = new File(mcpModelPath);
            
            // 获取操作系统名称
            String osName = System.getProperty("os.name").toLowerCase();
            
            if (osName.contains("windows")) {
                // Windows系统使用cmd /c start命令
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", file.getAbsolutePath()});
            } else if (osName.contains("mac")) {
                // macOS系统使用open命令
                Runtime.getRuntime().exec(new String[]{"open", file.getAbsolutePath()});
            } else {
                // Linux系统使用xdg-open命令
                Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
            }
            
            log.info("Opened MCP model file: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error opening MCP model file", e);
        }
    }

    private boolean createFile(FromType fromType) {
        String mcpModelPath = mcpConfig.getMcpModelPath(fromType);
        String mcpDir = mcpConfig.getMcpDir();

        if (!Files.exists(Paths.get(mcpDir)) || !Files.exists(Paths.get(mcpModelPath))) {
            try {
                log.info("Creating MCP directory and file for {}", fromType);
                Files.createDirectories(Paths.get(mcpDir));
                String defaultConfig = gson.toJson(new McpModelSettingDTO().buildEmpty());
                Files.write(Paths.get(mcpModelPath), defaultConfig.getBytes(StandardCharsets.UTF_8));
                log.info("Created MODEL file for {}", fromType);
            } catch (IOException e) {
                log.error("create mcp dir and file error for " + fromType, e);
                return true;
            }
        }
        return false;
    }
}
