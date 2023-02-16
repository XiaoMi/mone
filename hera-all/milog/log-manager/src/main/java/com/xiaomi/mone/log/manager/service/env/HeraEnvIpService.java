package com.xiaomi.mone.log.manager.service.env;

import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;

import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/15 19:25
 */
public interface HeraEnvIpService {
    /**
     * 查询该node下所有的pod信息
     *
     * @param nodeIp
     * @return
     */
    List<LogAgentListBo> queryInfoByNodeIp(String nodeIp);

    Map<String, List<LogAgentListBo>> queryAgentIpByPodIps(List<String> podIps);
    /**
     * 根据pode Ip查询node ip
     *
     * @param ips
     * @return
     */
    List<String> queryActualIps(List<String> ips);
}
