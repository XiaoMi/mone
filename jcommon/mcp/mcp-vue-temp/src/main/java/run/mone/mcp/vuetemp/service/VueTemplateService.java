package run.mone.mcp.vuetemp.service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Vue 模板生成服务类
 * @author goodjava@qq.com
 * @date 2025/1/15
 */
@Service
public class VueTemplateService {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(VueTemplateService.class);

    private static final String TEMPLATE_BASE_PATH = "template/";

    /**
     * 生成Vue项目模板
     * @param projectName 项目名称
     * @param description 项目描述
     * @param outputPath 输出路径
     * @return 生成结果
     */
    public String generateVueTemplate(String projectName, String description, String outputPath) {
        try {
            // 创建项目目录
            Path projectPath = Paths.get(outputPath, projectName);
            Files.createDirectories(projectPath);

            // 复制模板文件
            copyTemplateFiles(projectPath, projectName, description);

            return String.format("Vue项目模板已成功生成到: %s", projectPath.toString());

        } catch (Exception e) {
            log.error("生成Vue项目模板失败", e);
            throw new RuntimeException("生成Vue项目模板失败: " + e.getMessage(), e);
        }
    }

    /**
     * 复制模板文件
     */
    private void copyTemplateFiles(Path projectPath, String projectName, String description) throws IOException {
        // 需要复制的文件列表
        String[] filesToCopy = {
                "index.html",
                "package.json",
                "README.md",
                "tsconfig.json",
                "tsconfig.node.json",
                "vite.config.ts",
                "src/App.vue",
                "src/main.ts",
                "src/env.d.ts",
                "src/router/index.ts",
                "src/stores/counter.ts",
                "src/types/index.ts",
                "src/utils/index.ts",
                "src/styles/main.css",
                "src/components/HelloWorld.vue",
                "src/views/Home.vue",
                "src/views/About.vue",
                "src/views/Counter.vue"
        };

        for (String filePath : filesToCopy) {
            copyTemplateFile(filePath, projectPath, projectName, description);
        }
    }

    /**
     * 复制单个模板文件
     */
    private void copyTemplateFile(String templatePath, Path projectPath, String projectName, String description) throws IOException {
        Resource resource = new ClassPathResource(TEMPLATE_BASE_PATH + templatePath);
        
        if (!resource.exists()) {
            log.warn("模板文件不存在: {}", templatePath);
            return;
        }

        Path targetPath = projectPath.resolve(templatePath);
        Files.createDirectories(targetPath.getParent());

        // 读取模板内容
        String content = readResourceContent(resource);
        
        // 替换模板变量
        content = replaceTemplateVariables(content, projectName, description);

        // 写入目标文件
        Files.write(targetPath, content.getBytes("UTF-8"));
        
        log.info("已复制模板文件: {} -> {}", templatePath, targetPath);
    }

    /**
     * 读取资源文件内容
     */
    private String readResourceContent(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }

    /**
     * 替换模板变量
     */
    private String replaceTemplateVariables(String content, String projectName, String description) {
        Map<String, String> variables = new HashMap<>();
        variables.put("${PROJECT_NAME}", projectName);
        variables.put("${PROJECT_DESCRIPTION}", description);
        variables.put("${PROJECT_NAME_LOWER}", projectName.toLowerCase());
        variables.put("${PROJECT_NAME_KEBAB}", projectName.toLowerCase().replaceAll("\\s+", "-"));

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }

        return content;
    }
}
