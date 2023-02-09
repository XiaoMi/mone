/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.traffic.recording.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingStatusEnum;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.GetRecordingConfigListReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfig;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigList;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.RecordingDubboService;
import com.xiaomi.youpin.tesla.traffic.recording.common.TrafficException;
import com.xiaomi.youpin.tesla.traffic.recording.daoobj.RecordingConfigDao;
import com.youpin.xiaomi.tesla.bo.ModifyType;
import com.youpin.xiaomi.tesla.service.TeslaGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import static com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.GatewayEnvTypeEnum.*;
import static com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingSourceTypeEnum.*;
import static com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingStrategyEnum.PERCENTAGE_CODE;
import static com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingStrategyEnum.RECODRING_STRATEGY_CODES;

/**
 * @author 
 * 提供对外的dubbo接口
 */
@Slf4j
@Service(group = "$group", interfaceClass = RecordingDubboService.class)
public class RecordingDubboServiceImpl implements RecordingDubboService {

    @Resource
    private RecordingConfigService recordingConfigService;

    @Reference(cluster = "broadcast", check = false, interfaceClass = TeslaGatewayService.class, group = "$gateway_online_group")
    private TeslaGatewayService teslaGatewayServiceOnline;

    @Reference(cluster = "broadcast", check = false, interfaceClass = TeslaGatewayService.class, group = "$gateway_intra_group")
    private TeslaGatewayService teslaGatewayServiceIntra;

    @Reference(cluster = "broadcast", check = false, interfaceClass = TeslaGatewayService.class, group = "$gateway_staging_group")
    private TeslaGatewayService teslaGatewayServiceStaging;

    @Override
    public Result<RecordingConfigList> getRecordingConfigList(GetRecordingConfigListReq req) {
        try {
            RecordingConfigList recordingConfigList = recordingConfigService.getRecordingConfigListByPage(req.getPage(), req.getPageSize(), req);
            return Result.success(recordingConfigList);
        } catch (Exception e) {
            log.error("RecordingDubboServiceImpl.getRecordingConfigList, ", e);
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> newRecordingConfig(RecordingConfig recordingConfig) {
        try {
            checkParam(recordingConfig);

            recordingConfig.setStatus(RecordingStatusEnum.TO_RECORD.getCode());
            long now = System.currentTimeMillis();
            recordingConfig.setUpdateTime(now);
            recordingConfig.setCreateTime(now);
            recordingConfig.setCreator(recordingConfig.getCreator());
            recordingConfig.setUpdater(recordingConfig.getUpdater());

            recordingConfigService.addRecordingConfig(RecordingConfigService.adapterToRecordingConfigDao(recordingConfig));
            return Result.success(true);
        } catch (Exception e) {
            log.error("RecordingDubboServiceImpl.newRecordingConfig, ", e);
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> updateRecordingConfig(RecordingConfig recordingConfig) {
        try {
            checkParam(recordingConfig);

            RecordingConfigDao r = recordingConfigService.getRecordingConfigDaoById(recordingConfig.getId());
            if (r == null) {
                return Result.fail("待更新的配置不存在");
            }
            if (r.getStatus() == RecordingStatusEnum.RECORDING.getCode()) {
                return Result.fail("该配置正在录制中，不允许修改");
            }
            if (recordingConfig.getStatus() == RecordingStatusEnum.RECORDING.getCode()) {
                return Result.fail("请点击开始录制按钮进行录制");
            }
            long now = System.currentTimeMillis();
            recordingConfig.setUpdateTime(now);
            recordingConfig.setUpdater(recordingConfig.getUpdater());

            recordingConfigService.updateRecordingConfig(RecordingConfigService.adapterToRecordingConfigDao(recordingConfig));
            return Result.success(true);
        } catch (Exception e) {
            log.error("RecordingDubboServiceImpl.updateRecordingConfig, ", e);
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteRecordingConfig(RecordingConfigReq req) {
        RecordingConfigDao r = recordingConfigService.getRecordingConfigDaoById(req.getId());
        if (r == null) {
            return Result.fail("待删除的配置不存在");
        }
        if (r.getStatus() == RecordingStatusEnum.RECORDING.getCode()) {
            return Result.fail("该配置正在录制中，不允许删除");
        }
        try {
            recordingConfigService.deleteRecordingConfig(req.getId());
            return Result.success(true);
        } catch (Exception e) {
            log.error("RecordingDubboServiceImpl.deleteRecordingConfig, ", e);
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<RecordingConfig> startRecording(RecordingConfigReq req) {
        RecordingConfigDao recordingConfigDao = recordingConfigService.getRecordingConfigDaoById(req.getId());
        if (recordingConfigDao == null) {
            return Result.fail("开始录制的配置不存在");
        }
        if (recordingConfigDao.getStatus() == RecordingStatusEnum.RECORDING.getCode()) {
            return Result.fail("该配置已经在录制中");
        }

        recordingConfigDao.setStatus(RecordingStatusEnum.RECORDING.getCode());
        recordingConfigDao.setUpdateTime(System.currentTimeMillis());
        recordingConfigDao.setUpdater(req.getUser());

        try {
            RecordingConfig recordingConfig = RecordingConfigService.adapterToRecordingConfig(recordingConfigDao);
            notifyRecordingSwitch(ModifyType.Add, recordingConfig);

            recordingConfigService.updateRecordingConfig(recordingConfigDao);
            return Result.success(recordingConfig);
        } catch (Exception e) {
            log.error("RecordingDubboServiceImpl.startRecording, ", e);
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<RecordingConfig> stopRecording(RecordingConfigReq req) {
        RecordingConfigDao recordingConfigDao = recordingConfigService.getRecordingConfigDaoById(req.getId());
        if (recordingConfigDao == null) {
            return Result.fail("停止录制的配置不存在");
        }
        if (recordingConfigDao.getStatus() == RecordingStatusEnum.TO_RECORD.getCode()) {
            return Result.fail("该配置已经停止");
        }

        recordingConfigDao.setStatus(RecordingStatusEnum.TO_RECORD.getCode());
        recordingConfigDao.setUpdateTime(System.currentTimeMillis());
        recordingConfigDao.setUpdater(req.getUser());
        try {
            RecordingConfig recordingConfig = RecordingConfigService.adapterToRecordingConfig(recordingConfigDao);
            notifyRecordingSwitch(ModifyType.Delete, recordingConfig);

            recordingConfigService.updateRecordingConfig(recordingConfigDao);
            return Result.success(recordingConfig);
        } catch (Exception e) {
            log.error("RecordingDubboServiceImpl.startRecording, ", e);
            return Result.fail(e.getMessage());
        }
    }

    private void notifyRecordingSwitch(ModifyType opt, RecordingConfig recordingConfig) {
        switch (recordingConfig.getSourceType()) {
            case GATEWAY_CODE: {
                notifyGateway(opt, recordingConfig);
                return;
            }
            case DUBBO_CODE: {
                notifyDubbo(opt, recordingConfig);
                return;
            }
            default: {
                log.error("RecordingDubboServiceImpl.notifyGateway error, sourceType is wrong, recordingConfig:{} ", recordingConfig);
                throw new TrafficException("配置的sourceType错误");
            }
        }
    }

    private void notifyGateway(ModifyType opt, RecordingConfig recordingConfig) {
        switch (recordingConfig.getGatewaySource().getEnvType()) {
            case ONLINE_CODE: {
                teslaGatewayServiceOnline.updateRecordingTraffic(opt, new Gson().toJson(recordingConfig));
                return;
            }
            case INTRANET_CODE: {
                teslaGatewayServiceIntra.updateRecordingTraffic(opt, new Gson().toJson(recordingConfig));
                return;
            }
            case STAGING_CODE: {
                teslaGatewayServiceStaging.updateRecordingTraffic(opt, new Gson().toJson(recordingConfig));
                return;
            }
            default: {
                log.error("RecordingDubboServiceImpl.notifyGateway error, env is wrong, recordingConfig:{} ", recordingConfig);
                throw new TrafficException("网关来源配置的env错误");
            }

        }
    }

    private void notifyDubbo(ModifyType opt, RecordingConfig recordingConfig) {
        //TODO 未来支持dubbo
        return;
    }

    private void checkParam(RecordingConfig recordingConfig) {
        if (!RECODRING_SOURCE_TYPE_CODES.contains(recordingConfig.getSourceType())) {
            throw new TrafficException("录制来源填写错误");
        }
        if (recordingConfig.getSourceType() == GATEWAY.getCode()) {
            //网关流量来源
            if (recordingConfig.getGatewaySource() == null) {
                throw new TrafficException("网关流量来源时，未填写相关配置");
            }
            if (!GATEWAY_ENV_TYPE_CODES.contains(recordingConfig.getGatewaySource().getEnvType())) {
                throw new TrafficException("网关流量来源时，网关环境填写错误");
            }
            if (StringUtils.isEmpty(recordingConfig.getGatewaySource().getUrl())) {
                throw new TrafficException("网关流量来源时，url必填");
            }
        }

        //校验录制策略
        if (recordingConfig.getRecordingStrategy() != 0) {
            if (!RECODRING_STRATEGY_CODES.contains(recordingConfig.getRecordingStrategy())) {
                throw new TrafficException("录制策略选择错误");
            }
            if (recordingConfig.getRecordingStrategy() == PERCENTAGE_CODE
                    && (recordingConfig.getPercentage() < 1 || recordingConfig.getPercentage() > 99)) {
                throw new TrafficException("录制策略选择百分比时，百分比填写错误");
            }
        }
    }


}
