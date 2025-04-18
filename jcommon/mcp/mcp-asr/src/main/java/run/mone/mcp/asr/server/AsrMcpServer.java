package run.mone.mcp.asr.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.asr.function.AsrFunction;
import run.mone.mcp.asr.service.AliAsrService;
import run.mone.mcp.asr.service.TencentAsrService;


/**
 * @author 龚文
 */
@Slf4j
@Component
public class AsrMcpServer {
    private final ServerMcpTransport transport;

    private McpSyncServer syncServer;
    @Value("${ali.asr.appKey}")
    private String appKey;
    @Value("${ali.asr.id}")
    private String id;
    @Value("${ali.asr.secret}")
    private String secret;
    @Value("${ali.asr.url}")
    private String url;
    @Value("${ali.asr.speechLength}")
    private String aliSpeechLength;
    @Value("${ali.asr.sleepTime}")
    private String aliSleepTime;
    @Value("${ali.asr.sampleRate}")
    private String sampleRate;

    @Value("${tencent.asr.appId}")
    private String appId;
    @Value("${tencent.asr.secretId}")
    private String secretId;
    @Value("${tencent.asr.secretKey}")
    private String secretKey;
    @Value("${tencent.asr.speechLength}")
    private String speechLength;
    @Value("${tencent.asr.sleepTime}")
    private String sleepTime;
    @Value("${tencent.asr.engineModelType}")
    private String engineModelType;

    public AsrMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("AsrMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting AsrMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("asr_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            AsrFunction function = new AsrFunction(
                    new TencentAsrService(appId, secretId, secretKey, speechLength, sleepTime, engineModelType),
                    new AliAsrService(appKey, id, secret, url, aliSpeechLength, aliSleepTime, sampleRate)
            );
            var toolRegistration = new McpServer.ToolStreamRegistration(
                    new McpSchema.Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
            );
            syncServer.addStreamTool(toolRegistration);

        } catch (Exception e) {
            log.error("Failed to register asr tool", e);
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
            log.info("Stopping AsrMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
}
