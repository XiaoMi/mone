package run.mone.hive.roles;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.service.MemoryQuery;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.schema.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReactorRole 记忆功能测试
 */
public class ReactorRoleMemoryTest {

    private ReactorRole reactorRole;

    @BeforeEach
    public void setUp() {
        // 创建模拟的LLM
        LLM mockLLM = new LLM(LLMConfig.builder().build());

        // 创建CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);

        // 创建ReactorRole实例
        reactorRole = new ReactorRole("TestRole", latch, mockLLM);

        // 配置记忆查询
        MemoryQuery memoryQuery = MemoryQuery.builder()
                .autoMemoryQuery(true)
                .maxResults(5)
                .threshold(0.7)
                .agentId("TestRole")
                .build();

        // 配置RoleMeta
        RoleMeta roleMeta = RoleMeta.builder()
                .profile("测试角色")
                .goal("测试记忆功能")
                .memoryQuery(memoryQuery)
                .build();

        reactorRole.setRoleMeta(roleMeta);
    }

    @AfterEach
    public void tearDown() {
        if (reactorRole != null && reactorRole.getMemoryManager() != null) {
            reactorRole.getMemoryManager().close();
        }
    }

    @Test
    public void testMemoryInitialization() {
        // 测试记忆系统是否正确初始化
        assertNotNull(reactorRole.getMemoryManager(), "记忆管理器应该被初始化");
        assertNotNull(reactorRole.getMemoryManager().getLongTermMemory(), "长期记忆系统应该被初始化");
    }

    @Test
    public void testAddMemory() throws Exception {
        // 测试添加记忆
        String content = "用户喜欢喝咖啡";
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("category", "preference");

        CompletableFuture<Map<String, Object>> future = reactorRole.addMemory(
                content,
                null,
                "TestRole",
                null,
                metadata
        );

        Map<String, Object> result = future.get();
        assertNotNull(result, "添加记忆应该返回结果");
        assertFalse(result.containsKey("error"), "添加记忆不应该有错误");
    }

    @Test
    public void testSearchMemory() throws Exception {
        // 先添加一些记忆
        reactorRole.addMemory("用户住在北京", null, "TestRole", null, null).get();
        reactorRole.addMemory("用户是软件工程师", null, "TestRole", null, null).get();

        // 搜索记忆
        CompletableFuture<Map<String, Object>> future = reactorRole.searchMemory(
                "用户的工作",
                null,
                "TestRole",
                null,
                5,
                0.5
        );

        Map<String, Object> result = future.get();
        assertNotNull(result, "搜索记忆应该返回结果");
        assertFalse(result.containsKey("error"), "搜索记忆不应该有错误");
    }

    @Test
    public void testGetAllMemories() throws Exception {
        // 先添加一些记忆
        reactorRole.addMemory("记忆1", null, "TestRole", null, null).get();
        reactorRole.addMemory("记忆2", null, "TestRole", null, null).get();

        // 获取所有记忆
        CompletableFuture<Map<String, Object>> future = reactorRole.getAllMemories(
                null,
                "TestRole",
                null,
                10
        );

        Map<String, Object> result = future.get();
        assertNotNull(result, "获取所有记忆应该返回结果");
        assertFalse(result.containsKey("error"), "获取所有记忆不应该有错误");
    }

    @Test
    public void testMemoryQueryBuilding() {
        // 测试记忆查询在prompt构建中的集成
        Message testMessage = Message.builder()
                .content("你还记得我之前说过什么吗？")
                .role("user")
                .build();

        // 这里我们主要测试方法不会抛出异常
        assertDoesNotThrow(() -> {
            String prompt = reactorRole.buildUserPrompt(testMessage, "历史记录", null);
            assertNotNull(prompt, "构建的prompt不应该为null");
            assertTrue(prompt.contains("memory_info"), "prompt应该包含memory_info占位符");
        });
    }

    @Test
    public void testResetMemory() throws Exception {
        // 先添加一些记忆
        reactorRole.addMemory("测试记忆", null, "TestRole", null, null).get();

        // 重置记忆
        CompletableFuture<Boolean> future = reactorRole.resetMemory();
        Boolean result = future.get();

        assertTrue(result, "重置记忆应该成功");
    }
}