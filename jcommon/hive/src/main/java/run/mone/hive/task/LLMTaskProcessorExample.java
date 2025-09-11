package run.mone.hive.task;

import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLMProvider;

/**
 * LLMTaskProcessor使用示例
 * 展示如何使用重命名后的LLM接口及其实现
 */
public class LLMTaskProcessorExample {
    
    public static void main(String[] args) {
        // 创建LLM配置
        LLMConfig config = new LLMConfig();
        config.setDebug(false);
        config.setLlmProvider(LLMProvider.DEEPSEEK);
        
        // 使用实现类创建LLMTaskProcessor
        LLMTaskProcessor llmProcessor = new LLMTaskProcessorImpl(config);
        
        // 示例1：简单消息发送
        System.out.println("=== 示例1：简单消息发送 ===");
        String response1 = llmProcessor.sendMessage("hi");
        System.out.println("响应：" + response1);

        // 示例2：带系统提示的消息发送
        System.out.println("\n=== 示例2：带系统提示的消息发送 ===");
        String systemPrompt = "你是一个专业的Java开发专家，请用简洁明了的方式回答问题";
        String userMessage = "什么是Spring框架？";
        String response2 = llmProcessor.sendMessage(systemPrompt, userMessage);
        System.out.println("响应：" + response2);
        
        // 示例3：检查task_progress支持
        System.out.println("\n=== 示例3：检查task_progress支持 ===");
        boolean supportsProgress = llmProcessor.supportsTaskProgress();
        System.out.println("支持task_progress参数：" + supportsProgress);
        
        // 示例4：使用内部LLM实例进行更复杂的操作
        if (llmProcessor instanceof LLMTaskProcessorImpl) {
            System.out.println("\n=== 示例4：使用内部LLM实例 ===");
            LLMTaskProcessorImpl impl = (LLMTaskProcessorImpl) llmProcessor;
            
            // 获取内部LLM实例，可以使用更多高级功能
            var llm = impl.getLLM();
            System.out.println("内部LLM模型：" + llm.getModel());
            System.out.println("LLM配置提供商：" + llm.getConfig().getLlmProvider());
            
            // 可以直接使用LLM的高级功能，比如多模态、流式处理等
            // String advancedResponse = llm.chat("更复杂的查询");
        }
        
        System.out.println("\n=== LLMTaskProcessor示例完成 ===");
    }
    
    /**
     * 创建一个简单的Mock实现用于测试
     */
    public static LLMTaskProcessor createMockProcessor() {
        return new LLMTaskProcessor() {
            @Override
            public String sendMessage(String message) {
                return "Mock响应：" + message;
            }
            
            @Override
            public String sendMessage(String systemPrompt, String userMessage) {
                return String.format("Mock响应 [系统提示：%s] [用户消息：%s]", 
                                   systemPrompt.substring(0, Math.min(20, systemPrompt.length())), 
                                   userMessage);
            }
            
            @Override
            public boolean supportsTaskProgress() {
                return true;
            }
        };
    }
}
