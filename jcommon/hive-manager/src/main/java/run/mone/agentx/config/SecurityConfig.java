package run.mone.agentx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/users/register", "/api/manager/v1/users/register", "/api/v1/users/login", "/api/manager/v1/users/login").permitAll()
                .antMatchers("/a2a/v1/healthz").permitAll()
                .antMatchers("/ping").permitAll()
                .antMatchers("/error").permitAll()
                .antMatchers("/agent-manager/**").permitAll()
                .antMatchers("/assets/**").permitAll()
                // .antMatchers("/api/manager/ws/agent/chat/**").permitAll()
                // .antMatchers("/api/manager/ws/realtime/**").permitAll()
                // .antMatchers("/ws/agent/chat/**").permitAll()
                // .antMatchers("/ws/realtime/**").permitAll()
                .antMatchers("/scripts/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
   
}