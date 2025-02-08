package run.mone.mcp.playwright.role;

import com.google.gson.JsonObject;
import run.mone.hive.Environment;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 14:59
 * 聊天者
 */
public class Chatter extends Role {

    public Chatter() {
        this.name = "Chatter";
        setEnvironment(new Environment());
    }

    @Override
    public CompletableFuture<Message> run() {
        String data = this.rc.news.poll().getContent();
        String chatRes = llm.chat(data);
        chatRes = "<action type=\"chat\">" + chatRes + "</action>";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("data",chatRes);
        return CompletableFuture.completedFuture(Message.builder().content(jsonObject.toString()).build());
    }
}
