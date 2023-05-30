package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.model.vo.HeraEnvIpVo;
import com.xiaomi.mone.log.api.enums.*;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.validation.HeraConfigValid;
import com.xiaomi.mone.log.manager.dao.*;
import com.xiaomi.mone.log.manager.mapper.MilogLogSearchSaveMapper;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
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
    private MilogLogstoreDao logstoreDao;

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

    Gson gson = new Gson();
    private static final String MIS_LOGPATH_PREFIX = "/home/work/logs";


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
        return QuickQueryVO.builder()
                .spaceId(logTailDo.getSpaceId())
                .storeId(logTailDo.getStoreId())
                .tailId(logTailDo.getId())
                .tailName(logTailDo.getTail())
                .isFavourite(isFavourite == null || isFavourite < 1 ? 0 : 1)
                .build();
    }

    private String verifyMilogLogtailParam(MilogLogtailParam param) {
        if (null == param.getMilogAppId()) {
            return "选择的应用不能为空";
        }
        if (null == param || StringUtils.isBlank(param.getLogPath())) {
            return "路径不能为空";
        }
        if (null == param.getSpaceId()) {
            return "spaceId不能为空";
        }
        if (null == param.getStoreId()) {
            return "storeId不能为空";
        }
        String path = param.getLogPath();
        MilogLogStoreDO milogLogstoreDO = logstoreDao.queryById(param.getStoreId());
        if (Objects.equals("staging", serverType) &&
                !MachineRegionEnum.CN_MACHINE.getEn().equals(milogLogstoreDO.getMachineRoom())) {
            return "测试环境只支持大陆机房，其它机房由于网络问题不支持";
        }
        if (path.equals("/home/work/log/") || path.equals("/home/work/log") || path.startsWith("/home/work/log") && path.split("/").length < 4) {
            return "日志路径错误，请确认后提交";
        }
        if (Objects.equals(ProjectTypeEnum.MIONE_TYPE.getCode(), param.getAppType())) {
            // 校验同名日志文件
            List<MilogLogTailDo> appLogTails = milogLogtailDao.queryByMilogAppAndEnv(param.getMilogAppId(), param.getEnvId());
            for (int i = 0; i < appLogTails.size() && null == param.getId(); i++) {
                if (appLogTails.get(i).getLogPath().equals(param.getLogPath())) {
                    return "当前部署环境该文件" + param.getLogPath() + "已配置日志采集,别名为：" + appLogTails.get(i).getTail();
                }
            }
        }
        return "";
    }

    private void handleMqTailParam(MilogLogtailParam param) {
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
            // 取默认的
            MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryDefaultMiddlewareConfig();
            param.setMiddlewareConfigId(config.getId());
        }
    }

    private void deleteMqRel(Long milogAppId, Long tailId) {
        milogAppMiddlewareRelDao.deleteRel(milogAppId, tailId);
    }

    @Override
    public Result<MilogTailDTO> newMilogLogTail(MilogLogtailParam param) {
        // 参数校验
        String errorMsg = heraConfigValid.verifyLogTailParam(param);
        if (StringUtils.isNotEmpty(errorMsg)) {
            return new Result<>(CommonError.ParamsError.getCode(), errorMsg);
        }

        MilogLogStoreDO logStore = logstoreDao.queryById(param.getStoreId());
        if (logStore == null) {
            return new Result<>(CommonError.ParamsError.getCode(), "logStore不存在");
        }

        String machineRoom = logStore.getMachineRoom();
        String tail = param.getTail();
        if (heraConfigValid.checkTailNameSame(tail, null, machineRoom)) {
            return new Result<>(CommonError.ParamsError.getCode(), "别名重复，请确定后提交");
        }

        param.setValueList(IndexUtils.getNumberValueList(logStore.getKeyList(), param.getValueList()));

        // 参数处理
        if (tailExtensionService.tailHandlePreprocessingSwitch(logStore, param)) {
            handleMqTailParam(param);
        }

        AppBaseInfo appBaseInfo = getAppBaseInfo(param);

        MilogLogTailDo mt = buildLogTailDo(param, logStore, appBaseInfo, MoneUserContext.getCurrentUser().getUser());
        MilogLogTailDo milogLogtailDo = milogLogtailDao.newMilogLogtail(mt);
        boolean supportedConsume = logTypeProcessor.supportedConsume(logStore.getLogType());
        try {
            if (null != milogLogtailDo) {
                if (tailExtensionService.bindMqResourceSwitch(param.getAppType())) {
                    // tail创建成功 绑定和mq的关系
                    tailExtensionService.defaultBindingAppTailConfigRel(
                            milogLogtailDo.getId(), param.getMilogAppId(),
                            null == param.getMiddlewareConfigId() ? logStore.getMqResourceId() : param.getMiddlewareConfigId(),
                            param.getTopicName(), param.getBatchSendSize());
                    /** 创建完tail后同步信息**/
                    tailExtensionService.sendMessageOnCreate(param, mt, param.getMilogAppId(), supportedConsume);
                } else if (tailExtensionService.bindPostProcessSwitch(param.getStoreId())) {
                    tailExtensionService.defaultBindingAppTailConfigRelPostProcess(milogLogtailDo.getSpaceId(), milogLogtailDo.getStoreId(), milogLogtailDo.getId(), milogLogtailDo.getMilogAppId(), logStore.getMqResourceId());
                    /** 创建完tail后同步信息**/
                    tailExtensionService.sendMessageOnCreate(param, mt, param.getMilogAppId(), supportedConsume);
                }
                MilogTailDTO ret = new MilogTailDTO();
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

    private MilogAppTopicRelDO getMilogAppTopicRelDO(MilogLogtailParam param) {
        return milogAppTopicRelDao.queryById(param.getMilogAppId());
    }

    @Override
    public void sengMessageNewTail(MilogLogtailParam param, MilogLogTailDo milogLogtailDo, MilogLogStoreDO milogLogStore) {
        // tail创建成功 绑定和中间件的关系
        milogAppMiddlewareRelService.bindingTailConfigRel(milogLogtailDo.getId(), param.getMilogAppId(), param.getMiddlewareConfigId(), param.getTopicName());
        /** 创建完tail后同步信息**/
        tailExtensionService.sendMessageOnCreate(param, milogLogtailDo, param.getMilogAppId(), logTypeProcessor.supportedConsume(milogLogStore.getLogType()));
    }

    @Override
    public MilogLogTailDo buildLogTailDo(MilogLogtailParam param, MilogLogStoreDO milogLogStore, AppBaseInfo appBaseInfo, String creator) {
        MilogLogTailDo mt = logTailParam2Do(param, milogLogStore, appBaseInfo);
        wrapBaseCommon(mt, OperateEnum.ADD_OPERATE, creator);
        return mt;
    }

    private boolean checkTailNameSame(String tailName, Long id, String machineRoom) {
        // 校验同名日志文件
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
            milogConfigNacosServiceImpl.publishStreamConfig(mt.getSpaceId(), mt.getId(), type, projectType, motorRoomEn);
        }
        milogConfigNacosServiceImpl.publishNameSpaceConfig(motorRoomEn, mt.getSpaceId(), mt.getStoreId(), mt.getId(), type, "");
    }

    @Override
    public Result<MilogTailDTO> getMilogLogtailById(Long id) {
        MilogLogTailDo tail = milogLogtailDao.queryById(id);
        if (null != tail) {
            // 处理value list
            MilogLogStoreDO milogLogstore = logstoreDao.queryById(tail.getStoreId());
            if (null != milogLogstore && StringUtils.isNotEmpty(milogLogstore.getKeyList())) {
                String keyList = milogLogstore.getKeyList();
                String valueList = getKeyValueList(keyList, tail.getValueList());
                tail.setValueList(valueList);
            }
            // 处理filterconf 转 rateLimit
            MilogTailDTO milogTailDTO = milogLogtailDO2DTO(tail);
            if (tailExtensionService.decorateTailDTOValId(milogTailDTO.getAppType().intValue())) {
                decorateMilogTailDTO(milogTailDTO);
            }
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), milogTailDTO);
        } else {
            return new Result<>(CommonError.UnknownError.getCode(), "tail 不存在");
        }
    }

    private void decorateMilogTailDTO(MilogTailDTO milogTailDTO) {
        Optional<AppBaseInfo> optionalAppBaseInfo = Optional.ofNullable(heraAppService.queryById(milogTailDTO.getMilogAppId()));
        optionalAppBaseInfo.ifPresent(appBaseInfo -> {
            milogTailDTO.setSource(appBaseInfo.getPlatformType().toString());
            List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(milogTailDTO.getMilogAppId(), null, milogTailDTO.getId());
            if (CollectionUtils.isNotEmpty(milogAppMiddlewareRels)) {
                MilogAppMiddlewareRel milogAppMiddlewareRel = milogAppMiddlewareRels.get(0);
                MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryById(milogAppMiddlewareRel.getMiddlewareId());
                milogTailDTO.setMiddlewareConfig(Arrays.asList(Long.valueOf(config.getType()), config.getId(), milogAppMiddlewareRel.getConfig().getTopic()));
            }
        });
    }

    @Override
    public Result<Map<String, Object>> getMilogLogBypage(Long storeId, int page, int pagesize) {
        List<MilogLogTailDo> ret = milogLogtailDao.getMilogLogtailByPage(storeId, page, pagesize);
        ArrayList<MilogTailDTO> res = Lists.newArrayList();
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
    public Result<List<MilogTailDTO>> getMilogLogtailByIds(List<Long> ids) {
        List<MilogLogTailDo> ret = milogLogtailDao.getMilogLogtail(ids);
        ArrayList<MilogTailDTO> res = Lists.newArrayList();
        ret.forEach(v -> {
            res.add(milogLogtailDO2DTO(v));
        });
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), res);
    }

    @Override
    public Result<Void> updateMilogLogTail(MilogLogtailParam param) {
        // 查询 MilogLogTailDo 对象
        MilogLogTailDo ret = milogLogtailDao.queryById(param.getId());
        if (ret == null) {
            return new Result<>(CommonError.ParamsError.getCode(), "tail不存在");
        }

        // 参数校验
        String errorMsg = verifyMilogLogtailParam(param);
        if (StringUtils.isNotEmpty(errorMsg)) {
            return new Result<>(CommonError.ParamsError.getCode(), errorMsg);
        }

        // 查询 MilogLogStoreDO 对象
        MilogLogStoreDO logStoreDO = logstoreDao.queryById(param.getStoreId());
        if (logStoreDO == null) {
            return new Result<>(CommonError.ParamsError.getCode(), "logstore不存在");
        }

        // 处理 MqTailParam 参数
        if (tailExtensionService.tailHandlePreprocessingSwitch(logStoreDO, param)) {
            handleMqTailParam(param);
        }

        // 检查别名是否重复
        String tail = param.getTail();
        Long id = param.getId();
        String machineRoom = logStoreDO.getMachineRoom();
        if (checkTailNameSame(tail, id, machineRoom)) {
            return new Result<>(CommonError.ParamsError.getCode(), "别名重复，请确定后提交");
        }

        // 处理 value list
        param.setValueList(IndexUtils.getNumberValueList(logStoreDO.getKeyList(), param.getValueList()));
        AppBaseInfo appBaseInfo = getAppBaseInfo(param);
        // tailRate 转 filterConf
        FilterDefine filterDefine = FilterDefine.consRateLimitFilterDefine(param.getTailRate());
        List<FilterDefine> defines = new ArrayList<>();
        if (filterDefine != null) {
            defines.add(filterDefine);
        }

        // 更新 MilogLogTailDo 对象
        MilogLogTailDo milogLogtailDo = logTailParam2Do(param, logStoreDO, appBaseInfo);
        wrapBaseCommon(milogLogtailDo, OperateEnum.UPDATE_OPERATE);
        boolean isSucceed = milogLogtailDao.update(milogLogtailDo);

        if (isSucceed) {
            Integer appType = param.getAppType();
            boolean processSwitch = tailExtensionService.bindPostProcessSwitch(param.getStoreId());
            if (tailExtensionService.bindMqResourceSwitch(appType) || processSwitch) {
                if (!processSwitch) {
                    tailExtensionService.defaultBindingAppTailConfigRel(param.getId(), param.getMilogAppId(),
                            null == param.getMiddlewareConfigId() ? logStoreDO.getMqResourceId() : param.getMiddlewareConfigId(),
                            param.getTopicName(), param.getBatchSendSize());
                }
                try {
                    List<String> oldIps = ret.getIps();
                    boolean supportedConsume = logTypeProcessor.supportedConsume(logStoreDO.getLogType());
                    tailExtensionService.updateSendMsg(milogLogtailDo, oldIps, supportedConsume);
                } catch (Exception e) {
                    new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage(), "推送配置错误");
                }
            }
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
        } else {
            log.warn("[MilogLogtailService.updateMilogLogtail] update MilogLogtail err,id:{}", param.getId());
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage());
        }
    }

    private AppBaseInfo getAppBaseInfo(MilogLogtailParam param) {
        return heraAppService.queryById(param.getMilogAppId());
    }

    private void mqConfigUpdate(MilogAppMiddlewareRel middlewareRel, Long spaceId, Long storeId, Long tailId) {
        // 设置Tag
        MilogAppMiddlewareRel.Config config = middlewareRel.getConfig();
        String tag = Utils.createTag(spaceId, storeId, tailId);
        config.setTag(tag);
        config.setConsumerGroup(DEFAULT_CONSUMER_GROUP + tag);
        middlewareRel.setConfig(config);
    }

    public void compareChangeDelIps(Long tailId, Long milogAppId, List<String> newIps, List<String> oldIps) {
        if (CollectionUtils.isEmpty(oldIps)) {
            return;
        }
        List<String> delIps = oldIps.stream().filter(s -> !newIps.contains(s)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(delIps)) {
            milogAgentService.publishIncrementDel(tailId, milogAppId, delIps);
        }
    }

    @Override
    public Result<Void> deleteMilogLogTail(Long id) {
        MilogLogTailDo milogLogtailDo = milogLogtailDao.queryById(id);
        if (null == milogLogtailDo) {
            return new Result<>(CommonError.ParamsError.getCode(), "tail 不存在");
        }
        if (milogLogtailDao.deleteMilogLogtail(id)) {
            MilogLogStoreDO logStoreDO = logstoreDao.queryById(milogLogtailDo.getStoreId());
            if (storeExtensionService.isNeedSendMsgType(logStoreDO.getLogType())) {
                CompletableFuture.runAsync(() -> sendMessageOnDelete(milogLogtailDo, logStoreDO));
            }
            CompletableFuture.runAsync(() -> sendMessageOnDelete(milogLogtailDo, logStoreDO));
            tailExtensionService.logTailDelPostProcess(logStoreDO, milogLogtailDo);
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
        } else {
            log.warn("[MilogLogtailService.deleteMilogLogtail] delete MilogLogtail err,id:{}", id);
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage());
        }
    }

    @Override
    public void sendMessageOnDelete(MilogLogTailDo mt, MilogLogStoreDO logStoreDO) {
        log.info("发送删除配置信息：mt:{}", gson.toJson(mt));
        delMqConfigResource(mt, logStoreDO);
        /**
         * 发送配置信息---log-agent
         */
        milogAgentService.publishIncrementDel(mt.getId(), mt.getMilogAppId(), mt.getIps());
        /**
         * 删除关系
         */
        milogAppMiddlewareRelDao.deleteRel(mt.getMilogAppId(), mt.getId());
    }

    private void delMqConfigResource(MilogLogTailDo mt, MilogLogStoreDO logStoreDO) {
        resourceExtensionService.deleteMqResourceProcessing(mt, logStoreDO);
    }

    @Override
    public Result<List<MapDTO>> getAppInfoByName(String appName, Integer type) {
        return Result.success(queryAllApps(appName, type));
    }

    private List<MapDTO> queryAllApps(String appName, Integer type) {
        return queryAppInfo(appName, type);
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
     * 如果应用为milog-agent,获取所有的机器列表通过额外的接口
     *
     * @param milogAppId
     * @param deployWay
     * @return
     */
    @Override
    public Result<List<MilogAppEnvDTO>> getEnInfosByAppId(Long milogAppId, Integer deployWay) {
        if (null == milogAppId) {
            return Result.failParam("参数不能为空");
        }
        AppBaseInfo appBaseInfo = heraAppService.queryById(milogAppId);
        if (null == appBaseInfo) {
            return Result.failParam("应用不存在");
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
            ret = milogLogTailDos.stream()
                    .filter(LogTailServiceImpl::filterNameEmpty)
                    .map(MilogLogTailDo::getTail)
                    .distinct()
                    .collect(Collectors.toList());
        }
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    @Override
    public Result<List<MapDTO<String, String>>> tailRatelimit() {
        ArrayList<MapDTO<String, String>> ret = new ArrayList<>();
        ret.add(MapDTO.Of("快速收集-尽快收集，占用一定资源", RateLimitEnum.RATE_LIMIT_FAST.getRateLimit()));
        ret.add(MapDTO.Of("常规收集", RateLimitEnum.RATE_LIMIT_MEDIUM.getRateLimit()));
        ret.add(MapDTO.Of("慢速收集", RateLimitEnum.RATE_LIMIT_SLOW.getRateLimit()));
        ret.add(MapDTO.Of("停止收集", RateLimitEnum.RATE_LIMIT_NONE.getRateLimit()));
        return Result.success(ret);
    }

    /***
     * miline 动态扩缩容
     * @param projectInfo
     */
    @Override
    public void dockerScaleDynamic(DockerScaleBo projectInfo) {
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryByAppAndEnv(projectInfo.getProjectId(), projectInfo.getEnvId());
        if (CollectionUtils.isNotEmpty(projectInfo.getIps()) && CollectionUtils.isNotEmpty(milogLogtailDos)) {
            log.info("动态扩容当前环境下的配置，projectId:{},envId:{},配置信息:{}",
                    projectInfo.getProjectId(), projectInfo.getEnvId(), gson.toJson(milogLogtailDos));
            for (MilogLogTailDo milogLogtailDo : milogLogtailDos) {
                List<String> exitIps = milogLogtailDo.getIps();
                List<String> newIps = projectInfo.getIps();
                if (!CollectionUtils.isEqualCollection(exitIps, newIps)) {
                    //1.修改配置
                    milogLogtailDo.setIps(newIps);
                    milogLogtailDao.update(milogLogtailDo);
                    //2.发送消息
                    compareIpToHandle(exitIps, newIps);
                }
            }
        }
    }

    public void compareIpToHandle(List<String> exitIps, List<String> newIps) {
        List<String> expandIps = newIps.stream().filter(ip -> !exitIps.contains(ip)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(expandIps)) {
            // 扩容---同步配置
            expandIps.forEach(ip -> {
                milogAgentService.configIssueAgent("", ip, "");
                milogStreamService.configIssueStream(ip);
            });
        }
        List<String> narrowIps = exitIps.stream().filter(ip -> !newIps.contains(ip)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(narrowIps)) {
            // 缩容--不用管
        }
    }

    private MilogLogTailDo logTailParam2Do(MilogLogtailParam logTailParam, MilogLogStoreDO logStoreDO, AppBaseInfo appBaseInfo) {
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
        milogLogtailDo.setLogSplitExpress((StringUtils.isNotEmpty(logTailParam.getLogSplitExpress()) ?
                logTailParam.getLogSplitExpress().trim() : ""));
        milogLogtailDo.setValueList(logTailParam.getValueList());
        FilterDefine filterDefine = FilterDefine.consRateLimitFilterDefine(logTailParam.getTailRate());
        if (filterDefine != null) {
            milogLogtailDo.setFilter(Arrays.asList(filterDefine));
        }
        tailExtensionService.logTailDoExtraFiled(milogLogtailDo, logStoreDO, logTailParam);
        milogLogtailDo.setDeployWay(logTailParam.getDeployWay());
        if (logStoreDO.isMatrixAppStore()) {
            milogLogtailDo.setDeploySpace((StringUtils.isNotEmpty(logTailParam.getDeploySpace()) ?
                    logTailParam.getDeploySpace().trim() : ""));
        }
        milogLogtailDo.setFirstLineReg((StringUtils.isNotEmpty(logTailParam.getFirstLineReg()) ?
                logTailParam.getFirstLineReg() : ""));
        return milogLogtailDo;
    }

    @Override
    public MilogTailDTO milogLogtailDO2DTO(MilogLogTailDo milogLogtailDo) {
        if (milogLogtailDo == null) {
            return null;
        }
        MilogTailDTO milogTailDTO = new MilogTailDTO();

        milogTailDTO.setId(milogLogtailDo.getId());
        milogTailDTO.setCtime(milogLogtailDo.getCtime());
        milogTailDTO.setUtime(milogLogtailDo.getUtime());
        milogTailDTO.setSpaceId(milogLogtailDo.getSpaceId());
        milogTailDTO.setStoreId(milogLogtailDo.getStoreId());
        milogTailDTO.setMilogAppId(milogLogtailDo.getMilogAppId());
        milogTailDTO.setAppId(milogLogtailDo.getAppId());
        milogTailDTO.setAppName(milogLogtailDo.getAppName());
        milogTailDTO.setEnvId(milogLogtailDo.getEnvId());
        milogTailDTO.setEnvName(milogLogtailDo.getEnvName());
        List<String> list = milogLogtailDo.getIps();
        if (CollectionUtils.isNotEmpty(list)) {
            milogTailDTO.setIps(list);
        }
        milogTailDTO.setTail(milogLogtailDo.getTail());
        milogTailDTO.setParseType(milogLogtailDo.getParseType());
        milogTailDTO.setParseScript(milogLogtailDo.getParseScript());
        milogTailDTO.setLogPath(milogLogtailDo.getLogPath());
        milogTailDTO.setLogSplitExpress(milogLogtailDo.getLogSplitExpress());
        milogTailDTO.setValueList(milogLogtailDo.getValueList());
        milogTailDTO.setAppType(milogLogtailDo.getAppType());
        milogTailDTO.setMachineType(milogLogtailDo.getMachineType());
        milogTailDTO.setMotorRooms(milogLogtailDo.getMotorRooms());
        // filterconf 转 tailRate
        milogTailDTO.setTailRate(RateLimitEnum.consTailRate(milogLogtailDo.getFilter()));
        milogTailDTO.setDeployWay(milogLogtailDo.getDeployWay());
        milogTailDTO.setDeploySpace(milogLogtailDo.getDeploySpace());
        milogTailDTO.setFirstLineReg(milogLogtailDo.getFirstLineReg());
        return milogTailDTO;
    }

    @Override
    public Result<List<MapDTO>> queryAppByStoreId(Long storeId) {
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryAppIdByStoreId(storeId);
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            return Result.success(milogLogtailDos.stream()
                    .map(milogLogtailDo -> MapDTO.Of(milogLogtailDo.getAppName(),
                            milogLogtailDo.getMilogAppId())).distinct().collect(Collectors.toList()));
        }
        return Result.success(Lists.newArrayList());
    }

    @Override
    public Result<List<AppTypeTailDTO>> queryAppTailByStoreId(Long storeId) {
        if (null == storeId) {
            return Result.failParam("storeId 不能为空");
        }
        List<AppTypeTailDTO> appTypeTailDTOS = Lists.newArrayList();
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryAppIdByStoreId(storeId);
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            Map<Integer, List<MilogLogTailDo>> appTypeMap = milogLogtailDos.stream().collect(Collectors.groupingBy(MilogLogTailDo::getAppType));
            MilogLogStoreDO milogLogstoreDO = logstoreDao.queryById(storeId);
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
            return Result.failParam("storeId 不能为空");
        }
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryStoreIdByRegionNameEN(nameEn);
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            return Result.success(milogLogtailDos.stream()
                    .map(milogLogtailDo -> logstoreDao.queryById(milogLogtailDo.getStoreId()))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toCollection(() ->
                                    new TreeSet<>(Comparator.comparing(o -> o.getLogstoreName()))),
                            ArrayList::new)));
        }
        return Result.success(Lists.newArrayList());
    }

    @Override
    public Result<List<MilogTailDTO>> getTailByStoreId(Long storeId) {
        if (storeId == null) {
            return Result.failParam("storeId不能为空");
        }
        List<MilogLogTailDo> tailList = milogLogtailDao.getMilogLogtailByStoreId(storeId);
        List<MilogTailDTO> dtoList = new ArrayList<>();
        MilogTailDTO milogTailDTO;
        for (MilogLogTailDo milogLogtailDo : tailList) {
            milogTailDTO = milogLogtailDO2DTO(milogLogtailDo);
            if (milogLogtailDo != null) {
                dtoList.add(milogTailDTO);
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
        MilogLogStoreDO logstoreDO = logstoreDao.queryById(mlogParseParam.getStoreId());
        if (null == logstoreDO) {
            return Result.fail(CommonError.NOT_EXISTS_DATA.getCode(), "logStore不存在");
        }
        String keyList = Utils.parse2KeyAndTypeList(logstoreDO.getKeyList(), logstoreDO.getColumnTypeList());
        String valueList = IndexUtils.getNumberValueList(logstoreDO.getKeyList(), mlogParseParam.getValueList());
        Long currentStamp = Instant.now().toEpochMilli();
        try {
            LogParser logParser = LogParserFactory.getLogParser(mlogParseParam.getParseType(), keyList, valueList, mlogParseParam.getParseScript());
            Map<String, Object> parseMsg = logParser.parseSimple(mlogParseParam.getMsg(), currentStamp);
            return Result.success(parseMsg);
        } catch (Exception e) {
            log.info("解析配置信息错误：配置信息：{}", gson.toJson(mlogParseParam), e);
        }
        return Result.success("解析错误 请核对配置信息");
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
            log.info("解析配置信息错误：配置信息：{}", GSON.toJson(mlogParseParam), e);
        }
        return Result.success("解析错误 请核对配置信息");
    }

    private String checkParseParam(MlogParseParam mlogParseParam) {
        StringBuilder sb = new StringBuilder();
        if (null == mlogParseParam.getStoreId()) {
            sb.append("store不能为空;");
        }
        if (null == mlogParseParam.getParseScript()) {
            sb.append("解析脚本不能为空;");
        }
        if (null == mlogParseParam.getValueList()) {
            sb.append("索引规则不能为空;");
        }
        if (null == mlogParseParam.getMsg()) {
            sb.append("日志信息不能为空;");
        }
        return sb.toString();
    }

    @Override
    public Result<List<QuickQueryVO>> quickQueryByApp(Long milogAppId) {
        if (null == milogAppId) {
            return Result.failParam("milogAppId不能为空");
        }
        List<MilogLogTailDo> milogLogTailDos = milogLogtailDao.getLogTailByMilogAppId(milogAppId);
        List<QuickQueryVO> quickQueryVOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(milogLogTailDos)) {
            quickQueryVOS = milogLogTailDos.stream().map(
                            this::applyQueryVO)
                    .collect(Collectors.toList());
            wrapStoreSpaceName(quickQueryVOS);
        }
        return Result.success(quickQueryVOS);
    }

    private void wrapStoreSpaceName(List<QuickQueryVO> quickQueryVOS) {
        List<Long> storeIds = quickQueryVOS.stream()
                .map(QuickQueryVO::getStoreId)
                .collect(Collectors.toList());
        List<MilogLogStoreDO> milogLogStoreDOS = logstoreDao.queryByIds(storeIds);
        List<Long> spaceIds = quickQueryVOS.stream()
                .map(QuickQueryVO::getSpaceId)
                .collect(Collectors.toList());
        List<MilogSpaceDO> milogSpaceDOS = milogSpaceDao.queryByIds(spaceIds);
        quickQueryVOS.stream().map(quickQueryVO -> {
            milogLogStoreDOS.stream()
                    .filter(milogLogsSoreDO -> Objects.equals(quickQueryVO.getStoreId(), milogLogsSoreDO.getId()))
                    .findFirst()
                    .map(milogLogStoreDO -> {
                        quickQueryVO.setStoreName(milogLogStoreDO.getLogstoreName());
                        return true;
                    });
            milogSpaceDOS.stream()
                    .filter(milogSpaceDO -> Objects.equals(quickQueryVO.getSpaceId(), milogSpaceDO.getId()))
                    .findFirst()
                    .map(milogSpaceDO -> {
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
            log.info("动态扩容当前环境下的配置，heraAppEnvVo:{}，logTailDos:{}",
                    gson.toJson(heraEnvIpVo), gson.toJson(logTailDos));
            for (MilogLogTailDo milogLogtailDo : logTailDos) {
                List<String> exitIps = milogLogtailDo.getIps();
                List<String> newIps = heraEnvIpVo.getIpList();
                if (!CollectionUtils.isEqualCollection(exitIps, newIps)) {
                    //1.修改配置
                    milogLogtailDo.setIps(newIps);
                    milogLogtailDo.setUtime(Instant.now().toEpochMilli());
                    milogLogtailDo.setUpdater(DEFAULT_JOB_OPERATOR);
                    milogLogtailDao.updateIps(milogLogtailDo);
                    //2.发送消息
                    compareIpToHandle(exitIps, newIps);
                }
            }
        }
    }

    @Override
    public Result<QuickQueryVO> queryAppStore(Long appId, Integer platFormCode) {
        if (null == appId || null == platFormCode) {
            return Result.failParam("参数不能为空");
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
