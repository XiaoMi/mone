package com.xiaomi.youpin.teambition.bo;

import lombok.Data;

import java.util.List;

/**
 * @author wmin
 * @date 2021/10/13
 */
@Data
public class TaskFlow {
    private String taskflowId;
    private String name;
    private List<FlowStatus> statuses;
}
