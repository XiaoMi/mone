package run.mone.moner.server.bo;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.schema.AiMessage;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2025/1/23 13:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SseReq {

    private String apiKey;

    private String model;

    private List<AiMessage> messageList;

    private String systemPrompt;

    private LLMConfig llmConfig;

    private BiConsumer<String, JsonObject> messageHandler;

    private Consumer<String> lineConsumer;
}
