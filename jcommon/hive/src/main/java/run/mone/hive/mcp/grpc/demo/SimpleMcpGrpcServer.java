package run.mone.hive.mcp.grpc.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.mone.hive.mcp.demo.function.CalculatorFunction;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.DefaultMcpSession;
import run.mone.hive.mcp.spec.McpError;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.mcp.util.Utils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 简单的 gRPC MCP 服务器示例
 */
@Slf4j
public class SimpleMcpGrpcServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    private static final int GRPC_PORT = 50051;


    private final CopyOnWriteArrayList<McpServer.ToolRegistration> tools;

    private final CopyOnWriteArrayList<McpServer.ToolStreamRegistration> streamTools;

    private final McpSchema.ServerCapabilities serverCapabilities;

    @Getter
    private final DefaultMcpSession mcpSession;


    public SimpleMcpGrpcServer(McpSchema.ServerCapabilities serverCapabilities, CopyOnWriteArrayList<McpServer.ToolRegistration> tools, CopyOnWriteArrayList<McpServer.ToolStreamRegistration> streamTools) {
        this.tools = tools;
        this.streamTools = streamTools;
        // 创建 gRPC 传输层
        this.transport = new GrpcServerTransport(GRPC_PORT);


        this.serverCapabilities = (serverCapabilities != null) ? serverCapabilities : new McpSchema.ServerCapabilities(
                null, // experimental
                new McpSchema.ServerCapabilities.LoggingCapabilities(), // Enable logging
                // by default
                null,
                null,
                !Utils.isEmpty(this.tools) ? new McpSchema.ServerCapabilities.ToolCapabilities(false) : null);


        Map<String, DefaultMcpSession.RequestHandler> requestHandlers = new HashMap<>();
        if (this.serverCapabilities.tools() != null) {
            requestHandlers.put(McpSchema.METHOD_TOOLS_LIST, toolsListRequestHandler());
            requestHandlers.put(McpSchema.METHOD_TOOLS_CALL, toolsCallRequestHandler());
        }

        Map<String, DefaultMcpSession.StreamRequestHandler> streamRequestHandlers = new HashMap<>();

        this.mcpSession = new DefaultMcpSession(Duration.ofSeconds(10), this.transport, requestHandlers,
                streamRequestHandlers, Maps.newHashMap());

    }

    private DefaultMcpSession.RequestHandler toolsCallRequestHandler() {
        // TODO: handle tool call request
        return params -> {
            McpSchema.CallToolRequest callToolRequest = transport.unmarshalFrom(params,
                    new TypeReference<McpSchema.CallToolRequest>() {
                    });

            Optional<McpServer.ToolRegistration> toolRegistration = this.tools.stream()
                    .filter(tr -> callToolRequest.name().equals(tr.tool().name()))
                    .findAny();

            if (toolRegistration.isEmpty()) {
                return Mono.<Object>error(new McpError("Tool not found: " + callToolRequest.name()));
            }

            return Mono.fromCallable(() -> toolRegistration.get().call().apply(callToolRequest.arguments()))
                    .map(result -> (Object) result)
                    .subscribeOn(Schedulers.boundedElastic());
        };
    }

    private DefaultMcpSession.RequestHandler toolsListRequestHandler() {
        return params -> {

            List<Tool> toolsRes = new ArrayList<>();

            List<Tool> tools = this.tools.stream().map(toolRegistration -> {
                return toolRegistration.tool();
            }).toList();

            List<Tool> streamTools = this.streamTools.stream().map(toolRegistration -> {
                return toolRegistration.tool();
            }).toList();

            toolsRes.addAll(tools);
            toolsRes.addAll(streamTools);

            return Mono.just(new McpSchema.ListToolsResult(toolsRes, null));
        };
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("grpc-mcp-server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // 注册计算器工具
        CalculatorFunction function = new CalculatorFunction();
        var toolStreamRegistration = new McpServer.ToolStreamRegistration(
                new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
        );

        syncServer.addStreamTool(toolStreamRegistration);

        log.info("gRPC MCP Server started on port: " + GRPC_PORT);
        return syncServer;
    }

    @PostConstruct
    public void init() {
        this.syncServer = start();
    }

    @PreDestroy
    public void stop() {
        if (this.syncServer != null) {
            this.syncServer.closeGracefully();
        }
    }

    public Mono<Void> addTool(McpServer.ToolRegistration toolRegistration) {
        if (toolRegistration == null) {
            return Mono.error(new McpError("Tool registration must not be null"));
        }
        if (toolRegistration.tool() == null) {
            return Mono.error(new McpError("Tool must not be null"));
        }
        if (toolRegistration.call() == null) {
            return Mono.error(new McpError("Tool call handler must not be null"));
        }
        if (this.serverCapabilities.tools() == null) {
            return Mono.error(new McpError("Server must be configured with tool capabilities"));
        }

        // Check for duplicate tool names
        if (this.tools.stream().anyMatch(th -> th.tool().name().equals(toolRegistration.tool().name()))) {
            return Mono.error(new McpError("Tool with name '" + toolRegistration.tool().name() + "' already exists"));
        }

        this.tools.add(toolRegistration);
        log.info("Added tool handler: {}", toolRegistration.tool().name());
        if (this.serverCapabilities.tools().listChanged()) {
            return notifyToolsListChanged();
        }
        return Mono.empty();
    }




    public Mono<Void> notifyToolsListChanged() {
       return Mono.empty();
    }
} 