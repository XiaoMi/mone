package run.mone.hive.memory.longterm.llm;

import run.mone.hive.memory.longterm.config.LlmConfig;

import java.util.List;
import java.util.Map;

/**
 * LLM基础接口
 * 定义所有LLM提供商的通用接口
 */
public interface LLMBase {
    
    /**
     * 生成响应
     * 
     * @param messages 消息列表，每个消息包含role和content
     * @param responseFormat 响应格式，如"json_object"
     * @return 生成的响应文本
     */
    String generateResponse(List<Map<String, Object>> messages, String responseFormat);
    
    /**
     * 生成响应 (带工具支持)
     * 
     * @param messages 消息列表
     * @param tools 可用工具列表
     * @param toolChoice 工具选择策略
     * @param responseFormat 响应格式
     * @return 生成的响应
     */
    default String generateResponse(List<Map<String, Object>> messages, 
                                  List<Map<String, Object>> tools,
                                  String toolChoice,
                                  String responseFormat) {
        return generateResponse(messages, responseFormat);
    }
    
    /**
     * 生成带工具调用的响应
     * 
     * @param messages 消息列表
     * @param tools 可用工具列表
     * @return 包含工具调用的响应Map
     */
    default Map<String, Object> generateResponseWithTools(List<Map<String, Object>> messages, 
                                                         List<Map<String, Object>> tools) {
        // 默认实现：调用普通的generateResponse并包装结果
        String response = generateResponse(messages, tools, null, null);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("content", response);
        result.put("tool_calls", new java.util.ArrayList<>());
        return result;
    }
    
    /**
     * 检查模型是否支持视觉功能
     * 
     * @return 是否支持视觉
     */
    default boolean supportsVision() {
        return false;
    }
    
    /**
     * 检查模型是否为推理模型
     * 推理模型通常不支持某些参数如temperature等
     * 
     * @return 是否为推理模型
     */
    default boolean isReasoningModel() {
        String model = getConfig().getModel().toLowerCase();
        return model.contains("o1") || model.contains("o3") || model.contains("gpt-5");
    }
    
    /**
     * 获取配置
     * 
     * @return LLM配置
     */
    LlmConfig getConfig();
    
    /**
     * 验证配置
     * 
     * @throws IllegalArgumentException 如果配置无效
     */
    default void validateConfig() {
        LlmConfig config = getConfig();
        if (config == null) {
            throw new IllegalArgumentException("LLM config cannot be null");
        }
        
        if (config.getModel() == null || config.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
    }
    
    /**
     * 获取支持的参数
     * 推理模型只支持部分参数
     * 
     * @param additionalParams 额外参数
     * @return 过滤后的参数
     */
    default Map<String, Object> getSupportedParams(Map<String, Object> additionalParams) {
        Map<String, Object> params = new java.util.HashMap<>();
        
        if (isReasoningModel()) {
            // 推理模型只支持基本参数
            if (additionalParams.containsKey("messages")) {
                params.put("messages", additionalParams.get("messages"));
            }
            if (additionalParams.containsKey("response_format")) {
                params.put("response_format", additionalParams.get("response_format"));
            }
            if (additionalParams.containsKey("tools")) {
                params.put("tools", additionalParams.get("tools"));
            }
            if (additionalParams.containsKey("tool_choice")) {
                params.put("tool_choice", additionalParams.get("tool_choice"));
            }
        } else {
            // 普通模型支持所有参数
            params.putAll(additionalParams);
            params.put("temperature", getConfig().getTemperature());
            params.put("max_tokens", getConfig().getMaxTokens());
            params.put("top_p", getConfig().getTopP());
        }
        
        return params;
    }
    
    /**
     * 关闭资源
     */
    default void close() {
        // 默认实现为空，子类可以重写
    }
}
