package run.mone.mcp.cursor.miapi.tool;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.cursor.miapi.http.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ApiTestTool implements ITool {

    private static HttpClient httpClient = new HttpClient();

    private static final Gson gson = new Gson();

    @Value("${gateway.host}")
    private String host;

    @Override
    public String getName() {
        return "stress_test_api";
    }

    @Override
    public boolean needExecute() {
        return true;
    }
    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                1.只进行接口压力测试，不做其他回答。
                2.根据用户的描述提取所需信息，如果缺少信息，则继续询问用户并让用户补全信息。
                3.不要自己臆想所需的必要参数。
                5.如果所需参数缺少，则询问用户提供，并等待用户输入，一直到参数齐全为止。
                """;
    }

    @Override
    public String parameters() {
        return """
                - url:（必填）要进行压力测试的接口地址.
                - body:（必填）要测试接口的请求参数.
                - method:（非必填）要测试接口的请求方式，get或post，默认值为get.
                - times:（非必填）每秒进行请求的次数，默认值为50次
                - durationSeconds:（非必填）持续发压时间，默认值为10秒
                """;
    }

    @Override
    public String usage() {
        return """
               (Attention: If you are using this tool, you must return the test result within the json):
                   
               Example 1: Get system overview
               ```json
                test result
               ```
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            String url = inputJson.has("url") ? inputJson.get("url").getAsString() : null;
            String body = inputJson.has("body") ? inputJson.get("body").getAsString() : "{}";
            Integer times = inputJson.has("times") ? inputJson.get("times").getAsInt() : 50;
            Integer durationSeconds = inputJson.has("durationSeconds") ? inputJson.get("durationSeconds").getAsInt() : 10;
            String method = inputJson.has("method") ? inputJson.get("method").getAsString() : "get";
            if (url == null || url.isEmpty()) {
                result.addProperty("message", "缺少必要参数url");
                return result;
            }
            try {
                CompletableFuture.runAsync(() -> {
                    stressTest(url, body, times, result, method, durationSeconds);
                });
                result.addProperty("code", 200);
                result.addProperty("message", "压测发压完成，注意持续关注服务情况");
            }catch (Exception e) {
                result.addProperty("code", 400);
                result.addProperty("message", e.getMessage());
            }
            return result;
        } catch (Exception e) {
            result.addProperty("error", "获取接口信息失败: " + e.getMessage());
            return result;
        }
    }

    private void stressTest(String url, String body, Integer times, JsonObject result, String method, Integer durationSeconds) {
        List<CompletableFuture<JsonObject>> futures = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        params.put("method", method);
        params.put("url", url);
        params.put("timeout", 20000);
        params.put("headers", gson.toJson(headers));
        params.put("body", body);
        params.put("useX5Filter", false);
        params.put("preScript", "");

        // 创建调度器
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(times);

        try {
            // 每秒发送times次请求，持续10秒
            for (int second = 0; second < durationSeconds; second++) {
                final int currentSecond = second;

                // 安排每秒的任务
                ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
                    log.info("开始第 {} 秒的压力测试，发送 {} 次请求", currentSecond + 1, times);

                    // 在当前秒内发送times次请求
                    for (int i = 0; i < times; i++) {
                        CompletableFuture<JsonObject> future = CompletableFuture.supplyAsync(() ->
                                testTargetApiDirectly(url, gson.toJson(params))
                        );
                        futures.add(future);
                    }
                }, second, TimeUnit.SECONDS);
            }

            // 等待所有调度任务完成
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(durationSeconds + 5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // 收集所有结果
            List<JsonObject> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            log.info("压力测试完成：总共发送 {} 次请求，持续 {} 秒，每秒 {} 次请求",
                    results.size(), durationSeconds, times);

        } catch (Exception e) {
            log.error("压力测试执行异常", e);
        } finally {
            scheduler.shutdown();
        }
    }

    private JsonObject testTargetApiDirectly(String url, String body) {
        try {
            JsonObject response = httpClient.post(host+"/mtop/miapitest/httpTest", body);
            log.info("testTargetApiDirectly result: {}", response.get("data").getAsJsonObject());
            return response.get("data").getAsJsonObject();
        } catch (Exception e) {
            JsonObject errorResult = new JsonObject();
            errorResult.addProperty("statusCode", 500);
            errorResult.addProperty("error", e.getMessage());
            return errorResult;
        }
    }
}
