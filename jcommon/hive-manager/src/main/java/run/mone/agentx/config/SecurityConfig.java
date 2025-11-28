package run.mone.agentx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/v1/users/register", "/api/manager/v1/users/register", "/api/v1/users/login", "/api/manager/v1/users/login").permitAll()
                        .requestMatchers("/a2a/v1/healthz").permitAll()
                        .requestMatchers("/ping").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/agent-manager/**").permitAll()
                        .requestMatchers("/assets/**").permitAll()
                        // .requestMatchers("/api/manager/ws/agent/chat/**").permitAll()
                        // .requestMatchers("/api/manager/ws/realtime/**").permitAll()
                        // .requestMatchers("/ws/agent/chat/**").permitAll()
                        // .requestMatchers("/ws/realtime/**").permitAll()
                        .requestMatchers("/api/v1/agents/instances/by-names","/api/v1/agents/health", "/api/v1/agents/unregister", "/api/v1/agents/register", "/api/v1/tasks/execute"
                                , "/api/v1/agents/config"
                                , "/api/v1/agents/config/save"
                                , "/api/v1/users/internal-account"
                        ).permitAll()
                        .requestMatchers("/scripts/**").permitAll()
                        // 对于其他请求，允许JWT过滤器处理，如果JWT过滤器没有设置认证，则由Spring Security处理
                        // 这样可以保持与旧API的兼容性
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JWT过滤器会在UsernamePasswordAuthenticationFilter之前执行，负责设置认证信息
                // 如果JWT过滤器没有设置认证，请求仍然可以通过（因为anyRequest().permitAll()）
                // 但JWT过滤器内部会检查token并返回403（如果需要的话）
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}