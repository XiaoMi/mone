package com.xiaomi.mone.log.manager.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.service.AgentConfigService;
import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;
import com.xiaomi.mone.log.manager.service.env.HeraEnvIpService;
import com.xiaomi.mone.log.manager.service.env.MilineHeraEnvIpService;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/8/4 19:42
 */
@Slf4j
@Service
public class AgentConfigServiceImpl implements AgentConfigService {

    @Resource
    private MilogAgentServiceImpl milogAgentService;

    /**
     * 1.查询出该物理机下的接入过日志的所有应用
     * 2.封装信息
     *
     * @param ip
     * @return
     */
    @Override
    public LogCollectMeta getLogCollectMetaFromManager(String ip) {
        log.info("getLogCollectMetaFromManager begin:{}", ip);
        try {
            long begin = System.currentTimeMillis();
            LogCollectMeta logCollectMeta;
            List<LogAgentListBo> k8sPodIps = queryK8sPodIps(ip);
            if (CollectionUtils.isNotEmpty(k8sPodIps)) {
                logCollectMeta = milogAgentService.queryMilogAgentConfigK8s(ip, k8sPodIps);
            } else {
                logCollectMeta = milogAgentService.queryMilogAgentConfig("", ip, "");
            }
            log.info("getLogCollectMetaFromManager end:{} {} {}", ip, new Gson().toJson(logCollectMeta), (System.currentTimeMillis() - begin));
            return logCollectMeta;
        } catch (Throwable ex) {
            log.error("getLogCollectMetaFromManager error:{}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public List<LogAgentListBo> queryK8sPodIps(String agentIP) {
        /**
         * 这块接入平台可以扩展
         */
        HeraEnvIpService heraEnvIpService = Ioc.ins().getBean(MilineHeraEnvIpService.class);
        return heraEnvIpService.queryInfoByNodeIp(agentIP);
    }

}
