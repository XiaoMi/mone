package com.google.a2a.common.types;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 表示任务的当前状态
 */
@Data
public class TaskStatus {
    @JsonProperty("state")
    private TaskState state;
    
    @JsonProperty("message")
    private Message message;
    
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime timestamp = ZonedDateTime.now();
} 