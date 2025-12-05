package run.mone.agentx.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.http.HttpHeaders;


@Configuration
public class ProxyConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String requestURI = request.getRequestURI();
                
                // 处理WebSocket请求
                if (requestURI.startsWith("/api/manager/ws/")) {
                    String newPath = requestURI.replace("/api/manager/ws/", "/ws/");
                    // 保持WebSocket相关的请求头
                    String upgrade = request.getHeader(HttpHeaders.UPGRADE);
                    if ("websocket".equalsIgnoreCase(upgrade)) {
                        request.getRequestDispatcher(newPath).forward(request, response);
                        return false;
                    }
                }
                
                // 处理普通HTTP请求
                if (requestURI.startsWith("/api/manager/")) {
                    String newPath = requestURI.replace("/api/manager/", "/api/");
                    request.getRequestDispatcher(newPath).forward(request, response);
                    return false;
                }
                
                return true;
            }
        });
    }
}
