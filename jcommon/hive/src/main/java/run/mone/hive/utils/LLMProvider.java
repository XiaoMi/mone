package run.mone.hive.utils;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.Message;

@Slf4j
public class LLMProvider {
    private static LLMProvider instance;
    
    private LLMProvider() {
        // Initialize your LLM configuration here
    }
    
    public static synchronized LLMProvider getInstance() {
        if (instance == null) {
            instance = new LLMProvider();
        }
        return instance;
    }
    
    public String chat(List<Message> messages) {
        // Implement your actual LLM chat logic here
        // This could be calling OpenAI API, local LLM, or any other implementation
        throw new UnsupportedOperationException("LLM chat implementation required");
    }
} 