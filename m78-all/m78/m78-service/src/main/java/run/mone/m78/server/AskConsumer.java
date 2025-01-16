package run.mone.m78.server;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.ws.WsSessionHolder;
import run.mone.m78.service.agent.state.AnswerType;
import run.mone.m78.service.agent.state.BotFsmManager;
import run.mone.m78.service.bo.AiProxyMessage;
import run.mone.m78.service.bo.chatgpt.Role;
import run.mone.m78.service.common.MarkDownUtils;
import run.mone.m78.service.context.ApplicationContextProvider;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.plugins.BotPluginService;

import java.util.Base64;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2024/5/13 22:30
 */
@Slf4j
public class AskConsumer implements Consumer<AiProxyMessage> {

    @Getter
    private String userName;

    private String msgId;

    private String topicId;


    private String sessionId;

    private String fsmKey;

    public AskConsumer(String userName, String topicId, String msgId, String sessionId, String fsmKey) {
        this.userName = userName;
        this.topicId = topicId;
        this.msgId = msgId;
        this.fsmKey = fsmKey;
        this.sessionId = sessionId;
    }

    private StringBuilder sb = new StringBuilder();

    @Override
    public void accept(AiProxyMessage msg) {
        if (msg.getType().equals("begin")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("msgId", msgId);
            WsSessionHolder.INSTANCE.sendMsgBySessionId(this.sessionId, obj.toString(), WebsocketMessageType.BOT_STREAM_BEGIN);
        }
        if (msg.getType().equals("end") || msg.getType().equals("failure")) {
            log.info("stream end");
            JsonObject obj = new JsonObject();
            obj.addProperty("msgId", msgId);
            saveChatResponseIfNotEmpty(WebsocketMessageType.BOT_STREAM_RESULT);
            parseAndHandleJsonObject().ifPresent(jsonObject -> obj.add("message", jsonObject));

            BotFsmManager.tell(fsmKey, ImmutableMap.of(), AnswerType.assistant, sb.toString(), Role.assistant.name(), new JsonObject());
            WsSessionHolder.INSTANCE.sendMsgBySessionId(this.sessionId, obj.toString(), WebsocketMessageType.BOT_STREAM_RESULT);
        }
        if (msg.getType().equals("event")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("msgId", msgId);
            obj.addProperty("type", "event");
            String strMessage = msg.getMessage();
            String eventMsg = new String(Base64.getDecoder().decode(strMessage));
            log.info("stream event msg:{}", eventMsg);
            sb.append(eventMsg);
            obj.addProperty("message", strMessage);
            WsSessionHolder.INSTANCE.sendMsgBySessionId(this.sessionId, obj.toString(), WebsocketMessageType.BOT_STREAM_EVENT);
        }

    }

    private void saveChatResponseIfNotEmpty(String messageType) {
        if (!sb.isEmpty()) {
            BotService botService = ApplicationContextProvider.getBean(BotService.class);
            botService.saveChatMessage(topicId, sb.toString(), userName, "ASSISTANT", ImmutableMap.of("messageType", messageType));
        }
    }

    //处理插件调用(流试处理,看看最后的是不是调用插件)
    private Optional<JsonObject> parseAndHandleJsonObject() {
        if (!sb.isEmpty()) {
            String str = sb.toString();
            str = MarkDownUtils.extractCodeBlock(str);
            try {
                JsonElement element = JsonParser.parseString(str);
                if (element.isJsonObject()) {
                    JsonObject jsonObj = element.getAsJsonObject();
                    // plugin call
                    if (jsonObj.has("pluginId")) {
                        String pluginId = jsonObj.get("pluginId").getAsString();
                        JsonObject params = jsonObj.get("params").getAsJsonObject();
                        log.info("call plugin:{}, params:{}", pluginId, params);
                        BotPluginService botPluginService = ApplicationContextProvider.getBean(BotPluginService.class);
                        return Optional.of(botPluginService.callPlugin(pluginId, params, userName));
                    }
                }
            } catch (Throwable ignore) {

            }
        }
        return Optional.empty();
    }

}
