package run.mone.hive.llm;

import run.mone.hive.configs.LLMConfig;

public class OpenAILLM extends BaseLLM {
    public OpenAILLM(LLMConfig config) {
        super(config);
    }

    @Override
    public String chat(String prompt) {
        // Implement OpenAI chat completion logic here
        return null;
    }
} 