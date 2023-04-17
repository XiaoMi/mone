package com.xiaomi.mone.log.manager.service.env;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/16 15:41
 */
@Service
public class MoneHeraEnvIpService implements HeraEnvIpService {

    @Override
    public List<LogAgentListBo> queryInfoByNodeIp(String nodeIp) {
        return Lists.newArrayList();
    }

    @Override
    public Map<String, List<LogAgentListBo>> queryAgentIpByPodIps(List<String> podIps) {
        return null;
    }

    @Override
    public List<String> queryActualIps(List<String> ips, String agentIp) {
        if (StringUtils.isNotBlank(agentIp)) {
            return Lists.newArrayList(agentIp);
        }
        return ips;
    }
}
