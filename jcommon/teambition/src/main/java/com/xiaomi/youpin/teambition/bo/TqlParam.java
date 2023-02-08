package com.xiaomi.youpin.teambition.bo;

import lombok.Data;

/**
 * @author wmin
 * @date 2021/10/8
 */
@Data
public class TqlParam {
    private String projectId;
    private boolean isArchived;
    private String condition;
    private String userId;
    private Integer priority;
    private Integer isDone;
    private String dueDateStart;
    private String dueDateEnd;
    private String parentId;
}
