package com.xiaomi.mone.log.api.service;

import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;

public interface LogProcessService {

    /**
     * 更新日志收集进度
     * @param cmd
     */
    void updateLogProcess(UpdateLogProcessCmd cmd);
}
