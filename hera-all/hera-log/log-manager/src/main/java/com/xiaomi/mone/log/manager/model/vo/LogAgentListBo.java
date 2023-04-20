package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;


/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/15 17:08
 */
@Data
public class LogAgentListBo {

    private String podIP;
    private String podName;
    private String agentIP;
    private String agentName;
}
