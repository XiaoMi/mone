package run.mone.supergateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import run.mone.supergateway.service.SseToStdioGateway;

@Slf4j
@Configuration
public class SuperGatewayConfig {

    @Value("${supergateway.sse.url:}")
    private String sseUrl;

    @Value("${supergateway.stdio.command:}")
    private String stdioCommand;

    @Value("${supergateway.cors.enabled:false}")
    private boolean corsEnabled;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if (corsEnabled) {
                    registry.addMapping("/**")
                            .allowedOrigins("*")
                            .allowedMethods("*")
                            .allowedHeaders("*");
                }
            }
        };
    }

    @Bean
    public SseToStdioGateway sseToStdioGateway() {
        log.info("初始化SSE到Stdio网关，SSE URL: {}, 命令: {}", sseUrl, stdioCommand);
        SseToStdioGateway gateway = new SseToStdioGateway(sseUrl, stdioCommand);
        try {
            gateway.start();
        } catch (Exception e) {
            log.error("启动SSE到Stdio网关失败", e);
        }
        return gateway;
    }
} 