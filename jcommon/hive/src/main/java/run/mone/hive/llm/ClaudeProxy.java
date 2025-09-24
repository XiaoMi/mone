package run.mone.hive.llm;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.ai.google.CloudeClient;
import run.mone.ai.google.bo.Message;
import run.mone.ai.google.bo.RequestPayload;
import run.mone.ai.google.bo.ResponsePayload;
import run.mone.hive.schema.AiMessage;
import run.mone.hive.utils.SafeRun;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


@Data
@Slf4j
public class ClaudeProxy {

    public static ConcurrentMap<String, CloudeClient> client = new ConcurrentHashMap<>();

    public static ConcurrentMap<String, AtomicBoolean> init = new ConcurrentHashMap<>();

    public static ConcurrentMap<String, String> claudeToken = new ConcurrentHashMap<>();

    public static String getClaudeKey(String modelName) {

        String token = claudeToken.get(modelName);
        if (StringUtils.isEmpty(token)) {
            initGCPClaude(modelName);
            return claudeToken.get(modelName);
        }
        return token;
    }

    public static void initGCPClaude(String modelName) {
        String token = getClaudeToekn();

        SafeRun.run(() -> Files.write(Paths.get("/tmp/key-" + getClaudeName() + ".json"), token.getBytes()));

        JsonObject obj = JsonParser.parseString(token).getAsJsonObject();
        String projectId = obj.get("project_id").getAsString();

        if (client.get(modelName) == null) {
            CloudeClient cloudeClient = new CloudeClient();
            cloudeClient.setUrl(getClaudeUrl());
            cloudeClient.setModel(getClaudeVersion());
            cloudeClient.setProjectId(projectId);
            client.put(getClaudeName(), cloudeClient);

            AtomicBoolean atomicBoolean = new AtomicBoolean();
            atomicBoolean.compareAndSet(false, true);

            init.put(getClaudeName(), atomicBoolean);
        }

        SafeRun.run(() -> {
            //存储token
            String t = client.get(modelName).token(modelName);
            claudeToken.put(getClaudeName(), t);
            log.info("google token:{}", t);
        });

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("get google token");
            SafeRun.run(() -> {
                String t = client.get(modelName).token(modelName);
                claudeToken.put(getClaudeName(), t);
                log.info("google token:{}", t);
            });
        }, 0, 1, TimeUnit.MINUTES);

    }

    public String callGCP(String model, List<AiMessage> msgs) {
        return callGCP(model, msgs, "");
    }


    public String callGCP(String model, List<AiMessage> msgs, String prefix) {
        if (prefix != null) {
            prefix = prefix.trim();
        }
        if (init.get(model) == null || !init.get(model).get()) {
            initGCPClaude(model);
        }

        List<Message> messages = msgs.stream().map(it -> {
            return Message.builder().role(it.getRole()).content(it.getContent()).build();
        }).collect(Collectors.toList());
        if (StringUtils.isNotEmpty(prefix)) {
            messages.add(Message.builder().role("assistant").content(prefix).build());
        }
        RequestPayload.RequestPayloadBuilder builder = run.mone.ai.google.bo.RequestPayload.builder().maxTokens(8192).anthropicVersion("vertex-2023-10-16").messages(messages);

        RequestPayload payload = builder.build();

        ResponsePayload res = client.get(model).call(getClaudeUrl(), client.get(model).getToken(), payload);
        if ("max_tokens".equals(res.getStopReason())) {
            return callGCP(model, msgs, prefix + res.getContent().get(0).getText());
        }
        return prefix + res.getContent().get(0).getText();
    }


    public static String getClaudeToekn() {
        switch (selectedClaude()) {
            case "CLAUDE35": return System.getenv("CLAUDE35_TOKEN");
            case "CLAUDE37": return System.getenv("CLAUDE37_TOKEN");
            case "CLAUDE4": return System.getenv("CLAUDE4_TOKEN");
        }
        return System.getenv("CLAUDE35_TOKEN");
    }

    public static Integer getClaudeMaxToekns() {
        String tokens = "";
        switch (selectedClaude()) {
            case "CLAUDE35": {
                tokens = System.getenv("CLAUDE35_MAX_TOKENS");
                break;
            }
            case "CLAUDE37": {
                tokens = System.getenv("CLAUDE37_MAX_TOKENS");
                break;
            }
            case "CLAUDE4": {
                tokens = System.getenv("CLAUDE4_MAX_TOKENS");
                break;
            }
        }

        if (StringUtils.isNotEmpty(tokens)) {
            return Integer.valueOf(tokens);
        }
        return 8192;

    }

    public static String getClaudeName() {
        switch (selectedClaude()) {
            case "CLAUDE35": return System.getenv("CLAUDE35_NAME");
            case "CLAUDE37": return System.getenv("CLAUDE37_NAME");
            case "CLAUDE4": return System.getenv("CLAUDE4_NAME");
        }
        return System.getenv("CLAUDE35_NAME");
    }

    public static String getClaudeUrl() {
        switch (selectedClaude()) {
            case "CLAUDE35": return System.getenv("CLAUDE35_URL");
            case "CLAUDE37": return System.getenv("CLAUDE37_URL");
            case "CLAUDE4": return System.getenv("CLAUDE4_URL");
        }
        return System.getenv("CLAUDE35_URL");
    }

    public static String getClaudeVersion() {
        switch (selectedClaude()) {
            case "CLAUDE35": return System.getenv("CLAUDE35_VERSION");
            case "CLAUDE37": return System.getenv("CLAUDE37_VERSION");
            case "CLAUDE4": return System.getenv("CLAUDE4_VERSION");
        }
        return System.getenv("CLAUDE35_VERSION");
    }

    //目前支持CLAUDE35，CLAUDE37，CLAUDE4
    private static String selectedClaude() {
        return Optional.ofNullable(System.getenv("CLAUDE_SELECTED"))
                .filter(StringUtils::isNotEmpty)
                .orElse("CLAUDE35");
    }

}
