package run.mone.mcp.mermaid.service;

import java.io.File;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MermaidService {
    public static boolean convert(String mmdc, String mermaidCode, String outputPath) {
        File tempFile;
        // 创建一个包含Mermaid语法的临时文件
        try {
            tempFile = File.createTempFile("diagram", ".mmd");
        } catch (IOException e) {
            log.error("Failed to create temp file: {}", e.getMessage());
            return false;
        }

        try {
            java.nio.file.Files.writeString(tempFile.toPath(), mermaidCode);

            // 构建mmdc命令
            ProcessBuilder pb = new ProcessBuilder(mmdc, "-i", tempFile.getAbsolutePath(), "-o", outputPath);

            // 执行命令
            Process process = pb.start();

            // 获取错误输出
            StringBuilder errorOutput = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("mermaid convert success");
                return true;
            } else {
                log.error("mermaid convert failed: {}", errorOutput.toString());
                return false;
            }      
        } catch (IOException | InterruptedException e) {
            log.error("mermaid convert failed: {}", e);
            return false;
        } finally {
            // 删除临时文件
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}