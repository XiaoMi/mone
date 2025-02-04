package run.mone.mcp.playwright.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.AiMessage;
import run.mone.mcp.playwright.websocket.WebSocketHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/2/3 23:47
 */
@Service
@Slf4j
public class LLMService {


    public String call(LLM llm, String text, String imgText) {
        JsonObject req = new JsonObject();

        if (llm.getConfig().getLlmProvider() == LLMProvider.GOOGLE_2) {
            JsonArray parts = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("text", text);
            JsonObject obj2 = new JsonObject();
            JsonObject objImg = new JsonObject();
            objImg.addProperty("mime_type", "image/jpeg");
            objImg.addProperty("data", imgText);
            obj2.add("inline_data", objImg);
            parts.add(obj);
            parts.add(obj2);
            req.add("parts", parts);
        }

        if (llm.getConfig().getLlmProvider() == LLMProvider.OPENROUTER || llm.getConfig().getLlmProvider() == LLMProvider.MOONSHOT) {
            req.addProperty("role", "user");
            JsonArray array = new JsonArray();

            JsonObject obj1 = new JsonObject();
            obj1.addProperty("type", "text");
            obj1.addProperty("text", text);
            array.add(obj1);

            JsonObject obj2 = new JsonObject();
            obj2.addProperty("type", "image_url");
            JsonObject img = new JsonObject();
            img.addProperty("url", "data:image/jpeg;base64," + imgText);
            obj2.add("image_url", img);
            array.add(obj2);

            req.add("content", array);
        }

        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        String sysPrompt = "你是一个聪明的人类,我给你一张浏览器的页面 和我提供的需求,你帮我分析下这个页面干什么的? thx";
        sysPrompt = WebSocketHandler.prompt.formatted("返回合理的action列表");
        String result = llm.chatCompletion(messages, sysPrompt);
        log.info("{}", result);
        return result;

    }

}
