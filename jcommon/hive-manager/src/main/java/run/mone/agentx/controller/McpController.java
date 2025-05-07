package run.mone.agentx.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import run.mone.agentx.dto.McpRequest;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.McpService;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.common.Result;
import run.mone.hive.schema.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * MCP控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/mcp")
@RequiredArgsConstructor
public class McpController {

    private final McpService mcpService;

    /**
     * 调用MCP服务(后边其实是一个Agent,只是通过mcp这个协议来调用)
     *
     * @return 消息流
     */
    @PostMapping(value = "/call", consumes = "text/event-stream", produces = "text/event-stream")
    public Flux<Message> call(@AuthenticationPrincipal User user, @RequestBody(required = false) String requestBody) {
        log.info("user:{} 调用MCP服务，请求参数: {}", user.getUsername(), requestBody);
        McpRequest request = GsonUtils.gson.fromJson(requestBody, McpRequest.class);
        Map<String, String> keyValuePairs = new HashMap<>();
        keyValuePairs.put("outerTag", request.getOuterTag());
        if (request.getContent() != null) {
            keyValuePairs.put("server_name", request.getContent().getServer_name());
            keyValuePairs.put("tool_name", request.getContent().getTool_name());
            keyValuePairs.put("arguments", request.getContent().getArguments());
        }

        // 创建Result对象
        Result result = new Result("mcp_request", keyValuePairs);
        result.setFrom("hive_manager");

        // 使用Flux.create创建消息流
        return Flux.create(sink -> {
            CompletableFuture.runAsync(() -> {
                //这里本质是当Agent调用的
                mcpService.callMcp(user.getUsername(), request.getAgentId(), request.getAgentInstance(), result, sink);
                sink.onDispose(() -> {
                    log.info("call mcp finish");
                });
                sink.complete();
            });
        });
    }
} 