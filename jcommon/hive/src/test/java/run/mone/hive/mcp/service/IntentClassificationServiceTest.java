package run.mone.hive.mcp.service;

import org.apache.commons.lang3.tuple.Pair;
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
        private final Double mockScore;
        private final boolean shouldThrowException;
        
        public TestableIntentClassificationService(String mockClassificationResult, Double mockScore) {
            this.mockClassificationResult = mockClassificationResult;
            this.mockScore = mockScore;
            this.shouldThrowException = false;
        }
        
        public TestableIntentClassificationService(boolean shouldThrowException) {
            this.mockClassificationResult = null;
            this.mockScore = null;
            this.shouldThrowException = shouldThrowException;
        }
        
        @Override
        public Pair<String, Double> getInterruptClassification(InterruptQuery interruptQuery, Message msg) {
            if (shouldThrowException) {
                throw new RuntimeException("模拟分类服务异常");
            }
            return Pair.of(mockClassificationResult, mockScore);
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
     * 测试当启用自动打断检测且分类结果为"打断"且分数>0.9时，应该返回 true
     */
    @Test 
    void testShouldInterruptExecution_WhenEnabledAndClassificationReturnsInterruptWithHighScore_ShouldReturnTrue() {
        // 创建测试专用服务，返回"打断"且分数>0.9
        TestableIntentClassificationService testService = new TestableIntentClassificationService("打断", 0.95);
        
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
        assertTrue(result, "当分类结果为'打断'且分数>0.9时，应该返回 true");
    }

    /**
     * 测试当启用自动打断检测且分类结果为"打断"但分数≤0.9时，应该返回 false
     */
    @Test
    void testShouldInterruptExecution_WhenEnabledAndClassificationReturnsInterruptWithLowScore_ShouldReturnFalse() {
        // 创建测试专用服务，返回"打断"但分数≤0.9
        TestableIntentClassificationService testService = new TestableIntentClassificationService("打断", 0.85);
        
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
        assertFalse(result, "当分类结果为'打断'但分数≤0.9时，应该返回 false");
    }

    /**
     * 测试当启用自动打断检测但分类结果为其他值时，应该返回 false
     */
    @Test
    void testShouldInterruptExecution_WhenEnabledAndClassificationReturnsOther_ShouldReturnFalse() {
        // 创建测试专用服务，返回"不需要打断"
        TestableIntentClassificationService testService = new TestableIntentClassificationService("不需要打断", 0.95);
        
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
        TestableIntentClassificationService testService = new TestableIntentClassificationService("未知", 0.5);
        
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
     * 测试不同的打断关键词和分数组合
     */
    @Test
    void testShouldInterruptExecution_WithDifferentInterruptKeywordsAndScores() {
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
        
        // 测试应该返回true的分类结果：只有"打断"且分数>0.9
        TestableIntentClassificationService testService1 = new TestableIntentClassificationService("打断", 0.95);
        boolean shouldInterrupt1 = testService1.shouldInterruptExecution(interruptQuery, message);
        assertTrue(shouldInterrupt1, "分类结果为'打断'且分数>0.9时应该返回 true");
        
        // 测试应该返回false的分类结果
        Object[][] nonInterruptResults = {
            {"打断", 0.85},  // 分类正确但分数不够
            {"打断", 0.9},   // 分类正确但分数等于阈值
            {"不需要打断", 0.95}, // 分数够但分类错误
            {"否", 0.95},
            {"继续", 0.95},
            {"未知", 0.95},
            {"", 0.95},
            {null, 0.95}
        };
        
        for (Object[] result : nonInterruptResults) {
            TestableIntentClassificationService testService = new TestableIntentClassificationService((String) result[0], (Double) result[1]);
            boolean shouldInterrupt = testService.shouldInterruptExecution(interruptQuery, message);
            assertFalse(shouldInterrupt, "分类结果为'" + result[0] + "'且分数为" + result[1] + "时应该返回 false");
        }
    }

    /**
     * 测试分数阈值边界条件
     */
    @Test
    void testShouldInterruptExecution_ScoreThresholdBoundary() {
        // 准备测试数据
        InterruptQuery interruptQuery = InterruptQuery.builder()
                .autoInterruptQuery(true)
                .version("test-version")
                .modelType("test-model")
                .releaseServiceName("test-service")
                .build();
        
        Message message = Message.builder()
                .content("测试分数阈值")
                .role("user")
                .build();
        
        // 测试分数刚好等于0.9的情况（应该返回false）
        TestableIntentClassificationService testService1 = new TestableIntentClassificationService("打断", 0.9);
        boolean result1 = testService1.shouldInterruptExecution(interruptQuery, message);
        assertFalse(result1, "当分数等于0.9时，应该返回 false");
        
        // 测试分数刚好大于0.9的情况（应该返回true）
        TestableIntentClassificationService testService2 = new TestableIntentClassificationService("打断", 0.900001);
        boolean result2 = testService2.shouldInterruptExecution(interruptQuery, message);
        assertTrue(result2, "当分数刚好大于0.9时，应该返回 true");
        
        // 测试分数为1.0的情况（应该返回true）
        TestableIntentClassificationService testService3 = new TestableIntentClassificationService("打断", 1.0);
        boolean result3 = testService3.shouldInterruptExecution(interruptQuery, message);
        assertTrue(result3, "当分数为1.0时，应该返回 true");
        
        // 测试分数为0的情况（应该返回false）
        TestableIntentClassificationService testService4 = new TestableIntentClassificationService("打断", 0.0);
        boolean result4 = testService4.shouldInterruptExecution(interruptQuery, message);
        assertFalse(result4, "当分数为0时，应该返回 false");
    }
}
