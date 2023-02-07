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
     * 任务id
     */
    private Integer taskId;

    /**
     * 任务内容
     */
    private String content;

    /**
     * 执行人
     */
    private String userName;

    /**
     * 执行类型 0自动触发、1手动执行
     */
    private int triggerType;

}
