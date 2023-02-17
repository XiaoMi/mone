package com.xiaomi.mone.log.manager.service.path;

import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.GSON;

/**
 * @author wtt
 * @version 1.0
 * @description 基于k8s的deaemonset的日志路径会挂载到nod中
 * @date 2022/11/15 18:52
 */
@Service
@Slf4j
public class MilineK8sLogPathMapping implements LogPathMapping {

    @Override
    public String getLogPath(String originLogPath, List<LogAgentListBo> logAgentList) {
        if (StringUtils.isEmpty(originLogPath) || originLogPath.isEmpty()) {
            return originLogPath;
        }
        try {
            String podNames = logAgentList.stream()
                    .sorted(Comparator.comparing(LogAgentListBo::getPodIP))
                    .map(LogAgentListBo::getPodName)
                    .collect(Collectors.joining("|"));
            String logPathSuffix = StringUtils.substringAfter(originLogPath, LOG_PATH_PREFIX);
            return new StringBuilder().append(LOG_PATH_PREFIX)
                    .append("/")
                    .append(String.format(podNames.split("\\|").length > 1 ? "(%s)" : "%s", podNames))
                    .append(logPathSuffix).toString();
        } catch (Exception e) {
            log.error(String.format("generateK8sTailLogPath error,logPath:%s,ipList:%s", originLogPath, GSON.toJson(logAgentList)), e);
        }
        return originLogPath;
    }
}
