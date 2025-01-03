package run.mone.hive.llm;

import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;

public class LLMFactory {
    public static LLM createLLM(LLMConfig config, Context context) {
        if (context == null) {
            context = new Context();
        }
        
        if (config != null) {
            return context.llmWithCostManagerFromLLMConfig(config);
        }
        
        return context.llm();
    }
} 