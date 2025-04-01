package run.mone.mcp.store.data.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.store.data.function.SkuFunction;
import run.mone.mcp.store.data.function.StockFunction;

@Slf4j
@Component
public class StoreDataServer {

    @Autowired
    private SkuFunction skuFunction;

    @Autowired
    private StockFunction stockFunction;

    private final ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public StoreDataServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        log.info("Starting StoreDataServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("store_data_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // Register sku tool
        log.info("Registering sku tool...");
        try {
            var skuToolRegistration = new McpServer.ToolRegistration(
                    new Tool(skuFunction.getName(), skuFunction.getDesc(), skuFunction.getToolScheme()),
                    skuFunction
            );
            syncServer.addTool(skuToolRegistration);
            log.info("Successfully registered sku tool");
        } catch (Exception e) {
            log.error("Failed to register sku tool", e);
            throw e;
        }

        // Register stock tool
        log.info("Registering stock tool...");
        try {
            var stockToolRegistration = new McpServer.ToolRegistration(
                    new Tool(stockFunction.getName(), stockFunction.getDesc(), stockFunction.getToolScheme()),
                    stockFunction
            );
            syncServer.addTool(stockToolRegistration);
            log.info("Successfully registered stock tool");
        } catch (Exception e) {
            log.error("Failed to register stock tool", e);
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