package com.xiaomi.mone.log.manager.service.env;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/16 17:34
 */
@Service
@Slf4j
public class MilineHeraEnvIpService implements HeraEnvIpService {



    @Override
    public List<LogAgentListBo> queryInfoByNodeIp(String nodeIp) {
        return Lists.newArrayList();
    }

    @Override
    public Map<String, List<LogAgentListBo>> queryAgentIpByPodIps(List<String> podIps) {
        Map<String, List<LogAgentListBo>> agentIpMap = Maps.newHashMap();
//        }
        return agentIpMap;
    }

    @Override
    public List<String> queryActualIps(List<String> ips) {
        return Lists.newArrayList();
    }
}
