package run.mone.hive.bo;

import lombok.Data;
import java.util.Map;

/**
 * 任务执行信息
 */
@Data
public class TaskExecutionInfo {
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 任务状态消息
     */
    private String statusMessage;
    
    /**
     * 任务元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 任务结果数据
     */
    private String result;

    private String token;
} 