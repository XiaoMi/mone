package run.mone.hive.llm;

import com.google.gson.JsonObject;

import java.util.function.Function;

public interface BotBridge {

    String call(String content, JsonObject params);

    String call(String content, JsonObject params, Function<String, String> responseHandler);
}
