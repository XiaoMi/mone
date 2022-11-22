package com.xiaomi.youpin.gwdash.service;

import java.util.Map;

/**
 * @author jiangzheng3
 * @version 1.0
 * @description: 审批相关
 * @date 2022/2/22 15:18
 */
public interface AuditingServiceAPI {

    Map<String, Object> groupApply(String feishuCardJson);

}
