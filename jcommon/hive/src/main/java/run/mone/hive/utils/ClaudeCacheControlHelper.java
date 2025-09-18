package run.mone.hive.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.llm.CustomConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ClaudeCacheControlHelper {

    private static final List<String> CLAUDE_CACHE_MODELS = java.util.Arrays.asList(
            "anthropic/claude-sonnet-4",
            "anthropic/claude-opus-4.1",
            "anthropic/claude-opus-4",
            "anthropic/claude-3.7-sonnet",
            "anthropic/claude-3.7-sonnet:beta",
            "anthropic/claude-3.7-sonnet:thinking",
            "anthropic/claude-3-7-sonnet",
            "anthropic/claude-3-7-sonnet:beta",
            "anthropic/claude-3.5-sonnet",
            "anthropic/claude-3.5-sonnet:beta",
            "anthropic/claude-3.5-sonnet-20240620",
            "anthropic/claude-3.5-sonnet-20240620:beta",
            "anthropic/claude-3-5-haiku",
            "anthropic/claude-3-5-haiku:beta",
            "anthropic/claude-3-5-haiku-20241022",
            "anthropic/claude-3-5-haiku-20241022:beta",
            "anthropic/claude-3-haiku",
            "anthropic/claude-3-haiku:beta",
            "anthropic/claude-3-opus",
            "anthropic/claude-3-opus:beta"
    );

    public static boolean isClaudeCacheModel(run.mone.hive.llm.LLMProvider provider, String model) {
        if (provider != run.mone.hive.llm.LLMProvider.OPENROUTER) {
            return false;
        }
        return CLAUDE_CACHE_MODELS.contains(model);
    }

    public static void applyCacheControlToSystemPrompt(JsonArray msgArray) {
        for (JsonElement msgElement : msgArray) {
            if (!msgElement.isJsonObject()) {
                continue;
            }
            JsonObject msgObject = msgElement.getAsJsonObject();
            if (msgObject.has("role") && msgObject.get("role").getAsString().equals("system")) {
                if (msgObject.has("content") && msgObject.get("content").isJsonPrimitive()) {
                    String sysPrompt = msgObject.get("content").getAsString();
                    JsonArray contents = new JsonArray(1);
                    JsonObject sysPromptObj = new JsonObject();
                    sysPromptObj.addProperty("type", "text");
                    sysPromptObj.addProperty("text", sysPrompt);
                    contents.add(sysPromptObj);
                    msgObject.add("content", contents);
                }
                if (msgObject.has("content") && msgObject.get("content").isJsonArray()) {
                    if (msgObject.get("content").getAsJsonArray().size() <= 0) {
                        continue;
                    }
                    JsonElement firstOne = msgObject.get("content").getAsJsonArray().get(0);
                    if (!firstOne.getAsJsonObject().get("type").getAsString().equals("text")) {
                        continue;
                    }
                    JsonObject cacheCtrl = new JsonObject();
                    cacheCtrl.addProperty("type", "ephemeral"); // ephemeral is the only type for now
                    firstOne.getAsJsonObject().add("cache_control", cacheCtrl);
                }
            }
        }
    }


    public static void applyCacheControlToUserMessages(JsonArray msgArray, String model, LLMProvider provider, CustomConfig customConfig) {
        if (!isClaudeCacheModel(provider, model)) {
            return;
        }
        applyCacheControlToSystemPrompt(msgArray);
        List<JsonObject> userMessages = new ArrayList<>();
        for (JsonElement msgElement : msgArray) {
            JsonObject msgObject = msgElement.getAsJsonObject();
            if (msgObject.has("role") && msgObject.get("role").getAsString().equals(LLM.ROLE_USER)) {
                userMessages.add(msgObject);
            }
        }

        int cacheTurn = customConfig.getCacheTurn();
        if (cacheTurn <= 0) {
            return;
        }

        int startIndex = Math.max(0, userMessages.size() - cacheTurn);
        List<JsonObject> lastUserMessages = userMessages.subList(startIndex, userMessages.size());

        log.debug("Applying cache_control for model: {} to last {} user messages", model, lastUserMessages.size());

        for (JsonObject userMsg : lastUserMessages) {
            if (userMsg.has("content") && userMsg.get("content").isJsonPrimitive()) {
                String textContent = userMsg.get("content").getAsString();
                userMsg.remove("content");
                JsonArray contentArray = new JsonArray();
                JsonObject textPart = new JsonObject();
                textPart.addProperty("type", "text");
                textPart.addProperty("text", textContent);
                JsonObject cacheControl = new JsonObject();
                cacheControl.addProperty("type", "ephemeral");
                textPart.add("cache_control", cacheControl);
                contentArray.add(textPart);
                userMsg.add("content", contentArray);
                log.debug("Applied cache_control to user message (original was string)");
            } else if (userMsg.has("content") && userMsg.get("content").isJsonArray()) {
                JsonArray contentArray = userMsg.getAsJsonArray("content");
                JsonObject lastTextPart = null;

                for (int i = contentArray.size() - 1; i >= 0; i--) {
                    JsonElement partElement = contentArray.get(i);
                    if (partElement.isJsonObject()) {
                        JsonObject part = partElement.getAsJsonObject();
                        if (part.has("type") && "text".equals(part.get("type").getAsString())) {
                            lastTextPart = part;
                            break;
                        }
                    }
                }

                if (lastTextPart == null) {
                    lastTextPart = new JsonObject();
                    lastTextPart.addProperty("type", "text");
                    lastTextPart.addProperty("text", "...");
                    contentArray.add(lastTextPart);
                }

                JsonObject cacheControl = new JsonObject();
                cacheControl.addProperty("type", "ephemeral");
                lastTextPart.add("cache_control", cacheControl);
                log.debug("Applied cache_control to user message (original was array)");
            }
        }
    }
}
