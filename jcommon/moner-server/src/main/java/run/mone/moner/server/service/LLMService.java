package run.mone.moner.server.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.AiMessage;
import run.mone.moner.server.bo.McpModel;
import run.mone.moner.server.bo.McpModelSettingDTO;
import run.mone.moner.server.mcp.McpModelSettingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        JsonObject req = getReq(llm, text, imgText);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        String result = llm.chatCompletion(messages, sysPrompt);
        log.info("{}", result);
        return result;

    }

    public String call(LLM llm, String text, List<String> imgTexts, String sysPrompt) {
        JsonObject req = getReq(llm, text, imgTexts);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        String result = llm.chatCompletion(messages, sysPrompt);
        log.info("{}", result);
        return result;

    }

    public String callStream(Role role, LLM llm, String text, List<String> imgTexts, String systemPrompt) {
        JsonObject req = getReq(llm, text, imgTexts);

        List<AiMessage> messages = new ArrayList<>();

        messages.add(AiMessage.builder().jsonContent(req).build());
        String result = llm.syncChat(role, messages, systemPrompt);
        log.info("{}", result);
        return result;

    }

    // TODO: 2025/2/17 实现mcp的流式调用
    public String callStreamWithMcp(LLM llm, String text, List<String> imgTexts, String systemPrompt) {

        return null;
    }

    private JsonObject getReq(LLM llm, String text, List<String> imgTexts) {
        JsonObject req = new JsonObject();

        if (llm.getConfig().getLlmProvider() == LLMProvider.GOOGLE_2) {
            JsonArray parts = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("text", text);
            parts.add(obj);

            if (imgTexts != null && !imgTexts.isEmpty()) {
                imgTexts.forEach(img -> {
                    JsonObject obj2 = new JsonObject();
                    JsonObject objImg = new JsonObject();
                    objImg.addProperty("mime_type", "image/jpeg");
                    objImg.addProperty("data", img);
                    obj2.add("inline_data", objImg);
                    parts.add(obj2);
                });
            }

            req.add("parts", parts);
        }

        if (llm.getConfig().getLlmProvider() == LLMProvider.OPENROUTER
                || llm.getConfig().getLlmProvider() == LLMProvider.MOONSHOT) {
            req.addProperty("role", "user");
            JsonArray array = new JsonArray();

            JsonObject obj1 = new JsonObject();
            obj1.addProperty("type", "text");
            obj1.addProperty("text", text);
            array.add(obj1);

            if (imgTexts != null && !imgTexts.isEmpty()) {
                imgTexts.forEach(img -> {
                    JsonObject obj2 = new JsonObject();
                    obj2.addProperty("type", "image_url");
                    JsonObject imgObj = new JsonObject();
                    if (!img.startsWith("data:image")) {
                        imgObj.addProperty("url", "data:image/jpeg;base64," + img);
                    } else {
                        imgObj.addProperty("url", img);
                    }
                    obj2.add("image_url", imgObj);
                    array.add(obj2);
                });
            }

            req.add("content", array);
        }
        return req;
    }

    private JsonObject getReq(LLM llm, String text, String imgText) {
        JsonObject req = new JsonObject();

        if (llm.getConfig().getLlmProvider() == LLMProvider.GOOGLE_2) {
            JsonArray parts = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("text", text);
            parts.add(obj);

            if (StringUtils.isNotEmpty(imgText)) {
                JsonObject obj2 = new JsonObject();
                JsonObject objImg = new JsonObject();
                objImg.addProperty("mime_type", "image/jpeg");
                objImg.addProperty("data", imgText);
                obj2.add("inline_data", objImg);
                parts.add(obj2);
            }

            req.add("parts", parts);
        }

        if (llm.getConfig().getLlmProvider() == LLMProvider.OPENROUTER
                || llm.getConfig().getLlmProvider() == LLMProvider.MOONSHOT) {
            req.addProperty("role", "user");
            JsonArray array = new JsonArray();

            JsonObject obj1 = new JsonObject();
            obj1.addProperty("type", "text");
            obj1.addProperty("text", text);
            array.add(obj1);

            if (StringUtils.isNotEmpty(imgText)) {
                JsonObject obj2 = new JsonObject();
                obj2.addProperty("type", "image_url");
                JsonObject img = new JsonObject();
                img.addProperty("url", "data:image/jpeg;base64," + imgText);
                obj2.add("image_url", img);
                array.add(obj2);
            }

            req.add("content", array);
        }
        return req;
    }

    public Pair<LLM, McpModel> getLLM(String from) {
        LLMProvider provider = null;
        String url = "";
        McpModelSettingDTO setting = mcpModelSettingService.getMcpModelSetting(from);
        String selectedProvider = setting.getSelectedProvider();
        String apiKey = "";
        String model = "";

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
                || provider.equals(LLMProvider.OPENAICOMPATIBLE)) {
            if (StringUtils.isNotEmpty(url)) {
                configBuilder.url(url);
            }
        }
        LLMConfig config = configBuilder.llmProvider(provider).build();
        if (config.getLlmProvider() == LLMProvider.GOOGLE_2) {
            config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "streamGenerateContent?alt=sse");
        }

        if (config.getLlmProvider() == LLMProvider.OPENROUTER
                && StringUtils.isNotEmpty(System.getenv("OPENROUTER_AI_GATEWAY"))) {
            config.setUrl(System.getenv("OPENROUTER_AI_GATEWAY"));
        }

        return Pair.of(new LLM(config), new McpModel(apiKey, model));
    }
}
