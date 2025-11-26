package run.mone.mcp.cursor.miapi.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.cursor.miapi.http.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class MiApiTestFunction implements McpFunction {
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "url": {
                        "type": "string",
                        "description": "要进行压力测试的接口地址"
                    },
                    "body": {
                        "type": "string",
                        "description": "要测试接口的请求参数"
                    },
                    "method": {
                        "type": "string",
                        "description": "要测试接口的请求方式，get或post，默认值为get"
                    },
                    "times": {
                        "type": "string",
                        "description": "每秒进行请求的次数，默认值为50次"
                    },
                    "durationSeconds": {
                        "type": "string",
                        "description": "持续发压时间，默认值为10秒"
                    }
                },
                "required": ["url", "body"]
            }
            """;

    @Value("${gateway.host}")
    private String host;

    private static HttpClient httpClient = new HttpClient();

    private static final Gson gson = new Gson();

    public MiApiTestFunction(){}

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("miapi mcp arguments: {}", arguments);
        try {
            Object url = arguments.get("url");
            Object body = arguments.get("body");
            Object method = arguments.get("method");
            Object times = arguments.get("times");
            Object durationSeconds = arguments.get("durationSeconds");

            if (url == null || StringUtils.isBlank(url.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: 施压地址不能为空")),
                        true
                ));
            }
            try {
                Integer t = convertToInteger(times, 50);
                Integer d = convertToInteger(durationSeconds, 10);
                String b;
                String m;
                if (body != null && !StringUtils.isBlank(body.toString())) {
                    b = body.toString();
                } else {
                    b = "{}";
                }
                if (method != null && !StringUtils.isBlank(method.toString())) {
                    m = method.toString();
                } else {
                    m = "get";
                }
                CompletableFuture.runAsync(() -> {
                    stressTest(url.toString(), b, t, m, d);
                });
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("压测发压完成，注意持续关注服务情况")),
                        false
                ));
            }catch (Exception e) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(e.getMessage())),
                        true
                ));
            }
        } catch (Exception e) {
            log.error("执行miapi操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    @Override
    public String getName() {
        return "stress_test_api";
    }

    @Override
    public String getDesc() {
        return """
                1.只进行接口压力测试，不做其他回答。
                2.根据用户的描述提取所需信息，如果缺少信息，则继续询问用户并让用户补全信息。
                3.不要自己臆想所需的必要参数。
                5.如果所需参数缺少，则询问用户提供，并等待用户输入，一直到参数齐全为止。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }


    private void stressTest(String url, String body, Integer times, String method, Integer durationSeconds) {
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

    private Integer convertToInteger(Object obj, Integer init) {
        if (obj instanceof Number n) {
            return n.intValue();
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else {
            return init;
        }
    }
}