package com.xiaomi.mone.log.manager.service.path;

import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;
import com.xiaomi.youpin.docean.anno.Service;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description mone日志路径是基于docker, 路径并没有变化
 * @date 2022/11/15 18:51
 */
@Service
public class MoneLogPathMapping implements LogPathMapping {
    @Override
    public String getLogPath(String originLogPath, List<LogAgentListBo> logAgentListBos) {
        return originLogPath;
    }
}
