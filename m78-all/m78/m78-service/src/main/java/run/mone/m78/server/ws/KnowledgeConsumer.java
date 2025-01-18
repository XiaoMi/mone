package run.mone.m78.server.ws;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class KnowledgeConsumer implements Consumer<JsonObject> {
    @Override
    public void accept(JsonObject jsonObject) {
        try {
            KnowledgeSessionHolder.INSTANCE.sendMsgBySessionId(jsonObject.get("sessionId").getAsString(), jsonObject.get("msg").getAsString());
        } catch (Exception e) {
            log.error("KnowledgeConsumer accept error:", e);
        }

    }
}
