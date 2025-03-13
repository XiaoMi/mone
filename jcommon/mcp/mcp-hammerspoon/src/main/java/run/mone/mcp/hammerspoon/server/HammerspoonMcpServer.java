package run.mone.mcp.hammerspoon.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.hammerspoon.function.DingTalkFunction;
import run.mone.mcp.hammerspoon.function.LocateCoordinatesFunction;
import run.mone.mcp.hammerspoon.function.tigertrade.SellPutOptionDecisionFunction;
import run.mone.mcp.hammerspoon.function.tigertrade.dto.Version;
import run.mone.mcp.hammerspoon.function.tigertrade.function.SellPutOptionFunction;


@Slf4j
@Component
public class HammerspoonMcpServer {

    @Value("${hammerspoon.function:trigertrade}")
    private String functionType;

    private final ServerMcpTransport transport;
    private final LocateCoordinatesFunction locateCoordinatesFunction;
    private final SellPutOptionDecisionFunction sellPutOptionDecisionFunction;
    private McpSyncServer syncServer;

    @Resource
    private LLM llm;

    @Resource
    private SellPutOptionFunction sellPutOptionFunction;

    public HammerspoonMcpServer(ServerMcpTransport transport, LocateCoordinatesFunction locateCoordinatesFunction, SellPutOptionDecisionFunction sellPutOptionDecisionFunction) {
        this.transport = transport;
        this.locateCoordinatesFunction = locateCoordinatesFunction;
        this.sellPutOptionDecisionFunction = sellPutOptionDecisionFunction;
    }

    public McpSyncServer start() {
        log.info("Starting SongMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("tigertrade_mcp", new Version().toString())
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // Register song tool
        log.info("Registering song tool...");
        try {
            if ("trigertrade".equalsIgnoreCase(functionType)) {
//                TrigerTradeProFunction function = new TrigerTradeProFunction();

//                function.setLlm(llm);
//                var toolRegistration = new McpServer.ToolRegistration(
//                        new Tool(function.getName(), function.getDesc(), function.getToolScheme()),function
//                        );
//                syncServer.addTool(toolRegistration);

                var sellPutOptionStreamRegistration = new McpServer.ToolStreamRegistration(
                        new Tool(sellPutOptionFunction.getName(), sellPutOptionFunction.getDesc(), sellPutOptionFunction.getToolScheme()), sellPutOptionFunction
                );
                syncServer.addStreamTool(sellPutOptionStreamRegistration);


                log.info("Successfully registered trigertrade tool");
            } else {
                DingTalkFunction function = new DingTalkFunction();

                var toolRegistration = new McpServer.ToolRegistration(
                        new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
                );
                syncServer.addTool(toolRegistration);
                log.info("Successfully registered DingTalk tool");
            }
        } catch (Exception e) {
            log.error("Failed to register song tool", e);
            throw e;
        }

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
} 