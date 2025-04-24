package run.mone.hive.a2a.types;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 表示任务的当前状态
 */
@Data
public class TaskStatus {
    /**
     * 任务状态值常量
     */
    public static final String PENDING = "PENDING";
    public static final String RUNNING = "RUNNING";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";
    public static final String CANCELED = "CANCELED";
    
    /**
     * 任务状态值
     */
    @JsonProperty("state")
    private TaskState state;
    
    @JsonProperty("message")
    private Message message;
    
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime timestamp = ZonedDateTime.now();
} 