package run.mone.mcp.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.bo.RegInfoDto;
import run.mone.hive.mcp.service.IHiveManagerService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class HiveManagerService implements IHiveManagerService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicReference<String> token = new AtomicReference<>();
    
    @Value("${hive.manager.base-url:http://127.0.0.1:8080}")
    private String baseUrl;
    
    @Value("${hive.manager.username:dp11}")
    private String username;
    
    @Value("${hive.manager.password:123456}")
    private String password;

    /**
     * 每10分钟登录一次，获取新的token
     */
    @Scheduled(fixedRate = 600000) // 10分钟
    public void login() {
        try {
            log.info("Logging in to get token");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("username", username);
            loginRequest.put("password", password);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);
            
            String loginUrl = baseUrl + "/api/v1/users/login";
            Map<String, Object> response = restTemplate.postForObject(loginUrl, request, Map.class);
            
            if (response != null && response.containsKey("data") && response.get("data") instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data.containsKey("token")) {
                    String newToken = (String) data.get("token");
                    token.set(newToken);
                    log.info("Successfully obtained new token");
                } else {
                    log.error("Token not found in response: {}", response);
                }
            } else {
                log.error("Invalid response format: {}", response);
            }
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
        }
    }
    
    /**
     * 注册Agent
     */
    public void register(RegInfo regInfo) {
        try {
            String currentToken = token.get();
            if (currentToken == null) {
                log.warn("No token available, attempting to login first");
                login();
                currentToken = token.get();
                if (currentToken == null) {
                    log.error("Failed to obtain token for registration");
                    return;
                }
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + currentToken);

            // 将 RegInfo 转换为 RegInfoDto
            RegInfoDto regInfoDto = RegInfoDto.fromRegInfo(regInfo);
            
            HttpEntity<RegInfoDto> request = new HttpEntity<>(regInfoDto, headers);
            
            String registerUrl = baseUrl + "/api/v1/agents/register";
            Object response = restTemplate.postForObject(registerUrl, request, Object.class);
            
            log.info("Registration response: {}", response);
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 注销Agent
     */
    public void unregister(RegInfo regInfo) {
        try {
            String currentToken = token.get();
            if (currentToken == null) {
                log.warn("No token available, attempting to login first");
                login();
                currentToken = token.get();
                if (currentToken == null) {
                    log.error("Failed to obtain token for unregistration");
                    return;
                }
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + currentToken);
            
            // 将 RegInfo 转换为 RegInfoDto
            RegInfoDto regInfoDto = RegInfoDto.fromRegInfo(regInfo);
            
            HttpEntity<RegInfoDto> request = new HttpEntity<>(regInfoDto, headers);
            
            String unregisterUrl = baseUrl + "/api/v1/agents/unregister";
            Object response = restTemplate.postForObject(unregisterUrl, request, Object.class);
            
            log.info("Unregistration response: {}", response);
        } catch (Exception e) {
            log.error("Error during unregistration: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送心跳
     */
    public void heartbeat(HealthInfo healthInfo) {
        try {
            String currentToken = token.get();
            if (currentToken == null) {
                log.warn("No token available, attempting to login first");
                login();
                currentToken = token.get();
                if (currentToken == null) {
                    log.error("Failed to obtain token for heartbeat");
                    return;
                }
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + currentToken);
            
            HttpEntity<HealthInfo> request = new HttpEntity<>(healthInfo, headers);
            
            String heartbeatUrl = baseUrl + "/api/v1/agents/health";
            Object response = restTemplate.postForObject(heartbeatUrl, request, Object.class);
            
            log.debug("Heartbeat response: {}", response);
        } catch (Exception e) {
            log.error("Error during heartbeat: {}", e.getMessage(), e);
        }
    }
} 