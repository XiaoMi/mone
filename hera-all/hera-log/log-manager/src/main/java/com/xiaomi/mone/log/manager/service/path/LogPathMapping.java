package com.xiaomi.mone.log.manager.service.path;

import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description 日志路径映射规则
 * @date 2022/11/15 18:50
 */
public interface LogPathMapping {
    String LOG_PATH_PREFIX = "/home/work/log";
    /**
     * 映射完毕后的日志路径
     *
     * @param origin
     * @return
     */
    String getLogPath(String origin, List<LogAgentListBo> logAgentListBos);
}
