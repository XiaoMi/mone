package run.mone.mcp.playwright.role;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import run.mone.hive.Environment;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.Message;
import run.mone.mcp.playwright.bo.ChatWebSocketResp;
import run.mone.mcp.playwright.common.GsonUtils;
import run.mone.mcp.playwright.constant.ResultType;

import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 14:59
 * 聊天者
 */
@Slf4j
public class Chatter extends Role {

    private WebSocketSession session;

    public Chatter(WebSocketSession session) {
        this.name = "Chatter";
        this.session = session;
        setEnvironment(new Environment());
    }

    @Override
    public CompletableFuture<Message> run() {
        String data = this.rc.news.poll().getContent();
        String chatRes = llm.syncChat(this, data);
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
