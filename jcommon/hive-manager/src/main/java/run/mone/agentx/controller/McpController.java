package run.mone.agentx.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import run.mone.agentx.dto.McpRequest;
import run.mone.agentx.service.McpService;
import run.mone.hive.common.Result;
import run.mone.hive.schema.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mcp")
public class McpController {

    private final McpService mcpService;

    public McpController(McpService mcpService) {
        this.mcpService = mcpService;
    }

    /**
     * 调用MCP服务
     *
     * @param request MCP请求
     * @return 消息流
     */
    @PostMapping("/call")
    public Flux<Message> callMcp(@RequestBody McpRequest request) {
        log.info("调用MCP服务，请求参数: {}", request);
        
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
            // 调用McpService的callMcp方法
            Message message = mcpService.callMcp(result, sink);
            if (message != null) {
                sink.next(message);
            }
            sink.complete();
        });
    }
} 