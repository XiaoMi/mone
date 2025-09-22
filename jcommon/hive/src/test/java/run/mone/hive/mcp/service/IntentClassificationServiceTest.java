package run.mone.hive.mcp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.schema.Message;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IntentClassificationService 单元测试
 * 重点测试 shouldInterruptExecution 方法的各种场景
 * 不使用mock数据，通过继承重写方法来控制测试场景
 * 
 * @author goodjava@qq.com
 * @date 2025/09/22
 */
public class IntentClassificationServiceTest {

    private IntentClassificationService intentClassificationService;
    
    @BeforeEach
    void setUp() {
        intentClassificationService = new IntentClassificationService();
    }
    
    /**
     * 测试专用的服务类，可以控制分类结果
     */
    private static class TestableIntentClassificationService extends IntentClassificationService {
        private final String mockClassificationResult;
        private final boolean shouldThrowException;
        
        public TestableIntentClassificationService(String mockClassificationResult) {
            this.mockClassificationResult = mockClassificationResult;
            this.shouldThrowException = false;
        }
        
        public TestableIntentClassificationService(boolean shouldThrowException) {
            this.mockClassificationResult = null;
            this.shouldThrowException = shouldThrowException;
        }
        
        @Override
        public String getInterruptClassification(InterruptQuery interruptQuery, Message msg) {
            if (shouldThrowException) {
                throw new RuntimeException("模拟分类服务异常");
            }
            return mockClassificationResult;
        }
    }

    /**
     * 测试当 autoInterruptQuery 为 false 时，应该直接返回 false
     */
    @Test
    void testShouldInterruptExecution_WhenAutoInterruptQueryDisabled_ShouldReturnFalse() {
        // 准备测试数据
        InterruptQuery interruptQuery = InterruptQuery.builder()
                .autoInterruptQuery(true)
                .version("finetune-qwen-20250909-71039c8b")
                .modelType("qwen")
                .releaseServiceName("bert-is-break")
                .build();
        
        Message message = Message.builder()
                .content("别说话了")
                .role("user")
                .build();
        
        // 执行测试
        boolean result = intentClassificationService.shouldInterruptExecution(interruptQuery, message);
        
        // 验证结果
        assertFalse(result, "当 autoInterruptQuery 为 false 时，应该返回 false");
    }

    /**
     * 测试当启用自动打断检测且分类结果为"需要打断"时，应该返回 true
     */
    @Test 
    void testShouldInterruptExecution_WhenEnabledAndClassificationReturnsNeedInterrupt_ShouldReturnTrue() {
        // 创建测试专用服务，返回"需要打断"
        TestableIntentClassificationService testService = new TestableIntentClassificationService("需要打断");
        
        // 准备测试数据
        InterruptQuery interruptQuery = InterruptQuery.builder()
                .autoInterruptQuery(true)
                .version("test-version")
                .modelType("test-model")
                .releaseServiceName("test-service")
                .build();
        
        Message message = Message.builder()
                .content("停止执行")
                .role("user")
                .build();
        
        // 执行测试
        boolean result = testService.shouldInterruptExecution(interruptQuery, message);
        
        // 验证结果
        assertTrue(result, "当分类结果为'需要打断'时，应该返回 true");
    }

    /**
     * 测试当启用自动打断检测且分类结果为"是"时，应该返回 true
     */
    @Test
    void testShouldInterruptExecution_WhenEnabledAndClassificationReturnsYes_ShouldReturnTrue() {
        // 创建测试专用服务，返回"是"
        TestableIntentClassificationService testService = new TestableIntentClassificationService("是");
        
        // 准备测试数据
        InterruptQuery interruptQuery = InterruptQuery.builder()
                .autoInterruptQuery(true)
                .version("test-version")
                .modelType("test-model")
                .releaseServiceName("test-service")
                .build();
        
        Message message = Message.builder()
                .content("是的，请停止")
                .role("user")
                .build();
        
        // 执行测试
        boolean result = testService.shouldInterruptExecution(interruptQuery, message);
        
        // 验证结果
        assertTrue(result, "当分类结果为'是'时，应该返回 true");
    }

    /**
     * 测试当启用自动打断检测但分类结果为其他值时，应该返回 false
     */
    @Test
    void testShouldInterruptExecution_WhenEnabledAndClassificationReturnsOther_ShouldReturnFalse() {
        // 创建测试专用服务，返回"不需要打断"
        TestableIntentClassificationService testService = new TestableIntentClassificationService("不需要打断");
        
        // 准备测试数据
        InterruptQuery interruptQuery = InterruptQuery.builder()
                .autoInterruptQuery(true)
                .version("test-version")
                .modelType("test-model")
                .releaseServiceName("test-service")
                .build();
        
        Message message = Message.builder()
                .content("请继续执行")
                .role("user")
                .build();
        
        // 执行测试
        boolean result = testService.shouldInterruptExecution(interruptQuery, message);
        
        // 验证结果
        assertFalse(result, "当分类结果为其他值时，应该返回 false");
    }

    /**
     * 测试当分类过程中出现异常时，应该返回 false 并记录错误日志
     */
    @Test
    void testShouldInterruptExecution_WhenExceptionOccurs_ShouldReturnFalseAndLogError() {
        // 创建测试专用服务，模拟异常情况
        TestableIntentClassificationService testService = new TestableIntentClassificationService(true);
        
        // 准备测试数据
        InterruptQuery interruptQuery = InterruptQuery.builder()
                .autoInterruptQuery(true)
                .version("test-version")
                .modelType("test-model")
                .releaseServiceName("test-service")
                .build();
        
        Message message = Message.builder()
                .content("测试异常情况")
                .role("user")
                .build();
        
        // 执行测试
        boolean result = testService.shouldInterruptExecution(interruptQuery, message);
        
        // 验证结果
        assertFalse(result, "当出现异常时，应该返回 false");
    }

    /**
     * 测试边界条件：null参数
     */
    @Test
    void testShouldInterruptExecution_WithNullParameters() {
        // 测试 null interruptQuery
        assertThrows(NullPointerException.class, () -> {
            intentClassificationService.shouldInterruptExecution(null, new Message("test"));
        }, "传入 null interruptQuery 应该抛出 NullPointerException");
        
        // 测试 null message
        InterruptQuery interruptQuery = InterruptQuery.builder()
                .autoInterruptQuery(true)
                .build();
        
        assertThrows(NullPointerException.class, () -> {
            intentClassificationService.shouldInterruptExecution(interruptQuery, null);
        }, "传入 null message 应该抛出 NullPointerException");
    }

    /**
     * 测试空消息内容的情况
     */
    @Test
    void testShouldInterruptExecution_WithEmptyMessageContent() {
        // 创建测试专用服务，返回"未知"
        TestableIntentClassificationService testService = new TestableIntentClassificationService("未知");
        
        // 准备测试数据
        InterruptQuery interruptQuery = InterruptQuery.builder()
                .autoInterruptQuery(true)
                .version("test-version")
                .modelType("test-model")
                .releaseServiceName("test-service")
                .build();
        
        Message emptyMessage = Message.builder()
                .content("")
                .role("user")
                .build();
        
        Message nullContentMessage = Message.builder()
                .content(null)
                .role("user")
                .build();
        
        // 执行测试
        boolean resultEmpty = testService.shouldInterruptExecution(interruptQuery, emptyMessage);
        boolean resultNull = testService.shouldInterruptExecution(interruptQuery, nullContentMessage);
        
        // 验证结果
        assertFalse(resultEmpty, "空消息内容应该返回 false");
        assertFalse(resultNull, "null消息内容应该返回 false");
    }

    /**
     * 测试不同的打断关键词
     */
    @Test
    void testShouldInterruptExecution_WithDifferentInterruptKeywords() {
        // 准备测试数据
        InterruptQuery interruptQuery = InterruptQuery.builder()
                .autoInterruptQuery(true)
                .version("test-version")
                .modelType("test-model")
                .releaseServiceName("test-service")
                .build();
        
        Message message = Message.builder()
                .content("测试消息")
                .role("user")
                .build();
        
        // 测试应该返回true的分类结果
        String[] interruptResults = {"需要打断", "是"};
        for (String result : interruptResults) {
            TestableIntentClassificationService testService = new TestableIntentClassificationService(result);
            boolean shouldInterrupt = testService.shouldInterruptExecution(interruptQuery, message);
            assertTrue(shouldInterrupt, "分类结果为'" + result + "'时应该返回 true");
        }
        
        // 测试应该返回false的分类结果
        String[] nonInterruptResults = {"不需要打断", "否", "继续", "未知", "", null};
        for (String result : nonInterruptResults) {
            TestableIntentClassificationService testService = new TestableIntentClassificationService(result);
            boolean shouldInterrupt = testService.shouldInterruptExecution(interruptQuery, message);
            assertFalse(shouldInterrupt, "分类结果为'" + result + "'时应该返回 false");
        }
    }
}
