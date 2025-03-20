package run.mone.mcp.idea.composer.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.FluxSink;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.AiMessage;
import run.mone.mcp.idea.composer.handler.biz.ComposerImagePo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static run.mone.hive.llm.ClaudeProxy.*;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 11:01
 */
@Data
public class BotClient {

    private LLM llm;

    private FluxSink<String> fluxSink;

    private StringBuffer sb = new StringBuffer();

    public BotClient(FluxSink<String> fluxSink) {
        LLMConfig config = LLMConfig.builder()
                .llmProvider(LLMProvider.CLAUDE35_COMPANY)
                .url(getClaude35Url())
                .version(getClaude35Version())
                .maxTokens(getClaude35MaxToekns())
                .build();
//        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.OPENROUTER).build();
        llm = new LLM(config);
        this.fluxSink = fluxSink;
    }

    @SneakyThrows
    public String sendPrompt(String userPrompt, String systemPrompt, ComposerImagePo image, boolean isComplete) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        if (image == null) {
            callLLM(List.of(new run.mone.hive.schema.AiMessage("user", userPrompt)), systemPrompt, countDownLatch, isComplete);
        } else {
            JsonObject req = getReq(userPrompt, image.getImageBase64());
            List<AiMessage> messages = new ArrayList<>();
            messages.add(AiMessage.builder().jsonContent(req).build());
            callLLM(messages, systemPrompt, countDownLatch, isComplete);
        }
        countDownLatch.await(3, TimeUnit.MINUTES);
        //返回整个调用的结果
        String result = sb.toString();
        sb = new StringBuffer();
        return result;
    }

    private void callLLM(List<AiMessage> messages, String systemPrompt, CountDownLatch countDownLatch, boolean isComplete){
        llm.chat(messages, (content, jsonResponse) -> {
            if("[BEGIN]".equals(content)){
                return;
            }
            if(!"[DONE]".equals(content.trim())) {
                fluxSink.next(content);
            }
            if ("[DONE]".equals(content.trim()) && isComplete) {
                fluxSink.next(content);
                fluxSink.complete();
            }
            if ("failure".equals(jsonResponse.get("type").getAsString()) || "finish".equals(jsonResponse.get("type").getAsString())) {
                countDownLatch.countDown();
            }
            if("event".equals(jsonResponse.get("type").getAsString())){
                sb.append(content);
            }
        }, systemPrompt);
    }

    private JsonObject getReq(String text, String imgText) {
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
}
