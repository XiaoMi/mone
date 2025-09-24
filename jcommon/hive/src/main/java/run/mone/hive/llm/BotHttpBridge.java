package run.mone.hive.llm;

import com.google.gson.JsonObject;
import run.mone.m78.client.bot.BotHttpClient;
import run.mone.m78.client.model.M78BotReq;

import java.util.function.Function;

public class BotHttpBridge implements BotBridge {
    private final BotHttpClient client;
    private final String botId;
    private final String userName;

    public BotHttpBridge(String url, String token, String botId, String userName) {
        this.client = BotHttpClient.builder()
                .token(token)
                .url(url)
                .build();
        this.botId = botId;
        this.userName = userName;
    }

    @Override
    public String call(String content, JsonObject params) {
        return call(content, params, null);
    }

    @Override
    public String call(String content, JsonObject params, Function<String, String> responseHandler) {
        return client.callBot(
                M78BotReq.builder()
                        .botId(botId)
                        .userName(userName)
                        .input(content)
                        .build(),
                params,
                responseHandler
        );
    }
}
