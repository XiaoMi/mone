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
            initGCPClude35(modelName);
            return claudeToken.get(modelName);
        }
        return token;
    }

    public static void initGCPClude35(String modelName) {
        String token = getClaude35Toekn();

        SafeRun.run(() -> Files.write(Paths.get("/tmp/key-" + getClaude35Name() + ".json"), token.getBytes()));

        JsonObject obj = JsonParser.parseString(token).getAsJsonObject();
        String projectId = obj.get("project_id").getAsString();

        if (client.get(modelName) == null) {
            CloudeClient cloudeClient = new CloudeClient();
            cloudeClient.setUrl(getClaude35Url());
            cloudeClient.setModel(getClaude35Version());
            cloudeClient.setProjectId(projectId);
            client.put(getClaude35Name(), cloudeClient);

            AtomicBoolean atomicBoolean = new AtomicBoolean();
            atomicBoolean.compareAndSet(false, true);

            init.put(getClaude35Name(), atomicBoolean);
        }

        SafeRun.run(() -> {
            //存储token
            String t = client.get(modelName).token(modelName);
            claudeToken.put(getClaude35Name(), t);
            log.info("google token:{}", t);
        });

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("get google token");
            SafeRun.run(() -> {
                String t = client.get(modelName).token(modelName);
                claudeToken.put(getClaude35Name(), t);
                log.info("google token:{}", t);
            });
        }, 0, 1, TimeUnit.MINUTES);

    }

    public String callGCP35(String model, List<AiMessage> msgs) {
        return callGCP35(model, msgs, "");
    }


    public String callGCP35(String model, List<AiMessage> msgs, String prefix) {
        if (prefix != null) {
            prefix = prefix.trim();
        }
        if (init.get(model) == null || !init.get(model).get()) {
            initGCPClude35(model);
        }

        List<Message> messages = msgs.stream().map(it -> {
            return Message.builder().role(it.getRole()).content(it.getContent()).build();
        }).collect(Collectors.toList());
        if (StringUtils.isNotEmpty(prefix)) {
            messages.add(Message.builder().role("assistant").content(prefix).build());
        }
        RequestPayload.RequestPayloadBuilder builder = run.mone.ai.google.bo.RequestPayload.builder().maxTokens(8192).anthropicVersion("vertex-2023-10-16").messages(messages);

        RequestPayload payload = builder.build();

        ResponsePayload res = client.get(model).call(getClaude35Url(), client.get(model).getToken(), payload);
        if ("max_tokens".equals(res.getStopReason())) {
            return callGCP35(model, msgs, prefix + res.getContent().get(0).getText());
        }
        return prefix + res.getContent().get(0).getText();
    }


    public static String getClaude35Toekn() {
        return System.getenv("CLAUDE35_TOKEN");
    }

    public static Integer getClaude35MaxToekns() {
        String tokens = System.getenv("CLAUDE35_MAX_TOKENS");
        if (StringUtils.isNotEmpty(tokens)) {
            return Integer.valueOf(tokens);
        }
        return 8192;
    }

    public static String getClaude35Name() {
        return System.getenv("CLAUDE35_NAME");
    }

    public static String getClaude35Url() {
        return System.getenv("CLAUDE35_URL");
    }

    public static String getClaude35Version() {
        return System.getenv("CLAUDE35_VERSION");
    }

}
