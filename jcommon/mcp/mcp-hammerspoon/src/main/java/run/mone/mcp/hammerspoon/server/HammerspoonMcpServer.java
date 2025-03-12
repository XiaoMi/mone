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
import run.mone.mcp.hammerspoon.function.TrigerTradeProFunction;
import run.mone.mcp.hammerspoon.function.trigertrade.SellPutOptionDecisionFunction;


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

    public HammerspoonMcpServer(ServerMcpTransport transport, LocateCoordinatesFunction locateCoordinatesFunction, SellPutOptionDecisionFunction sellPutOptionDecisionFunction) {
        this.transport = transport;
        this.locateCoordinatesFunction = locateCoordinatesFunction;
        this.sellPutOptionDecisionFunction = sellPutOptionDecisionFunction;
    }

    public McpSyncServer start() {
        log.info("Starting SongMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("hammerspoon_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // Register song tool
        log.info("Registering song tool...");
        try {
            if ("trigertrade".equalsIgnoreCase(functionType)) {
                TrigerTradeProFunction function = new TrigerTradeProFunction();
                function.setLlm(llm);

                var toolRegistration = new McpServer.ToolRegistration(
                        new Tool(function.getName(), function.getDesc(), function.getToolScheme()),function
                        );
                syncServer.addTool(toolRegistration);

                // 注册locateCoordinatesFunction
//                syncServer.addTool(new McpServer.ToolRegistration(
//                    new Tool(locateCoordinatesFunction.getName(), locateCoordinatesFunction.getDesc(), locateCoordinatesFunction.getToolScheme()),
//                    locateCoordinatesFunction
//                ));
//
//                //注册期权选择
//                syncServer.addTool(new McpServer.ToolRegistration(
//                        new Tool(sellPutOptionDecisionFunction.getName(), sellPutOptionDecisionFunction.getDesc(), sellPutOptionDecisionFunction.getToolScheme()),
//                        sellPutOptionDecisionFunction
//                ));


                log.info("Successfully registered trigertrade tool");
            } else {
                DingTalkFunction function = new DingTalkFunction();

                var toolRegistration = new McpServer.ToolRegistration(
                        new Tool(function.getName(), function.getDesc(), function.getToolScheme()),function
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