package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/27 17:25
 */
public interface MilogStreamService {
    Result<String> configIssueStream(String ip);

    void executeSql(String sql);
}
