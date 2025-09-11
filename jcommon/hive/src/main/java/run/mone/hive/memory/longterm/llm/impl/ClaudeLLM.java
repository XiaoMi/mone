package run.mone.hive.memory.longterm.llm.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;

import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.llm.LLMBase;

import java.util.List;
import java.util.Map;

/**
 * Claude LLM实现
 * 支持Claude-3系列模型
 */
@Slf4j
@Data
public class ClaudeLLM implements LLMBase {
    
    private final LlmConfig config;
    
    public ClaudeLLM(LlmConfig config) {
        this.config = config;
        validateConfig();
        log.info("Claude LLM initialized with model: {}", config.getModel());
    }
    
    @Override
    public String generateResponse(List<Map<String, Object>> messages, String responseFormat) {
        // TODO: 实现Claude API调用
        log.warn("Claude LLM implementation is not yet complete");
        throw new UnsupportedOperationException("Claude LLM implementation coming soon");
    }
    
    @Override
    public boolean supportsVision() {
        String model = config.getModel().toLowerCase();
        return model.contains("claude-3");
    }
}
