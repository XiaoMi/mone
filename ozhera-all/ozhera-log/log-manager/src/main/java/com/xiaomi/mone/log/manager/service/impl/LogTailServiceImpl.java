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
package com.xiaomi.mone.log.manager.service.impl;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.model.vo.HeraEnvIpVo;
import com.xiaomi.mone.log.api.enums.*;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.utils.ManagerUtil;
import com.xiaomi.mone.log.manager.common.validation.HeraConfigValid;
import com.xiaomi.mone.log.manager.dao.*;
import com.xiaomi.mone.log.manager.mapper.MilogLogSearchSaveMapper;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.model.bo.LogTailParam;
import com.xiaomi.mone.log.manager.model.bo.MlogParseParam;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.pojo.*;
import com.xiaomi.mone.log.manager.model.vo.QuickQueryVO;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.mone.log.manager.service.LogTailService;
import com.xiaomi.mone.log.manager.service.bind.LogTypeProcessor;
import com.xiaomi.mone.log.manager.service.bind.LogTypeProcessorFactory;
import com.xiaomi.mone.log.manager.service.env.HeraEnvIpServiceFactory;
import com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentService;
import com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentServiceFactory;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.extension.resource.ResourceExtensionService;
import com.xiaomi.mone.log.manager.service.extension.resource.ResourceExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.extension.store.StoreExtensionService;
import com.xiaomi.mone.log.manager.service.extension.store.StoreExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.extension.tail.TailExtensionService;
import com.xiaomi.mone.log.manager.service.extension.tail.TailExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.nacos.impl.StreamConfigNacosProvider;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.mone.log.parse.LogParserFactory;
import com.xiaomi.mone.log.utils.IndexUtils;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;
import static com.xiaomi.mone.log.manager.common.Utils.getKeyValueList;

@Slf4j
@Service
public class LogTailServiceImpl extends BaseService implements LogTailService {

    @Resource
    private HeraEnvIpServiceFactory heraEnvIpServiceFactory;

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private MilogLogstoreDao logStoreDao;

    @Resource
    private MilogSpaceDao milogSpaceDao;

    @Resource
    private MilogConfigNacosServiceImpl milogConfigNacosServiceImpl;

    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;

    @Resource
    private MilogStreamServiceImpl milogStreamService;

    @Resource
    private MilogAppMiddlewareRelServiceImpl milogAppMiddlewareRelService;
    @Resource
    private MilogAppMiddlewareRelDao milogAppMiddlewareRelDao;
    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;
    @Resource
    private StreamConfigNacosProvider streamConfigNacosProvider;
    @Value("$log_type_mq_not_consume")
    private String logTypeMqNotConsume;
    @Value("$dubbo.miline.rpc.env")
    private String dubboMilineRpcEnv;
    @Value("$server.type")
    private String serverType;
    @Resource
    private HeraAppServiceImpl heraAppService;
    @Resource
    private HeraAppEnvServiceImpl heraAppEnvService;
    @Resource
    private MilogLogTemplateMapper milogLogTemplateMapper;
    @Resource
    private MilogLogSearchSaveMapper searchSaveMapper;
    @Resource
    private HeraConfigValid heraConfigValid;
    @Resource
    private LogTailServiceImpl logTailService;

    @Resource
    private LogTypeProcessorFactory logTypeProcessorFactory;

    private LogTypeProcessor logTypeProcessor;

    private TailExtensionService tailExtensionService;

    private MilogAgentService milogAgentService;

    private StoreExtensionService storeExtensionService;

    private ResourceExtensionService resourceExtensionService;

    public void init() {
        logTypeProcessorFactory.setMilogLogTemplateMapper(milogLogTemplateMapper);
        logTypeProcessor = logTypeProcessorFactory.getLogTypeProcessor();
        tailExtensionService = TailExtensionServiceFactory.getTailExtensionService();
        milogAgentService = MilogAgentServiceFactory.getAgentExtensionService();
        storeExtensionService = StoreExtensionServiceFactory.getStoreExtensionService();
        resourceExtensionService = ResourceExtensionServiceFactory.getResourceExtensionService();
    }

    private static boolean filterNameEmpty(MilogLogTailDo milogLogTailDo) {
        return StringUtils.isNotBlank(milogLogTailDo.getTail());
    }

    private QuickQueryVO applyQueryVO(MilogLogTailDo logTailDo) {
        Integer isFavourite = searchSaveMapper.isMyFavouriteTail(MoneUserContext.getCurrentUser().getUser(), logTailDo.getId().toString());
        return applyQueryVO(logTailDo, isFavourite);
    }

    private QuickQueryVO applyQueryVO(MilogLogTailDo logTailDo, Integer isFavourite) {
        return QuickQueryVO.builder().spaceId(logTailDo.getSpaceId()).storeId(logTailDo.getStoreId()).tailId(logTailDo.getId()).tailName(logTailDo.getTail()).isFavourite(isFavourite == null || isFavourite < 1 ? 0 : 1).build();
    }


    private void handleMqTailParam(LogTailParam param) {
        if (CollectionUtils.isEmpty(param.getMiddlewareConfig())) {
            return;
        }
        param.setMiddlewareConfig(param.getMiddlewareConfig().stream().filter(Objects::nonNull).collect(Collectors.toList()));
        if (CollectionUtils.isNotEmpty(param.getMiddlewareConfig()) && param.getMiddlewareConfig().size() == 3) {
            param.setMiddlewareConfigId(((Double) param.getMiddlewareConfig().get(1)).longValue());
            param.setTopicName((String) param.getMiddlewareConfig().get(2));
        } else if (CollectionUtils.isNotEmpty(param.getMiddlewareConfig()) && param.getMiddlewareConfig().size() == 2) {
            param.setMiddlewareConfigId(((Double) param.getMiddlewareConfig().get(1)).longValue());
            deleteMqRel(param.getMilogAppId(), param.getId());
        } else {
            deleteMqRel(param.getMilogAppId(), param.getId());
            // Take the default
            MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryDefaultMiddlewareConfig();
            param.setMiddlewareConfigId(config.getId());
        }
    }

    private void deleteMqRel(Long milogAppId, Long tailId) {
        milogAppMiddlewareRelDao.deleteRel(milogAppId, tailId);
    }

    @Override
    public Result<LogTailDTO> newMilogLogTail(LogTailParam param) {
        // Parameter validation
        String errorMsg = heraConfigValid.verifyLogTailParam(param);
        if (StringUtils.isNotEmpty(errorMsg)) {
            return new Result<>(CommonError.ParamsError.getCode(), errorMsg);
        }

        MilogLogStoreDO logStore = logStoreDao.queryById(param.getStoreId());
        if (logStore == null) {
            return new Result<>(CommonError.ParamsError.getCode(), "logStore not found");
        }

        String machineRoom = logStore.getMachineRoom();
        String tail = param.getTail();
        if (heraConfigValid.checkTailNameSame(tail, null, machineRoom)) {
            return new Result<>(CommonError.ParamsError.getCode(), "The alias is duplicated, please confirm and submit");
        }

        param.setValueList(IndexUtils.getNumberValueList(logStore.getKeyList(), param.getValueList()));

        // Parameter handling
        if (tailExtensionService.tailHandlePreprocessingSwitch(logStore, param)) {
            handleMqTailParam(param);
        }

        AppBaseInfo appBaseInfo = getAppBaseInfo(param);

        MilogLogTailDo mt = buildLogTailDo(param, logStore, appBaseInfo, MoneUserContext.getCurrentUser().getUser());
        MilogLogTailDo milogLogtailDo = milogLogtailDao.add(mt);
        boolean supportedConsume = logTypeProcessor.supportedConsume(logStore.getLogType());
        try {
            if (null != milogLogtailDo) {
                if (tailExtensionService.bindMqResourceSwitch(param.getAppType())) {
                    // tail creates a successful binding and MQ relationship
                    tailExtensionService.defaultBindingAppTailConfigRel(milogLogtailDo.getId(), param.getMilogAppId(), null == param.getMiddlewareConfigId() ? logStore.getMqResourceId() : param.getMiddlewareConfigId(), param.getTopicName(), param.getBatchSendSize());
                    /** Synchronize the information after creating a tail**/
                    tailExtensionService.sendMessageOnCreate(param, mt, param.getMilogAppId(), supportedConsume);
                } else if (tailExtensionService.bindPostProcessSwitch(param.getStoreId())) {
                    tailExtensionService.defaultBindingAppTailConfigRelPostProcess(milogLogtailDo.getSpaceId(), milogLogtailDo.getStoreId(), milogLogtailDo.getId(), milogLogtailDo.getMilogAppId(), logStore.getMqResourceId());
                    /** Synchronize the information after creating a tail**/
                    tailExtensionService.sendMessageOnCreate(param, mt, param.getMilogAppId(), supportedConsume);
                }
                LogTailDTO ret = new LogTailDTO();
                ret.setId(mt.getId());
                return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
            } else {
                log.warn("[MilogLogtailService.newMilogLogtail] creator MilogLogtail err,milogAppId:{},logpath:{}", param.getMilogAppId(), param.getLogPath());
                return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage());
            }
        } catch (Exception e) {
            log.warn("[MilogLogtailService.newMilogLogtail] creator MilogLogtail err,milogAppId:{},logpath:{}", param.getMilogAppId(), param.getLogPath(), e);
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage());
        }
    }

    @Override
    public void sengMessageNewTail(LogTailParam param, MilogLogTailDo milogLogtailDo, MilogLogStoreDO milogLogStore) {
        // tail creates a successful binding and middleware relationship
        milogAppMiddlewareRelService.bindingTailConfigRel(milogLogtailDo.getId(), param.getMilogAppId(), param.getMiddlewareConfigId(), param.getTopicName());
        /** Synchronize the information after creating a tail**/
        tailExtensionService.sendMessageOnCreate(param, milogLogtailDo, param.getMilogAppId(), logTypeProcessor.supportedConsume(milogLogStore.getLogType()));
    }

    @Override
    public MilogLogTailDo buildLogTailDo(LogTailParam param, MilogLogStoreDO milogLogStore, AppBaseInfo appBaseInfo, String creator) {
        MilogLogTailDo mt = logTailParam2Do(param, milogLogStore, appBaseInfo);
        wrapBaseCommon(mt, OperateEnum.ADD_OPERATE, creator);
        return mt;
    }

    private boolean checkTailNameSame(String tailName, Long id, String machineRoom) {
        // Verify the log file with the same name
        List<MilogLogTailDo> logtailDoList = milogLogtailDao.queryTailNameExists(tailName, machineRoom);
        if (null == id) {
            return CollectionUtils.isNotEmpty(logtailDoList);
        } else {
            if (CollectionUtils.isEmpty(logtailDoList)) {
                return false;
            }
            MilogLogTailDo milogLogtailDo = logtailDoList.get(logtailDoList.size() - 1);
            return !milogLogtailDo.getId().equals(id);
        }
    }

    @Override
    public void sengMessageToAgent(Long milogAppId, MilogLogTailDo logtailDo) {
        milogAgentService.publishIncrementConfig(logtailDo.getId(), milogAppId, logtailDo.getIps());
    }

    @Override
    public void sengMessageToStream(MilogLogTailDo mt, Integer type) {
        handleNaocsConfigByMotorRoom(mt, MachineRegionEnum.CN_MACHINE.getEn(), type, ProjectTypeEnum.MIONE_TYPE.getCode());
    }

    @Override
    public void handleNaocsConfigByMotorRoom(MilogLogTailDo mt, String motorRoomEn, Integer type, Integer projectType) {
        milogConfigNacosServiceImpl.chooseCurrentEnvNacosSerevice(motorRoomEn);
        if (OperateEnum.ADD_OPERATE.getCode().equals(type) || OperateEnum.UPDATE_OPERATE.getCode().equals(type)) {
            milogConfigNacosServiceImpl.publishStreamConfig(mt.getSpaceId(), type, projectType, motorRoomEn);
        }
        milogConfigNacosServiceImpl.publishNameSpaceConfig(motorRoomEn, mt.getSpaceId(), mt.getStoreId(), mt.getId(), type, "");
    }

    @Override
    public boolean deleteConfigRemote(Long spaceId, Long id, String motorRoomEn, LogStructureEnum logStructureEnum) {
        Assert.notNull(spaceId, "deleteConfigRemote spaceId can not null");
        Assert.notNull(id, "deleteConfigRemote id can not null");
        milogConfigNacosServiceImpl.chooseCurrentEnvNacosSerevice(motorRoomEn);
        if (LogStructureEnum.SPACE == logStructureEnum) {
            milogConfigNacosServiceImpl.publishStreamConfig(spaceId, OperateEnum.DELETE_OPERATE.getCode(), null, motorRoomEn);
            return true;
        }
        if (LogStructureEnum.STORE == logStructureEnum) {
            milogConfigNacosServiceImpl.publishNameSpaceConfig(motorRoomEn, spaceId, id, null, OperateEnum.DELETE_OPERATE.getCode(), logStructureEnum.getCode());
            return true;
        }
        MilogLogTailDo tailDo = milogLogtailDao.queryById(id);
        milogConfigNacosServiceImpl.publishNameSpaceConfig(motorRoomEn, spaceId, tailDo.getStoreId(), id, OperateEnum.DELETE_OPERATE.getCode(), logStructureEnum.getCode());
        return true;
    }

    @Override
    public Result<LogTailDTO> getMilogLogtailById(Long id) {
        MilogLogTailDo tail = milogLogtailDao.queryById(id);
        if (null != tail) {
            // handle value list
            MilogLogStoreDO milogLogstore = logStoreDao.queryById(tail.getStoreId());
            if (null != milogLogstore && StringUtils.isNotEmpty(milogLogstore.getKeyList())) {
                String keyList = milogLogstore.getKeyList();
                String valueList = getKeyValueList(keyList, tail.getValueList());
                tail.setValueList(valueList);
            }
            // Handle filterconf to rateLimit
            LogTailDTO logTailDTO = milogLogtailDO2DTO(tail);
            if (tailExtensionService.decorateTailDTOValId(logTailDTO.getAppType().intValue())) {
                decorateMilogTailDTO(logTailDTO);
            }
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), logTailDTO);
        } else {
            return new Result<>(CommonError.UnknownError.getCode(), "tail not found");
        }
    }

    private void decorateMilogTailDTO(LogTailDTO logTailDTO) {
        Optional<AppBaseInfo> optionalAppBaseInfo = Optional.ofNullable(heraAppService.queryById(logTailDTO.getMilogAppId()));
        optionalAppBaseInfo.ifPresent(appBaseInfo -> {
            logTailDTO.setSource(appBaseInfo.getPlatformType().toString());
            List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(logTailDTO.getMilogAppId(), null, logTailDTO.getId());
            if (CollectionUtils.isNotEmpty(milogAppMiddlewareRels)) {
                MilogAppMiddlewareRel milogAppMiddlewareRel = milogAppMiddlewareRels.get(0);
                MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryById(milogAppMiddlewareRel.getMiddlewareId());
                logTailDTO.setMiddlewareConfig(Arrays.asList(Long.valueOf(config.getType()), config.getId(), milogAppMiddlewareRel.getConfig().getTopic()));
                logTailDTO.setBatchSendSize(milogAppMiddlewareRel.getConfig().getBatchSendSize());
            }
        });
    }

    @Override
    public Result<Map<String, Object>> getMilogLogBypage(Long storeId, int page, int pagesize) {
        List<MilogLogTailDo> ret = milogLogtailDao.getMilogLogtailByPage(storeId, page, pagesize);
        ArrayList<LogTailDTO> res = Lists.newArrayList();
        ret.forEach(v -> {
            res.add(milogLogtailDO2DTO(v));
        });
        Map<String, Object> result = new HashMap<>();
        result.put("list", res);
        result.put("total", milogLogtailDao.getTailCount(storeId));
        result.put("page", page);
        result.put("pageSize", pagesize);
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), result);
    }

    @Override
    public Result<Map<String, Object>> getLogTailCountByStoreId(Long storeId) {
        Map<String, Object> result = new HashMap<>();
        result.put("count", milogLogtailDao.getTailCount(storeId));
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), result);
    }

    @Override
    public Result<List<LogTailDTO>> getMilogLogtailByIds(List<Long> ids) {
        List<MilogLogTailDo> ret = milogLogtailDao.getMilogLogtail(ids);
        ArrayList<LogTailDTO> res = Lists.newArrayList();
        ret.forEach(v -> {
            res.add(milogLogtailDO2DTO(v));
        });
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), res);
    }

    @Override
    public Result<Void> updateMilogLogTail(LogTailParam param) {
        MilogLogTailDo ret = milogLogtailDao.queryById(param.getId());
        if (ret == null) {
            return new Result<>(CommonError.ParamsError.getCode(), "tail does not exist");
        }
        // Parameter validation
        String errorMsg = heraConfigValid.verifyLogTailParam(param);
        if (StringUtils.isNotEmpty(errorMsg)) {
            return new Result<>(CommonError.ParamsError.getCode(), errorMsg);
        }

        MilogLogStoreDO logStoreDO = logStoreDao.queryById(param.getStoreId());
        if (logStoreDO == null) {
            return new Result<>(CommonError.ParamsError.getCode(), "Logstore does not exist");
        }

        // Process the MqTailParam parameter
        if (tailExtensionService.tailHandlePreprocessingSwitch(logStoreDO, param)) {
            handleMqTailParam(param);
        }

        // Check for duplicate aliases
        String tail = param.getTail();
        Long id = param.getId();
        String machineRoom = logStoreDO.getMachineRoom();
        if (checkTailNameSame(tail, id, machineRoom)) {
            return new Result<>(CommonError.ParamsError.getCode(), "The alias is duplicated, please confirm and submit");
        }

        // handle value list
        param.setValueList(IndexUtils.getNumberValueList(logStoreDO.getKeyList(), param.getValueList()));
        AppBaseInfo appBaseInfo = getAppBaseInfo(param);
        // tailRate to filterConf
        FilterDefine filterDefine = FilterDefine.consRateLimitFilterDefine(param.getTailRate());
        List<FilterDefine> defines = new ArrayList<>();
        if (filterDefine != null) {
            defines.add(filterDefine);
        }

        MilogLogTailDo milogLogtailDo = logTailParam2Do(param, logStoreDO, appBaseInfo);
        wrapBaseCommon(milogLogtailDo, OperateEnum.UPDATE_OPERATE);
        boolean isSucceed = milogLogtailDao.update(milogLogtailDo);

        if (isSucceed) {
            Integer appType = param.getAppType();
            boolean processSwitch = tailExtensionService.bindPostProcessSwitch(param.getStoreId());
            if (tailExtensionService.bindMqResourceSwitch(appType) || processSwitch) {
                if (null != param.getMiddlewareConfigId()) {
                    tailExtensionService.defaultBindingAppTailConfigRel(param.getId(), param.getMilogAppId(), null == param.getMiddlewareConfigId() ? logStoreDO.getMqResourceId() : param.getMiddlewareConfigId(), param.getTopicName(), param.getBatchSendSize());
                }
                try {
                    List<String> oldIps = ret.getIps();
                    boolean supportedConsume = logTypeProcessor.supportedConsume(logStoreDO.getLogType());
                    tailExtensionService.updateSendMsg(milogLogtailDo, oldIps, supportedConsume);
                } catch (Exception e) {
                    new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage(), "Push configuration error");
                }
            }
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
        } else {
            log.warn("[MilogLogtailService.updateMilogLogtail] update MilogLogtail err,id:{}", param.getId());
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage());
        }
    }

    private AppBaseInfo getAppBaseInfo(LogTailParam param) {
        return heraAppService.queryById(param.getMilogAppId());
    }

    public void compareChangeDelIps(Long tailId, String logPath, List<String> newIps, List<String> oldIps) {
        if (CollectionUtils.isEmpty(oldIps)) {
            return;
        }
        List<String> delIps = oldIps.stream().filter(s -> !newIps.contains(s)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(delIps)) {
            milogAgentService.delLogCollDirectoryByIp(tailId, ManagerUtil.getPhysicsDirectory(logPath), delIps);
        }
    }

    @Override
    public Result<Void> deleteLogTail(Long id) {
        MilogLogTailDo milogLogtailDo = milogLogtailDao.queryById(id);
        if (null == milogLogtailDo) {
            return new Result<>(CommonError.ParamsError.getCode(), "tail does not exist");
        }
        //Precondition check before deletion.
        String validMsg = tailExtensionService.deleteCheckProcessPre(id);
        if (StringUtils.isNotEmpty(validMsg)) {
            return new Result<>(CommonError.ParamsError.getCode(), validMsg);
        }

        MilogLogStoreDO logStoreDO = logStoreDao.queryById(milogLogtailDo.getStoreId());
        deleteRemoteConfig(id, logStoreDO);

        if (milogLogtailDao.deleteMilogLogtail(id)) {
            if (storeExtensionService.isNeedSendMsgType(logStoreDO.getLogType())) {
                CompletableFuture.runAsync(() -> sendMessageOnDelete(milogLogtailDo, logStoreDO));
            }
            tailExtensionService.logTailDelPostProcess(logStoreDO, milogLogtailDo);
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
        } else {
            log.warn("[LogTailService.deleteLogTail] delete LogTail error,id:{}", id);
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage());
        }
    }

    private void deleteRemoteConfig(Long id, MilogLogStoreDO storeDO) {
        if (logTypeProcessor.supportedConsume(storeDO.getLogType())) {
            logTailService.deleteConfigRemote(storeDO.getSpaceId(), id, storeDO.getMachineRoom(), LogStructureEnum.TAIL);
        }
    }

    @Override
    public void sendMessageOnDelete(MilogLogTailDo mt, MilogLogStoreDO logStoreDO) {
        log.info("Send delete configuration information，mt:{}", GSON.toJson(mt));
        resourceExtensionService.deleteMqResourceProcessing(mt, logStoreDO);
        /**
         * Send configuration information ---log-agent
         */
        milogAgentService.publishIncrementDel(mt.getId(), mt.getMilogAppId(), mt.getIps());
        /**
         * Delete the relationship
         */
        milogAppMiddlewareRelDao.deleteRel(mt.getMilogAppId(), mt.getId());
    }

    @Override
    public Result<List<MapDTO>> getAppInfoByName(String appName, Integer type) {
        return Result.success(queryAllApps(appName, type));
    }

    /**
     * Only 200 are returned in full
     *
     * @param appName
     * @param type
     * @return
     */
    private List<MapDTO> queryAllApps(String appName, Integer type) {
        List<MapDTO> mapDTOS = queryAppInfo(appName, type);
//        if (StringUtils.isEmpty(appName)) {
//            return mapDTOS.stream().limit(200).collect(Collectors.toList());
//        }
        return mapDTOS;
    }

    private List<MapDTO> queryAppInfo(String appName, Integer type) {
        List<AppBaseInfo> apps = heraAppService.queryAppInfoWithLog(appName, type);
        List<MapDTO> mapDTOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(apps)) {
            mapDTOList = apps.stream().map(response -> {
                MapDTO mapDTO = new MapDTO();
                mapDTO.setLabel(String.format("%s_%s", response.getPlatformName(), response.getAppName()));
                mapDTO.setValue(response.getId());
                mapDTO.setKey(response.getBindId());
                return mapDTO;
            }).collect(Collectors.toList());
        }
        return mapDTOList;
    }

    /**
     * If the application is milog-agent, get a list of all machines through an additional interface
     *
     * @param milogAppId
     * @param deployWay
     * @return
     */
    @Override
    public Result<List<MilogAppEnvDTO>> getEnInfosByAppId(Long milogAppId, Integer deployWay) {
        if (null == milogAppId) {
            return Result.failParam("The parameter cannot be empty");
        }
        AppBaseInfo appBaseInfo = heraAppService.queryById(milogAppId);
        if (null == appBaseInfo) {
            return Result.failParam("The app does not exist");
        }
        List<MilogAppEnvDTO> appEnvDTOList = tailExtensionService.getEnInfosByAppId(appBaseInfo, milogAppId, deployWay);
        return Result.success(appEnvDTOList);
    }

    @Override
    public Result<List<String>> getTailNamesBystoreId(String tail, Integer appType, Long id) {
        List<MilogLogTailDo> milogLogTailDos = milogLogtailDao.getMilogLogtailByIdsAndName(new ArrayList<Long>() {{
            add(id);
        }}, tail, appType);
        List<String> ret = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(milogLogTailDos)) {
            ret = milogLogTailDos.stream().filter(LogTailServiceImpl::filterNameEmpty).map(MilogLogTailDo::getTail).distinct().collect(Collectors.toList());
        }
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    @Override
    public Result<List<MapDTO<String, String>>> tailRatelimit() {
        ArrayList<MapDTO<String, String>> ret = new ArrayList<>();
        ret.add(MapDTO.Of("Collect quickly - Collect as soon as possible, taking up a certain amount of resources", RateLimitEnum.RATE_LIMIT_FAST.getRateLimit()));
        ret.add(MapDTO.Of("Regular collection", RateLimitEnum.RATE_LIMIT_MEDIUM.getRateLimit()));
        ret.add(MapDTO.Of("Slow collection", RateLimitEnum.RATE_LIMIT_SLOW.getRateLimit()));
        ret.add(MapDTO.Of("Stop collecting", RateLimitEnum.RATE_LIMIT_NONE.getRateLimit()));
        return Result.success(ret);
    }

    /***
     * miline Dynamic scaling and contracting
     * @param projectInfo
     */
    @Override
    public void dockerScaleDynamic(DockerScaleBo projectInfo) {
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryByAppAndEnv(projectInfo.getProjectId(), projectInfo.getEnvId());
        if (CollectionUtils.isNotEmpty(projectInfo.getIps()) && CollectionUtils.isNotEmpty(milogLogtailDos)) {
            log.info("Dynamically expand the configuration in the current environment,projectId:{},envId:{},config:{}", projectInfo.getProjectId(), projectInfo.getEnvId(), GSON.toJson(milogLogtailDos));
            for (MilogLogTailDo milogLogtailDo : milogLogtailDos) {
                List<String> exitIps = milogLogtailDo.getIps();
                List<String> newIps = projectInfo.getIps();
                if (!CollectionUtils.isEqualCollection(exitIps, newIps)) {
                    //1.update config
                    milogLogtailDo.setIps(newIps);
                    milogLogtailDao.update(milogLogtailDo);
                    //2.send msg
                    compareIpToHandle(milogLogtailDo.getId(), milogLogtailDo.getLogPath(), exitIps, newIps);
                }
            }
        }
    }

    public void compareIpToHandle(Long tailId, String logPath, List<String> exitIps, List<String> newIps) {
        List<String> expandIps = newIps.stream().filter(ip -> !exitIps.contains(ip)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(expandIps)) {
            // Scaling --- synchronous configuration
            expandIps.forEach(ip -> {
                milogAgentService.configIssueAgent("", ip, "");
                milogStreamService.configIssueStream(ip);
            });
        }
        List<String> stopFileCollIps = exitIps.stream().filter(ip -> !newIps.contains(ip)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(stopFileCollIps)) {
            milogAgentService.delLogCollDirectoryByIp(tailId, ManagerUtil.getPhysicsDirectory(logPath), stopFileCollIps);
        }
    }

    private MilogLogTailDo logTailParam2Do(LogTailParam logTailParam, MilogLogStoreDO logStoreDO, AppBaseInfo appBaseInfo) {
        MilogLogTailDo milogLogtailDo = new MilogLogTailDo();
        milogLogtailDo.setId(logTailParam.getId());
        milogLogtailDo.setTail(logTailParam.getTail());
        milogLogtailDo.setSpaceId(logTailParam.getSpaceId());
        milogLogtailDo.setStoreId(logTailParam.getStoreId());
        milogLogtailDo.setMilogAppId(logTailParam.getMilogAppId());
        milogLogtailDo.setAppId(logTailParam.getMilogAppId());
        milogLogtailDo.setAppName(logTailParam.getAppName());
        if (null != appBaseInfo) {
            milogLogtailDo.setMilogAppId(logTailParam.getMilogAppId());
            milogLogtailDo.setAppId(Long.valueOf(appBaseInfo.getBindId()));
            milogLogtailDo.setAppName(appBaseInfo.getAppName());
        }
        milogLogtailDo.setEnvId(logTailParam.getEnvId());
        milogLogtailDo.setEnvName(logTailParam.getEnvName());
        milogLogtailDo.setMachineType(logTailParam.getMachineType());
        Integer appType = logTailParam.getAppType();
        milogLogtailDo.setAppType(appType);
        milogLogtailDo.setParseType(logTailParam.getParseType());
        milogLogtailDo.setParseScript(StringUtils.isEmpty(logTailParam.getParseScript()) ? DEFAULT_TAIL_SEPARATOR : logTailParam.getParseScript());
        milogLogtailDo.setLogPath(logTailParam.getLogPath().trim());
        milogLogtailDo.setLogSplitExpress((StringUtils.isNotEmpty(logTailParam.getLogSplitExpress()) ? logTailParam.getLogSplitExpress().trim() : ""));
        milogLogtailDo.setValueList(logTailParam.getValueList());
        FilterDefine filterDefine = FilterDefine.consRateLimitFilterDefine(logTailParam.getTailRate());
        if (filterDefine != null) {
            milogLogtailDo.setFilter(Arrays.asList(filterDefine));
        }
        tailExtensionService.logTailDoExtraFiled(milogLogtailDo, logStoreDO, logTailParam);
        milogLogtailDo.setDeployWay(logTailParam.getDeployWay());
        if (logStoreDO.isMatrixAppStore()) {
            milogLogtailDo.setDeploySpace((StringUtils.isNotEmpty(logTailParam.getDeploySpace()) ? logTailParam.getDeploySpace().trim() : ""));
        }
        milogLogtailDo.setFirstLineReg((StringUtils.isNotEmpty(logTailParam.getFirstLineReg()) ? logTailParam.getFirstLineReg() : ""));
        return milogLogtailDo;
    }

    @Override
    public LogTailDTO milogLogtailDO2DTO(MilogLogTailDo milogLogtailDo) {
        if (milogLogtailDo == null) {
            return null;
        }
        LogTailDTO logTailDTO = new LogTailDTO();

        logTailDTO.setId(milogLogtailDo.getId());
        logTailDTO.setCtime(milogLogtailDo.getCtime());
        logTailDTO.setUtime(milogLogtailDo.getUtime());
        logTailDTO.setSpaceId(milogLogtailDo.getSpaceId());
        logTailDTO.setStoreId(milogLogtailDo.getStoreId());
        logTailDTO.setMilogAppId(milogLogtailDo.getMilogAppId());
        logTailDTO.setAppId(milogLogtailDo.getAppId());
        logTailDTO.setAppName(milogLogtailDo.getAppName());
        logTailDTO.setEnvId(milogLogtailDo.getEnvId());
        logTailDTO.setEnvName(milogLogtailDo.getEnvName());
        List<String> list = milogLogtailDo.getIps();
        if (CollectionUtils.isNotEmpty(list)) {
            logTailDTO.setIps(list);
        }
        logTailDTO.setTail(milogLogtailDo.getTail());
        logTailDTO.setParseType(milogLogtailDo.getParseType());
        logTailDTO.setParseScript(milogLogtailDo.getParseScript());
        logTailDTO.setLogPath(milogLogtailDo.getLogPath());
        logTailDTO.setLogSplitExpress(milogLogtailDo.getLogSplitExpress());
        logTailDTO.setValueList(milogLogtailDo.getValueList());
        logTailDTO.setAppType(milogLogtailDo.getAppType());
        logTailDTO.setMachineType(milogLogtailDo.getMachineType());
        logTailDTO.setMotorRooms(milogLogtailDo.getMotorRooms());
        // filterconf to tailRate
        logTailDTO.setTailRate(RateLimitEnum.consTailRate(milogLogtailDo.getFilter()));
        logTailDTO.setDeployWay(milogLogtailDo.getDeployWay());
        logTailDTO.setDeploySpace(milogLogtailDo.getDeploySpace());
        logTailDTO.setFirstLineReg(milogLogtailDo.getFirstLineReg());
        return logTailDTO;
    }

    @Override
    public Result<List<MapDTO>> queryAppByStoreId(Long storeId) {
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryAppIdByStoreId(storeId);
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            return Result.success(milogLogtailDos.stream().map(milogLogtailDo -> MapDTO.Of(milogLogtailDo.getAppName(), milogLogtailDo.getMilogAppId())).distinct().collect(Collectors.toList()));
        }
        return Result.success(Lists.newArrayList());
    }

    @Override
    public Result<List<AppTypeTailDTO>> queryAppTailByStoreId(Long storeId) {
        if (null == storeId) {
            return Result.failParam("The storeId cannot be empty");
        }
        List<AppTypeTailDTO> appTypeTailDTOS = Lists.newArrayList();
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryAppIdByStoreId(storeId);
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            Map<Integer, List<MilogLogTailDo>> appTypeMap = milogLogtailDos.stream().collect(Collectors.groupingBy(MilogLogTailDo::getAppType));
            MilogLogStoreDO milogLogstoreDO = logStoreDao.queryById(storeId);
            String nameEn = milogLogstoreDO.getMachineRoom();

            for (Map.Entry<Integer, List<MilogLogTailDo>> listEntry : appTypeMap.entrySet()) {
                AppTypeTailDTO appTypeTailDTO = new AppTypeTailDTO();
                appTypeTailDTO.setAppType(listEntry.getKey());
                appTypeTailDTO.setAppTypName(ProjectTypeEnum.queryTypeByCode(listEntry.getKey()));
                List<AppTypeTailDTO.TailApp> tailAppList = Lists.newArrayList();
                AppTypeTailDTO.TailApp tailApp = new AppTypeTailDTO.TailApp();
                tailApp.setNameEn(nameEn);
                tailApp.setNameCn(CommonExtensionServiceFactory.getCommonExtensionService().getMachineRoomName(nameEn));
                List<MilogLogTailDo> logtailDos = listEntry.getValue();
                if (CollectionUtils.isNotEmpty(logtailDos)) {
                    List<AppTypeTailDTO.TailInfo> tailInfos = logtailDos.stream().map(milogLogtailDo -> {
                        AppTypeTailDTO.TailInfo tailInfo = new AppTypeTailDTO.TailInfo();
                        tailInfo.setId(milogLogtailDo.getId());
                        tailInfo.setTailName(milogLogtailDo.getTail());
                        return tailInfo;
                    }).collect(Collectors.toList());
                    tailApp.setTailInfos(tailInfos);
                }
                tailAppList.add(tailApp);
                appTypeTailDTO.setTailAppList(tailAppList);
                appTypeTailDTOS.add(appTypeTailDTO);
            }
        }
        return Result.success(appTypeTailDTOS);
    }

    @Override
    public Result<List<MilogLogStoreDO>> queryLogStoreByRegionEn(String nameEn) {
        if (StringUtils.isEmpty(nameEn)) {
            return Result.failParam("storeId Cannot be empty");
        }
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryStoreIdByRegionNameEN(nameEn);
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            return Result.success(milogLogtailDos.stream().map(milogLogtailDo -> logStoreDao.queryById(milogLogtailDo.getStoreId())).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getLogstoreName()))), ArrayList::new)));
        }
        return Result.success(Lists.newArrayList());
    }

    @Override
    public Result<List<LogTailDTO>> getTailByStoreId(Long storeId) {
        if (storeId == null) {
            return Result.failParam("storeId Cannot be empty");
        }
        List<MilogLogTailDo> tailList = milogLogtailDao.getMilogLogtailByStoreId(storeId);
        List<LogTailDTO> dtoList = new ArrayList<>();
        LogTailDTO logTailDTO;
        for (MilogLogTailDo milogLogtailDo : tailList) {
            logTailDTO = milogLogtailDO2DTO(milogLogtailDo);
            if (milogLogtailDo != null) {
                dtoList.add(logTailDTO);
            }
        }
        return Result.success(dtoList);
    }

    @Override
    public Result<Object> parseScriptTest(MlogParseParam mlogParseParam) {
        String checkMsg = checkParseParam(mlogParseParam);
        if (StringUtils.isNotEmpty(checkMsg)) {
            return Result.failParam(checkMsg);
        }
        MilogLogStoreDO logstoreDO = logStoreDao.queryById(mlogParseParam.getStoreId());
        if (null == logstoreDO) {
            return Result.fail(CommonError.NOT_EXISTS_DATA.getCode(), "logStore does not exist");
        }
        String keyList = Utils.parse2KeyAndTypeList(logstoreDO.getKeyList(), logstoreDO.getColumnTypeList());
        String valueList = IndexUtils.getNumberValueList(logstoreDO.getKeyList(), mlogParseParam.getValueList());
        Long currentStamp = Instant.now().toEpochMilli();
        try {
            LogParser logParser = LogParserFactory.getLogParser(mlogParseParam.getParseType(), keyList, valueList, mlogParseParam.getParseScript());
            Map<String, Object> parseMsg = logParser.parseSimple(mlogParseParam.getMsg(), currentStamp);
            return Result.success(parseMsg);
        } catch (Exception e) {
            log.info("Parsing configuration information errors: Configuration information:{}", GSON.toJson(mlogParseParam), e);
        }
        return Result.success("Resolution error Please check the configuration information");
    }

    public Result<Object> parseExample(MlogParseParam mlogParseParam) {
        String checkMsg = heraConfigValid.checkParseExampleParam(mlogParseParam);
        if (StringUtils.isNotEmpty(checkMsg)) {
            return Result.failParam(checkMsg);
        }
        try {
            LogParser logParser = LogParserFactory.getLogParser(mlogParseParam.getParseType(), "", "", mlogParseParam.getParseScript());
            List<String> parsedLog = logParser.parseLogData(mlogParseParam.getMsg());
            return Result.success(parsedLog);
        } catch (Exception e) {
            log.info("Parsing configuration information errors: Configuration information:{}", GSON.toJson(mlogParseParam), e);
        }
        return Result.success("Resolution error Please check the configuration information");
    }

    private String checkParseParam(MlogParseParam mlogParseParam) {
        StringBuilder sb = new StringBuilder();
        if (null == mlogParseParam.getStoreId()) {
            sb.append("store cannot be empty;");
        }
        if (null == mlogParseParam.getParseScript()) {
            sb.append("The parsing script cannot be empty;");
        }
        if (null == mlogParseParam.getValueList()) {
            sb.append("Indexing rules cannot be empty;");
        }
        if (null == mlogParseParam.getMsg()) {
            sb.append("Log information cannot be empty;");
        }
        return sb.toString();
    }

    @Override
    public Result<List<QuickQueryVO>> quickQueryByApp(Long milogAppId) {
        if (null == milogAppId) {
            return Result.failParam("The milog App ID cannot be empty");
        }
        List<MilogLogTailDo> milogLogTailDos = milogLogtailDao.getLogTailByMilogAppId(milogAppId);
        List<QuickQueryVO> quickQueryVOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(milogLogTailDos)) {
            quickQueryVOS = milogLogTailDos.stream().map(this::applyQueryVO).collect(Collectors.toList());
            wrapStoreSpaceName(quickQueryVOS);
        }
        return Result.success(quickQueryVOS);
    }

    private void wrapStoreSpaceName(List<QuickQueryVO> quickQueryVOS) {
        List<Long> storeIds = quickQueryVOS.stream().map(QuickQueryVO::getStoreId).collect(Collectors.toList());
        List<MilogLogStoreDO> milogLogStoreDOS = logStoreDao.queryByIds(storeIds);
        List<Long> spaceIds = quickQueryVOS.stream().map(QuickQueryVO::getSpaceId).collect(Collectors.toList());
        List<MilogSpaceDO> milogSpaceDOS = milogSpaceDao.queryByIds(spaceIds);
        quickQueryVOS.stream().map(quickQueryVO -> {
            milogLogStoreDOS.stream().filter(milogLogsSoreDO -> Objects.equals(quickQueryVO.getStoreId(), milogLogsSoreDO.getId())).findFirst().map(milogLogStoreDO -> {
                quickQueryVO.setStoreName(milogLogStoreDO.getLogstoreName());
                return true;
            });
            milogSpaceDOS.stream().filter(milogSpaceDO -> Objects.equals(quickQueryVO.getSpaceId(), milogSpaceDO.getId())).findFirst().map(milogSpaceDO -> {
                quickQueryVO.setSpaceName(milogSpaceDO.getSpaceName());
                return true;
            });
            return quickQueryVO;
        }).collect(Collectors.toList());
    }

    @Override
    public void machineIpChange(HeraEnvIpVo heraEnvIpVo) {
        List<MilogLogTailDo> logTailDos = milogLogtailDao.queryByMilogAppAndEnvId(heraEnvIpVo.getHeraAppId(), heraEnvIpVo.getId());
        if (CollectionUtils.isNotEmpty(logTailDos)) {
            log.info("Dynamically expand the configuration in the current environment，heraAppEnvVo:{}，logTailDos:{}", GSON.toJson(heraEnvIpVo), GSON.toJson(logTailDos));
            for (MilogLogTailDo milogLogtailDo : logTailDos) {
                List<String> exitIps = milogLogtailDo.getIps();
                List<String> newIps = heraEnvIpVo.getIpList();
                if (!CollectionUtils.isEqualCollection(exitIps, newIps)) {
                    //1.Modify the configuration
                    milogLogtailDo.setIps(newIps);
                    milogLogtailDo.setUtime(Instant.now().toEpochMilli());
                    milogLogtailDo.setUpdater(DEFAULT_JOB_OPERATOR);
                    milogLogtailDao.updateIps(milogLogtailDo);
                    //2.Send a message
                    compareIpToHandle(milogLogtailDo.getId(), milogLogtailDo.getLogPath(), exitIps, newIps);
                }
            }
        }
    }

    @Override
    public Result<QuickQueryVO> queryAppStore(Long appId, Integer platFormCode) {
        if (null == appId || null == platFormCode) {
            return Result.failParam("The parameter cannot be empty");
        }
        AppBaseInfo appBaseInfo = heraAppService.queryByAppIdPlatFormType(appId.toString(), platFormCode);
        if (null == appBaseInfo) {
            log.info("queryAppStore app not exist,milogAppId:{},platFormCode:{}", appId, platFormCode);
            return Result.success(new QuickQueryVO());
        }
        List<MilogLogTailDo> logTailDos = milogLogtailDao.queryByAppId(appId, appBaseInfo.getId().longValue());
        if (CollectionUtils.isEmpty(logTailDos)) {
            return Result.success(new QuickQueryVO());
        }
        return Result.success(applyQueryVO(logTailDos.get(logTailDos.size() - 1), null));
    }
}
