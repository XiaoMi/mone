package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExecuteCommandTool test class - 专门测试 mkdir 命令
 * 
 * @author goodjava@qq.com
 * @date 2025/1/17
 */
class ExecuteCommandToolTest {

    private ExecuteCommandTool tool;
    private ReactorRole role;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        tool = new ExecuteCommandTool();

        // 创建一个测试用的 ReactorRole，设置工作目录为临时目录
        role = new ReactorRole("test", null, new LLM(LLMConfig.builder().build()));
        role.setWorkspacePath(tempDir.toString());
        // 通过反射或其他方式设置工作目录（如果 ReactorRole 有相应的方法）
        // 这里假设 ReactorRole 有设置工作目录的方法
    }

    @Test
    void testGetName() {
        assertEquals("execute_command", tool.getName());
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
    void testCompleted() {
        assertTrue(tool.completed());
    }

    @Test
    void testDescription() {
        String description = tool.description();
        assertNotNull(description);
        assertTrue(description.contains("CLI command"));
        assertTrue(description.contains("system operations"));
    }

    @Test
    void testParameters() {
        String parameters = tool.parameters();
        assertNotNull(parameters);
        assertTrue(parameters.contains("command"));
        assertTrue(parameters.contains("requires_approval"));
        assertTrue(parameters.contains("timeout"));
    }

    @Test
    void testUsage() {
        String usage = tool.usage();
        assertNotNull(usage);
        assertTrue(usage.contains("execute_command"));
        assertTrue(usage.contains("<command>"));
    }

    @Test
    void testExample() {
        String example = tool.example();
        assertNotNull(example);
        assertTrue(example.contains("Example"));
        assertTrue(example.contains("execute_command"));
    }

    @Test
    void testExecuteWithMissingCommand() {
        JsonObject input = new JsonObject();
        input.addProperty("requires_approval", false);

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("command"));
    }

    @Test
    void testExecuteWithMissingRequiresApproval() {
        JsonObject input = new JsonObject();
        input.addProperty("command", "mkdir test");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("requires_approval"));
    }

    @Test
    void testExecuteWithEmptyCommand() {
        JsonObject input = new JsonObject();
        input.addProperty("command", "");
        input.addProperty("requires_approval", false);

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("command"));
    }

    /**
     * 测试基本的 mkdir 命令 - 创建单个目录
     * 在类Unix系统上执行
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testMkdirBasicDirectory() throws IOException {
        String testDirName = "test_mkdir_basic";
        Path expectedDir = tempDir.resolve(testDirName);
        
        // 确保目录不存在
        assertFalse(Files.exists(expectedDir));

        JsonObject input = new JsonObject();
        input.addProperty("command", "cd " + tempDir.toString() + " && mkdir " + testDirName);
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 30);

        JsonObject result = tool.execute(role, input);

        // 验证命令执行结果
        assertNotNull(result);
        assertTrue(result.has("exit_code"));
        assertEquals(0, result.get("exit_code").getAsInt());
        assertTrue(result.has("success"));
        assertTrue(result.get("success").getAsBoolean());
        assertTrue(result.has("output"));
        assertTrue(result.has("command"));
        assertTrue(result.has("working_directory"));

        // 验证目录确实被创建了
        assertTrue(Files.exists(expectedDir), "Directory should be created");
        assertTrue(Files.isDirectory(expectedDir), "Created path should be a directory");
    }

    /**
     * 测试 mkdir -p 命令 - 创建嵌套目录
     * 在类Unix系统上执行
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testMkdirNestedDirectories() throws IOException {
        String nestedPath = "parent/child/grandchild";
        Path expectedDir = tempDir.resolve(nestedPath);
        
        // 确保目录不存在
        assertFalse(Files.exists(expectedDir));

        JsonObject input = new JsonObject();
        input.addProperty("command", "cd " + tempDir.toString() + " && mkdir -p " + nestedPath);
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 30);

        JsonObject result = tool.execute(role, input);

        // 验证命令执行结果
        assertNotNull(result);
        assertEquals(0, result.get("exit_code").getAsInt());
        assertTrue(result.get("success").getAsBoolean());

        // 验证整个嵌套目录结构都被创建了
        assertTrue(Files.exists(expectedDir), "Nested directory should be created");
        assertTrue(Files.isDirectory(expectedDir), "Created path should be a directory");
        
        // 验证中间目录也被创建了
        assertTrue(Files.exists(tempDir.resolve("parent")), "Parent directory should be created");
        assertTrue(Files.exists(tempDir.resolve("parent/child")), "Child directory should be created");
    }

    /**
     * 测试对已存在目录执行 mkdir 的情况
     * 在类Unix系统上执行
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testMkdirExistingDirectory() throws IOException {
        String testDirName = "existing_dir";
        Path existingDir = tempDir.resolve(testDirName);
        
        // 先创建目录
        Files.createDirectory(existingDir);
        assertTrue(Files.exists(existingDir));

        JsonObject input = new JsonObject();
        input.addProperty("command", "cd " + tempDir.toString() + " && mkdir " + testDirName);
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 30);

        JsonObject result = tool.execute(role, input);

        // mkdir 对已存在的目录应该返回错误（非零退出码）
        assertNotNull(result);
        assertTrue(result.has("exit_code"));
        assertNotEquals(0, result.get("exit_code").getAsInt());
        assertTrue(result.has("success"));
        assertFalse(result.get("success").getAsBoolean());
        
        // 输出应该包含错误信息
        assertTrue(result.has("output"));
        String output = result.get("output").getAsString();
        assertTrue(output.contains("exist") || output.contains("File exists"), 
                  "Output should indicate directory already exists: " + output);
    }

    /**
     * 测试 mkdir 创建多个目录
     * 在类Unix系统上执行
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testMkdirMultipleDirectories() throws IOException {
        String dir1 = "dir1";
        String dir2 = "dir2";
        String dir3 = "dir3";
        
        Path expectedDir1 = tempDir.resolve(dir1);
        Path expectedDir2 = tempDir.resolve(dir2);
        Path expectedDir3 = tempDir.resolve(dir3);
        
        // 确保目录不存在
        assertFalse(Files.exists(expectedDir1));
        assertFalse(Files.exists(expectedDir2));
        assertFalse(Files.exists(expectedDir3));

        JsonObject input = new JsonObject();
        input.addProperty("command", "cd " + tempDir.toString() + " && mkdir " + dir1 + " " + dir2 + " " + dir3);
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 30);

        JsonObject result = tool.execute(role, input);

        // 验证命令执行结果
        assertNotNull(result);
        assertEquals(0, result.get("exit_code").getAsInt());
        assertTrue(result.get("success").getAsBoolean());

        // 验证所有目录都被创建了
        assertTrue(Files.exists(expectedDir1), "Directory 1 should be created");
        assertTrue(Files.exists(expectedDir2), "Directory 2 should be created");
        assertTrue(Files.exists(expectedDir3), "Directory 3 should be created");
        
        assertTrue(Files.isDirectory(expectedDir1), "Path 1 should be a directory");
        assertTrue(Files.isDirectory(expectedDir2), "Path 2 should be a directory");
        assertTrue(Files.isDirectory(expectedDir3), "Path 3 should be a directory");
    }

    /**
     * 测试 mkdir 命令超时情况
     * 在类Unix系统上执行
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testMkdirWithTimeout() {
        JsonObject input = new JsonObject();
        input.addProperty("command", "cd " + tempDir.toString() + " && mkdir timeout_test");
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 1); // 很短的超时时间

        JsonObject result = tool.execute(role, input);

        // mkdir 命令通常很快，所以应该能在1秒内完成
        // 但如果真的超时了，应该有相应的错误信息
        assertNotNull(result);
        assertTrue(result.has("exit_code") || result.has("error"));
        
        if (result.has("error")) {
            String error = result.get("error").getAsString();
            if (error.contains("超时")) {
                // 如果真的超时了，这是预期的行为
                assertTrue(true);
            }
        }
    }

    /**
     * 测试带有任务进度的 mkdir 命令
     * 在类Unix系统上执行
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testMkdirWithTaskProgress() throws IOException {
        String testDirName = "progress_test";
        Path expectedDir = tempDir.resolve(testDirName);
        String taskProgress = "- [x] Navigate to directory\n- [x] Create directory\n- [ ] Set permissions";
        
        assertFalse(Files.exists(expectedDir));

        JsonObject input = new JsonObject();
        input.addProperty("command", "cd " + tempDir.toString() + " && mkdir " + testDirName);
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 30);
        input.addProperty("task_progress", taskProgress);

        JsonObject result = tool.execute(role, input);

        // 验证命令执行结果
        assertNotNull(result);
        assertEquals(0, result.get("exit_code").getAsInt());
        assertTrue(result.get("success").getAsBoolean());
        
        // 验证任务进度被包含在结果中
        assertTrue(result.has("task_progress"));
        assertEquals(taskProgress, result.get("task_progress").getAsString());

        // 验证目录被创建
        assertTrue(Files.exists(expectedDir));
        assertTrue(Files.isDirectory(expectedDir));
    }

    /**
     * 测试需要批准的 mkdir 命令（模拟潜在风险操作）
     * 在类Unix系统上执行
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testMkdirWithApprovalRequired() throws IOException {
        String testDirName = "approval_test";
        Path expectedDir = tempDir.resolve(testDirName);
        
        assertFalse(Files.exists(expectedDir));

        JsonObject input = new JsonObject();
        input.addProperty("command", "cd " + tempDir.toString() + " && mkdir " + testDirName);
        input.addProperty("requires_approval", true); // 需要批准
        input.addProperty("timeout", 30);

        JsonObject result = tool.execute(role, input);

        // 验证命令执行结果
        assertNotNull(result);
        assertEquals(0, result.get("exit_code").getAsInt());
        assertTrue(result.get("success").getAsBoolean());
        
        // 验证 requires_approval 标志被包含在结果中
        assertTrue(result.has("requires_approval"));
        assertTrue(result.get("requires_approval").getAsBoolean());

        // 验证目录被创建
        assertTrue(Files.exists(expectedDir));
        assertTrue(Files.isDirectory(expectedDir));
    }

    /**
     * 测试 Windows 系统上的 mkdir 命令
     * 仅在 Windows 系统上执行
     */
    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testMkdirOnWindows() throws IOException {
        String testDirName = "windows_test";
        Path expectedDir = tempDir.resolve(testDirName);
        
        assertFalse(Files.exists(expectedDir));

        JsonObject input = new JsonObject();
        // Windows 使用不同的命令格式
        input.addProperty("command", "cd /d " + tempDir.toString() + " && mkdir " + testDirName);
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 30);

        JsonObject result = tool.execute(role, input);

        // 验证命令执行结果
        assertNotNull(result);
        assertTrue(result.has("exit_code"));
        assertTrue(result.has("success"));
        
        // 在 Windows 上，如果命令成功，目录应该被创建
        if (result.get("success").getAsBoolean()) {
            assertTrue(Files.exists(expectedDir));
            assertTrue(Files.isDirectory(expectedDir));
        }
    }

    /**
     * 测试清除 zsh 环境变量缓存功能
     */
    @Test
    void testClearZshEnvironmentCache() {
        // 这是一个静态方法测试
        assertDoesNotThrow(() -> {
            ExecuteCommandTool.clearZshEnvironmentCache();
        });
        
        // 可以多次调用而不出错
        assertDoesNotThrow(() -> {
            ExecuteCommandTool.clearZshEnvironmentCache();
            ExecuteCommandTool.clearZshEnvironmentCache();
        });
    }

    /**
     * 测试无效的工作目录处理
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testMkdirWithInvalidWorkingDirectory() {
        JsonObject input = new JsonObject();
        // 使用一个不存在的工作目录路径
        input.addProperty("command", "mkdir test_invalid_wd");
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 30);

        // 创建一个没有有效工作目录的 ReactorRole
        ReactorRole invalidRole = new ReactorRole("test", null, new LLM(LLMConfig.builder().build()));

        JsonObject result = tool.execute(invalidRole, input);

        // 命令应该仍然能执行，只是使用默认的工作目录
        assertNotNull(result);
        assertTrue(result.has("exit_code"));
        assertTrue(result.has("working_directory"));
        
        // 工作目录应该回退到系统默认目录
        String workingDir = result.get("working_directory").getAsString();
        assertNotNull(workingDir);
        assertFalse(workingDir.isEmpty());
    }
}
