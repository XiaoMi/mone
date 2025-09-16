package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import run.mone.hive.roles.ReactorRole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReplaceInFileTool测试类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
class ReplaceInFileToolTest {

    private ReplaceInFileTool tool;
    private ReactorRole role;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        tool = new ReplaceInFileTool();
        role = new ReactorRole(); // 假设有默认构造函数
    }

    @Test
    void testGetName() {
        assertEquals("replace_in_file", tool.getName());
    }

    @Test
    void testNeedExecute() {
        assertTrue(tool.needExecute());
    }

    @Test
    void testShow() {
        assertTrue(tool.show());
    }

    @Test
    void testDescription() {
        String description = tool.description();
        assertNotNull(description);
        assertTrue(description.contains("SEARCH/REPLACE"));
    }

    @Test
    void testParameters() {
        String parameters = tool.parameters();
        assertNotNull(parameters);
        assertTrue(parameters.contains("path"));
        assertTrue(parameters.contains("diff"));
    }

    @Test
    void testUsage() {
        String usage = tool.usage();
        assertNotNull(usage);
        assertTrue(usage.contains("replace_in_file"));
    }

    @Test
    void testExecuteWithMissingPath() {
        JsonObject input = new JsonObject();
        input.addProperty("diff", "some diff");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("path"));
    }

    @Test
    void testExecuteWithMissingDiff() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "test.txt");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("diff"));
    }

    @Test
    void testExecuteWithNonExistentFile() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "nonexistent.txt");
        input.addProperty("diff", "------- SEARCH\ntest\n=======\nreplaced\n+++++++ REPLACE");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("不存在"));
    }

    @Test
    void testSimpleReplacement() throws IOException {
        // 创建测试文件
        Path testFile = tempDir.resolve("test.txt");
        String originalContent = "Hello World\nThis is a test\nEnd of file";
        Files.writeString(testFile, originalContent, StandardCharsets.UTF_8);

        // 准备替换操作
        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("diff", """
                ------- SEARCH
                This is a test
                =======
                This is a replacement
                +++++++ REPLACE
                """);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        
        // 验证文件内容
        String newContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertTrue(newContent.contains("This is a replacement"));
        assertFalse(newContent.contains("This is a test"));
        assertTrue(newContent.contains("Hello World"));
        assertTrue(newContent.contains("End of file"));
    }

    @Test
    void testMultipleReplacements() throws IOException {
        // 创建测试文件
        Path testFile = tempDir.resolve("test.txt");
        String originalContent = """
                public class Test {
                    private String name;
                    
                    public String getName() {
                        return name;
                    }
                    
                    public void setName(String name) {
                        this.name = name;
                    }
                }
                """;
        Files.writeString(testFile, originalContent, StandardCharsets.UTF_8);

        // 准备替换操作 - 添加注解和修改方法
        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("diff", """
                ------- SEARCH
                public class Test {
                =======
                @Component
                public class Test {
                +++++++ REPLACE
                
                ------- SEARCH
                    public String getName() {
                        return name;
                    }
                =======
                    public String getName() {
                        return this.name;
                    }
                +++++++ REPLACE
                """);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        
        // 验证文件内容
        String newContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertTrue(newContent.contains("@Component"));
        assertTrue(newContent.contains("return this.name;"));
    }

    @Test
    void testDeletionReplacement() throws IOException {
        // 创建测试文件
        Path testFile = tempDir.resolve("test.txt");
        String originalContent = """
                Line 1
                Line to be deleted
                Line 3
                """;
        Files.writeString(testFile, originalContent, StandardCharsets.UTF_8);

        // 准备删除操作（空的REPLACE部分）
        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("diff", """
                ------- SEARCH
                Line to be deleted
                =======
                +++++++ REPLACE
                """);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        
        // 验证文件内容
        String newContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertFalse(newContent.contains("Line to be deleted"));
        assertTrue(newContent.contains("Line 1"));
        assertTrue(newContent.contains("Line 3"));
    }

    @Test
    void testInvalidSearchContent() throws IOException {
        // 创建测试文件
        Path testFile = tempDir.resolve("test.txt");
        String originalContent = "Hello World\nThis is a test";
        Files.writeString(testFile, originalContent, StandardCharsets.UTF_8);

        // 准备替换操作 - 搜索不存在的内容
        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("diff", """
                ------- SEARCH
                This content does not exist
                =======
                Replacement content
                +++++++ REPLACE
                """);

        JsonObject result = tool.execute(role, input);

        // 验证结果 - 应该返回错误
        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("未找到匹配"));
    }

    @Test
    void testWhitespaceHandling() throws IOException {
        // 创建测试文件，包含不同的缩进
        Path testFile = tempDir.resolve("test.txt");
        String originalContent = """
                public void method() {
                    if (condition) {
                        doSomething();
                    }
                }
                """;
        Files.writeString(testFile, originalContent, StandardCharsets.UTF_8);

        // 准备替换操作 - 测试空格处理
        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());
        input.addProperty("diff", """
                ------- SEARCH
                    if (condition) {
                        doSomething();
                    }
                =======
                    if (condition) {
                        doSomething();
                        doSomethingElse();
                    }
                +++++++ REPLACE
                """);

        JsonObject result = tool.execute(role, input);

        // 验证结果
        assertTrue(result.has("result"));
        
        // 验证文件内容
        String newContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertTrue(newContent.contains("doSomethingElse()"));
    }
}
