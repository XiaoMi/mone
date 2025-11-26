package run.mone.hive.task;

import run.mone.hive.llm.LLM;
import run.mone.hive.configs.LLMConfig;

/**
 * LLM任务处理接口的实现类
 * 内部使用 run.mone.hive.llm.LLM 提供的能力
 */
public class LLMTaskProcessorImpl implements LLMTaskProcessor {
    
    private final LLM llm;
    
    public LLMTaskProcessorImpl(LLMConfig config) {
        this.llm = new LLM(config);
    }
    
    public LLMTaskProcessorImpl(LLM llm) {
        this.llm = llm;
    }
    
    @Override
    public String sendMessage(String message) {
        return llm.chat(message);
    }
    
    @Override
    public String sendMessage(String systemPrompt, String userMessage) {
        // 使用LLM的chatCompletion方法，传入系统提示和用户消息
        return llm.chatCompletion(llm.getToken(), 
                                 java.util.Arrays.asList(
                                     run.mone.hive.schema.AiMessage.builder()
                                         .role("user")
                                         .content(userMessage)
                                         .build()
                                 ), 
                                 llm.getModel(), 
                                 systemPrompt, 
                                 llm.getConfig());
    }
    
    @Override
    public boolean supportsTaskProgress() {
        // 根据LLM的配置或能力来判断是否支持task_progress
        return true;
    }
    
    /**
     * 获取内部LLM实例，用于更高级的操作
     * @return LLM实例
     */
    public LLM getLLM() {
        return this.llm;
    }
}
