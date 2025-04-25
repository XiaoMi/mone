package run.mone.hive.mcp.service;

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
import run.mone.hive.bo.TaskExecutionInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class HiveManagerService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicReference<String> token = new AtomicReference<>();
    
    // 存储本地任务状态
    private final Map<String, TaskExecutionInfo> taskStatusCache = new ConcurrentHashMap<>();
    
    @Value("${hive.manager.base-url:http://127.0.0.1:8080}")
    private String baseUrl;
    
    @Value("${hive.manager.username:dp11}")
    private String username;
    
    @Value("${hive.manager.password:123456}")
    private String password;

    @Value("${hive.manager.reg.switch:false}")
    private Boolean enableRegHiveManager;

    /**
     * 每10分钟登录一次，获取新的token
     */
    @Scheduled(fixedRate = 600000) // 10分钟
    public void login() {

        if (!enableRegHiveManager) {
            return;
        }

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

        if (!enableRegHiveManager) {
            return;
        }

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

        if (!enableRegHiveManager) {
            return;
        }

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

        if (!enableRegHiveManager) {
            return;
        }

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
    
    /**
     * 发送任务到HiveManager
     * @param taskInfo 任务执行信息
     */
    public void sendTask(TaskExecutionInfo taskInfo) {
        if (!enableRegHiveManager) {
            // 如果未启用HiveManager，模拟异步任务执行过程
            simulateTaskExecution(taskInfo);
            return;
        }

        try {
            String currentToken = token.get();
            if (currentToken == null) {
                log.warn("No token available, attempting to login first");
                login();
                currentToken = token.get();
                if (currentToken == null) {
                    log.error("Failed to obtain token for sending task");
                    return;
                }
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + currentToken);
            
            HttpEntity<TaskExecutionInfo> request = new HttpEntity<>(taskInfo, headers);
            
            String taskUrl = baseUrl + "/api/v1/tasks/execute";
            Map<String, Object> response = restTemplate.postForObject(taskUrl, request, Map.class);
            
            log.info("Task submission response: {}", response);
            
            // 缓存任务信息
            taskStatusCache.put(taskInfo.getTaskId(), taskInfo);
        } catch (Exception e) {
            log.error("Error during task submission: {}", e.getMessage(), e);
            
            // 如果通信失败，模拟异步任务执行
            simulateTaskExecution(taskInfo);
        }
    }
    
    /**
     * 获取任务执行状态
     * @param taskId 任务ID
     * @return 任务执行信息
     */
    public TaskExecutionInfo getTaskStatus(String taskId) {
        if (!enableRegHiveManager) {
            // 如果未启用HiveManager，返回本地缓存的任务状态
            return taskStatusCache.get(taskId);
        }
        
        try {
            String currentToken = token.get();
            if (currentToken == null) {
                log.warn("No token available, attempting to login first");
                login();
                currentToken = token.get();
                if (currentToken == null) {
                    log.error("Failed to obtain token for getting task status");
                    return taskStatusCache.get(taskId);
                }
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + currentToken);
            
            String taskStatusUrl = baseUrl + "/api/v1/tasks/" + taskId + "/status";
            
            HttpEntity<?> request = new HttpEntity<>(headers);
            Map<String, Object> response = restTemplate.getForObject(taskStatusUrl, Map.class);
            
            if (response != null && response.containsKey("data") && response.get("data") instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                
                TaskExecutionInfo updatedInfo = new TaskExecutionInfo();
                updatedInfo.setTaskId(taskId);
                updatedInfo.setStatus((String) data.get("status"));
                updatedInfo.setStatusMessage((String) data.get("statusMessage"));
                updatedInfo.setResult((String) data.get("result"));
                
                // 更新缓存
                taskStatusCache.put(taskId, updatedInfo);
                
                return updatedInfo;
            } else {
                log.error("Invalid response format or task not found: {}", response);
                return taskStatusCache.get(taskId);
            }
        } catch (Exception e) {
            log.error("Error during getting task status: {}", e.getMessage(), e);
            return taskStatusCache.get(taskId);
        }
    }
    
    /**
     * 模拟异步任务执行过程
     * 这是一个内部方法，当未启用HiveManager或通信失败时使用
     * @param taskInfo 任务执行信息
     */
    private void simulateTaskExecution(TaskExecutionInfo taskInfo) {
        // 缓存初始任务状态
        taskStatusCache.put(taskInfo.getTaskId(), taskInfo);
        
        // 创建一个线程来模拟任务执行
        new Thread(() -> {
            try {
                // 更新状态为运行中
                TaskExecutionInfo runningInfo = new TaskExecutionInfo();
                runningInfo.setTaskId(taskInfo.getTaskId());
                runningInfo.setStatus("RUNNING");
                runningInfo.setStatusMessage("任务正在执行中...");
                runningInfo.setMetadata(taskInfo.getMetadata());
                
                taskStatusCache.put(taskInfo.getTaskId(), runningInfo);
                log.info("任务 {} 开始执行", taskInfo.getTaskId());
                
                // 模拟任务执行时间
                Thread.sleep(5000);
                
                // 模拟50%概率任务成功，50%概率任务失败
                boolean success = Math.random() > 0.5;
                
                TaskExecutionInfo finalInfo = new TaskExecutionInfo();
                finalInfo.setTaskId(taskInfo.getTaskId());
                finalInfo.setMetadata(taskInfo.getMetadata());
                
                if (success) {
                    finalInfo.setStatus("COMPLETED");
                    finalInfo.setStatusMessage("任务执行成功");
                    finalInfo.setResult("{\"success\": true, \"data\": \"这是模拟的执行结果数据\"}");
                    log.info("任务 {} 执行成功", taskInfo.getTaskId());
                } else {
                    finalInfo.setStatus("FAILED");
                    finalInfo.setStatusMessage("任务执行失败");
                    finalInfo.setResult("{\"success\": false, \"error\": \"模拟的错误信息\"}");
                    log.info("任务 {} 执行失败", taskInfo.getTaskId());
                }
                
                taskStatusCache.put(taskInfo.getTaskId(), finalInfo);
                
            } catch (InterruptedException e) {
                log.warn("模拟任务执行被中断: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }, "task-simulator-" + taskInfo.getTaskId()).start();
    }
} 