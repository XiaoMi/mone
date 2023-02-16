package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.log.api.enums.*;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.dao.*;
import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
import com.xiaomi.mone.log.manager.model.pojo.*;
import com.xiaomi.mone.log.manager.model.vo.LogTailSendLokiVo;
import com.xiaomi.mone.log.manager.service.MiLogToolService;
import com.xiaomi.mone.log.model.LogtailConfig;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SUCCESS_MESSAGE;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/5 10:55
 */
@Slf4j
@Service
public class MiLogToolServiceImpl implements MiLogToolService {

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private LogstoreDao logstoreDao;

    @Resource
    private SpaceDao milogSpaceDao;

    @Resource
    private MilogConfigNacosServiceImpl milogConfigNacosService;

    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;

    @Resource
    private MilogMiddlewareConfigServiceImpl milogMiddlewareConfigService;

    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    @Resource
    private MilogConfigNacosServiceImpl milogConfigNacosServiceImpl;

    @Resource
    private LogTailServiceImpl logTailService;

    private Gson gson = new Gson();

    private static boolean test(MilogLogStoreDO milogLogstoreDO) {
        return null == milogLogstoreDO.getMqResourceId();
    }

    @Reference(interfaceClass = HeraAppService.class, group = "$dubbo.env.group", check = false)
    private HeraAppService heraAppService;

    @Override
    public Result<String> sendLokiMsg(Long tailId) {
        List<Long> tailIds = Lists.newArrayList();
        if (null != tailId) {
            tailIds.add(tailId);
        } else {
            tailIds = milogLogtailDao.queryAllIds();
        }
        tailIds.parallelStream().forEach(sendTailId -> {
            LogTailSendLokiVo logTailSendLokiVo = generateLokiVo(sendTailId);
            log.info("send data:{}", gson.toJson(logTailSendLokiVo));
        });
        return Result.success();
    }

    @Override
    public void fixAlertAppId() {
    }

    @Override
    public void fixMilogAlertTailId() {
    }

    @Override
    public String fixResourceLabel() {
        return milogMiddlewareConfigService.synchronousResourceLabel(null);
    }

    @Override
    public String fixLogStoreMqResourceId(Long storeId) {
        List<MilogLogStoreDO> doList = Lists.newArrayList();
        if (null != storeId) {
            doList.add(logstoreDao.queryById(storeId));
        } else {
            doList = logstoreDao.queryAll();
        }
        doList = doList.stream()
                .filter(MiLogToolServiceImpl::test)
                .collect(Collectors.toList());
        doList.forEach(milogLogstoreDO -> fixPerStoreMqResourceId(milogLogstoreDO));
        return SUCCESS_MESSAGE;
    }

    @Override
    public String fixNacosEsInfo(Long spaceId) {
        List<MilogLogStoreDO> storeDOS = Lists.newArrayList();
        if (null != spaceId) {
            storeDOS = logstoreDao.getMilogLogstoreBySpaceId(spaceId);
        } else {
            storeDOS = logstoreDao.queryAll();
        }
        List<MilogLogStoreDO> logStoreDOS = storeDOS.stream().filter(milogLogStoreDO -> {
            LogTypeEnum logTypeEnum = LogTypeEnum.type2enum(milogLogStoreDO.getLogType());
            if (logTypeEnum != LogTypeEnum.OPENTELEMETRY &&
                    logTypeEnum != LogTypeEnum.LOKI_APP_LOG &&
                    logTypeEnum != LogTypeEnum.MATRIX_ES_LOG) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        for (MilogLogStoreDO logStoreDO : logStoreDOS) {
            // 只改了中井云的，中井云的id为1
            if (!Objects.equals(1L, logStoreDO.getEsClusterId())) {
                continue;
            }

            List<MilogLogTailDo> logTailDos = milogLogtailDao.queryTailsByStoreId(logStoreDO.getId());
            milogConfigNacosServiceImpl.chooseCurrentEnvNacosSerevice(logStoreDO.getMachineRoom());

            for (MilogLogTailDo logTailDo : logTailDos) {
                try {
                    milogConfigNacosServiceImpl.publishNameSpaceConfig(logStoreDO.getMachineRoom(),
                            logTailDo.getSpaceId(), logTailDo.getStoreId(), logTailDo.getId(), OperateEnum.UPDATE_OPERATE.getCode(), "");
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    log.error("publishNameSpaceConfig error,logTailId:{},logStoreDO；{}", logTailDo.getId(), gson.toJson(logStoreDO), e);
                }
            }
        }
        log.info("fixNacosEsInfo end");
        return "success";
    }

    @Override
    public void fixLogTailLogAppId(String appName) {
        if (StringUtils.isNotBlank(appName)) {
            List<MilogAppTopicRelDO> appTopicRelDOS = milogAppTopicRelDao.queryAppbyName(appName);
            int count = 0;
            if (CollectionUtils.isNotEmpty(appTopicRelDOS)) {
                for (MilogAppTopicRelDO appTopicRelDO : appTopicRelDOS) {
                    List<MilogLogTailDo> logTailDos = milogLogtailDao.queryByAppId(appTopicRelDO.getId());
                    AppBaseInfo appBaseInfo = heraAppService.queryByAppId(appTopicRelDO.getAppId(), appTopicRelDO.getType());
                    for (MilogLogTailDo logTailDo : logTailDos) {
                        logTailDo.setMilogAppId(Long.valueOf(appBaseInfo.getBindId()));
                        logTailDo.setAppName(appBaseInfo.getAppName());
                        milogLogtailDao.update(logTailDo);
                        log.info("update num:{}", ++count);
                    }
                }
            }
        } else {
            fixTail();
        }
    }

    private void fixTail() {
        int offset = 0, rows = 100;
        int num = 0;
        while (true) {
            // 2、***分页取tail表mis应用***
            log.info("轮询应用,offset:{},rows:{}", offset, rows);
            List<MilogLogTailDo> logTailDos = milogLogtailDao.getLogTailByLimit(offset, rows);
            offset += rows;
            if (CollectionUtils.isNotEmpty(logTailDos)) {
                log.info("exit");
                break;
            }
            for (MilogLogTailDo logTailDo : logTailDos) {
                MilogAppTopicRelDO appTopicRelDO = milogAppTopicRelDao.queryById(logTailDo.getMilogAppId());
                AppBaseInfo appBaseInfo = heraAppService.queryByAppId(appTopicRelDO.getId(), appTopicRelDO.getType());
                logTailDo.setMilogAppId(Long.valueOf(appBaseInfo.getBindId()));
                logTailDo.setAppName(appBaseInfo.getAppName());
                milogLogtailDao.update(logTailDo);
                log.info("updated num:{}", ++num);
            }

        }
    }

    private void fixPerStoreMqResourceId(MilogLogStoreDO milogLogstoreDO) {
        // 查询默认的resourceId
        MilogMiddlewareConfig milogMiddlewareConfig = milogMiddlewareConfigDao.queryDefaultMqMiddlewareConfigMotorRoom(milogLogstoreDO.getMachineRoom());
        milogLogstoreDO.setMqResourceId(milogMiddlewareConfig.getId());
        logstoreDao.updateMilogLogStore(milogLogstoreDO);

    }

    private LogTailSendLokiVo generateLokiVo(Long sendTailId) {
        LogTailSendLokiVo logTailSendLokiVo = new LogTailSendLokiVo();
        logTailSendLokiVo.setTailId(sendTailId);
        MilogLogTailDo logtailDo = milogLogtailDao.queryById(sendTailId);
        logTailSendLokiVo.setTailName(logtailDo.getTail());
        LogSpaceDO milogSpace = milogSpaceDao.queryById(logtailDo.getSpaceId());
        logTailSendLokiVo.setSpaceId(logtailDo.getSpaceId());
        logTailSendLokiVo.setSpaceName(milogSpace.getSpaceName());
        MilogLogStoreDO logstoreDO = logstoreDao.queryById(logtailDo.getStoreId());
        logTailSendLokiVo.setStoreId(logtailDo.getStoreId());
        logTailSendLokiVo.setStoreName(logstoreDO.getLogstoreName());
        logTailSendLokiVo.setKeyList(Utils.parse2KeyAndTypeList(logstoreDO.getKeyList(), logstoreDO.getColumnTypeList()));
        LogtailConfig logtailConfig = milogConfigNacosService.assembleLogTailConfigs(sendTailId);
        logTailSendLokiVo.setConfig(logtailConfig);
        return logTailSendLokiVo;
    }

    public String batchCopyMultiMachineTail(Long targetStoreId, Long sourceStoreId) {
        if (null == targetStoreId || null == sourceStoreId) {
            throw new MilogManageException("targetStoreId and sourceStoreId can not be null");
        }
        List<MilogLogTailDo> logTailDos = milogLogtailDao.queryTailsByStoreId(sourceStoreId);
        logTailDos = logTailDos.stream()
                .filter(milogLogTailDo -> Objects.equals(MachineTypeEnum.PHYSICAL_MACHINE.getType(), milogLogTailDo.getMachineType()))
                .collect(Collectors.toList());
        for (MilogLogTailDo logTailDo : logTailDos) {
            MilogLogtailParam milogLogtailParam = buildLogTailParam(targetStoreId, logTailDo);
            log.info("LogTailParam:{}", gson.toJson(milogLogtailParam));
            if (CollectionUtils.isNotEmpty(milogLogtailParam.getIps())) {
                try {
                    logTailService.newMilogLogTail(milogLogtailParam);
                } catch (Exception e) {
                    log.error("batchCopyMultiMachineTail-newLogTail,LogTailParam:{},error:", gson.toJson(milogLogtailParam), e);
                }
            }
        }
        return SUCCESS_MESSAGE;
    }

    private MilogLogtailParam buildLogTailParam(Long targetStoreId, MilogLogTailDo logTailDo) {
        MilogLogtailParam milogLogtailParam = new MilogLogtailParam();
        MilogLogStoreDO milogLogStoreDO = logstoreDao.queryById(targetStoreId);
        MilogAppTopicRelDO milogAppTopicRelDO = milogAppTopicRelDao.queryById(logTailDo.getMilogAppId());
        milogLogtailParam.setSpaceId(milogLogStoreDO.getSpaceId());
        milogLogtailParam.setStoreId(targetStoreId);
        milogLogtailParam.setMilogAppId(logTailDo.getMilogAppId());
        milogLogtailParam.setAppId(logTailDo.getAppId());
        milogLogtailParam.setAppName(logTailDo.getAppName());
        milogLogtailParam.setIps(queryIps(milogLogStoreDO.getMachineRoom(), milogAppTopicRelDO));
        milogLogtailParam.setTail(String.format("%s_%s", logTailDo.getTail(), milogLogStoreDO.getMachineRoom()));
        milogLogtailParam.setParseType(LogParserEnum.CUSTOM_PARSE.getCode());
        milogLogtailParam.setParseScript("#$%");
        milogLogtailParam.setLogPath(logTailDo.getLogPath());
        milogLogtailParam.setValueList("message");
        milogLogtailParam.setTailRate(RateLimitEnum.RATE_LIMIT_MEDIUM.getRateLimit());
        milogLogtailParam.setUtime(Instant.now().toEpochMilli());
        milogLogtailParam.setCtime(Instant.now().toEpochMilli());
        milogLogtailParam.setLogSplitExpress(logTailDo.getLogSplitExpress());
        milogLogtailParam.setAppType(logTailDo.getAppType());
        milogLogtailParam.setMachineType(logTailDo.getMachineType());

        return milogLogtailParam;
    }

    private List<String> queryIps(String machineRoom, MilogAppTopicRelDO milogAppTopicRelDO) {
        LinkedHashMap<String, List<String>> nodeIPs = milogAppTopicRelDO.getNodeIPs();
        return nodeIPs.get(machineRoom);
    }
}
