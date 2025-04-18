package run.mone.agentx.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import run.mone.agentx.dto.McpRequest;
import run.mone.agentx.service.McpService;
import run.mone.hive.common.Result;
import run.mone.hive.schema.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * MCP控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/mcp")
public class McpController {

    private final McpService mcpService;

    public McpController(McpService mcpService) {
        this.mcpService = mcpService;
    }

    /**
     * 调用MCP服务
     *
     * @param requestBody 请求体
     * @return 消息流
     */
    @PostMapping(value = "/call", consumes = "text/event-stream", produces = "text/event-stream")
    public Flux<Message> call(@RequestBody(required = false) String requestBody) {
        log.info("调用MCP服务，请求参数: {}", requestBody);
        
        McpRequest request;
        try {
            // 手动解析JSON
            ObjectMapper objectMapper = new ObjectMapper();
            request = objectMapper.readValue(requestBody, McpRequest.class);
        } catch (Exception e) {
            log.error("解析请求体失败", e);
            return Flux.error(new RuntimeException("解析请求体失败: " + e.getMessage()));
        }
        
        // 创建Map存储请求数据
        Map<String, String> keyValuePairs = new HashMap<>();
        keyValuePairs.put("outerTag", request.getOuterTag());
        if (request.getContent() != null) {
            keyValuePairs.put("server_name", request.getContent().getServer_name());
            keyValuePairs.put("tool_name", request.getContent().getTool_name());
            keyValuePairs.put("arguments", request.getContent().getArguments());
        }
        
        // 创建Result对象
        Result result = new Result("mcp_request", keyValuePairs);
        
        // 使用Flux.create创建消息流
        return Flux.create(sink -> {
            CompletableFuture.runAsync(() -> {
                // 调用McpService的callMcp方法
                mcpService.callMcp(result, sink);

                // 注意：不要在这里调用sink.complete()，应该在McpService中适当的时候调用
                // 或者使用sink.onDispose()来处理流的完成
                sink.onDispose(() -> {
                    log.info("MCP流已结束");
                });
            });
        });
    }
} 