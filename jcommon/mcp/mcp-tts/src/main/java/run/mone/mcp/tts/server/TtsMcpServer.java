package run.mone.mcp.tts.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.tts.function.TtsFunction;
import run.mone.mcp.tts.service.AliTtsService;
import run.mone.mcp.tts.service.TencentTtsService;


/**
 * @author 龚文
 */
@Slf4j
@Component
public class TtsMcpServer {
    private ServerMcpTransport transport;

    private McpSyncServer syncServer;
    @Value("${ali.tts.appKey}")
    private String appKey;
    @Value("${ali.tts.id}")
    private String id;
    @Value("${ali.tts.secret}")
    private String secret;
    @Value("${ali.tts.url}")
    private String url;
    @Value("${ali.tts.voice}")
    private String voice;
    @Value("${ali.tts.outputFormat}")
    private String outputFormat;
    @Value("${ali.tts.sampleRate}")
    private String aliSampleRate;

    @Value("${tencent.tts.appId}")
    private String appId;
    @Value("${tencent.tts.secretId}")
    private String secretId;
    @Value("${tencent.tts.secretKey}")
    private String secretKey;
    @Value("${tencent.tts.codec}")
    private String codec;
    @Value("${tencent.tts.sampleRate}")
    private String sampleRate;
    @Value("${tencent.tts.voiceType}")
    private String voiceType;


    public TtsMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("TtsMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting TtsMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("tts_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {

            TtsFunction function = new TtsFunction(
                    new TencentTtsService(appId, secretId, secretKey, voiceType, sampleRate, codec),
                    new AliTtsService(appKey, id, secret, url, voice, aliSampleRate, outputFormat)
            );
            var toolRegistration = new McpServer.ToolStreamRegistration(
                    new McpSchema.Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
            );
            syncServer.addStreamTool(toolRegistration);
            log.info("Successfully registered TtsMcpServer tool");

        } catch (Exception e) {
            log.error("Failed to register TtsMcpServer tool", e);
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
            log.info("Stopping TtsMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
}
