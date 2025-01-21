package run.mone.mcp.memory.server;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.memory.function.MemoryFunctions;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.spec.McpSchema.Tool;

@Component
public class MemoryMcpServer {

    private ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public MemoryMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("memory_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        MemoryFunctions.CreateEntitiesFunction function1 = new MemoryFunctions.CreateEntitiesFunction();
        var toolRegistration1 = new ToolRegistration(
                new Tool(function1.getName(), function1.getDesc(), function1.getToolScheme()), function1
        );

        MemoryFunctions.CreateRelationsFunction function2 = new MemoryFunctions.CreateRelationsFunction();
        var toolRegistration2 = new ToolRegistration(
                new Tool(function2.getName(), function2.getDesc(), function2.getToolScheme()), function2
        );

        MemoryFunctions.AddObservationsFunction function3 = new MemoryFunctions.AddObservationsFunction();
        var toolRegistration3 = new ToolRegistration(
                new Tool(function3.getName(), function3.getDesc(), function3.getToolScheme()), function3
        );

        MemoryFunctions.DeleteEntitiesFunction function4 = new MemoryFunctions.DeleteEntitiesFunction(); 
        var toolRegistration4 = new ToolRegistration(
                new Tool(function4.getName(), function4.getDesc(), function4.getToolScheme()), function4
        );

        MemoryFunctions.DeleteRelationsFunction function5 = new MemoryFunctions.DeleteRelationsFunction();
        var toolRegistration5 = new ToolRegistration(
                new Tool(function5.getName(), function5.getDesc(), function5.getToolScheme()), function5
        );

        MemoryFunctions.DeleteObservationsFunction function6 = new MemoryFunctions.DeleteObservationsFunction();
        var toolRegistration6 = new ToolRegistration(
                new Tool(function6.getName(), function6.getDesc(), function6.getToolScheme()), function6
        );

        MemoryFunctions.ReadGraphFunction function7 = new MemoryFunctions.ReadGraphFunction();
        var toolRegistration7 = new ToolRegistration(
                new Tool(function7.getName(), function7.getDesc(), function7.getToolScheme()), function7
        );

        MemoryFunctions.SearchNodesFunction function8 = new MemoryFunctions.SearchNodesFunction();
        var toolRegistration8 = new ToolRegistration(
                new Tool(function8.getName(), function8.getDesc(), function8.getToolScheme()), function8
        );

        MemoryFunctions.OpenNodesFunction function9 = new MemoryFunctions.OpenNodesFunction();
        var toolRegistration9 = new ToolRegistration(
                new Tool(function9.getName(), function9.getDesc(), function9.getToolScheme()), function9
        );

        syncServer.addTool(toolRegistration1);
        syncServer.addTool(toolRegistration2);
        syncServer.addTool(toolRegistration3);
        syncServer.addTool(toolRegistration4);
        syncServer.addTool(toolRegistration5);
        syncServer.addTool(toolRegistration6);
        syncServer.addTool(toolRegistration7);
        syncServer.addTool(toolRegistration8);
        syncServer.addTool(toolRegistration9);
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
