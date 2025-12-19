package run.mone.hive.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 FocusChain 在任务完成时的自动完成功能
 */
class FocusChainAutoCompleteTest {

    @TempDir
    Path tempDir;

    private FocusChainManager focusChainManager;
    private TaskState taskState;
    private AtomicReference<String> lastMessage;

    @BeforeEach
    void setUp() {
        taskState = new TaskState();
        FocusChainSettings settings = new FocusChainSettings(true, 6);
        LLMTaskProcessor mockLlm = new LLMTaskProcessor() {
            @Override
            public String sendMessage(String message) {
                return "Mock response";
            }

            @Override
            public String sendMessage(String systemPrompt, String userMessage) {
                return "Mock response";
            }
        };

        lastMessage = new AtomicReference<>();

        try {
            focusChainManager = new FocusChainManager(
                "test-task-001",
                taskState,
                Mode.ACT,
                tempDir.toString(),
                settings,
                mockLlm
            );

            focusChainManager.setSayCallback(msg -> lastMessage.set(msg));
        } catch (Exception e) {
            fail("Failed to initialize FocusChainManager: " + e.getMessage());
        }
    }

    @Test
    void testAutoCompleteAllTasks_WithIncompleteItems() {
        // 准备：创建一个有未完成项的任务列表
        String incompleteTodoList =
            "- [x] 分析用户需求\n" +
            "- [x] 设计系统架构\n" +
            "- [ ] 创建项目结构\n" +
            "- [ ] 实现核心功能\n" +
            "- [ ] 编写测试用例";

        // 设置当前的 Focus Chain 列表
        taskState.setCurrentFocusChainChecklist(incompleteTodoList);

        // 验证初始状态
        FocusChainFileUtils.FocusChainCounts initialCounts =
            FocusChainFileUtils.parseFocusChainListCounts(incompleteTodoList);
        assertEquals(5, initialCounts.getTotalItems(), "应该有5个任务项");
        assertEquals(2, initialCounts.getCompletedItems(), "应该有2个已完成项");
        assertEquals(3, initialCounts.getIncompleteItems(), "应该有3个未完成项");

        // 执行：调用自动完成方法
        focusChainManager.autoCompleteAllTasks();

        // 验证：所有任务应该被标记为完成
        String updatedList = taskState.getCurrentFocusChainChecklist();
        assertNotNull(updatedList, "更新后的列表不应为空");

        FocusChainFileUtils.FocusChainCounts updatedCounts =
            FocusChainFileUtils.parseFocusChainListCounts(updatedList);
        assertEquals(5, updatedCounts.getTotalItems(), "任务总数应该保持不变");
        assertEquals(5, updatedCounts.getCompletedItems(), "所有任务应该被标记为完成");
        assertEquals(0, updatedCounts.getIncompleteItems(), "不应该有未完成项");

        // 验证所有行都是 [x]
        String[] lines = updatedList.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("-")) {
                assertTrue(line.contains("[x]"),
                    "所有任务项应该包含 [x]: " + line);
            }
        }
    }

    @Test
    void testAutoCompleteAllTasks_AlreadyComplete() {
        // 准备：创建一个所有项都已完成的列表
        String completeTodoList =
            "- [x] 分析用户需求\n" +
            "- [x] 设计系统架构\n" +
            "- [x] 创建项目结构";

        taskState.setCurrentFocusChainChecklist(completeTodoList);

        String beforeAuto = taskState.getCurrentFocusChainChecklist();

        // 执行：调用自动完成方法
        focusChainManager.autoCompleteAllTasks();

        // 验证：列表应该保持不变（因为已经全部完成）
        String afterAuto = taskState.getCurrentFocusChainChecklist();
        assertEquals(beforeAuto, afterAuto,
            "已完成的列表应该保持不变");
    }

    @Test
    void testAutoCompleteAllTasks_NoFocusChain() {
        // 准备：没有 Focus Chain 列表的情况
        taskState.setCurrentFocusChainChecklist(null);

        // 执行：调用自动完成方法（不应该抛出异常）
        assertDoesNotThrow(() -> focusChainManager.autoCompleteAllTasks(),
            "当没有 Focus Chain 时不应抛出异常");

        // 验证：列表应该仍然为空
        assertNull(taskState.getCurrentFocusChainChecklist(),
            "列表应该保持为 null");
    }

    @Test
    void testAutoCompleteAllTasks_DisabledFocusChain() {
        // 准备：禁用 Focus Chain 功能
        FocusChainSettings disabledSettings = new FocusChainSettings(false, 6);
        LLMTaskProcessor mockLlm = new LLMTaskProcessor() {
            @Override
            public String sendMessage(String message) {
                return "Mock";
            }

            @Override
            public String sendMessage(String systemPrompt, String userMessage) {
                return "Mock";
            }
        };

        FocusChainManager disabledManager = new FocusChainManager(
            "test-disabled",
            taskState,
            Mode.ACT,
            tempDir.toString(),
            disabledSettings,
            mockLlm
        );

        String todoList = "- [ ] 未完成任务";
        taskState.setCurrentFocusChainChecklist(todoList);

        // 执行：调用自动完成方法
        disabledManager.autoCompleteAllTasks();

        // 验证：列表不应该被修改（因为功能被禁用）
        assertEquals(todoList, taskState.getCurrentFocusChainChecklist(),
            "禁用时列表不应被修改");
    }

    @Test
    void testAttemptCompletionIntegration() throws IOException {
        // 模拟完整的工作流程
        String initialList =
            "- [x] 创建组件\n" +
            "- [ ] 添加样式\n" +
            "- [ ] 测试功能";

        // 1. 更新初始列表
        focusChainManager.updateFCListFromToolResponse(initialList);

        // 2. 模拟任务执行中...

        // 3. 模拟 attempt_completion 被调用（没有 task_progress）
        focusChainManager.autoCompleteAllTasks();

        // 验证：所有任务应该被自动完成
        String finalList = taskState.getCurrentFocusChainChecklist();
        FocusChainFileUtils.FocusChainCounts finalCounts =
            FocusChainFileUtils.parseFocusChainListCounts(finalList);

        assertEquals(3, finalCounts.getTotalItems());
        assertEquals(3, finalCounts.getCompletedItems());
        assertEquals(0, finalCounts.getIncompleteItems());

        // 验证文件也被更新
        String fileContent = focusChainManager.readFocusChainFromDisk();
        assertNotNull(fileContent);
        assertTrue(fileContent.contains("- [x] 添加样式"));
        assertTrue(fileContent.contains("- [x] 测试功能"));
    }
}
