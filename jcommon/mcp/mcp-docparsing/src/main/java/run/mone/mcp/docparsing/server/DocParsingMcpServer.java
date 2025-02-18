package run.mone.mcp.docparsing.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.docparsing.function.DocParsingFunction;

@Slf4j
@Component
public class DocParsingMcpServer {
    
    private final ServerMcpTransport transport;

    private McpSyncServer syncServer;
    
    public DocParsingMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("DocParsingMcpServer initialized with transport: {}", transport);
    }
    
    public McpSyncServer start() {
        log.info("Starting DocParsingMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("docparsing_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering docParsing tool...");

        try {
            DocParsingFunction docParsingFunction = new DocParsingFunction(Lists.newArrayList("/"),new ObjectMapper());
            var docParsingToolRegistration = new ToolRegistration(
                    new Tool(docParsingFunction.getName(), docParsingFunction.getDesc(), docParsingFunction.getDocParsingToolSchema()),
                    docParsingFunction
            );
            syncServer.addTool(docParsingToolRegistration);
            log.info("Successfully registered docParsing tool");
        } catch (Exception e) {
            log.error("Failed to register docParsing tool", e);
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
            log.info("Stopping DocParsingMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
} 