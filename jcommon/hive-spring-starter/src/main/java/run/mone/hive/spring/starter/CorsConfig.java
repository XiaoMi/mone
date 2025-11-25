package run.mone.hive.spring.starter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域配置
 * 
 * 通过配置项 hive.starter.enabled 控制是否启用（默认：true）
 * 
 * @author goodjava@qq.com
 */
@Configuration
@ConditionalOnProperty(name = "hive.starter.enabled", havingValue = "true", matchIfMissing = true)
public class CorsConfig {

    @Value("${mcp.agent.mode:}")
    private String agentMode;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有来源（生产环境建议配置具体的域名）
        config.addAllowedOriginPattern("*");
        
        // 允许所有请求头
        config.addAllowedHeader("*");
        
        // 允许所有请求方法
        config.addAllowedMethod("*");

        // 注意：MCP 模式下不需要携带凭证，避免与 allowedOriginPattern("*") 冲突导致 CORS 错误
        // 其他模式（如前端发起的正常 HTTP 请求）仍需要凭证支持
        if (!"MCP".equalsIgnoreCase(agentMode)) {
            config.setAllowCredentials(true);
        }

        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用 CORS 配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

