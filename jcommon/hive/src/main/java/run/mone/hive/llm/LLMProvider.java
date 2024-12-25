
package run.mone.hive.llm;

import lombok.Getter;

@Getter
public enum LLMProvider {

    STEPFUN("https://api.stepfun.com/v1/chat/completions", "STEPFUN_API_KEY", "step-1-8k"),
    GOOGLE("https://generativelanguage.googleapis.com/v1beta/openai/chat/completions", "GOOGLE_API_KEY", "gemini-2.0-flash-exp"),
    DEEPSEEK("https://api.deepseek.com/v1/chat/completions", "DEEPSEEK_API_KEY", "deepseek-chat");

    private final String url;

    private final String envName;

    private final String defaultModel;

    LLMProvider(String url, String envName, String defaultModel) {
        this.url = url;
        this.envName = envName;
        this.defaultModel = defaultModel;
    }
}
