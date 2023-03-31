package com.xiaomi.mone.log.manager.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.log.api.enums.*;
import com.xiaomi.mone.log.api.model.meta.MQConfig;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.helper.MilogAccessHelper;
import com.xiaomi.mone.log.manager.dao.*;
import com.xiaomi.mone.log.manager.domain.Tpc;
import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.bo.AccessMilogParam;
import com.xiaomi.mone.log.manager.model.bo.AppTopicParam;
import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.*;
import com.xiaomi.mone.log.manager.model.vo.AccessMiLogVo;
import com.xiaomi.mone.log.manager.model.vo.LogPathTopicVo;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.mone.log.manager.service.MilogAppTopicService;
import com.xiaomi.mone.log.manager.service.RocketMqService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.pager.Pager;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;
import static com.xiaomi.mone.log.manager.common.Utils.getKeyValueList;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/27 11:20
 */
@Service
@Slf4j
public class MilogAppTopicServiceImpl extends BaseService implements MilogAppTopicService {

    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;
    @Resource
    private RocketMqService rocketMqService;
    @Resource
    private MilogLogTailDao milogLogtailDao;
    @Resource
    private MilogLogstoreDao logstoreDao;
    @Resource
    private LogStoreServiceImpl logStoreService;
    @Resource
    private MilogSpaceDao milogSpaceDao;
    @Resource
    private LogSpaceServiceImpl milogSpaceService;
    @Resource
    private LogTailServiceImpl logTailService;
    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;
    @Resource
    private MilogAgentServiceImpl milogAgentService;
    @Resource
    private MilogAppMiddlewareRelDao milogAppMiddlewareRelDao;

    @Resource
    private MilogAccessHelper milogAccessHelper;

    @Resource
    private MilogAppMiddlewareRelServiceImpl milogAppMiddlewareRelService;

    @Value("$app.env")
    private String env;

    @Resource
    private Gson gson;

    @Resource
    private Tpc tpc;

    @Reference(interfaceClass = HeraAppService.class, group = "$dubbo.env.group", check = false)
    private HeraAppService heraAppService;

    @Override
    public Result<PageInfo<MilogAppConfigTailDTO>> queryAppTopicList(AppTopicParam param) {
        log.info("查询前的时间：{}", SimpleDateFormat.getInstance().format(new Date()));
        Long currentTime = System.currentTimeMillis();
        Integer totalCount = milogAppTopicRelDao.queryAppTopicPageCount(handleParamToCondition(param));
        List<MilogAppTopicRelDO> topicRels = milogAppTopicRelDao.queryAppTopicList(handleParamToCondition(param), new Pager(param.getPage(), param.getPageSize()));
        List<MilogAppConfigTailDTO> milogAppConfigTailDTOS = topicRels.stream().map(milogAppTopicRel -> {
            MilogAppConfigTailDTO milogAppConfigTailDTO = new MilogAppConfigTailDTO();
            BeanUtil.copyProperties(milogAppTopicRel, milogAppConfigTailDTO);

            List<MilogAppConfigTailDTO.ConfigTailDTO> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByAMilogAppId(milogAppTopicRel.getId());
            milogAppConfigTailDTO.setConfigTailDTOList(milogAppMiddlewareRels);
            return milogAppConfigTailDTO;
        }).collect(Collectors.toList());
        log.info("查询后的时间：{}", SimpleDateFormat.getInstance().format(new Date()));
        log.info("查询数据库花费的时间:{}", System.currentTimeMillis() - currentTime);
        PageInfo<MilogAppConfigTailDTO> pageInfo = new PageInfo<>(param.getPage(), param.getPageSize(), totalCount, milogAppConfigTailDTOS);
        return Result.success(pageInfo);
    }

    @Override
    public Result<String> createTopic(Long appId, String appName) {
        if (null == appId) {
            return Result.failParam("appId不能为空");
        }
        if (StringUtils.isEmpty(appName)) {
            return Result.failParam("appName不能为空");
        }
//        String topicNameSimple = createTopicNameSimple(appId, appName);
        String topicNameSimple = Utils.assembleTopicName(appId, appName);
        String createTopicResult = rocketMqService.httpCreateTopic(topicNameSimple);
        if (Constant.SUCCESS_MESSAGE.equalsIgnoreCase(createTopicResult)) {
            // topic 修改权限
            rocketMqService.updateTopiSubGroupAuth(topicNameSimple);
            //修改修改库中的
            milogAppTopicRelDao.updateTopicName(appId, topicNameSimple);
            updateTopicSendMsg(appId);
            return Result.success(topicNameSimple);
        } else {
            return Result.fail(CommonError.SERVER_ERROR.getCode(), "创建topic失败:" + createTopicResult);
        }
    }

    @Override
    public Result<String> updateExistsTopic(Long id, String existTopic) {
        if (null == id) {
            return Result.failParam("id不能为空");
        }
        if (StringUtils.isEmpty(existTopic)) {
            return Result.failParam("existTopic不能为空");
        }
        boolean isExist = topicIsExist(existTopic);
        if (!isExist) {
            return Result.fail(CommonError.SERVER_ERROR.getCode(), "existTopic不存在");
        }
        milogAppTopicRelDao.updateAppTopicRelMqConfigById(id, existTopic);
        MilogAppTopicRelDO milogAppTopicRel = milogAppTopicRelDao.queryById(id);
        if (null != milogAppTopicRel) {
            // topic权限绑定
            rocketMqService.updateTopiSubGroupAuth(existTopic);
            updateTopicSendMsg(milogAppTopicRel.getAppId());
        }
        return Result.success("成功");
    }

    private void updateTopicSendMsg(Long appId) {
        List<MilogLogTailDo> milogLogtailByAppId = milogLogtailDao.queryByAppId(appId);
        milogLogtailByAppId.forEach(milogLogtailDo -> {
            logTailService.updateSendMsg(milogLogtailDo, Lists.newArrayList());
        });
    }

    @Override
    public Result<List<MapDTO>> queryAllExistTopicList() {
        Set<String> existTopics = rocketMqService.queryExistTopic();
        List<MapDTO> collect = existTopics.stream().map(s -> MapDTO.Of(s, s)).collect(Collectors.toList());
        return Result.success(collect);
    }

    @Override
    public Result<String> delTopicRecord(Long appId) {
        milogAppTopicRelDao.deleteAppTopicRelDb(appId, "", null, MoneUserContext.getCurrentUser().getZone());
        return Result.success();
    }

    @Override
    public Result<String> delTopicRecordAll() {
        milogAppTopicRelDao.delTopicRecordAll();
        return Result.success();
    }

    @Override
    public Result<List<MilogAppOpenVo>> queryAllMilogAppList() {
        List<AppBaseInfo> appBaseInfos = queryAllAccessMilogAppList();
        if (CollectionUtils.isEmpty(appBaseInfos)) {
            return Result.success();
        }
        List<MilogAppOpenVo> collect = appBaseInfos.stream().map(s -> MilogAppOpenVo.builder()
                .label(s.getAppName() + String.format("(%s)", s.getPlatformName())).value(s.getId().longValue())
                .source(s.getPlatformName().toString()).build()).collect(Collectors.toList());
        return Result.success(collect);
    }

    private List<AppBaseInfo> queryAllAccessMilogAppList() {
        List<AppBaseInfo> appBaseInfos = heraAppService.queryAllExistsApp();
        List<Integer> accessAppIds = milogLogtailDao.queryAllAppId();
        if (CollectionUtils.isNotEmpty(appBaseInfos) && CollectionUtils.isNotEmpty(accessAppIds)) {
            return appBaseInfos.stream()
                    .filter(appBaseInfo -> accessAppIds.contains(appBaseInfo.getId()))
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public List<LogPathTopicVo> queryTopicConfigByAppId(Long milogAppId) {
        List<LogPathTopicVo> logPathTopicVos = Lists.newArrayList();
        if (null == milogAppId) {
            return logPathTopicVos;
        }
//        MilogAppTopicRelDO milogAppTopicRel = milogAppTopicRelDao.queryById(milogAppId);
        AppBaseInfo appBaseInfo = heraAppService.queryById(milogAppId);
        if (null == appBaseInfo) {
            return Lists.newArrayList();
        }
        List<MilogLogTailDo> logtailDos = milogLogtailDao.getLogTailByMilogAppId(milogAppId);
        if (CollectionUtils.isNotEmpty(logtailDos)) {
            logPathTopicVos.addAll(logtailDos.stream().map(milogLogtailDo -> {
                //查询关系
                List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(appBaseInfo.getId().longValue(), null, milogLogtailDo.getId());
                if (CollectionUtils.isNotEmpty(milogAppMiddlewareRels)) {
                    MilogAppMiddlewareRel milogAppMiddlewareRel = milogAppMiddlewareRels.get(milogAppMiddlewareRels.size() - 1);
                    MQConfig mqConfigDest = generateConfig(milogAppMiddlewareRel, milogLogtailDo);
                    // 处理value list
                    MilogLogStoreDO milogLogstoreDO = logstoreDao.queryById(milogLogtailDo.getStoreId());
                    String valueList = "";
                    if (null != milogLogstoreDO) {
                        String keyList = milogLogstoreDO.getKeyList();
                        valueList = getKeyValueList(keyList, milogLogtailDo.getValueList());
                    }
                    return LogPathTopicVo.builder().logPath(milogLogtailDo.getLogPath()).source(appBaseInfo.getPlatformName())
                            .parseScript(milogLogtailDo.getParseScript())
                            .valueList(valueList)
                            .serveAlias(milogLogtailDo.getTail())
                            .tailId(milogLogtailDo.getId()).mqConfig(mqConfigDest).build();
                }
                return null;
            }).collect(Collectors.toList()));
        }
        return logPathTopicVos.stream().filter(logPathTopicVo -> null != logPathTopicVo).collect(Collectors.toList());
    }

    @Override
    public Boolean synchronousMisApp(List<MisAppInfoDTO> data) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("初始化mis项目开始,共有{}个", data.size());
        AtomicInteger count = new AtomicInteger();
        data.stream().forEach(misAppInfoDTO -> {
            int increment = count.getAndIncrement();
            log.debug("mis应用同步,总有{}个应用，开始执行第{}个，还剩下{}个", data.size(), increment, data.size() - increment);
            MilogAppTopicRelDO milogAppTopicRel = getMilogAppTopicRel(misAppInfoDTO);
            MilogAppTopicRelDO appTopicRel = milogAppTopicRelDao.queryIsExists(milogAppTopicRel);
            if (null == appTopicRel) {
                milogAppTopicRelDao.insert(milogAppTopicRel);
            } else {
                appTopicRel.setTreeIds(milogAppTopicRel.getTreeIds());
                milogAppTopicRel.setId(appTopicRel.getId());
                milogAppTopicRelDao.update(milogAppTopicRel);
                // 查询是否接入了日志
                List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryMisTailByAppId(misAppInfoDTO.getService_id());
                if (CollectionUtils.isNotEmpty(milogLogtailDos) && null != milogAppTopicRel.getNodeIPs()) {
                    List<String> amsIPs = milogAppTopicRel.getNodeIPs().get(MachineRegionEnum.CN_MACHINE.getEn());
                    milogLogtailDos.forEach(milogLogtailDo -> {
                        List<String> ips = milogLogtailDo.getIps();
                        if (CollectionUtils.isNotEmpty(amsIPs) && CollectionUtils.isNotEmpty(ips)) {
                            List<String> increasedIps = increasedIps(ips, amsIPs);
                            if (CollectionUtils.isNotEmpty(increasedIps)) {
                                // 发送新增事件
                                milogAgentService.publishIncrementConfig(milogLogtailDo.getId(), milogLogtailDo.getMilogAppId(), increasedIps);
                                // 修改tail
                                ips.addAll(increasedIps);
                                milogLogtailDo.setIps(ips);
                                milogLogtailDao.update(milogLogtailDo);
                            }
                        }
                    });
                }
            }
            milogAppTopicRelDao.insertNoExists(getMilogAppTopicRel(misAppInfoDTO));
        });
        stopwatch.stop();
        log.info("初始化mis项目结束，花费时间：{} s", stopwatch.elapsed().getSeconds());
        return null;
    }

    @Override
    public Boolean synchronousRadarApp(List<RadarAppInfoDTO> data) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("初始化radar项目开始,共有{}个", data.size());
        AtomicInteger count = new AtomicInteger();
        data.stream().forEach(radarAppInfoDTO -> {
            int increment = count.getAndIncrement();
            log.debug("radar应用同步,总有{}个应用，开始执行第{}个，还剩下{}个", data.size(), increment, data.size() - increment);
            MilogAppTopicRelDO milogAppTopicRel = getMilogAppTopicRel(radarAppInfoDTO);
            MilogAppTopicRelDO appTopicRel = milogAppTopicRelDao.queryIsExists(milogAppTopicRel);
            if (null == appTopicRel) {
                milogAppTopicRelDao.insert(milogAppTopicRel);
            } else {
                appTopicRel.setTreeIds(milogAppTopicRel.getTreeIds());
                milogAppTopicRelDao.update(appTopicRel);
            }
        });
        stopwatch.stop();
        log.info("初始化radar项目结束，花费时间：{} s", stopwatch.elapsed().getSeconds());
        return null;
    }

    @Override
    public Result<AccessMiLogVo> accessToMilog(AccessMilogParam milogParam) {
        //1.param valid
        String errorMsg = milogAccessHelper.validParam(milogParam);
        if (StringUtils.isNotBlank(errorMsg)) {
            return Result.failParam(errorMsg);
        }
        log.info("mifaas access log param:{}", gson.toJson(milogParam));
        // 2.处理应用信息
        MilogAppTopicRelDO topicRelDO = handleMilogAppTopicRel(milogParam);
        //3.handle space
        MilogSpaceDO spaceDO = handleMilogSpace(milogParam.getSpaceName(), milogParam.getAppCreator());
        //4.handle store
        MilogLogStoreDO logStoreDO = handleMilogStore(spaceDO.getId(), milogParam);
        //5.handle tail
        MilogLogTailDo logTailDo = handleMilogTail(logStoreDO.getSpaceId(), logStoreDO, milogParam, topicRelDO);
        log.info("mifaas access log tail:{}", gson.toJson(logTailDo));
        return Result.success(generateAccessMiLogVo(logTailDo.getSpaceId(), logTailDo.getStoreId(), logTailDo.getId(), logTailDo.getTail()));
    }

    private AccessMiLogVo generateAccessMiLogVo(Long spaceId, Long storeId, Long tailId, String tailName) {
        return AccessMiLogVo.builder()
                .spaceId(spaceId)
                .storeId(storeId)
                .tailId(tailId)
                .tailName(tailName).build();
    }

    private MilogLogTailDo handleMilogTail(Long spaceId, MilogLogStoreDO logStoreDO, AccessMilogParam milogParam,
                                           MilogAppTopicRelDO appInfo) {
        String tailName = milogParam.getEnvName();
        MilogLogtailParam logTailParam = MilogLogtailParam.builder()
                .spaceId(spaceId)
                .storeId(logStoreDO.getId())
                .milogAppId(appInfo.getId())
                .envId(milogParam.getEnvId())
                .envName(milogParam.getEnvName())
                .tail(tailName)
                .parseType(LogParserEnum.SEPARATOR_PARSE.getCode())
                .parseScript(DEFAULT_TAIL_SEPARATOR)
                .logPath(milogParam.getLogPath().trim())
                .valueList(DEFAULT_VALUE_LIST)
                .appType(ProjectTypeEnum.MIONE_TYPE.getCode())
//                .deployWay(DeployWayEnum.MILINE.getCode())
                .build();
        MilogLogTailDo logTailDo = logTailService.buildLogTailDo(logTailParam, logStoreDO, null, milogParam.getAppCreator());
        // 查询是否存在，如果存在是否有变化，变化则修改
        MilogLogTailDo milogLogTailDo = milogLogtailDao.queryServerlessTailByFuncId(spaceId, logStoreDO.getId(),
                appInfo.getId(), milogParam.getEnvId());
        if (null != milogLogTailDo) {
            logTailDo.setId(milogLogTailDo.getId());
            logTailDo.setStoreId(milogLogTailDo.getStoreId());
            if (!milogAccessHelper.compareSame(milogLogTailDo, logTailDo)) {
                milogLogtailDao.update(logTailDo);
                mifaasConfigSyncToNacos(logTailDo, appInfo.getId());
            }
        } else {
            milogLogtailDao.newMilogLogtail(logTailDo);
            mifaasConfigSyncToNacos(logTailDo, appInfo.getId());
        }
        return logTailDo;
    }


    private void mifaasConfigSyncToNacos(MilogLogTailDo logTailDo, Long milogAppId) {
        //写入nacos
        MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryDefaultMiddlewareConfig();
        milogAppMiddlewareRelService.bindingTailConfigRel(logTailDo.getId(), milogAppId,
                config.getId(), "");
        logTailService.sengMessageToStream(logTailDo, OperateEnum.ADD_OPERATE.getCode());
    }


    private MilogLogStoreDO handleMilogStore(Long spaceId, AccessMilogParam milogParam) {
        String storeName = milogParam.getStoreName();
        String machineRoom = milogParam.getMachineRoom();
        MilogLogStoreDO milogLogStoreDO = logstoreDao.queryStoreBySpaceStoreName(spaceId, storeName, machineRoom);
        if (null != milogLogStoreDO) {
            return milogLogStoreDO;
        }
        return logstoreDao.insert(
                logStoreService.buildLogStoreEsInfo(
                        buildLogStoreParam(spaceId, storeName, machineRoom), milogParam.getAppCreator()));
    }


    private LogStoreParam buildLogStoreParam(Long spaceId, String storeName, String machineRoom) {
        return LogStoreParam.builder()
                .spaceId(spaceId)
                .logstoreName(storeName)
                .storePeriod(7)
                .shardCnt(1)
                .keyList(DEFAULT_KEY_LIST)
                .columnTypeList(DEFAULT_COLUMN_TYPE_LIST)
                .logType(LogTypeEnum.APP_LOG_MULTI.getType())
                .machineRoom(machineRoom)
                .build();
    }

    private MilogAppTopicRelDO handleMilogAppTopicRel(AccessMilogParam milogParam) {
        List<MilogAppTopicRelDO> appInfos = milogAppTopicRelDao.queryAppInfo(
                milogParam.getAppId(), milogParam.getAppName(), null, generateAppSource(
                        milogParam.getAppType(), milogParam.getAppTypeText()));
        if (CollectionUtils.isNotEmpty(appInfos)) {
            return appInfos.get(appInfos.size() - 1);
        } else {
            return saveAppInfo(milogParam);
        }
    }

    private MilogAppTopicRelDO saveAppInfo(AccessMilogParam milogParam) {
        MilogAppTopicRelDO milogAppTopicRelDO = new MilogAppTopicRelDO();
        milogAppTopicRelDO.setAppId(milogParam.getAppId());
        milogAppTopicRelDO.setAppName(milogParam.getAppName());
        milogAppTopicRelDO.setSource(generateAppSource(milogParam.getAppType(), milogParam.getAppTypeText()));
        milogAppTopicRelDO.setType(generateAppType(milogParam.getAppType(), milogParam.getAppTypeText()));
        wrapBaseCommon(milogAppTopicRelDO, OperateEnum.ADD_OPERATE, milogParam.getAppCreator());
        milogAppTopicRelDO.setCreator(milogParam.getAppCreator());
        milogAppTopicRelDO.setCtime(milogParam.getAppCreatTime());
        milogAppTopicRelDao.insert(milogAppTopicRelDO);
        return milogAppTopicRelDO;
    }

    private Integer generateAppType(Integer appType, String appTypeText) {
        return ProjectTypeEnum.MIONE_TYPE.getCode();
    }

    private String generateAppSource(Integer appType, String appTypeText) {
        return ProjectSourceEnum.ONE_SOURCE.getSource();
    }

    private MilogSpaceDO handleMilogSpace(String spaceName, String creator) {
        List<MilogSpaceDO> milogSpaceDOS = milogSpaceDao.queryBySpaceName(spaceName);
        if (CollectionUtils.isNotEmpty(milogSpaceDOS)) {
            return milogSpaceDOS.get(milogSpaceDOS.size() - 1);
        } else {
            MilogSpaceDO spaceDO = milogSpaceService.buildMiLogSpace(
                    MilogSpaceParam.builder()
                            .spaceName(spaceName)
                            .description(String.format(DEFAULT_SPACE_DESC, spaceName))
                            .build(), creator);
            spaceDO.setCreator(DEFAULT_OPERATOR);
            spaceDO = milogSpaceDao.insert(spaceDO);
            tpc.saveSpacePerm(spaceDO, creator);
            return spaceDO;
        }
    }

    private List<String> increasedIps(List<String> origin, List<String> source) {
        return source.stream().filter(s -> !origin.contains(s)).collect(Collectors.toList());
    }

    @NotNull
    private MilogAppTopicRelDO getMilogAppTopicRel(MisAppInfoDTO misAppInfoDTO) {
        MilogAppTopicRelDO milogAppTopicRel = new MilogAppTopicRelDO();
        milogAppTopicRel.setAppId(misAppInfoDTO.getService_id());
        milogAppTopicRel.setAppName(misAppInfoDTO.getService());
        milogAppTopicRel.setCtime(Instant.now().toEpochMilli());
        milogAppTopicRel.setUtime(Instant.now().toEpochMilli());
        milogAppTopicRel.setOperator(misAppInfoDTO.getUser());
        milogAppTopicRel.setSource(ProjectSourceEnum.ONE_SOURCE.getSource());
//        milogAppTopicRel.setType(ProjectTypeEnum.MIS_TYPE.getCode());
        milogAppTopicRel.setTreeIds(misAppInfoDTO.getTree_id());
        milogAppTopicRel.setNodeIPs(misAppInfoDTO.getCluster_info());
        return milogAppTopicRel;
    }

    private MilogAppTopicRelDO getMilogAppTopicRel(RadarAppInfoDTO radarAppInfoDTO) {
        MilogAppTopicRelDO milogAppTopicRel = new MilogAppTopicRelDO();
        milogAppTopicRel.setAppId(radarAppInfoDTO.getId());
        milogAppTopicRel.setAppName(radarAppInfoDTO.getName());
        milogAppTopicRel.setCtime(Instant.now().toEpochMilli());
        milogAppTopicRel.setUtime(Instant.now().toEpochMilli());
        milogAppTopicRel.setOperator(radarAppInfoDTO.getMembers().stream().map(RadarAppInfoDTO.Member::getUserId).collect(Collectors.joining(SYMBOL_COMMA)));
        milogAppTopicRel.setSource(ProjectSourceEnum.ONE_SOURCE.getSource());
        milogAppTopicRel.setType(ProjectTypeEnum.MIONE_TYPE.getCode());
        milogAppTopicRel.setTreeIds(Lists.newArrayList(radarAppInfoDTO.getId()));
        milogAppTopicRel.setNodeIPs(Maps.newLinkedHashMap());
        return milogAppTopicRel;
    }

    private MQConfig generateConfig(MilogAppMiddlewareRel milogAppMiddlewareRel, MilogLogTailDo milogLogtailDo) {
        MilogMiddlewareConfig milogMiddlewareConfig = milogMiddlewareConfigDao.queryById(milogAppMiddlewareRel.getMiddlewareId());
        MQConfig mqConfigDest = new MQConfig();
        mqConfigDest.setType(MiddlewareEnum.queryNameByCode(milogMiddlewareConfig.getType()));
        String clusterInfo = "";
        if (MiddlewareEnum.ROCKETMQ.getCode().equals(milogMiddlewareConfig.getType())) {
            clusterInfo = milogMiddlewareConfig.getNameServer();
        }
        mqConfigDest.setClusterInfo(clusterInfo);
        MilogAppMiddlewareRel.Config config = milogAppMiddlewareRel.getConfig();
        mqConfigDest.setAk(milogMiddlewareConfig.getAk());
        mqConfigDest.setSk(milogMiddlewareConfig.getSk());
        mqConfigDest.setTopic(config.getTopic());
        String tag = Utils.createTag(milogLogtailDo.getSpaceId(), milogLogtailDo.getStoreId(), milogLogtailDo.getId());
        mqConfigDest.setProducerGroup(DEFAULT_CONSUMER_GROUP + tag);
        mqConfigDest.setTag(tag);
        mqConfigDest.setPartitionCnt(config.getPartitionCnt());
        mqConfigDest.setEsConsumerGroup(config.getEsConsumerGroup());
        mqConfigDest.setBatchSendSize(config.getBatchSendSize());
        return mqConfigDest;
    }

    private static String timeToStr() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMddhh");
        return ldt.format(formatter);
    }

    private boolean topicIsExist(String existTopic) {
        Set<String> existTopics = rocketMqService.queryExistTopic();
        if (existTopics.contains(existTopic)) {
            return true;
        }
        return false;
    }

    private Condition handleParamToCondition(AppTopicParam param) {
        Cnd cnd = Cnd.NEW();
        if (null != param.getAppId()) {
            cnd.and("app_id", EQUAL_OPERATE, param.getAppId());
        }
        if (StringUtils.isNotEmpty(MoneUserContext.getCurrentUser().getZone())) {
            cnd.and("source", EQUAL_OPERATE, MoneUserContext.getCurrentUser().getZone());
        }
        if (StringUtils.isNotEmpty(param.getAppName())) {
            cnd.and("app_name", "like", "%" + param.getAppName() + "%");
        }
        return cnd;
    }

}
