package run.mone.hive.mcp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;
import run.mone.hive.bo.RegInfoDto;
import run.mone.hive.bo.TaskExecutionInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class HiveManagerService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicReference<String> token = new AtomicReference<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // 存储本地任务状态
    private final Map<String, TaskExecutionInfo> taskStatusCache = new ConcurrentHashMap<>();

    @Value("${hive.manager.base-url:http://127.0.0.1:8080}")
    private String baseUrl;

    @Value("${hive.manager.username:dp11}")
    private String username;

    @Value("${hive.manager.password:123456}")
    private String password;

    @Value("${hive.manager.reg.switch:true}")
    private Boolean enableRegHiveManager;

    @PostConstruct
    public void init() {
        // 20秒后开始执行，每10分钟执行一次
        scheduler.scheduleAtFixedRate(this::login, 20, 600, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 登录获取token
     */
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
     * 处理403错误，重新登录并重试
     * @param operation 操作名称，用于日志
     * @param operationFunction 需要重试的操作
     * @return 操作结果
     */
    private <T> T handle403AndRetry(String operation, OperationFunction<T> operationFunction) {
        try {
            return operationFunction.execute();
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.warn("Received 403 Forbidden for {}, attempting to re-login", operation);
                login();
                // 重试一次
                return operationFunction.execute();
            }
            throw e;
        }
    }

    /**
     * 注册Agent
     */
    public void register(RegInfo regInfo) {
        if (!enableRegHiveManager) {
            return;
        }

        handle403AndRetry("registration", () -> {
            String currentToken = token.get();
            if (currentToken == null) {
                log.warn("No token available, attempting to login first");
                login();
                currentToken = token.get();
                if (currentToken == null) {
                    log.error("Failed to obtain token for registration");
                    return null;
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
            return null;
        });
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

        handle403AndRetry("heartbeat", () -> {
            String currentToken = token.get();
            if (currentToken == null) {
                log.warn("No token available, attempting to login first");
                login();
                currentToken = token.get();
                if (currentToken == null) {
                    log.error("Failed to obtain token for heartbeat");
                    return null;
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + currentToken);

            HttpEntity<HealthInfo> request = new HttpEntity<>(healthInfo, headers);

            String heartbeatUrl = baseUrl + "/api/v1/agents/health";
            Object response = restTemplate.postForObject(heartbeatUrl, request, Object.class);

            log.info("Heartbeat response: {}", response);
            return null;
        });
    }

    /**
     * 发送任务到HiveManager
     *
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
     *
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
     * 获取配置信息
     *
     * @param request 包含agentId和userId的请求参数Map
     * @return 配置信息映射
     */
    public Map<String, String> getConfig(Map<String, String> request) {
        if (!enableRegHiveManager) {
            // 如果未启用HiveManager，返回默认配置
            return getDefaultConfig(request);
        }

        try {
            String currentToken = token.get();
            if (currentToken == null) {
                log.warn("No token available, attempting to login first");
                login();
                currentToken = token.get();
                if (currentToken == null) {
                    log.error("Failed to obtain token for getting config");
                    return getDefaultConfig(request);
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + currentToken);

            HttpEntity<Map<String, String>> httpRequest = new HttpEntity<>(request, headers);

            String configUrl = baseUrl + "/api/v1/agents/config";
            Map<String, Object> response = restTemplate.postForObject(configUrl, httpRequest, Map.class);

            log.info("Config retrieval response: {}", response);

            if (response != null && response.containsKey("data") && response.get("data") instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                Map<String, String> configs = new HashMap<>();
                // 将 Object 类型的值转换为 String 类型
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    configs.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
                return configs;
            } else {
                log.error("Invalid response format for config: {}", response);
                return getDefaultConfig(request);
            }
        } catch (Exception e) {
            log.error("Error during getting config: {}", e.getMessage(), e);
            return getDefaultConfig(request);
        }
    }

    /**
     * 获取默认配置信息
     * 当无法从HiveManager获取配置时使用
     *
     * @param request 包含agentId和userId的请求参数Map
     * @return 默认配置信息映射
     */
    private Map<String, String> getDefaultConfig(Map<String, String> request) {
        Map<String, String> defaultConfig = new HashMap<>();
        // 从请求中获取agentId和userId
        if (request.containsKey("agentId")) {
            defaultConfig.put("agentId", String.valueOf(request.get("agentId")));
        }
        if (request.containsKey("userId")) {
            defaultConfig.put("userId", String.valueOf(request.get("userId")));
        }
        defaultConfig.put("timestamp", String.valueOf(System.currentTimeMillis()));
        log.info("Using default config for request: {}", request);
        return defaultConfig;
    }

    /**
     * 模拟异步任务执行过程
     * 这是一个内部方法，当未启用HiveManager或通信失败时使用
     *
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

    /**
     * 操作函数接口，用于处理需要重试的操作
     */
    @FunctionalInterface
    private interface OperationFunction<T> {
        T execute();
    }
} 