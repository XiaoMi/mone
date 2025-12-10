package run.mone.mcp.tester.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 单元测试生成工具
 * 根据提供的项目路径和源代码文件，生成对应的单元测试代码
 *
 * @date 2025/12/09
 */
@Slf4j
public class TestGenerationTool implements ITool {

    public static final String name = "generate_unit_test";

    private final String testFramework;
    private final String mockFramework;
    private final String testOutputPath;

    public TestGenerationTool(String testFramework, String mockFramework, String testOutputPath) {
        this.testFramework = testFramework;
        this.mockFramework = mockFramework;
        this.testOutputPath = testOutputPath;
    }

    @Override
    public boolean completed() {
        return false;
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
    public String getName() {
        return name;
    }

    @Override
    public String description() {
        return """
                根据源代码文件生成单元测试。支持 Java 项目的单元测试生成，
                可以分析源代码的类、方法、依赖关系，并生成相应的测试代码。
                """;
    }

    @Override
    public String parameters() {
        return """
                - project_path (必需): 项目的根路径
                - source_file_path (可选): 要生成测试的源文件路径，如果不指定则为整个项目生成测试
                - test_name (可选): 生成的测试类名称，默认为 {SourceClassName}Test
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                生成单元测试中...
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                示例 1: 为整个项目生成测试
                <generate_unit_test>
                <project_path>/path/to/your/project</project_path>
                %s
                </generate_unit_test>

                示例 2: 为单个文件生成测试
                <generate_unit_test>
                <project_path>/path/to/your/project</project_path>
                <source_file_path>src/main/java/com/example/MyClass.java</source_file_path>
                %s
                </generate_unit_test>

                示例 3: 自定义测试类名称
                <generate_unit_test>
                <project_path>/path/to/your/project</project_path>
                <source_file_path>src/main/java/com/example/MyClass.java</source_file_path>
                <test_name>MyClassCustomTest</test_name>
                %s
                </generate_unit_test>
                """.formatted(taskProgress, taskProgress, taskProgress);
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject arguments) {
        JsonObject result = new JsonObject();
        try {
            String projectPath = arguments.get("project_path").getAsString();
            String sourceFilePath = arguments.has("source_file_path") ?
                    arguments.get("source_file_path").getAsString() : null;
            String testName = arguments.has("test_name") ?
                    arguments.get("test_name").getAsString() : null;

            log.info("生成单元测试: projectPath={}, sourceFilePath={}, testName={}",
                    projectPath, sourceFilePath, testName);

            // 验证项目路径是否存在
            File projectDir = new File(projectPath);
            if (!projectDir.exists() || !projectDir.isDirectory()) {
                result.addProperty("success", false);
                result.addProperty("message", "项目路径不存在或不是目录: " + projectPath);
                return result;
            }

            StringBuilder message = new StringBuilder();
            message.append("开始分析项目并生成单元测试...\n\n");
            message.append("项目路径: ").append(projectPath).append("\n");
            message.append("测试框架: ").append(testFramework).append("\n");
            message.append("Mock 框架: ").append(mockFramework).append("\n\n");

            if (sourceFilePath != null && !sourceFilePath.isEmpty()) {
                // 为单个文件生成测试
                String testResult = generateTestForFile(projectPath, sourceFilePath, testName);
                message.append(testResult);
            } else {
                // 为整个项目生成测试
                List<String> sourceFiles = findSourceFiles(projectPath);
                message.append("找到 ").append(sourceFiles.size()).append(" 个源文件\n\n");

                for (String file : sourceFiles) {
                    message.append("处理文件: ").append(file).append("\n");
                    String testResult = generateTestForFile(projectPath, file, null);
                    message.append(testResult).append("\n");
                }
            }

            result.addProperty("success", true);
            result.addProperty("message", message.toString());
            return result;

        } catch (Exception e) {
            log.error("生成单元测试失败", e);
            result.addProperty("success", false);
            result.addProperty("message", "生成单元测试失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 为单个文件生成测试
     */
    private String generateTestForFile(String projectPath, String sourceFilePath, String testName) {
        try {
            Path sourcePath = resolveFilePath(projectPath, sourceFilePath);

            if (!Files.exists(sourcePath)) {
                return "错误: 源文件不存在: " + sourceFilePath + "\n";
            }

            // 读取源文件内容
            String sourceContent = Files.readString(sourcePath);

            // 分析源文件，提取类名、方法等信息
            SourceFileInfo fileInfo = analyzeSourceFile(sourcePath, sourceContent);

            if (testName == null || testName.isEmpty()) {
                testName = fileInfo.getClassName() + "Test";
            }

            // 生成测试文件路径
            Path testFilePath = generateTestFilePath(projectPath, sourcePath, testName);

            // 生成测试内容
            String testContent = generateTestContent(fileInfo, testName);

            // 创建测试文件目录
            Files.createDirectories(testFilePath.getParent());

            // 写入测试文件
            Files.writeString(testFilePath, testContent);

            return String.format("✓ 成功生成测试文件: %s\n  源文件: %s\n  测试类: %s\n  方法数: %d\n",
                    testFilePath, sourcePath.getFileName(), testName, fileInfo.getMethods().size());

        } catch (Exception e) {
            log.error("为文件生成测试失败: " + sourceFilePath, e);
            return "✗ 生成失败: " + sourceFilePath + " - " + e.getMessage() + "\n";
        }
    }

    /**
     * 查找项目中的所有源文件
     */
    private List<String> findSourceFiles(String projectPath) throws Exception {
        Path srcPath = Paths.get(projectPath, "src", "main", "java");

        if (!Files.exists(srcPath)) {
            return new ArrayList<>();
        }

        try (Stream<Path> paths = Files.walk(srcPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(p -> srcPath.relativize(p).toString())
                    .collect(Collectors.toList());
        }
    }

    /**
     * 解析文件路径
     */
    private Path resolveFilePath(String projectPath, String filePath) {
        Path path = Paths.get(filePath);
        if (path.isAbsolute()) {
            return path;
        }

        // 尝试相对于项目路径
        Path projectRelative = Paths.get(projectPath, filePath);
        if (Files.exists(projectRelative)) {
            return projectRelative;
        }

        // 尝试相对于 src/main/java
        return Paths.get(projectPath, "src", "main", "java", filePath);
    }

    /**
     * 分析源文件，提取类名、方法等信息
     */
    private SourceFileInfo analyzeSourceFile(Path filePath, String content) {
        SourceFileInfo info = new SourceFileInfo();

        // 提取包名
        String packageName = extractPackageName(content);
        info.setPackageName(packageName);

        // 提取类名
        String className = extractClassName(content);
        info.setClassName(className);

        // 提取方法信息
        List<MethodInfo> methods = extractMethods(content);
        info.setMethods(methods);

        // 提取导入
        List<String> imports = extractImports(content);
        info.setImports(imports);

        return info;
    }

    /**
     * 提取包名
     */
    private String extractPackageName(String content) {
        int packageIndex = content.indexOf("package ");
        if (packageIndex >= 0) {
            int semicolonIndex = content.indexOf(";", packageIndex);
            if (semicolonIndex > packageIndex) {
                return content.substring(packageIndex + 8, semicolonIndex).trim();
            }
        }
        return "";
    }

    /**
     * 提取类名
     */
    private String extractClassName(String content) {
        String[] keywords = {"public class ", "class ", "public interface ", "interface "};

        for (String keyword : keywords) {
            int classIndex = content.indexOf(keyword);
            if (classIndex >= 0) {
                int startIndex = classIndex + keyword.length();
                int endIndex = content.indexOf(" ", startIndex);
                int braceIndex = content.indexOf("{", startIndex);
                int genericIndex = content.indexOf("<", startIndex);

                int realEndIndex = Math.min(
                        endIndex > 0 ? endIndex : Integer.MAX_VALUE,
                        Math.min(
                                braceIndex > 0 ? braceIndex : Integer.MAX_VALUE,
                                genericIndex > 0 ? genericIndex : Integer.MAX_VALUE
                        )
                );

                if (realEndIndex != Integer.MAX_VALUE) {
                    return content.substring(startIndex, realEndIndex).trim();
                }
            }
        }

        return "UnknownClass";
    }

    /**
     * 提取方法信息（简单实现）
     */
    private List<MethodInfo> extractMethods(String content) {
        List<MethodInfo> methods = new ArrayList<>();

        // 简单的正则匹配方法声明
        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // 检测方法声明（简化版本）
            if ((line.contains("public ") || line.contains("private ") || line.contains("protected "))
                    && line.contains("(") && line.contains(")")
                    && !line.contains("class ") && !line.contains("interface ")) {

                MethodInfo method = new MethodInfo();

                // 提取方法名
                int parenIndex = line.indexOf("(");
                String beforeParen = line.substring(0, parenIndex).trim();
                String[] parts = beforeParen.split("\\s+");
                if (parts.length > 0) {
                    method.setName(parts[parts.length - 1]);

                    // 提取返回类型
                    if (parts.length > 1) {
                        method.setReturnType(parts[parts.length - 2]);
                    }
                }

                methods.add(method);
            }
        }

        return methods;
    }

    /**
     * 提取导入语句
     */
    private List<String> extractImports(String content) {
        List<String> imports = new ArrayList<>();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("import ") && line.endsWith(";")) {
                imports.add(line.substring(7, line.length() - 1).trim());
            }
        }

        return imports;
    }

    /**
     * 生成测试文件路径
     */
    private Path generateTestFilePath(String projectPath, Path sourcePath, String testName) {
        // 获取源文件相对于 src/main/java 的路径
        String sourcePathStr = sourcePath.toString();
        int srcMainJavaIndex = sourcePathStr.indexOf("src/main/java");

        String relativePath;
        if (srcMainJavaIndex >= 0) {
            relativePath = sourcePathStr.substring(srcMainJavaIndex + 14); // 14 = "src/main/java/".length()
        } else {
            relativePath = sourcePath.getFileName().toString();
        }

        // 构建测试文件路径
        Path testDir = Paths.get(projectPath, testOutputPath);

        // 保持包结构
        int lastSlash = relativePath.lastIndexOf("/");
        String packagePath = lastSlash > 0 ? relativePath.substring(0, lastSlash) : "";

        return testDir.resolve(packagePath).resolve(testName + ".java");
    }

    /**
     * 生成测试内容
     */
    private String generateTestContent(SourceFileInfo fileInfo, String testName) {
        StringBuilder content = new StringBuilder();

        // 包声明
        if (!fileInfo.getPackageName().isEmpty()) {
            content.append("package ").append(fileInfo.getPackageName()).append(";\n\n");
        }

        // 导入测试框架
        if ("junit5".equals(testFramework)) {
            content.append("import org.junit.jupiter.api.Test;\n");
            content.append("import org.junit.jupiter.api.BeforeEach;\n");
            content.append("import org.junit.jupiter.api.AfterEach;\n");
            content.append("import static org.junit.jupiter.api.Assertions.*;\n");
        } else {
            content.append("import org.junit.Test;\n");
            content.append("import org.junit.Before;\n");
            content.append("import org.junit.After;\n");
            content.append("import static org.junit.Assert.*;\n");
        }

        // 导入 Mock 框架
        if ("mockito".equals(mockFramework)) {
            content.append("import org.mockito.Mock;\n");
            content.append("import org.mockito.InjectMocks;\n");
            content.append("import org.mockito.MockitoAnnotations;\n");
            content.append("import static org.mockito.Mockito.*;\n");
        }

        content.append("\n");

        // 测试类声明
        content.append("/**\n");
        content.append(" * ").append(fileInfo.getClassName()).append(" 的单元测试\n");
        content.append(" * 自动生成 by mcp-tester\n");
        content.append(" */\n");
        content.append("public class ").append(testName).append(" {\n\n");

        // 测试对象
        content.append("    @InjectMocks\n");
        content.append("    private ").append(fileInfo.getClassName()).append(" ").append(toLowerCamelCase(fileInfo.getClassName())).append(";\n\n");

        // setUp 方法
        if ("junit5".equals(testFramework)) {
            content.append("    @BeforeEach\n");
        } else {
            content.append("    @Before\n");
        }
        content.append("    public void setUp() {\n");
        content.append("        MockitoAnnotations.openMocks(this);\n");
        content.append("    }\n\n");

        // 为每个方法生成测试
        for (MethodInfo method : fileInfo.getMethods()) {
            if (!method.getName().isEmpty() && !method.getName().equals(fileInfo.getClassName())) {
                content.append("    @Test\n");
                content.append("    public void test").append(toUpperCamelCase(method.getName())).append("() {\n");
                content.append("        // TODO: 实现测试逻辑\n");
                content.append("        // 1. 准备测试数据\n");
                content.append("        // 2. 执行被测方法\n");
                content.append("        // 3. 验证结果\n");
                content.append("        fail(\"测试未实现\");\n");
                content.append("    }\n\n");
            }
        }

        content.append("}\n");

        return content.toString();
    }

    private String toLowerCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    private String toUpperCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 源文件信息
     */
    private static class SourceFileInfo {
        private String packageName;
        private String className;
        private List<MethodInfo> methods = new ArrayList<>();
        private List<String> imports = new ArrayList<>();

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public List<MethodInfo> getMethods() {
            return methods;
        }

        public void setMethods(List<MethodInfo> methods) {
            this.methods = methods;
        }

        public List<String> getImports() {
            return imports;
        }

        public void setImports(List<String> imports) {
            this.imports = imports;
        }
    }

    /**
     * 方法信息
     */
    private static class MethodInfo {
        private String name;
        private String returnType;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }
    }
}
