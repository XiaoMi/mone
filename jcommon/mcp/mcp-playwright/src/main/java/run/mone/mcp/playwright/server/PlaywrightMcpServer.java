package run.mone.mcp.playwright.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.playwright.function.PlaywrightFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions;
import run.mone.mcp.playwright.function.PlaywrightFunctions.CleanupFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.ClickFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.DeleteFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.EvaluateFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.FillFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.GetContentFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.GetFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.HoverFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.NavigateFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.PatchFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.PostFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.PutFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.ScreenshotFunction;
import run.mone.mcp.playwright.function.PlaywrightFunctions.SelectFunction;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;

@Slf4j
@Component
public class PlaywrightMcpServer {
    private ServerMcpTransport transport;
    private McpSyncServer syncServer;
    private PlaywrightFunction playwrightFunction;

    public PlaywrightMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("PlaywrightMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting PlaywrightMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("playwright_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering execute_playwright tool...");
        try {
            // playwrightFunction = new PlaywrightFunction();
            // var playwrightToolRegistration = new ToolRegistration(
            //         new Tool(playwrightFunction.getName(), playwrightFunction.getDesc(), playwrightFunction.getPlaywrightToolSchema()), playwrightFunction
            // );

            // syncServer.addTool(playwrightToolRegistration);
            // log.info("Successfully registered execute_playwright tool");

            NavigateFunction navigateFunction = new PlaywrightFunctions.NavigateFunction();
            var navigateToolRegistration = new ToolRegistration(
                    new Tool(navigateFunction.getName(), navigateFunction.getDesc(), navigateFunction.getToolScheme()), navigateFunction
            );
            syncServer.addTool(navigateToolRegistration);
            log.info("Successfully registered navigate tool");

            ClickFunction clickFunction = new PlaywrightFunctions.ClickFunction();
            var clickToolRegistration = new ToolRegistration(
                    new Tool(clickFunction.getName(), clickFunction.getDesc(), clickFunction.getToolScheme()), clickFunction
            );
            syncServer.addTool(clickToolRegistration);
            log.info("Successfully registered click tool");

            ScreenshotFunction screenshotFunction = new PlaywrightFunctions.ScreenshotFunction();
            var screenshotToolRegistration = new ToolRegistration(
                    new Tool(screenshotFunction.getName(), screenshotFunction.getDesc(), screenshotFunction.getToolScheme()), screenshotFunction
            );
            syncServer.addTool(screenshotToolRegistration);
            log.info("Successfully registered screenshot tool");

            FillFunction fillFunction = new PlaywrightFunctions.FillFunction();
            var fillToolRegistration = new ToolRegistration(
                    new Tool(fillFunction.getName(), fillFunction.getDesc(), fillFunction.getToolScheme()), fillFunction
            );
            syncServer.addTool(fillToolRegistration);
            log.info("Successfully registered fill tool");

            SelectFunction selectFunction = new PlaywrightFunctions.SelectFunction();
            var selectToolRegistration = new ToolRegistration(
                    new Tool(selectFunction.getName(), selectFunction.getDesc(), selectFunction.getToolScheme()), selectFunction
            );
            syncServer.addTool(selectToolRegistration);
            log.info("Successfully registered select tool");

            HoverFunction hoverFunction = new PlaywrightFunctions.HoverFunction();
            var hoverToolRegistration = new ToolRegistration(
                    new Tool(hoverFunction.getName(), hoverFunction.getDesc(), hoverFunction.getToolScheme()), hoverFunction
            );
            syncServer.addTool(hoverToolRegistration);
            log.info("Successfully registered hover tool"); 

            EvaluateFunction evalFunction = new PlaywrightFunctions.EvaluateFunction();
            var evalToolRegistration = new ToolRegistration(
                    new Tool(evalFunction.getName(), evalFunction.getDesc(), evalFunction.getToolScheme()), evalFunction
            );
            syncServer.addTool(evalToolRegistration);
            log.info("Successfully registered eval tool");

            GetContentFunction getContentFunction = new PlaywrightFunctions.GetContentFunction();
            var getContentToolRegistration = new ToolRegistration(
                    new Tool(getContentFunction.getName(), getContentFunction.getDesc(), getContentFunction.getToolScheme()), getContentFunction
            );
            syncServer.addTool(getContentToolRegistration);
            log.info("Successfully registered get_content tool");

            GetFunction getFunction = new PlaywrightFunctions.GetFunction();
            var getToolRegistration = new ToolRegistration(
                    new Tool(getFunction.getName(), getFunction.getDesc(), getFunction.getToolScheme()), getFunction
            );
            syncServer.addTool(getToolRegistration);
            log.info("Successfully registered get tool");

            PostFunction postFunction = new PlaywrightFunctions.PostFunction();
            var postToolRegistration = new ToolRegistration(
                    new Tool(postFunction.getName(), postFunction.getDesc(), postFunction.getToolScheme()), postFunction
            );
            syncServer.addTool(postToolRegistration);
            log.info("Successfully registered post tool");

            PutFunction putFunction = new PlaywrightFunctions.PutFunction();
            var putToolRegistration = new ToolRegistration(
                    new Tool(putFunction.getName(), putFunction.getDesc(), putFunction.getToolScheme()), putFunction
            );
            syncServer.addTool(putToolRegistration);
            log.info("Successfully registered put tool");

            DeleteFunction deleteFunction = new PlaywrightFunctions.DeleteFunction();
            var deleteToolRegistration = new ToolRegistration(
                    new Tool(deleteFunction.getName(), deleteFunction.getDesc(), deleteFunction.getToolScheme()), deleteFunction
            );
            syncServer.addTool(deleteToolRegistration);
            log.info("Successfully registered delete tool");    

            PatchFunction patchFunction = new PlaywrightFunctions.PatchFunction();
            var patchToolRegistration = new ToolRegistration(
                    new Tool(patchFunction.getName(), patchFunction.getDesc(), patchFunction.getToolScheme()), patchFunction
            );
            syncServer.addTool(patchToolRegistration);
            log.info("Successfully registered patch tool");

            CleanupFunction cleanupFunction = new PlaywrightFunctions.CleanupFunction();
            var cleanupToolRegistration = new ToolRegistration(
                    new Tool(cleanupFunction.getName(), cleanupFunction.getDesc(), cleanupFunction.getToolScheme()), 
                    cleanupFunction
            );
            syncServer.addTool(cleanupToolRegistration);
            log.info("Successfully registered cleanup tool");
        } catch (Exception e) {
            log.error("Failed to register execute_playwright tool", e);
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
            log.info("Stopping PlaywrightMcpServer...");
            this.syncServer.closeGracefully();
        }
        if (this.playwrightFunction != null) {
            this.playwrightFunction.close();
        }
    }
}
