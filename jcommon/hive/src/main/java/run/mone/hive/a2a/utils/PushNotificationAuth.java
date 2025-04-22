package com.google.a2a.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 推送通知认证基类
 */
@Slf4j
public abstract class PushNotificationAuth {
    
    protected static final String AUTH_HEADER_PREFIX = "Bearer ";
    protected final ObjectMapper objectMapper;
    
    public PushNotificationAuth() {
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }
    
    /**
     * 计算请求体的SHA256哈希
     * @param data 请求数据
     * @return SHA256哈希值的十六进制字符串
     */
    protected String calculateRequestBodySha256(Object data) {
        try {
            String bodyStr = objectMapper.writeValueAsString(data);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bodyStr.getBytes());
            
            // 转为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to calculate SHA-256 hash", e);
        }
    }
}