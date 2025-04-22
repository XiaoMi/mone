package com.google.a2a.common.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表示A2A任务
 */
@Data
public class Task {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("sessionId")
    private String sessionId;
    
    @JsonProperty("status")
    private TaskStatus status;
    
    @JsonProperty("artifacts")
    private List<Artifact> artifacts;
    
    @JsonProperty("history")
    private List<Message> history;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
} 