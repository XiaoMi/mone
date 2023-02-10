package com.xiaomi.data.push.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskExecuteContentDTO implements Serializable {

    /**
     * taskId
     */
    private Integer taskId;

    /**
     * task content
     */
    private String content;

    /**
     * operator
     */
    private String userName;

    /**
     * Execute type 0 Automatically or 1 Manually
     */
    private int triggerType;

}
