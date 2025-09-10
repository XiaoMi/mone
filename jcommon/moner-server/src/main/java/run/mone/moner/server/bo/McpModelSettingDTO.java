package run.mone.moner.server.bo;

import lombok.Data;

@Data
public class McpModelSettingDTO {
    //OpenRouter|DeepSeek|OpenAI|OpenAICompatible
    private String selectedProvider;
    private OpenRouter openRouter;
    private DeepSeek deepSeek;
    private OpenAI openAI;
    private Gemini gemini;
    private DouBao doubao;
    private OpenAICompatible openAICompatible;
    private Mify mify;
    private String customInstructions;
    
    @Data
    public static class OpenRouter {
        private String apiKey;
        private String model;
        private String cloudflareProxy;
    }

    @Data
    public static class DouBao {
        private String apiKey;
        private String modelKey;
    }
    
    @Data
    public static class DeepSeek {
        private String apiKey;
        private String model;
    }
    
    @Data
    public static class OpenAI {
        private String apiKey;
        private String model;
        private String cloudflareProxy;
    }

    @Data
    public static class Gemini {
        private String apiKey;
        private String model;
        private String cloudflareProxy;
    }
    
    @Data
    public static class OpenAICompatible {
        private String apiKey;
        private String modelID;
        private String baseUrl;
        private Boolean setAzureAPIVersion;
    }

    @Data
    public static class Mify {
        private String apiKey;
        private String baseUrl;
    }

    public static McpModelSettingDTO buildEmpty() {
        McpModelSettingDTO setting = new McpModelSettingDTO();
        
        OpenRouter openRouter = new OpenRouter();
        openRouter.setApiKey("");
        openRouter.setModel("");
        setting.setOpenRouter(openRouter);
        
        DeepSeek deepSeek = new DeepSeek();
        deepSeek.setApiKey("");
        deepSeek.setModel("");
        setting.setDeepSeek(deepSeek);

        OpenAI openAI = new OpenAI();
        openAI.setApiKey("");
        openAI.setModel("");
        setting.setOpenAI(openAI);

        Gemini gemini = new Gemini();
        gemini.setApiKey("");
        gemini.setModel("gemini-2.0-flash-exp");
        setting.setGemini(gemini);
        
        OpenAICompatible openAICompatible = new OpenAICompatible();
        openAICompatible.setApiKey("");
        openAICompatible.setModelID("");
        openAICompatible.setBaseUrl("");
        openAICompatible.setSetAzureAPIVersion(false);
        setting.setOpenAICompatible(openAICompatible);
        
        setting.setCustomInstructions("");
        
        return setting;
    }
    
}
