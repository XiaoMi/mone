/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.extension.tail;

import com.google.common.collect.Lists;
import com.xiaomi.mone.app.api.model.HeraSimpleEnv;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.model.bo.LogTailParam;
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
import org.apache.commons.lang3.StringUtils;

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

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Override
    public boolean tailHandlePreprocessingSwitch(MilogLogStoreDO milogLogStore, LogTailParam param) {
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
    public void sendMessageOnCreate(LogTailParam param, MilogLogTailDo mt, Long milogAppId, boolean supportedConsume) {
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
        logTailService.compareChangeDelIps(milogLogtailDo.getId(), milogLogtailDo.getLogPath(), milogLogtailDo.getIps(), oldIps);
    }

    @Override
    public void logTailDoExtraFiled(MilogLogTailDo milogLogtailDo, MilogLogStoreDO logStoreDO, LogTailParam logTailParam) {
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

    @Override
    public boolean decorateTailDTOValId(Integer appType) {
        return true;
    }

    @Override
    public List<String> getStreamMachineUniqueList(Integer projectTypeCode, String motorRoomEn) {
        return Lists.newArrayList();
    }

    @Override
    public String deleteCheckProcessPre(Long id) {
        return StringUtils.EMPTY;
    }

    @Override
    public String validLogPath(LogTailParam param) {
        if (Objects.equals(ProjectTypeEnum.MIONE_TYPE.getCode(), param.getAppType())) {
            // 校验同名日志文件
            List<MilogLogTailDo> appLogTails = milogLogtailDao.queryByMilogAppAndEnv(param.getMilogAppId(), param.getEnvId());
            for (int i = 0; i < appLogTails.size() && null == param.getId(); i++) {
                if (appLogTails.get(i).getLogPath().equals(param.getLogPath())) {
                    return "当前部署环境该文件" + param.getLogPath() + "已配置日志采集,别名为：" + appLogTails.get(i).getTail();
                }
            }
        }
        return StringUtils.EMPTY;
    }

}
