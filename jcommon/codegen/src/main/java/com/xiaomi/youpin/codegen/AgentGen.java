package com.xiaomi.youpin.codegen;

import com.xiaomi.youpin.codegen.common.FileUtils;
import com.xiaomi.youpin.codegen.generator.ClassGenerator;
import com.xiaomi.youpin.codegen.generator.DirectoryGenerator;
import com.xiaomi.youpin.codegen.generator.FileGenerator;
import com.xiaomi.youpin.codegen.generator.PomGenerator;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Agent项目代码生成器
 * 用于生成基于Hive的MCP Agent项目
 *
 * @author goodjava@qq.com
 * @date 2025/10/7 15:24
 */
@Slf4j
@Data
public class AgentGen {

    private boolean zip = false;

    /**
     * 生成Agent项目并打包
     *
     * @param projectPath      项目生成路径
     * @param projectName      项目名称（例如：mcp-coder）
     * @param groupId          Maven GroupId
     * @param packageName      包名（例如：run.mone.mcp.coder）
     * @param author           作者
     * @param versionId        版本号
     * @param agentName        Agent名称（例如：coder）
     * @param agentGroup       Agent分组（例如：staging）
     * @param agentProfile     Agent简介
     * @param agentGoal        Agent目标
     * @param agentConstraints Agent约束
     * @return 生成的zip文件路径
     */
    public Result<String> generateAndZip(String projectPath, String projectName, String groupId,
                                         String packageName, String author, String versionId,
                                         String agentName, String agentGroup, String agentProfile,
                                         String agentGoal, String agentConstraints) {
        return generateAndZip(projectPath, projectName, groupId, packageName, author, versionId,
                agentName, agentGroup, agentProfile, agentGoal, agentConstraints,
                "run.mone", "mcp", "1.6.1-jdk21-SNAPSHOT",
                "9186", "http://127.0.0.1:8080", "qwen", "21");
    }

    /**
     * 生成Agent项目并打包（完整参数版本）
     */
    public Result<String> generateAndZip(String projectPath, String projectName, String groupId,
                                         String packageName, String author, String versionId,
                                         String agentName, String agentGroup, String agentProfile,
                                         String agentGoal, String agentConstraints,
                                         String parentGroupId, String parentArtifactId, String parentVersion,
                                         String grpcPort, String hiveManagerUrl, String llmModel, String javaVersion) {

        String srcPath = "/src/main/java/";
        String resourcesPath = "/src/main/resources/";
        String packagePath = packageName.replaceAll("\\.", "/");
        String bootstrapClassName = adapterProjectNameToCamelName(projectName) + "Bootstrap";

        try {
            // 创建项目根目录
            DirectoryGenerator rootDir = new DirectoryGenerator(projectPath, projectName, "");
            rootDir.generator();

            // 创建包目录结构
            DirectoryGenerator packageDir = new DirectoryGenerator(projectPath, projectName,
                    srcPath + packagePath);
            packageDir.generator();

            // 创建config包
            DirectoryGenerator configDir = new DirectoryGenerator(projectPath, projectName,
                    srcPath + packagePath + "/config");
            configDir.generator();

            // 创建resources目录
            DirectoryGenerator resourcesDir = new DirectoryGenerator(projectPath, projectName,
                    resourcesPath);
            resourcesDir.generator();

            // 生成pom.xml
            generatePom(projectPath, projectName, groupId, versionId, packageName, bootstrapClassName,
                    parentGroupId, parentArtifactId, parentVersion, javaVersion);

            // 生成Bootstrap启动类
            generateBootstrap(projectPath, projectName, packageName, author, packagePath,
                    srcPath, bootstrapClassName);

            // 生成AgentConfig配置类
            generateAgentConfig(projectPath, projectName, packageName, author, packagePath,
                    srcPath, agentProfile, agentGoal, agentConstraints);

            // 生成application.properties
            generateApplicationProperties(projectPath, projectName, resourcesPath, agentName,
                    agentGroup, versionId, grpcPort, hiveManagerUrl, llmModel);

            // 生成logback.xml
            generateLogback(projectPath, projectName, resourcesPath, agentName);

            // 生成README.md
            generateReadme(projectPath, projectName, agentName);

            if (zip) {
                // 打包成zip
                FileUtils.compress(projectPath + File.separator + projectName,
                        projectPath + File.separator + projectName + ".zip");
            }

            log.info("Agent project generated successfully: {}", projectName);

        } catch (Exception e) {
            log.error("AgentGen failed for project: " + projectName, e);
            return Result.fail(GeneralCodes.InternalError, "InternalError: " + e.getMessage());
        }

        String data = projectPath + File.separator;
        if (zip) {
            data = data + projectName + ".zip";
        }

        return Result.success(data);
    }

    /**
     * 生成pom.xml文件
     */
    private void generatePom(String projectPath, String projectName, String groupId, String versionId,
                             String packageName, String bootstrapClassName, String parentGroupId,
                             String parentArtifactId, String parentVersion, String javaVersion) {
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "agent/pom_xml.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("parentGroupId", parentGroupId);
        m.put("parentArtifactId", parentArtifactId);
        m.put("parentVersion", parentVersion);
        m.put("groupId", groupId);
        m.put("artifactId", projectName);
        m.put("version", versionId + "-SNAPSHOT");
        m.put("javaVersion", javaVersion);
        m.put("package", packageName);
        m.put("project", extractLastPartOfPackage(packageName));
        m.put("bootstrapClassName", bootstrapClassName);
        pomGenerator.generator(m);
    }

    /**
     * 生成Bootstrap启动类
     */
    private void generateBootstrap(String projectPath, String projectName, String packageName,
                                   String author, String packagePath, String srcPath,
                                   String bootstrapClassName) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath, bootstrapClassName, "agent/bootstrap.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("project", extractLastPartOfPackage(packageName));
        m.put("bootstrapClassName", bootstrapClassName);
        m.put("author", author);
        m.put("date", getCurrentDate());
        classGenerator.generator(m);
    }

    /**
     * 生成AgentConfig配置类
     */
    private void generateAgentConfig(String projectPath, String projectName, String packageName,
                                     String author, String packagePath, String srcPath,
                                     String agentProfile, String agentGoal, String agentConstraints) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath + "/config", "AgentConfig", "agent/agent_config.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("project", extractLastPartOfPackage(packageName));
        m.put("author", author);
        m.put("date", getCurrentDate());
        m.put("agentProfile", agentProfile);
        m.put("agentGoal", agentGoal);
        m.put("agentConstraints", agentConstraints);
        classGenerator.generator(m);
    }

    /**
     * 生成application.properties
     */
    private void generateApplicationProperties(String projectPath, String projectName,
                                               String resourcesPath, String agentName,
                                               String agentGroup, String versionId,
                                               String grpcPort, String hiveManagerUrl,
                                               String llmModel) {
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName,
                resourcesPath + "application.properties", "agent/application_properties.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("agentName", agentName);
        m.put("agentGroup", agentGroup);
        m.put("agentVersion", versionId);
        m.put("grpcPort", grpcPort);
        m.put("hiveManagerUrl", hiveManagerUrl);
        m.put("llmModel", llmModel);
        fileGenerator.generator(m);
    }

    /**
     * 生成logback.xml
     */
    private void generateLogback(String projectPath, String projectName, String resourcesPath,
                                 String agentName) {
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName,
                resourcesPath + "logback.xml", "agent/logback.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("agentName", agentName);
        fileGenerator.generator(m);
    }

    /**
     * 生成README.md
     */
    private void generateReadme(String projectPath, String projectName, String agentName) {
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName,
                "README.md", "agent/readme.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("projectName", projectName);
        m.put("agentName", agentName);
        fileGenerator.generator(m);
    }

    /**
     * 将项目名转换为驼峰命名
     */
    private String adapterProjectNameToCamelName(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        try {
            String[] strings = name.split("-");
            if (strings.length > 1) {
                StringBuilder res = new StringBuilder();
                for (String str : strings) {
                    if (!StringUtils.isEmpty(str)) {
                        res.append(StringUtils.capitalize(str));
                    }
                }
                return res.toString();
            }
        } catch (Exception e) {
            return StringUtils.capitalize(name);
        }
        return StringUtils.capitalize(name);
    }

    /**
     * 从包名中提取最后一部分作为项目标识
     * 例如：run.mone.mcp.coder -> coder
     */
    private String extractLastPartOfPackage(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return "";
        }
        String[] parts = packageName.split("\\.");
        return parts[parts.length - 1];
    }

    /**
     * 获取当前日期
     */
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdf.format(new Date());
    }

    /**
     * 主函数，用于测试
     */
    public static void main(String[] args) {
        AgentGen agentGen = new AgentGen();

        // 测试参数
        String projectPath = "/tmp/agent-test";
        String projectName = "mcp-test-agent";
        String groupId = "run.mone";
        String packageName = "run.mone.mcp.test";
        String author = "goodjava@qq.com";
        String versionId = "1.0.0";
        String agentName = "test";
        String agentGroup = "staging";
        String agentProfile = "你是一名优秀的测试工程师";
        String agentGoal = "你的目标是编写高质量的测试代码";
        String agentConstraints = "不要探讨和测试不相关的东西";

        Result<String> result = agentGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId,
                agentName, agentGroup, agentProfile, agentGoal, agentConstraints
        );

        if (result.getCode() == 0) {
            System.out.println("✅ Agent项目生成成功！");
            System.out.println("📦 生成文件位置: " + result.getData());
        } else {
            System.err.println("❌ Agent项目生成失败: " + result.getMessage());
        }
    }
}
