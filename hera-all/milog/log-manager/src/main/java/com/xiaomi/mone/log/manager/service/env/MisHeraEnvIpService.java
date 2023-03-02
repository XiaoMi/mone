package com.xiaomi.mone.log.manager.service.env;

import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;
import com.xiaomi.youpin.docean.anno.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description 查询mis
 * @date 2022/11/16 15:39
 */
@Service
public class MisHeraEnvIpService implements HeraEnvIpService{

    @Override
    public List<LogAgentListBo> queryInfoByNodeIp(String nodeIp) {
        return null;
    }

    @Override
    public Map<String, List<LogAgentListBo>> queryAgentIpByPodIps(List<String> podIps) {
        return null;
    }

    @Override
    public List<String> queryActualIps(List<String> ips) {
        return ips;
    }
}
