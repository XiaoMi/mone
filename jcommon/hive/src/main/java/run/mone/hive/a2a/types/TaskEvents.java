package run.mone.hive.a2a.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class TaskEvents {
    /**
     * 表示任务状态更新事件
     */
    public record TaskStatusUpdateEvent(
        @JsonProperty("id") String id,
        @JsonProperty("status") TaskStatus status,
        @JsonProperty("final") Boolean isFinal,
        @JsonProperty("metadata") Map<String, Object> metadata
    ) {
        public TaskStatusUpdateEvent(String id, TaskStatus status, Map<String, Object> metadata) {
            this(id, status, false, metadata);
        }
        
        public TaskStatusUpdateEvent(String id, TaskStatus status) {
            this(id, status, false, null);
        }
    }

    /**
     * 表示任务制品更新事件
     */
    public record TaskArtifactUpdateEvent(
        @JsonProperty("id") String id,
        @JsonProperty("artifact") Artifact artifact,
        @JsonProperty("metadata") Map<String, Object> metadata
    ) {
        public TaskArtifactUpdateEvent(String id, Artifact artifact) {
            this(id, artifact, null);
        }
    }
}