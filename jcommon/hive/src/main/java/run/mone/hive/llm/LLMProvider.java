
package run.mone.hive.llm;

import lombok.Getter;

@Getter
public enum LLMProvider {

    STEPFUN("https://api.stepfun.com/v1/chat/completions", "STEPFUN_API_KEY", "step-1-8k", null),
    GLM_45_AIR("https://open.bigmodel.cn/api/paas/v4/chat/completions", "GLM_API_KEY", "glm-4.5-air", null),
    GLM_45_V("https://open.bigmodel.cn/api/paas/v4/chat/completions", "GLM_API_KEY", "glm-4.5v", null),
    GLM_46("https://open.bigmodel.cn/api/paas/v4/chat/completions", "GLM_API_KEY", "glm-4.6", null),
    QWEN("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions", "QWEN_MODEL_KEY", "qwen3-max", null),
    //kimi-k2-turbo-preview moonshot-v1-auto
    MOONSHOT("https://api.moonshot.cn/v1/chat/completions", "MOONSHOT_MODEL_KEY", "kimi-k2-turbo-preview", null),
    KIMI_K2_TURBO_PREVIEW("https://api.moonshot.cn/v1/chat/completions", "MOONSHOT_MODEL_KEY", "kimi-k2-turbo-preview", null),
    STEPFUN_ASR("https://api.stepfun.com/v1/audio/transcriptions", "STEPFUN_API_KEY", "step-asr", null),
    STEPFUN_TTS("https://api.stepfun.com/v1/audio/speech", "STEPFUN_API_KEY", "step-tts-mini", null),
    GOOGLE("https://generativelanguage.googleapis.com/v1beta/openai/chat/completions", "GOOGLE_API_KEY", "gemini-2.0-flash-exp", null),
    //gemini-2.0-pro-exp-02-05 gemini-2.0-flash gemini-2.5-flash
    GOOGLE_2("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=", "GOOGLE_API_KEY", "gemini-2.5-flash", null),
    DEEPSEEK("https://api.deepseek.com/v1/chat/completions", "DEEPSEEK_API_KEY", "deepseek-chat", null),
    //openai/gpt-4o-2024-11-20 anthropic/claude-3.5-sonnet:beta anthropic/claude-3.7-sonnet
    OPENROUTER("https://openrouter.ai/api/v1/chat/completions", "OPENROUTER_API_KEY", "anthropic/claude-3.7-sonnet", null),
    OPENROUTER_CLAUDE_SONNET_45("https://openrouter.ai/api/v1/chat/completions", "OPENROUTER_API_KEY", "anthropic/claude-sonnet-4.5", null),
    OPENROUTER_OPENAI_CODEX_MINI("https://openrouter.ai/api/v1/chat/completions", "OPENROUTER_API_KEY", "openai/codex-mini", null),
    OPENROUTER_OPENAI_CODEX("https://openrouter.ai/api/v1/chat/completions", "OPENROUTER_API_KEY", "openai/gpt-5-codex", null),
    OPENROUTER_GEMINI_25_FLUSH("https://openrouter.ai/api/v1/chat/completions", "OPENROUTER_API_KEY", "google/gemini-2.5-flash", null),
    OPENROUTER_GEMINI_25_PRO("https://openrouter.ai/api/v1/chat/completions", "OPENROUTER_API_KEY", "google/gemini-2.5-pro", null),
    OPENROUTER_GROK_CODE_FAST("https://openrouter.ai/api/v1/chat/completions", "OPENROUTER_API_KEY", "x-ai/grok-code-fast-1", null),
    OPENAICOMPATIBLE("", "", "", null),
    OPENAI_MULTIMODAL_COMPATIBLE("", "", "", null),
    DOUBAO("https://ark.cn-beijing.volces.com/api/v3/chat/completions", "DOUBAO_API_KEY", null, "DOUBAO_MODEL_KEY"),
    DOUBAO_VISION("https://ark.cn-beijing.volces.com/api/v3/chat/completions", "DOUBAO_API_KEY", null, "DOUBAO_VISION_MODEL_KEY"),
    DOUBAO_UI_TARS("https://ark.cn-beijing.volces.com/api/v3/chat/completions", "DOUBAO_API_KEY", null, "DOUBAO_UI_TARS_MODEL_KEY"),
    DOUBAO_DEEPSEEK_R1("https://ark.cn-beijing.volces.com/api/v3/chat/completions", "DOUBAO_API_KEY", null, "DOUBAO_DEEPSEEK_R1_MODEL_KEY"),
    DOUBAO_DEEPSEEK_V3("https://ark.cn-beijing.volces.com/api/v3/chat/completions", "DOUBAO_API_KEY", null, "DOUBAO_DEEPSEEK_V3_MODEL_KEY"),
    //claude-3-5-sonnet@20240620
    CLAUDE_COMPANY("CLAUDE_URL", "CLAUDE_TOKEN", "claude-3-5-sonnet@20240620", "CLAUDE35_MODEL_KEY"),
    TENCENT_DEEPSEEK("https://api.lkeap.cloud.tencent.com/v1/chat/completions", "TENCENT_API_KEY", "deepseek-r1", null),
    GROK("https://api.x.ai/v1/chat/completions", "XAI_API_KEY", "grok-3-latest", null),
    MINIMAX("https://api.minimax.chat/v1/text/chatcompletion_v2", "MINIMAX_API_KEY", "MiniMax-Text-01", "MINIMAX_GROUP_ID"),
    QWEN3("", "xxx", "Qwen3-14B", ""),
    // 这里的模型设置不生效，需要去对应Agent上修改
    MIFY("XXX", "MIFY_API_KEY", "deepseek-r1", null),
    // MIFY 统一网关模型
    MIFY_GATEWAY("XXX", "MIFY_API_KEY", "deepseek-r1", null),
    // url只能从llmConfig里取
    CLOUDML_CLASSIFY("", "xxx", "CLOUDML_CLASSIFY", ""),
    AZURE_GPT5_CODEX("https://b2c-mione-gpt-o1.openai.azure.com/openai/v1/responses", "AZURE_OPENAI_CODEX_API_KEY", "gpt-5-codex", null),
    AZURE_GPT5("https://b2c-mione-gpt4o.openai.azure.com/openai/v1/chat/completions", "AZURE_OPENAI_API_KEY", "gpt-5", null),
    KNOWLEDGE_BASE("", "xxx", "KNOWLEDGE_BASE", "");

    private final String url;

    private final String envName;

    private final String defaultModel;

    // 允许在环境变量中设置模型名称
    private final String customModelEnv;

    LLMProvider(String url, String envName, String defaultModel, String customModelEnv) {
        this.url = url;
        this.envName = envName;
        if (customModelEnv != null) {
            this.defaultModel = System.getenv(customModelEnv);
        } else {
            this.defaultModel = defaultModel;
        }
        this.customModelEnv = customModelEnv;
    }
}
