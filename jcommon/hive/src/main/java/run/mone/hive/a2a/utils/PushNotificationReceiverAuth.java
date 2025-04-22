package com.google.a2a.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

/**
 * 推送通知接收方认证
 */
@Slf4j
public class PushNotificationReceiverAuth extends PushNotificationAuth {
    
    private JwtParser jwtParser;
    
    public PushNotificationReceiverAuth() {
        super();
    }
    
    /**
     * 加载JWKS
     * @param jwksUrl JWKS URL
     */
    public void loadJwks(String jwksUrl) {
        // 在实际实现中，这里需要从JWKS URL获取公钥并配置JWT解析器
        // 为简化示例，这里省略了详细实现
        jwtParser = Jwts.parser()
                // .setSigningKeyResolver(...)
                .build();
    }
    
    /**
     * 验证推送通知
     * @param authHeader 认证头
     * @param requestBody 请求体
     * @return 验证结果
     */
    public boolean verifyPushNotification(String authHeader, String requestBody) {
        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_PREFIX)) {
            log.warn("Invalid authorization header");
            return false;
        }
        
        String token = authHeader.substring(AUTH_HEADER_PREFIX.length());
        
        try {
            // 解析JWT令牌
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            
            // 验证请求体签名
            String expectedSha256 = claims.get("request_body_sha256", String.class);
            String actualSha256 = calculateRequestBodySha256(requestBody);
            
            if (!expectedSha256.equals(actualSha256)) {
                log.warn("Invalid request body");
                return false;
            }
            
            // 验证令牌时间
            Long iat = claims.get("iat", Long.class);
            long currentTime = System.currentTimeMillis() / 1000;
            
            if (currentTime - iat > 60 * 5) {
                log.warn("Token is expired");
                return false;
            }
            
            return true;
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
} 