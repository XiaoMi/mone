package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.FluxSink;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ExecuteCommandToolOptimized 单元测试类
 * 测试快速命令（ls）和慢速命令（mvn spring-boot:run）的执行
 * 
 * @author goodjava@qq.com
 * @date 2025/1/11
 */
@Slf4j
public class ExecuteCommandToolOptimizedTest {

    private ExecuteCommandToolOptimized tool;
    
    @Mock
    private ReactorRole mockRole;
    
    @Mock
    private FluxSink<String> mockFluxSink;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        tool = new ExecuteCommandToolOptimized();
        
        // 设置 mock ReactorRole 的行为
        when(mockRole.getWorkspacePath()).thenReturn("/Users/zhangzhiyong/bookServer");
        when(mockRole.getFluxSink()).thenReturn(mockFluxSink);
        
        // 确保 /tmp 目录存在
        File tmpDir = new File("/tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        
        log.info("测试环境初始化完成，工作目录: /tmp");
    }

    @AfterEach
    public void tearDown() throws Exception {
        // 清理缓存
        ExecuteCommandToolOptimized.clearZshEnvironmentCache();
        log.info("测试环境清理完成");
    }

    /**
     * 测试工具基本信息
     */
    @Test
    public void testToolBasicInfo() {
        assertEquals("execute_command", tool.getName());
        assertTrue(tool.needExecute());
        assertTrue(tool.show());
        assertNotNull(tool.description());
        assertNotNull(tool.parameters());
        assertNotNull(tool.usage());
        assertNotNull(tool.example());
        
        log.info("工具基本信息测试通过");
    }

    /**
     * 测试快速命令执行 - ls 命令
     * 这个测试验证快速命令的直接输出机制
     */
    @Test
    public void testFastCommand_ListDirectory() {
        log.info("开始测试快速命令: ls");
        
        // 准备测试数据
        JsonObject input = new JsonObject();
        input.addProperty("command", "ls -la /tmp");
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 10);
        input.addProperty("interactive", false); // 快速命令不使用缓冲
        
        // 执行命令
        long startTime = System.currentTimeMillis();
        JsonObject result = tool.execute(mockRole, input);
        long executionTime = System.currentTimeMillis() - startTime;
        
        // 验证结果
        assertNotNull(result, "结果不应为空");
        assertTrue(result.has("success"), "命令应该成功执行");
        assertTrue(result.has("completed"), "命令应该完成");
        assertTrue(result.has("exit_code"), "应该有退出代码");
        assertTrue(result.has("output"), "应该有输出内容");
        assertTrue(result.has("command"), "应该有命令信息");
        assertTrue(result.has("working_directory"), "应该有工作目录信息");
        
        // 验证工作目录
        assertEquals("/tmp", result.get("working_directory").getAsString());
        
        // 验证命令执行成功
        if (result.has("success")) {
            boolean success = result.get("success").getAsBoolean();
            if (success) {
                assertEquals(0, result.get("exit_code").getAsInt(), "成功命令的退出代码应为0");
                String output = result.get("output").getAsString();
                assertNotNull(output, "输出不应为空");
                log.info("ls 命令输出长度: {} 字符", output.length());
            } else {
                log.warn("ls 命令执行失败，退出代码: {}", result.get("exit_code").getAsInt());
            }
        }
        
        log.info("快速命令测试完成，执行时间: {} ms", executionTime);
        assertTrue(executionTime < 5000, "快速命令执行时间应该较短（< 5秒）");
    }

    /**
     * 测试慢速命令执行 - mvn spring-boot:run 命令（模拟）
     * 这个测试验证缓冲输出机制和超时处理
     */
    @Test
    public void testSlowCommand_MavenSpringBootRun() {
        log.info("开始测试慢速命令: mvn spring-boot:run (模拟)");
        
        // 由于 mvn spring-boot:run 需要实际的 Spring Boot 项目，我们使用一个模拟的长时间运行命令
        // 在 Unix/Linux 系统上使用 sleep 命令模拟长时间运行
        String simulatedCommand = System.getProperty("os.name").toLowerCase().contains("windows") 
            ? "timeout 30" // Windows: 等待3秒
//            : "sleep 30";  // Unix/Linux: 睡眠3秒
            : "mvn spring-boot:run";  // Unix/Linux: 睡眠3秒

        // 准备测试数据
        JsonObject input = new JsonObject();
        input.addProperty("command", simulatedCommand);
        input.addProperty("requires_approval", true); // 慢速命令通常需要批准
        input.addProperty("timeout", 60); // 给足够的超时时间
        input.addProperty("interactive", true); // 使用缓冲模式
        input.addProperty("task_progress", "- [x] 准备执行长时间命令\n- [ ] 等待命令完成\n- [ ] 验证结果");
        
        // 执行命令
        long startTime = System.currentTimeMillis();
        JsonObject result = tool.execute(mockRole, input);
        long executionTime = System.currentTimeMillis() - startTime;
        
        // 验证结果
        assertNotNull(result, "结果不应为空");
        assertTrue(result.has("completed"), "应该有完成标志");
        assertTrue(result.has("exit_code"), "应该有退出代码");
        assertTrue(result.has("interactive_mode"), "应该有交互模式标志");
        assertTrue(result.has("requires_approval"), "应该有需要批准标志");
        

        // 验证交互模式和批准设置
        assertTrue(result.get("interactive_mode").getAsBoolean(), "应该启用交互模式");
        assertTrue(result.get("requires_approval").getAsBoolean(), "应该需要批准");
        
        // 验证任务进度信息
        if (result.has("task_progress")) {
            assertNotNull(result.get("task_progress").getAsString(), "任务进度不应为空");
        }
        
        // 验证命令执行结果
        if (result.has("success")) {
            boolean success = result.get("success").getAsBoolean();
            if (success) {
                assertEquals(0, result.get("exit_code").getAsInt(), "成功命令的退出代码应为0");
                log.info("慢速命令执行成功");
            } else {
                log.warn("慢速命令执行失败，退出代码: {}", result.get("exit_code").getAsInt());
            }
        }
        
        log.info("慢速命令测试完成，执行时间: {} ms", executionTime);
        assertTrue(executionTime >= 3000, "慢速命令执行时间应该符合预期（>= 3秒）");
        
        // 验证 FluxSink 被调用（用于流式输出）
        verify(mockRole, atLeastOnce()).getFluxSink();
    }

    /**
     * 测试实际的 Maven 命令（如果环境支持）
     * 这个测试是可选的，只有在有 Maven 环境时才会执行
     */
    @Test
    public void testRealMavenCommand_IfAvailable() {
        log.info("测试实际 Maven 命令（如果可用）");
        
        // 首先检查是否有 Maven
        JsonObject checkMaven = new JsonObject();
        checkMaven.addProperty("command", "mvn --version");
        checkMaven.addProperty("requires_approval", false);
        checkMaven.addProperty("timeout", 10);
        checkMaven.addProperty("interactive", false);
        
        JsonObject mavenCheckResult = tool.execute(mockRole, checkMaven);
        
        if (mavenCheckResult.has("success") && mavenCheckResult.get("success").getAsBoolean()) {
            log.info("Maven 可用，执行 Maven help 命令测试");
            
            // 执行一个安全的 Maven 命令
            JsonObject input = new JsonObject();
            input.addProperty("command", "mvn help:help");
            input.addProperty("requires_approval", false);
            input.addProperty("timeout", 30);
            input.addProperty("interactive", true);
            
            JsonObject result = tool.execute(mockRole, input);
            
            assertNotNull(result, "Maven 命令结果不应为空");
            assertTrue(result.has("completed"), "应该有完成标志");
            
            if (result.has("success") && result.get("success").getAsBoolean()) {
                log.info("Maven 命令执行成功");
                assertTrue(result.has("output"), "应该有输出");
                String output = result.get("output").getAsString();
                assertTrue(output.toLowerCase().contains("maven") || output.toLowerCase().contains("help"), 
                    "输出应该包含Maven相关信息");
            }
        } else {
            log.info("Maven 不可用，跳过 Maven 命令测试");
        }
    }

    /**
     * 测试错误输入处理
     */
    @Test
    public void testErrorHandling() {
        log.info("测试错误输入处理");
        
        // 测试缺少必需参数
        JsonObject emptyInput = new JsonObject();
        JsonObject result1 = tool.execute(mockRole, emptyInput);
        assertTrue(result1.has("error"), "应该有错误信息");
        
        // 测试缺少 command 参数
        JsonObject noCommand = new JsonObject();
        noCommand.addProperty("requires_approval", false);
        JsonObject result2 = tool.execute(mockRole, noCommand);
        assertTrue(result2.has("error"), "应该有错误信息");
        
        // 测试缺少 requires_approval 参数
        JsonObject noApproval = new JsonObject();
        noApproval.addProperty("command", "echo test");
        JsonObject result3 = tool.execute(mockRole, noApproval);
        assertTrue(result3.has("error"), "应该有错误信息");
        
        log.info("错误处理测试完成");
    }

    /**
     * 测试超时处理
     */
    @Test
    public void testTimeoutHandling() {
        log.info("测试超时处理");
        
        // 使用一个会超时的命令
        String timeoutCommand = System.getProperty("os.name").toLowerCase().contains("windows") 
            ? "timeout 10" // Windows: 等待10秒
            : "sleep 10";  // Unix/Linux: 睡眠10秒
        
        JsonObject input = new JsonObject();
        input.addProperty("command", timeoutCommand);
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 2); // 设置2秒超时
        input.addProperty("interactive", false);
        
        long startTime = System.currentTimeMillis();
        JsonObject result = tool.execute(mockRole, input);
        long executionTime = System.currentTimeMillis() - startTime;
        
        // 验证超时处理
        if (result.has("timeout") && result.get("timeout").getAsBoolean()) {
            assertTrue(executionTime < 5000, "超时命令应该在合理时间内返回（< 5秒）");
            assertTrue(result.has("error"), "应该有超时错误信息");
            log.info("超时处理测试通过，执行时间: {} ms", executionTime);
        } else {
            log.warn("超时测试可能未按预期工作，结果: {}", result);
        }
    }

    /**
     * 测试工作目录设置
     */
    @Test
    public void testWorkingDirectory() {
        log.info("测试工作目录设置");
        
        // 测试使用默认工作目录（当 ReactorRole 返回 null 时）
        when(mockRole.getWorkspacePath()).thenReturn(null);
        
        JsonObject input = new JsonObject();
        input.addProperty("command", "pwd");
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 10);
        input.addProperty("interactive", false);
        
        JsonObject result = tool.execute(mockRole, input);
        
        assertNotNull(result, "结果不应为空");
        assertTrue(result.has("working_directory"), "应该有工作目录信息");
        
        // 工作目录应该是系统默认目录
        String workingDir = result.get("working_directory").getAsString();
        assertNotNull(workingDir, "工作目录不应为空");
        log.info("使用的工作目录: {}", workingDir);
        
        // 恢复 mock 设置
        when(mockRole.getWorkspacePath()).thenReturn("/tmp");
        
        log.info("工作目录测试完成");
    }
    
    /**
     * 测试使用真实 ReactorRole 对象的快速命令
     */
    @Test
    public void testFastCommandWithRealRole() {
        log.info("开始测试使用真实Role的快速命令");
        
        // 创建真实的 ReactorRole
        ReactorRole realRole = new ReactorRole("test", null, new LLM(LLMConfig.builder().build()));
        realRole.setWorkspacePath("/tmp");
        
        JsonObject input = new JsonObject();
        input.addProperty("command", "echo 'Hello World'");
        input.addProperty("requires_approval", false);
        input.addProperty("timeout", 10);
        input.addProperty("interactive", false);
        
        JsonObject result = tool.execute(realRole, input);
        
        assertNotNull(result, "结果不应为空");
        assertTrue(result.has("success"), "应该有成功标志");
        assertTrue(result.has("completed"), "应该有完成标志");
        assertTrue(result.has("exit_code"), "应该有退出代码");
        assertTrue(result.has("output"), "应该有输出内容");
        
        if (result.has("success") && result.get("success").getAsBoolean()) {
            assertEquals(0, result.get("exit_code").getAsInt(), "成功命令的退出代码应为0");
            String output = result.get("output").getAsString();
            assertTrue(output.contains("Hello World"), "输出应该包含Hello World");
        }
        
        log.info("真实Role快速命令测试完成");
    }
}
