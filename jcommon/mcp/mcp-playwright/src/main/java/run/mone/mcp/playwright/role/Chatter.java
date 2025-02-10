package run.mone.mcp.playwright.role;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import run.mone.hive.Environment;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.Message;
import run.mone.mcp.playwright.bo.ChatWebSocketResp;
import run.mone.mcp.playwright.common.GsonUtils;
import run.mone.mcp.playwright.constant.ResultType;
import run.mone.mcp.playwright.context.ApplicationContextProvider;
import run.mone.mcp.playwright.service.LLMService;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 14:59
 * 聊天者
 */
@Slf4j
public class Chatter extends Role {

    private WebSocketSession session;

    private LLMService llmService;

    private static final Type LIST_STRING = new TypeToken<List<String>>(){}.getType();

    public Chatter(WebSocketSession session) {
        this.name = "Chatter";
        this.session = session;
        setEnvironment(new Environment());
        llmService = ApplicationContextProvider.getBean(LLMService.class);
    }

    @Override
    public CompletableFuture<Message> run() {
        Message message = this.rc.news.poll();
        String data = message.getContent();
        String chatRes;
        if("json".equals(message.getType())){
            JsonObject obj = JsonParser.parseString(data).getAsJsonObject();
            JsonArray img = obj.getAsJsonArray("img");
            List<String> images = null;
            if(img != null){
                images = GsonUtils.gson.fromJson(img, LIST_STRING);
            }
            String text = obj.get("text").getAsString();
            chatRes = llmService.callStream(this, llm, text, images);
        }else{
            chatRes = llm.syncChat(this, data);
        }
        chatRes = "<action type=\"chat\">" + chatRes + "</action>";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("data",chatRes);
        return CompletableFuture.completedFuture(Message.builder().content(jsonObject.toString()).build());
    }

    @Override
    public void sendMessage(Message message) {
        sendMessage(message.getContent(), message.getType());
    }

    private void sendMessage(String content, String messageType) {
        try {
            ChatWebSocketResp resp = ChatWebSocketResp.builder().roleName(name).roleType("ASSISTANT").content(content).messageType(messageType).type(ResultType.CHAT).build();
            session.sendMessage(new TextMessage(GsonUtils.gson.toJson(resp)));
        } catch (Exception e) {
            log.error("send message error, ", e);
        }
    }
}
