package run.mone.hive.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Used to interact with Hive Task
 */
@Slf4j
public class HiveClient {

    private static final String HIVE_URL = "http://localhost:8080";
    
    private static final String API_PATH = "/api/v1/tasks";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final Gson gson;
    private final String baseUrl;
    private String token;
    
    /**
     * 创建HiveClient实例
     */
    public HiveClient() {
        this(HIVE_URL);
    }
    
    /**
     * 创建HiveClient实例
     * 
     * @param baseUrl 基础URL
     */
    public HiveClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * 设置认证token
     * 
     * @param token 认证token
     * @return HiveClient实例
     */
    public HiveClient withToken(String token) {
        this.token = token;
        return this;
    }
    
    /**
     * 创建任务
     *
     * @param task 任务信息
     * @return 创建的任务
     * @throws IOException 网络异常
     */
    public Task createTask(Task task) throws IOException {
        String url = baseUrl + API_PATH;
        RequestBody body = RequestBody.create(gson.toJson(task), JSON);
        
        Request request = createRequestBuilder(url)
                .post(body)
                .build();
                
        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, Task.class);
        }
    }
    
    /**
     * 根据任务UUID获取任务
     *
     * @param taskUuid 任务UUID
     * @return 任务信息
     * @throws IOException 网络异常
     */
    public Task getTask(String taskUuid) throws IOException {
        String url = baseUrl + API_PATH + "/" + taskUuid;
        
        Request request = createRequestBuilder(url)
                .get()
                .build();
                
        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, Task.class);
        }
    }
    
    /**
     * 获取任务列表
     *
     * @return 任务列表
     * @throws IOException 网络异常
     */
    public List<Task> getTasks() throws IOException {
        return getTasks(null, null);
    }
    
    /**
     * 根据条件获取任务列表
     *
     * @param clientAgentId 客户端代理ID
     * @param serverAgentId 服务端代理ID
     * @return 任务列表
     * @throws IOException 网络异常
     */
    public List<Task> getTasks(Long clientAgentId, Long serverAgentId) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + API_PATH).newBuilder();
        
        if (clientAgentId != null) {
            urlBuilder.addQueryParameter("clientAgentId", String.valueOf(clientAgentId));
        }
        
        if (serverAgentId != null) {
            urlBuilder.addQueryParameter("serverAgentId", String.valueOf(serverAgentId));
        }
        
        Request request = createRequestBuilder(urlBuilder.build().toString())
                .get()
                .build();
                
        try (Response response = client.newCall(request).execute()) {
            Type listType = new TypeToken<List<Task>>(){}.getType();
            return handleResponse(response, listType);
        }
    }
    
    /**
     * 更新任务状态
     *
     * @param taskUuid 任务UUID
     * @param status 状态
     * @return 更新后的任务
     * @throws IOException 网络异常
     */
    public Task updateTaskStatus(String taskUuid, String status) throws IOException {
        String url = baseUrl + API_PATH + "/" + taskUuid + "/status";
        
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("status", status);
                
        Request request = createRequestBuilder(urlBuilder.build().toString())
                .put(RequestBody.create("", MediaType.parse("text/plain")))
                .build();
                
        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, Task.class);
        }
    }
    
    /**
     * 更新任务结果
     *
     * @param taskUuid 任务UUID
     * @param result 结果
     * @return 更新后的任务
     * @throws IOException 网络异常
     */
    public Task updateTaskResult(String taskUuid, String result) throws IOException {
        String url = baseUrl + API_PATH + "/" + taskUuid + "/result";
        
        RequestBody body = RequestBody.create(result, MediaType.parse("text/plain"));
        
        Request request = createRequestBuilder(url)
                .put(body)
                .build();
                
        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, Task.class);
        }
    }
    
    /**
     * 执行任务
     *
     * @param taskExecutionInfo 任务执行信息
     * @return 执行的任务
     * @throws IOException 网络异常
     */
    public Task executeTask(TaskExecutionInfo taskExecutionInfo) throws IOException {
        String url = baseUrl + API_PATH + "/execute";
        
        RequestBody body = RequestBody.create(gson.toJson(taskExecutionInfo), JSON);
        
        Request request = createRequestBuilder(url)
                .post(body)
                .build();
                
        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, Task.class);
        }
    }
    
    /**
     * 获取任务状态
     *
     * @param taskUuid 任务UUID
     * @return 任务状态信息
     * @throws IOException 网络异常
     */
    public Map<String, Object> getTaskStatus(String taskUuid) throws IOException {
        String url = baseUrl + API_PATH + "/" + taskUuid + "/status";
        
        Request request = createRequestBuilder(url)
                .get()
                .build();
                
        try (Response response = client.newCall(request).execute()) {
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            return handleResponse(response, mapType);
        }
    }
    
    private <T> T handleResponse(Response response, Class<T> clazz) throws IOException {
        return handleResponse(response, (Type) clazz);
    }
    
    private <T> T handleResponse(Response response, Type typeOfT) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("请求失败: " + response.code() + " " + response.message());
        }
        
        String responseBody = response.body().string();
        ApiResponse<T> apiResponse = gson.fromJson(responseBody, TypeToken.getParameterized(ApiResponse.class, typeOfT).getType());
        
        if (apiResponse.getCode() != 200) {
            throw new IOException("API错误: " + apiResponse.getCode() + " " + apiResponse.getMessage());
        }
        
        return apiResponse.getData();
    }
    
    private Request.Builder createRequestBuilder(String url) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json");
                
        if (StringUtils.isNotBlank(token)) {
            builder.header("Authorization", "Bearer " + token);
        }
        
        return builder;
    }
    
    /**
     * API响应包装类
     */
    @Data
    public static class ApiResponse<T> {
        private int code;
        private String message;
        private T data;
    }
    
    /**
     * 任务模型
     */
    @Data
    @Builder
    public static class Task {
        private String taskUuid;
        private String username;
        private String status;
        private String result;
        private Long clientAgentId;
        private Long serverAgentId;
        private String token;
    }
    
    /**
     * 任务执行信息
     */
    @Data
    @Builder
    public static class TaskExecutionInfo {
        private String id;
        private String token;
        private String userName;
        private Map<String, String> metadata;
    }
    
    /**
     * 服务器使用的自定义任务类型
     */
    @Data
    @Builder
    public static class HiveTask {
        private String taskId;
        private String userName;
        private Map<String, String> metadata;
        private String token;
        
        /**
         * 从HiveTask创建TaskExecutionInfo
         *
         * @param hiveTask Hive任务
         * @return 任务执行信息
         */
        public static TaskExecutionInfo toTaskExecutionInfo(HiveTask hiveTask) {
            return TaskExecutionInfo.builder()
                    .id(hiveTask.getTaskId())
                    .token(hiveTask.getToken())
                    .userName(hiveTask.getUserName())
                    .metadata(hiveTask.getMetadata() != null ? hiveTask.getMetadata() : Collections.emptyMap())
                    .build();
        }
    }
}
