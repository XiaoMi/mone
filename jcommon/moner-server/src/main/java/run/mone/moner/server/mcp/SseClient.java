package run.mone.moner.server.mcp;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.moner.server.bo.McpModelSetting;
import run.mone.moner.server.bo.McpModelSettingDTO;
import run.mone.moner.server.bo.SseReq;

public class SseClient {

    public void stream(SseReq req) {
        LLMProvider provider = null;
        String url = "";

        McpModelSettingDTO setting = McpModelSetting.getMcpModelSetting();
        String selectedProvider = setting.getSelectedProvider().toLowerCase(Locale.ROOT);

        switch (selectedProvider) {
            case "doubao" :{
                McpModelSettingDTO.DouBao doubao = setting.getDoubao();
                provider = LLMProvider.valueOf("DOUBAO");
                req.setApiKey(doubao.getApiKey());
                req.setModel(doubao.getModelKey());
                break;
            }
            case "openaicompatible":{
                McpModelSettingDTO.OpenAICompatible openAiCompatible = setting.getOpenAICompatible();
                req.setModel(openAiCompatible.getModelID());
                url = openAiCompatible.getBaseUrl();
                req.setApiKey(openAiCompatible.getApiKey());
                provider = LLMProvider.OPENAICOMPATIBLE;
                break;
            }
            case "deepseek": {
                McpModelSettingDTO.DeepSeek deepseek = setting.getDeepSeek();
                provider = LLMProvider.valueOf("DEEPSEEK");
                req.setApiKey(deepseek.getApiKey());
                req.setModel(deepseek.getModel());
                break;
            }
            case "openrouter": {
                McpModelSettingDTO.OpenRouter openRouter = setting.getOpenRouter();
                if (StringUtils.isNotEmpty(openRouter.getCloudflareProxy())) {
                    url = openRouter.getCloudflareProxy();
                }
                provider = LLMProvider.valueOf("OPENROUTER");
                req.setApiKey(openRouter.getApiKey());
                req.setModel(openRouter.getModel());
                break;
            }
            case "gemini": {
                McpModelSettingDTO.Gemini gemini = setting.getGemini();
                if (StringUtils.isNotEmpty(gemini.getCloudflareProxy())) {
                    url = gemini.getCloudflareProxy();
                }
                provider = LLMProvider.valueOf("GOOGLE_2");
                req.setApiKey(gemini.getApiKey());
                req.setModel(gemini.getModel());
                break;
            }
            default: {
                provider = LLMProvider.valueOf("DOUBAO");
                req.setApiKey(System.getenv(provider.getEnvName()));
                req.setModel(provider.getDefaultModel());
            }
        }

        req.setLineConsumer((line) -> {
        });
        LLMConfig.LLMConfigBuilder configBuilder = LLMConfig.builder();
        if (provider.equals(LLMProvider.OPENROUTER) 
            || provider.equals(LLMProvider.GOOGLE_2) 
            || provider.equals(LLMProvider.OPENAICOMPATIBLE)) {
            if (StringUtils.isNotEmpty(url)) {
                configBuilder.url(url);
            }
        }

        new LLM(configBuilder.llmProvider(provider).build()).chatCompletionStream(req.getApiKey(), req.getMessageList(), req.getModel(), req.getMessageHandler(), req.getLineConsumer(),"");
    }

}