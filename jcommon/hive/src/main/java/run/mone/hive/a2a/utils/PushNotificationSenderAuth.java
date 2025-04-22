package com.google.a2a.common.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.util.*;

/**
 * 推送通知发送方认证
 */
@Slf4j
public class PushNotificationSenderAuth extends PushNotificationAuth {
    
    private final List<Map<String, Object>> publicKeys = new ArrayList<>();
    private KeyPair keyPair;
    private String keyId;
    private final WebClient webClient;
    
    public PushNotificationSenderAuth() {
        super();
        this.webClient = WebClient.builder().build();
        generateJwk();
    }
    
    /**
     * 验证推送通知URL
     * @param url 要验证的URL
     * @return 验证结果
     */
    public Mono<Boolean> verifyPushNotificationUrl(String url) {
        String validationToken = UUID.randomUUID().toString();
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam("validationToken", validationToken)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    boolean isVerified = response.equals(validationToken);
                    log.info("Verified push-notification URL: {} => {}", url, isVerified);
                    return isVerified;
                })
                .onErrorResume(e -> {
                    log.warn("Error during sending push-notification for URL {}: {}", url, e.getMessage());
                    return Mono.just(false);
                });
    }
    
    /**
     * 生成JWK密钥对
     */
    public void generateJwk() {
        keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        keyId = UUID.randomUUID().toString();
        
        // 提取公钥信息
        Map<String, Object> publicKeyInfo = new HashMap<>();
        publicKeyInfo.put("kty", "RSA");
        publicKeyInfo.put("kid", keyId);
        publicKeyInfo.put("use", "sig");
        publicKeyInfo.put("alg", "RS256");
        
        // 在实际实现中，这里需要提取RSA公钥的n和e参数
        // 为简化示例，这里省略了详细实现
        
        publicKeys.add(publicKeyInfo);
    }
    
    /**
     * 处理JWKS端点请求
     * @return JWKS响应
     */
    public Map<String, Object> handleJwksEndpoint() {
        Map<String, Object> jwks = new HashMap<>();
        jwks.put("keys", publicKeys);
        return jwks;
    }
    
    /**
     * 生成JWT令牌
     * @param data 请求数据
     * @return JWT令牌
     */
    private String generateJwt(Object data) {
        Date now = new Date();
        String requestBodySha256 = calculateRequestBodySha256(data);
        
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .claim("iat", now.getTime() / 1000)
                .claim("request_body_sha256", requestBodySha256)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }
    
    /**
     * 发送推送通知
     * @param url 通知URL
     * @param data 通知数据
     * @return 发送结果
     */
    public Mono<Boolean> sendPushNotification(String url, Object data) {
        String jwtToken = generateJwt(data);
        
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + jwtToken)
                .bodyValue(data)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("Push-notification sent for URL: {}", url);
                    return true;
                })
                .onErrorResume(e -> {
                    log.warn("Error during sending push-notification for URL {}: {}", url, e.getMessage());
                    return Mono.just(false);
                });
    }
} 