package run.mone.hive.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HiveClient使用示例
 */
@Slf4j
public class UsageDemo {

    private final HiveClient hiveClient;

    /**
     * 创建任务客户端
     */
    public UsageDemo() {
        this.hiveClient = new HiveClient();
    }

    /**
     * 创建任务客户端
     *
     * @param baseUrl 基础URL
     */
    public UsageDemo(String baseUrl) {
        this.hiveClient = new HiveClient(baseUrl);
    }

    /**
     * 设置认证token
     *
     * @param token JWT认证token
     * @return TaskClient实例
     */
    public UsageDemo withToken(String token) {
        this.hiveClient.withToken(token);
        return this;
    }

    /**
     * 创建任务
     *
     * @param username 用户名
     * @param clientAgentId 客户端代理ID
     * @param serverAgentId 服务端代理ID
     * @return 创建的任务
     */
    public HiveClient.Task createTask(String username, Long clientAgentId, Long serverAgentId) {
        HiveClient.Task task = HiveClient.Task.builder()
                .username(username)
                .clientAgentId(clientAgentId)
                .serverAgentId(serverAgentId)
                .status("PENDING")
                .build();

        try {
            return hiveClient.createTask(task);
        } catch (IOException e) {
            log.error("创建任务失败", e);
            return null;
        }
    }

    /**
     * 执行任务
     *
     * @param taskId 任务ID
     * @param username 用户名
     * @param metadata 元数据
     * @return 执行的任务
     */
    public HiveClient.Task executeTask(String token, String taskId, String username, Map<String, String> metadata) {
        HiveClient.TaskExecutionInfo taskInfo = HiveClient.TaskExecutionInfo.builder()
                .token(token)
                .id(taskId)
                .userName(username)
                .metadata(metadata)
                .build();

        try {
            return hiveClient.executeTask(taskInfo);
        } catch (IOException e) {
            log.error("执行任务失败", e);
            return null;
        }
    }

    /**
     * 根据用户获取任务列表
     *
     * @return 任务列表
     */
    public List<HiveClient.Task> getTasks() {
        try {
            return hiveClient.getTasks();
        } catch (IOException e) {
            log.error("获取任务列表失败", e);
            return null;
        }
    }

    /**
     * 更新任务状态
     *
     * @param taskUuid 任务UUID
     * @param status 状态
     * @return 更新后的任务
     */
    public HiveClient.Task updateTaskStatus(String taskUuid, String status) {
        try {
            return hiveClient.updateTaskStatus(taskUuid, status);
        } catch (IOException e) {
            log.error("更新任务状态失败", e);
            return null;
        }
    }

    /**
     * 更新任务结果
     *
     * @param taskUuid 任务UUID
     * @param result 结果
     * @return 更新后的任务
     */
    public HiveClient.Task updateTaskResult(String taskUuid, String result) {
        try {
            return hiveClient.updateTaskResult(taskUuid, result);
        } catch (IOException e) {
            log.error("更新任务结果失败", e);
            return null;
        }
    }

    /**
     * 获取任务状态
     *
     * @param taskUuid 任务UUID
     * @return 任务状态信息
     */
    public Map<String, Object> getTaskStatus(String taskUuid) {
        try {
            return hiveClient.getTaskStatus(taskUuid);
        } catch (IOException e) {
            log.error("获取任务状态失败", e);
            return null;
        }
    }
    
    /**
     * 使用示例
     */
    public static void main(String[] args) {
        // 创建客户端
        UsageDemo taskClient = new UsageDemo("http://localhost:8080")
                .withToken("your-jwt-token");
                
        // 创建任务
        HiveClient.Task task = taskClient.createTask("username", 1L, 2L);
        if (task != null) {
            String taskUuid = task.getTaskUuid();
            System.out.println("任务创建成功: " + taskUuid);
            
            // 更新任务状态
            task = taskClient.updateTaskStatus(taskUuid, "RUNNING");
            System.out.println("任务状态更新: " + task.getStatus());
            
            // // 执行任务
            // Map<String, String> metadata = new HashMap<>();
            // metadata.put("param1", "value1");
            // metadata.put("param2", "value2");
            // HiveClient.Task executedTask = taskClient.executeTask("ba38a095d4524d689af79d976dfec84f", taskUuid, "username", metadata);
            // System.out.println("任务执行成功: " + (executedTask != null));
            
            // // 更新任务结果
            // task = taskClient.updateTaskResult(taskUuid, "{\"result\": \"success\"}");
            // System.out.println("任务结果: " + task.getResult());
            
            // // 获取任务状态
            // Map<String, Object> status = taskClient.getTaskStatus(taskUuid);
            // System.out.println("任务状态: " + status);
        }
    }
} 