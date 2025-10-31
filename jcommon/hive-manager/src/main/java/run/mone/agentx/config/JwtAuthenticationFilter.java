package run.mone.agentx.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.JwtService;
import run.mone.agentx.service.UserService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private static final String TOKEN_COOKIE_NAME = "auth_token";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.endsWith("/users/login") || path.endsWith("/users/register");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = null;
        String username = null;

        // 如果请求路径是静态资源，则不进行 token 认证
        // 登录和注册也不需要校验
        String path = request.getRequestURI();
        if(path.isEmpty() ||
                path.startsWith("/agent-manager") ||
                path.startsWith("/assets/") ||
                path.startsWith("/scripts/") ||
                path.equals("/api/v1/users/register") ||
                path.equals("/api/manager/v1/users/register") ||
                path.equals("/api/v1/users/login") ||
                path.equals("/api/manager/v1/users/login") ||
                path.equals("/a2a/v1/healthz") ||
                path.equals("/ping") ||
                path.equals("/") ||
                path.equals("/api/v1/agents/health") ||
                path.equals("/api/v1/tasks/execute") ||
                path.equals("/api/v1/agents/unregister") ||
                path.equals("/api/v1/agents/register") ||
                path.equals("/api/v1/agents/instances/by-names") ||
                path.equals("/api/v1/agents/config") ||
                path.equals("/api/v1/agents/config/save") ||
                path.equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 首先尝试从 Authorization header 获取 token
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else {
            // 如果 header 中没有，则尝试从 cookie 中获取
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (jwt == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"Token is required\"}");
            return;
        }
        
        try {
            username = jwtService.extractUsername(jwt);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.findByUsername(username).block();
                if (user != null && jwtService.isTokenValid(jwt, user)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, null);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
            filterChain.doFilter(request, response);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"Token has expired\"}");
            return;
        } catch (Exception e) {
            throw e;
        }
    }
} 