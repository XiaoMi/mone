package com.xiaomi.youpin.tesla.file.server.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaomi.youpin.tesla.file.server.common.Cons;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/5/14 15:21
 */
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    /**
     * List files for a specific user
     *
     * @param ctx           Channel context
     * @param userKey       User key for file organization
     * @param directoryPath Optional subdirectory path
     */
    public void listFiles(ChannelHandlerContext ctx, String userKey, String directoryPath) {
        try {
            // Construct the base path for this user
            String basePath = Cons.DATAPATH + File.separator + userKey;

            // If directory path is provided, append it to the base path
            String targetPath = basePath;
            if (directoryPath != null && !directoryPath.isEmpty()) {
                targetPath = basePath + File.separator + directoryPath;
            }

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
    public void deleteFile(ChannelHandlerContext ctx, String userKey, String name) {
        try {
            // Construct the full path to the file
            String filePath = Cons.DATAPATH + File.separator + userKey + File.separator + name;

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
}
