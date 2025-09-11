package run.mone.hive.memory.longterm.llm.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.llm.LLMBase;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class OllamaLLM implements LLMBase {
    private final LlmConfig config;
    
    public OllamaLLM(LlmConfig config) {
        this.config = config;
        validateConfig();
    }
    
    @Override
    public String generateResponse(List<Map<String, Object>> messages, String responseFormat) {
        throw new UnsupportedOperationException("Ollama LLM implementation coming soon");
    }
}
