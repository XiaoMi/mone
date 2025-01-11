
package run.mone.hive.llm;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.schema.AiMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class LLMTest {

    private LLM llm;

    private LLMConfig config;

    @BeforeEach
    void setUp() {
        config = new LLMConfig();
        config.setDebug(false);
        config.setJson(false);
        config.setLlmProvider(LLMProvider.DOUBAO);
        llm = new LLM(config);
    }

    @Test
    void testAskInDebugMode() throws ExecutionException, InterruptedException {
        String prompt = "Hello, world!";
        CompletableFuture<String> future = llm.ask(prompt);
        String result = future.get();
        System.out.println(result);
    }

    @Test
    void testChat() {
        String prompt = "hi";
        String result = llm.chat(prompt);
        log.info("{}", result);
        assertNotNull(result);
    }

    @Test
    public void testJson() {
        String res = llm.chat(Lists.newArrayList(AiMessage.builder().role("user").content("1+1=?").build()), LLMConfig.builder().json(true).build());
        System.out.println(res);
    }

    @Test
    public void testChat2() {
        String res = llm.chat(Lists.newArrayList(AiMessage.builder().role("user").content("a=12").build(), AiMessage.builder().role("user").content("2*a+a=?").build()));
        System.out.println(res);
    }

    @Test
    void testGetApiUrl() {
        String apiUrl = llm.getApiUrl();
        assertEquals("https://api.stepfun.com/v1/chat/completions", apiUrl);
    }

    @Test
    void testGetApiUrlGoogle() {
        llm.setGoogle(true);
        String apiUrl = llm.getApiUrl();
        assertEquals("https://generativelanguage.googleapis.com/v1beta/openai/chat/completions", apiUrl);
    }

    @Test
    public void testWebSearch() {
        String apiKey = System.getenv(config.getLlmProvider().getEnvName());
        String res = llm.chatCompletion(apiKey, Lists.newArrayList(AiMessage.builder().role("user").content("苏轼最好的10首词").build()), config.getLlmProvider().getDefaultModel(), "", LLMConfig.builder().webSearch(true).build());
        System.out.println(res);
    }

    @Test
    void testChatCompletionStream() throws InterruptedException {
        String apiKey = System.getenv(config.getLlmProvider().getEnvName());
        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().role("user").content("Hello, can you tell me a short joke?").build());
        String model = config.getLlmProvider().getDefaultModel();

        StringBuilder responseBuilder = new StringBuilder();
        List<JsonObject> jsonResponses = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        llm.chatCompletionStream(
                apiKey,
                messages,
                model,
                (content, jsonResponse) -> {
                    if ("[DONE]".equals(content)) {
                        latch.countDown();
                    } else {
                        System.out.println(content);
                        responseBuilder.append(content);
                        jsonResponses.add(jsonResponse);
                    }
                },
                line -> log.info("Received line: {}", line)
        );

        latch.await();

        String fullResponse = responseBuilder.toString();
        log.info("Full response: {}", fullResponse);

        assertFalse(fullResponse.isEmpty(), "Response should not be empty");
        assertFalse(jsonResponses.isEmpty(), "Should have received JSON responses");
    }
}

