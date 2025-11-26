package run.mone.hive.a2a.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TaskParams {
    /**
     * 表示任务ID参数
     */
    @Data
    public class TaskIdParams {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("metadata")
        private Map<String, Object> metadata;
    }

    /**
     * 表示任务查询参数
     */
    @Data
    public class TaskQueryParams extends TaskIdParams {
        @JsonProperty("historyLength")
        private Integer historyLength;
    }

    /**
     * 表示发送任务的参数
     */
    @Data
    public class TaskSendParams {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("sessionId")
        private String sessionId = UUID.randomUUID().toString().replace("-", "");
        
        @JsonProperty("message")
        private Message message;
        
        @JsonProperty("acceptedOutputModes")
        private List<String> acceptedOutputModes;
        
        @JsonProperty("pushNotification")
        private AuthenticationInfos.PushNotificationConfig pushNotification;
        
        @JsonProperty("historyLength")
        private Integer historyLength;
        
        @JsonProperty("metadata")
        private Map<String, Object> metadata;
    }

    /**
     * 表示任务推送通知配置
     */
    @Data
    public static class TaskPushNotificationConfig {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("pushNotificationConfig")
        private AuthenticationInfos.PushNotificationConfig pushNotificationConfig;
    } 

}