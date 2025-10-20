package run.mone.moner.server.service;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.CustomConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.llm.LLM.LLMCompoundMsg;
import run.mone.hive.llm.LLM.LLMPart;
import run.mone.hive.roles.Role;
import run.mone.moner.server.bo.McpModel;
import run.mone.moner.server.bo.McpModelSettingDTO;
import run.mone.moner.server.mcp.McpModelSettingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2025/2/3 23:47
 */
@Service
@Slf4j
public class LLMService {

    @Resource
    private McpModelSettingService mcpModelSettingService;

    public String call(LLM llm, String text, String imgText, String sysPrompt) {
        String result =  llm.call(LLMPart.builder().type(LLM.TYPE_IMAGE).text(text).data(imgText).build(), sysPrompt);
        log.info("{}", result);
        return result;

    }

    public String call(LLM llm, String text, List<String> imgTexts, String sysPrompt) {
        String result = llm.call(LLMCompoundMsg.builder()
            .content(text)
            .parts(imgTexts == null
                        ? new ArrayList<>()
                        : imgTexts.stream().map(it -> LLMPart.builder().type(LLM.TYPE_IMAGE).data(it).mimeType("image/jpeg").build()).collect(Collectors.toList())).build(), sysPrompt);
        log.info("{}", result);
        return result;

    }

    public String callStream(Role role, LLM llm, String text, List<String> imgTexts, String systemPrompt) {
        String result = llm.callStream(role, LLMCompoundMsg.builder()
            .content(text)
            .parts(imgTexts == null
                    ? new ArrayList<>()
                    : imgTexts.stream().map(it -> LLMPart.builder().type(LLM.TYPE_IMAGE).data(it).mimeType("image/jpeg").build()).collect(Collectors.toList())).build(), systemPrompt, llm.getConfig().getCustomConfig());
        log.info("{}", result);
        return result;

    }

    public Pair<LLM, McpModel> getLLM(String from) {
        LLMProvider provider = null;
        String url = "";
        McpModelSettingDTO setting = mcpModelSettingService.getMcpModelSetting(from);
        String selectedProvider = setting.getSelectedProvider();
        String apiKey = "";
        String model = "";
        String mifyProvider = "";

        if (StringUtils.isNotBlank(selectedProvider)) {
            selectedProvider = selectedProvider.toLowerCase(Locale.ROOT);
            switch (selectedProvider) {
                case "doubao": {
                    McpModelSettingDTO.DouBao doubao = setting.getDoubao();
                    provider = LLMProvider.valueOf("DOUBAO");
                    apiKey = doubao.getApiKey();
                    model = doubao.getModelKey();
                    break;
                }
                case "openaicompatible": {
                    McpModelSettingDTO.OpenAICompatible openAiCompatible = setting.getOpenAICompatible();
                    model = openAiCompatible.getModelID();
                    url = openAiCompatible.getBaseUrl();
                    apiKey = openAiCompatible.getApiKey();
                    provider = LLMProvider.OPENAICOMPATIBLE;
                    break;
                }
                case "deepseek": {
                    McpModelSettingDTO.DeepSeek deepseek = setting.getDeepSeek();
                    provider = LLMProvider.valueOf("DEEPSEEK");
                    apiKey = deepseek.getApiKey();
                    model = deepseek.getModel();
                    break;
                }
                case "openrouter": {
                    McpModelSettingDTO.OpenRouter openRouter = setting.getOpenRouter();
                    if (StringUtils.isNotEmpty(openRouter.getCloudflareProxy())) {
                        url = openRouter.getCloudflareProxy();
                    }
                    provider = LLMProvider.valueOf("OPENROUTER");
                    apiKey = openRouter.getApiKey();
                    model = openRouter.getModel();
                    break;
                }
                case "gemini": {
                    McpModelSettingDTO.Gemini gemini = setting.getGemini();
                    if (StringUtils.isNotEmpty(gemini.getCloudflareProxy())) {
                        url = gemini.getCloudflareProxy();
                    }
                    provider = LLMProvider.valueOf("GOOGLE_2");
                    apiKey = gemini.getApiKey();
                    model = gemini.getModel();
                    break;
                }
                case "mify": {
                    McpModelSettingDTO.Mify mify = setting.getMify();
                    provider = LLMProvider.valueOf("MIFY_GATEWAY");
                    apiKey = mify.getApiKey();
                    url = mify.getBaseUrl();
                    model = mify.getModel();
                    mifyProvider = mify.getProvider();
                    break;
                }
                default: {
                    provider = LLMProvider.valueOf("GOOGLE_2");
                    apiKey = System.getenv(provider.getEnvName());
                    model = provider.getDefaultModel();
                }
            }
        } else {
            provider = LLMProvider.GOOGLE_2;
        }

        LLMConfig.LLMConfigBuilder configBuilder = LLMConfig.builder();
        if (provider.equals(LLMProvider.OPENROUTER)
                || provider.equals(LLMProvider.GOOGLE_2)
                || provider.equals(LLMProvider.OPENAICOMPATIBLE)
                || provider.equals(LLMProvider.MIFY_GATEWAY)) {
            if (StringUtils.isNotEmpty(url)) {
                configBuilder.url(url);
            }
        }
        LLMConfig config = configBuilder.llmProvider(provider).token(apiKey).customConfig(CustomConfig.DUMMY).build();
        if (config.getLlmProvider() == LLMProvider.GOOGLE_2
                && StringUtils.isNotEmpty(System.getenv("GOOGLE_AI_GATEWAY"))) {
            config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "streamGenerateContent?alt=sse");
        }

        if (config.getLlmProvider() == LLMProvider.OPENROUTER
                && StringUtils.isNotEmpty(System.getenv("OPENROUTER_AI_GATEWAY"))) {
            config.setUrl(System.getenv("OPENROUTER_AI_GATEWAY"));
        }

        if (config.getLlmProvider() == LLMProvider.MIFY_GATEWAY) {
            CustomConfig customConfig = new CustomConfig();
            customConfig.setModel(model);
            customConfig.addCustomHeader(CustomConfig.X_MODEL_PROVIDER_ID, mifyProvider);
            config.setCustomConfig(customConfig);
        }

        return Pair.of(new LLM(config), new McpModel(apiKey, model));
    }
}
