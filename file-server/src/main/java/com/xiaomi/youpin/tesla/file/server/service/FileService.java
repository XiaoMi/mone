package com.xiaomi.youpin.tesla.file.server.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaomi.youpin.tesla.file.server.common.Cons;
import com.xiaomi.youpin.tesla.file.server.utils.DirUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author goodjava@qq.com
 * @date 2025/5/14 15:21
 */
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    
    // Regex pattern to validate that string only contains alphanumeric characters and hyphens
    private static final Pattern VALID_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-]+$");

    /**
     * Validates if a string contains only alphanumeric characters and hyphens
     * 
     * @param input String to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidKeyFormat(String input) {
        return input != null && VALID_KEY_PATTERN.matcher(input).matches();
    }

    /**
     * List files for a specific user
     *
     * @param ctx           Channel context
     * @param userKey       User key for file organization
     * @param directoryPath Optional subdirectory path
     */
    public void listFiles(ChannelHandlerContext ctx, String userKey, String directoryPath) {
        try {
            // Validate userKey
            if (!isValidKeyFormat(userKey)) {
                BaseService.send(ctx, "error:Invalid userKey format. Only alphanumeric characters and hyphens are allowed.");
                return;
            }

            // Validate directory path if provided
            if (StringUtils.isNotEmpty(directoryPath)) {
                if (!DirUtils.isValidDirectoryPath(directoryPath)) {
                    BaseService.send(ctx, "error:Invalid directory path format. Path can only contain alphanumeric characters, hyphens, and forward slashes.");
                    return;
                }
            }

            // If directory path is provided, append it to the base path
            String targetPath = DirUtils.dirPath(userKey, directoryPath);

            File directory = new File(targetPath);
            if (!directory.exists() || !directory.isDirectory()) {
                // Create directory if it doesn't exist
                try {
                    FileUtils.forceMkdir(directory);
                } catch (IOException e) {
                    log.error("Failed to create directory: {}", targetPath, e);
                    BaseService.send(ctx, "error:Failed to access directory");
                    return;
                }
            }

            // Get the list of files in the directory
            File[] files = directory.listFiles();
            List<Map<String, Object>> fileList = new ArrayList<>();

            if (files != null) {
                for (File file : files) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", file.getName());
                    fileInfo.put("isDirectory", file.isDirectory());
                    fileInfo.put("size", file.length());
                    fileInfo.put("lastModified", file.lastModified());
                    fileList.add(fileInfo);
                }
            }

            // Convert to JSON and send back to client
            BaseService.send(ctx, JSONObject.toJSONString(fileList));

        } catch (Exception e) {
            log.error("Error listing files: {}", e.getMessage(), e);
            BaseService.send(ctx, "error:Failed to list files: " + e.getMessage());
        }
    }

    /**
     * Delete a file or directory
     *
     * @param ctx     Channel context
     * @param userKey User key for file organization
     * @param name    Name of the file or directory to delete
     */
    public void deleteFile(ChannelHandlerContext ctx, String userKey, String directoryPath, String name) {
        try {
            // Validate userKey
            if (!isValidKeyFormat(userKey)) {
                BaseService.send(ctx, "error:Invalid userKey format. Only alphanumeric characters and hyphens are allowed.");
                return;
            }

            // Validate directory path if provided
            if (StringUtils.isNotEmpty(directoryPath)) {
                if (!DirUtils.isValidDirectoryPath(directoryPath)) {
                    BaseService.send(ctx, "error:Invalid directory path format. Path can only contain alphanumeric characters, hyphens, and forward slashes.");
                    return;
                }
            }
            
            // Validate name
            if (!isValidKeyFormat(name)) {
                BaseService.send(ctx, "error:Invalid file name format. Only alphanumeric characters and hyphens are allowed.");
                return;
            }
            
            // Construct the full path to the file
            String filePath = DirUtils.filePath(userKey, directoryPath, name);

            File file = new File(filePath);
            if (!file.exists()) {
                BaseService.send(ctx, "error:File not found: " + name);
                return;
            }

            // Delete the file or directory
            boolean deleted;
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
                deleted = true;
            } else {
                deleted = file.delete();
            }

            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "File deleted successfully");
                BaseService.send(ctx, JSONObject.toJSONString(response));
            } else {
                BaseService.send(ctx, "error:Failed to delete file: " + name);
            }

        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            BaseService.send(ctx, "error:Failed to delete file: " + e.getMessage());
        }
    }

    /**
     * Create a new directory
     *
     * @param ctx           Channel context
     * @param userKey       User key for file organization
     * @param directoryName Name of the directory to create
     */
    public void createDirectory(ChannelHandlerContext ctx, String userKey, String directoryName) {
        try {
            // Validate userKey
            if (!isValidKeyFormat(userKey)) {
                BaseService.send(ctx, "error:Invalid userKey format. Only alphanumeric characters and hyphens are allowed.");
                return;
            }
            
            // Validate directory name
            if (StringUtils.isEmpty(directoryName) || !DirUtils.isValidDirectoryPath(directoryName)) {
                BaseService.send(ctx, "error:Invalid directory path format. Path can only contain alphanumeric characters, hyphens, and forward slashes.");
                return;
            }

            // Construct the full path for the new directory
            String directoryPath = Cons.DATAPATH + File.separator + userKey + File.separator + directoryName;
            File directory = new File(directoryPath);

            // Check if directory already exists
            if (directory.exists()) {
                BaseService.send(ctx, "error:Directory already exists: " + directoryName);
                return;
            }

            // Create the directory
            FileUtils.forceMkdir(directory);

            // Send success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Directory created successfully");
            response.put("path", directoryName);
            BaseService.send(ctx, JSONObject.toJSONString(response));

        } catch (Exception e) {
            log.error("Error creating directory: {}", e.getMessage(), e);
            BaseService.send(ctx, "error:Failed to create directory: " + e.getMessage());
        }
    }
}
