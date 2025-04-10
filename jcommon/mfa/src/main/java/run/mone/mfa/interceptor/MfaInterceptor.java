package run.mone.mfa.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MFA拦截器
 * 用于拦截需要MFA验证的请求
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MfaInterceptor implements HandlerInterceptor {

    private final MfaService mfaService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie中的sessionId
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        // 如果没有sessionId，说明用户未登录，放行(后续会由其他拦截器处理)
        if (sessionId == null || sessionId.isEmpty()) {
            return true;
        }

        // 如果是登录请求或静态资源则放行
        if (RequestUtils.isPublicResource(request.getRequestURI())) {
            return true;
        }

        // 验证token并获取用户信息
        Optional<User> userOpt = userService.validateToken(sessionId);
        if (userOpt.isEmpty()) {
            return true; // token无效，放行(后续会由其他拦截器处理)
        }

        User user = userOpt.get();
        // 如果用户已启用MFA，检查MFA验证状态
        if (user.getMfaEnabled() != null && user.getMfaEnabled()) {
            // 从Cookie中获取MFA验证状态
            boolean mfaVerified = false;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("mfaVerified".equals(cookie.getName()) && "true".equals(cookie.getValue())) {
                        mfaVerified = true;
                        break;
                    }
                }
            }

            // 如果未通过MFA验证，返回401状态码
            if (!mfaVerified) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":401,\"message\":\"需要MFA验证\",\"requireMfa\":true,\"data\":null}");
                return false;
            }
        }

        return true;
    }
} 
