package com.xiaomi.mone.log.manager.service.extension.tail;

import com.google.common.collect.Lists;
import com.xiaomi.mone.app.api.model.HeraSimpleEnv;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
import com.xiaomi.mone.log.manager.model.dto.MilogAppEnvDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.HeraAppEnvServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.MilogAppMiddlewareRelServiceImpl;
import com.xiaomi.mone.log.model.LogtailConfig;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentService.DEFAULT_AGENT_EXTENSION_SERVICE_KEY;
import static com.xiaomi.mone.log.manager.service.extension.tail.TailExtensionService.DEFAULT_TAIL_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/10 17:06
 */
@Service(name = DEFAULT_TAIL_EXTENSION_SERVICE_KEY)
@Slf4j
public class DefaultTailExtensionService implements TailExtensionService {

    @Resource
    private LogTailServiceImpl logTailService;

    @Resource
    private MilogAppMiddlewareRelServiceImpl milogAppMiddlewareRelService;

    @Resource
    private HeraAppEnvServiceImpl heraAppEnvService;

    @Resource(name = DEFAULT_AGENT_EXTENSION_SERVICE_KEY)
    private MilogAgentServiceImpl milogAgentService;

    @Override
    public boolean tailHandlePreprocessingSwitch(MilogLogStoreDO milogLogStore, MilogLogtailParam param) {
        return true;
    }

    @Override
    public boolean bindMqResourceSwitch(Integer appType) {
        return Objects.equals(ProjectTypeEnum.MIONE_TYPE.getCode().intValue(), appType);
    }

    @Override
    public boolean bindPostProcessSwitch(Long storeId) {
        return false;
    }

    @Override
    public void postProcessing() {

    }

    @Override
    public void defaultBindingAppTailConfigRel(Long id, Long milogAppId, Long middleWareId, String topicName, Integer batchSendSize) {
        milogAppMiddlewareRelService.defaultBindingAppTailConfigRel(id, milogAppId, middleWareId, topicName, batchSendSize);
    }

    @Override
    public void defaultBindingAppTailConfigRelPostProcess(Long spaceId, Long storeId, Long tailId, Long milogAppId, Long storeMqResourceId) {

    }

    @Override
    public void sendMessageOnCreate(MilogLogtailParam param, MilogLogTailDo mt, Long milogAppId, boolean supportedConsume) {
        /**
         * 发送配置信息---log-agent
         */
        CompletableFuture.runAsync(() -> logTailService.sengMessageToAgent(milogAppId, mt));
        /**
         * 发送最终配置信息---log-stream-- 查看日志模板类型，如果是opentelemetry日志，只发送mq不消费
         */
        if (supportedConsume) {
            logTailService.sengMessageToStream(mt, OperateEnum.ADD_OPERATE.getCode());
        }
    }

    @Override
    public void updateSendMsg(MilogLogTailDo milogLogtailDo, List<String> oldIps, boolean supportedConsume) {
        /**
         * 同步log-agent
         */
        CompletableFuture.runAsync(() -> milogAgentService.publishIncrementConfig(milogLogtailDo.getId(), milogLogtailDo.getMilogAppId(), milogLogtailDo.getIps()));
        /**
         * 同步 log-stream 如果是opentelemetry日志，只发送mq不消费
         */
        if (supportedConsume) {
//            List<MilogAppMiddlewareRel> middlewareRels = milogAppMiddlewareRelDao.queryByCondition(milogLogtailDo.getMilogAppId(), null, milogLogtailDo.getId());
//            createConsumerGroup(milogLogtailDo.getSpaceId(), milogLogtailDo.getStoreId(), milogLogtailDo.getId(), milogMiddlewareConfigDao.queryById(middlewareRels.get(0).getMiddlewareId()), milogLogtailDo.getMilogAppId(), false);
            logTailService.sengMessageToStream(milogLogtailDo, OperateEnum.UPDATE_OPERATE.getCode());
        }
        logTailService.compareChangeDelIps(milogLogtailDo.getId(), milogLogtailDo.getMilogAppId(), milogLogtailDo.getIps(), oldIps);
    }

    @Override
    public void logTailDoExtraFiled(MilogLogTailDo milogLogtailDo, MilogLogStoreDO logStoreDO, MilogLogtailParam logTailParam) {
        milogLogtailDo.setIps(logTailParam.getIps());
    }

    @Override
    public void logTailConfigExtraField(LogtailConfig logtailConfig, MilogMiddlewareConfig middlewareConfig) {

    }

    @Override
    public void logTailDelPostProcess(MilogLogStoreDO logStoreDO, MilogLogTailDo milogLogtailDo) {

    }

    @Override
    public List<MilogAppEnvDTO> getEnInfosByAppId(AppBaseInfo appBaseInfo, Long milogAppId, Integer deployWay) {
        List<HeraSimpleEnv> heraSimpleEnvs = null;
        try {
            heraSimpleEnvs = heraAppEnvService.querySimpleEnvAppBaseInfoId(milogAppId.intValue());
        } catch (Exception e) {
            log.error(String.format("query ip error:milogAppId:%s,deployWay:%s", milogAppId, deployWay), e);
        }
        if (CollectionUtils.isNotEmpty(heraSimpleEnvs)) {
            return heraSimpleEnvs.stream().map(envBo -> MilogAppEnvDTO.builder().label(envBo.getName()).value(envBo.getId()).ips(envBo.getIps()).build()).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

}
