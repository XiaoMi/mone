package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.app.api.model.HeraSimpleEnv;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.model.vo.HeraEnvIpVo;
import com.xiaomi.mone.log.api.enums.*;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.validation.HeraConfigValid;
import com.xiaomi.mone.log.manager.dao.*;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
import com.xiaomi.mone.log.manager.model.bo.MlogParseParam;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.pojo.*;
import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;
import com.xiaomi.mone.log.manager.model.vo.QuickQueryVO;
import com.xiaomi.mone.log.manager.service.BaseMilogRpcConsumerService;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.mone.log.manager.service.LogTailService;
import com.xiaomi.mone.log.manager.service.bind.LogTypeProcessor;
import com.xiaomi.mone.log.manager.service.bind.LogTypeProcessorFactory;
import com.xiaomi.mone.log.manager.service.env.HeraEnvIpService;
import com.xiaomi.mone.log.manager.service.env.HeraEnvIpServiceFactory;
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
import static com.xiaomi.mone.log.manager.service.path.LogPathMapping.LOG_PATH_PREFIX;

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
    private RocketMqConfigService rocketMqConfigService;

    @Resource
    private MilogAgentServiceImpl milogAgentService;

    @Resource
    private MilogStreamServiceImpl milogStreamService;

    @Resource
    private RocketMqConfigService mqConfigService;

    @Resource
    private MilogAppMiddlewareRelServiceImpl milogAppMiddlewareRelService;
    @Resource
    private MilogAppMiddlewareRelDao milogAppMiddlewareRelDao;
    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;
    @Resource
    private NeoAppInfoServiceImpl neoAppInfoService;
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
    private HeraConfigValid heraConfigValid;

    Gson gson = new Gson();
    private static final String MIS_LOGPATH_PREFIX = "/home/work/logs";


    @Resource
    private LogTypeProcessorFactory logTypeProcessorFactory;

    private LogTypeProcessor logTypeProcessor;

    public void init() {
        logTypeProcessorFactory.setMilogLogTemplateMapper(milogLogTemplateMapper);
        logTypeProcessor = logTypeProcessorFactory.getLogTypeProcessor();
    }

    private static boolean filterNameEmpty(MilogLogTailDo milogLogTailDo) {
        return StringUtils.isNotBlank(milogLogTailDo.getTail());
    }

    private static QuickQueryVO applyQueryVO(MilogLogTailDo logTailDo) {
        return QuickQueryVO.builder()
                .spaceId(logTailDo.getSpaceId())
                .storeId(logTailDo.getStoreId())
                .tailId(logTailDo.getId())
                .tailName(logTailDo.getTail())
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
        MilogLogStoreDO milogLogStore = logstoreDao.queryById(param.getStoreId());
        if (null != milogLogStore) {
            if (heraConfigValid.checkTailNameSame(param.getTail(), null, milogLogStore.getMachineRoom())) {
                return new Result<>(CommonError.ParamsError.getCode(), "别名重复，请确定后提交");
            }
            String valueList = IndexUtils.getNumberValueList(milogLogStore.getKeyList(), param.getValueList());
            param.setValueList(valueList);
        } else {
            return new Result<>(CommonError.ParamsError.getCode(), "logStore不存在");
        }
        // 参数处理
        handleMqTailParam(param);
        /*** 处理value list */
        if (checkTailNameSame(param.getTail(), null, milogLogStore.getMachineRoom())) {
            return new Result<>(CommonError.ParamsError.getCode(), "别名重复，请确定后提交");
        }
        String valueList = IndexUtils.getNumberValueList(milogLogStore.getKeyList(), param.getValueList());
        param.setValueList(valueList);

        AppBaseInfo appBaseInfo = heraAppService.queryById(param.getMilogAppId());

        MilogLogTailDo mt = buildLogTailDo(param, milogLogStore, appBaseInfo, MoneUserContext.getCurrentUser().getUser());
        MilogLogTailDo milogLogtailDo = milogLogtailDao.newMilogLogtail(mt);
        boolean supportedConsume = logTypeProcessor.supportedConsume(LogTypeEnum.type2enum(milogLogStore.getLogType()));
        try {
            if (null != milogLogtailDo) {
                if (ProjectTypeEnum.MIONE_TYPE.getCode().intValue() == param.getAppType().intValue()) {
                    // tail创建成功 绑定和mq的关系
                    milogAppMiddlewareRelService.defaultBindingAppTailConfigRel(
                            milogLogtailDo.getId(), param.getMilogAppId(),
                            null == param.getMiddlewareConfigId() ? milogLogStore.getMqResourceId() : param.getMiddlewareConfigId(),
                            param.getTopicName(), param.getBatchSendSize());
                    /** 创建完tail后同步信息**/
                    sendMessageOnCreate(param, mt, param.getMilogAppId(), supportedConsume);
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
        sendMessageOnCreate(param, milogLogtailDo, param.getMilogAppId(), logTypeProcessor.supportedConsume(LogTypeEnum.type2enum(milogLogStore.getLogType())));
    }

    @Override
    public MilogLogTailDo buildLogTailDo(MilogLogtailParam param, MilogLogStoreDO milogLogStore, AppBaseInfo appBaseInfo, String creator) {
        MilogLogTailDo mt = milogLogtailParam2Do(param, milogLogStore);
        wrapBaseCommon(mt, OperateEnum.ADD_OPERATE, creator);
        //查看topic是否创建完，没有则创建
//        if (StringUtils.isEmpty(milogLogStore.getTopic())) {
//            MilogMiddlewareConfig middlewareConfig = milogMiddlewareConfigDao.queryById(milogLogStore.getMqResourceId());
//            MilogAppMiddlewareRel.Config config = mqConfigService.generateConfig(middlewareConfig.getAk(),
//                    middlewareConfig.getSk(), middlewareConfig.getNameServer(), middlewareConfig.getServiceUrl(),
//                    middlewareConfig.getAuthorization(), middlewareConfig.getOrgId(),
//                    middlewareConfig.getTeamId(), null,
//                    milogLogStore.getLogstoreName(), "", milogLogStore.getId());
//            if (StringUtils.isEmpty(param.getTopicName())) {
//                param.setTopicName(config.getTopic());
//            }
//        }
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

    /**
     * 创建完logtail后发送通知消息
     *
     * @param param
     * @param mt
     */
    private void sendMessageOnCreate(MilogLogtailParam param, MilogLogTailDo mt, Long milogAppId, boolean supportedConsume) {
//        MilogMiddlewareConfig config = milogAppMiddlewareRelService.queryMiddlewareConfig(param.getMiddlewareConfigId());
        /**
         * 创建consumerGroup-开源不需要
         */
//        createConsumerGroup(param.getSpaceId(), param.getStoreId(), mt.getId(), config, milogAppId, notSendStream);
        /**
         * 发送配置信息---log-agent
         */
        CompletableFuture.runAsync(() -> sengMessageToAgent(milogAppId, mt));
        /**
         * 发送最终配置信息---log-stream-- 查看日志模板类型，如果是opentelemetry日志，只发送mq不消费
         */
        if (supportedConsume) {
            sengMessageToStream(mt, OperateEnum.ADD_OPERATE.getCode());
        }
    }

    private void createConsumerGroup(Long spaceId, Long storeId, Long tailId, MilogMiddlewareConfig config, Long milogAppId, boolean notSendStream) {
        if (!notSendStream && config.getType().equals(MiddlewareEnum.ROCKETMQ.getCode())) {
            rocketMqConfigService.createSubscribeGroup(config.getServiceUrl(), config.getAuthorization(), config.getOrgId(),
                    spaceId, storeId, tailId, milogAppId);
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
            decorateMilogTailDTO(milogTailDTO);
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), milogTailDTO);
        } else {
            return new Result<>(CommonError.UnknownError.getCode(), "tail 不存在");
        }
    }

    private void decorateMilogTailDTO(MilogTailDTO milogTailDTO) {
        MilogAppTopicRelDO milogAppTopicRel = milogAppTopicRelDao.queryById(milogTailDTO.getMilogAppId());
        if (null == milogAppTopicRel) {
            log.error("当前查询应用错误：{}", gson.toJson(milogTailDTO));
            return;
        }
        milogTailDTO.setSource(milogAppTopicRel.getSource());
        List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(milogAppTopicRel.getId(), null, milogTailDTO.getId());
        if (CollectionUtils.isEmpty(milogAppMiddlewareRels)) {
            return;
        }
        MilogAppMiddlewareRel milogAppMiddlewareRel = milogAppMiddlewareRels.get(0);
        MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryById(milogAppMiddlewareRel.getMiddlewareId());
        milogTailDTO.setMiddlewareConfig(Arrays.asList(Long.valueOf(config.getType()), config.getId(), milogAppMiddlewareRel.getConfig().getTopic()));
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
        MilogLogTailDo ret = milogLogtailDao.queryById(param.getId());
        if (null == ret) {
            return new Result<>(CommonError.ParamsError.getCode(), "tail 不存在");
        }
        List<String> oldIps = ret.getIps();
        String errorMsg = verifyMilogLogtailParam(param);
        if (StringUtils.isNotEmpty(errorMsg)) {
            return new Result<>(CommonError.ParamsError.getCode(), errorMsg);
        }
        handleMqTailParam(param);
        // 处理value list
        MilogLogStoreDO milogLogStoreDO = logstoreDao.queryById(param.getStoreId());
        if (null != milogLogStoreDO) {
            if (checkTailNameSame(param.getTail(), param.getId(), milogLogStoreDO.getMachineRoom())) {
                return new Result<>(CommonError.ParamsError.getCode(), "别名重复，请确定后提交");
            }
            String keyList = milogLogStoreDO.getKeyList();
            String valueList = IndexUtils.getNumberValueList(keyList, param.getValueList());
            param.setValueList(valueList);
        } else {
            return new Result<>(CommonError.ParamsError.getCode(), "logstore不存在");
        }
        // tailRate 转 filterConf
        FilterDefine filterDefine = FilterDefine.consRateLimitFilterDefine(param.getTailRate());

        List<FilterDefine> defines = new ArrayList<>();
        if (filterDefine != null) {
            defines.add(filterDefine);
        }
        MilogLogTailDo milogLogtailDo = milogLogtailParam2Do(param, milogLogStoreDO);
        wrapBaseCommon(milogLogtailDo, OperateEnum.UPDATE_OPERATE);
        boolean isSucceed = milogLogtailDao.update(milogLogtailDo);
        if (isSucceed) {
            Integer appType = param.getAppType();
            if (Objects.equals(ProjectTypeEnum.MIONE_TYPE.getCode(), appType)) {
                milogAppMiddlewareRelService.defaultBindingAppTailConfigRel(
                        param.getId(), param.getMilogAppId(),
                        null == param.getMiddlewareConfigId() ? milogLogStoreDO.getMqResourceId() : param.getMiddlewareConfigId(),
                        param.getTopicName(), param.getBatchSendSize());
                try {
                    updateSendMsg(milogLogtailDo, oldIps);
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

    private void mqConfigUpdate(MilogAppMiddlewareRel middlewareRel, Long spaceId, Long storeId, Long tailId) {
        // 设置Tag
        MilogAppMiddlewareRel.Config config = middlewareRel.getConfig();
        String tag = Utils.createTag(spaceId, storeId, tailId);
        config.setTag(tag);
        config.setConsumerGroup(DEFAULT_CONSUMER_GROUP + tag);
        middlewareRel.setConfig(config);
    }

    @Override
    public void updateSendMsg(MilogLogTailDo milogLogtailDo, List<String> oldIps) {
        MilogLogStoreDO logStoreDO = logstoreDao.queryById(milogLogtailDo.getStoreId());
        /**
         * 同步log-agent
         */
        CompletableFuture.runAsync(() -> milogAgentService.publishIncrementConfig(milogLogtailDo.getId(), milogLogtailDo.getMilogAppId(), milogLogtailDo.getIps()));
        /**
         * 同步 log-stream 如果是opentelemetry日志，只发送mq不消费
         */
        if (logTypeProcessor.supportedConsume(LogTypeEnum.type2enum(logStoreDO.getLogType()))) {
//            List<MilogAppMiddlewareRel> middlewareRels = milogAppMiddlewareRelDao.queryByCondition(milogLogtailDo.getMilogAppId(), null, milogLogtailDo.getId());
//            createConsumerGroup(milogLogtailDo.getSpaceId(), milogLogtailDo.getStoreId(), milogLogtailDo.getId(), milogMiddlewareConfigDao.queryById(middlewareRels.get(0).getMiddlewareId()), milogLogtailDo.getMilogAppId(), false);
            sengMessageToStream(milogLogtailDo, OperateEnum.UPDATE_OPERATE.getCode());
        }
        compareChangeDelIps(milogLogtailDo.getId(), milogLogtailDo.getMilogAppId(), milogLogtailDo.getIps(), oldIps);
    }

    private void compareChangeDelIps(Long tailId, Long milogAppId, List<String> newIps, List<String> oldIps) {
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
            CompletableFuture.runAsync(() -> sendMessageOnDelete(milogLogtailDo, logStoreDO));
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
        List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(mt.getMilogAppId(), null, mt.getId());
        logTypeProcessor.supportedConsume(LogTypeEnum.type2enum(logStoreDO.getLogType()));
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
        List<HeraSimpleEnv> heraSimpleEnvs = null;
        try {
            heraSimpleEnvs = heraAppEnvService.querySimpleEnvAppBaseInfoId(milogAppId.intValue());
        } catch (Exception e) {
            log.error(String.format("query ip error:milogAppId:%s,deployWay:%s", milogAppId, deployWay), e);
        }
        if (CollectionUtils.isEmpty(heraSimpleEnvs)) {
            return Result.success(Lists.newArrayList());
        }
        return Result.success(heraSimpleEnvs.stream().map(envBo -> MilogAppEnvDTO.builder().label(envBo.getName()).value(envBo.getId()).ips(envBo.getIps()).build()).collect(Collectors.toList()));
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

    private void compareIpToHandle(List<String> exitIps, List<String> newIps) {
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

    private MilogLogTailDo milogLogtailParam2Do(MilogLogtailParam milogLogtailParam, MilogLogStoreDO milogLogstore) {
        MilogLogTailDo milogLogtailDo = new MilogLogTailDo();
        milogLogtailDo.setId(milogLogtailParam.getId());
        milogLogtailDo.setTail(milogLogtailParam.getTail());
        milogLogtailDo.setSpaceId(milogLogtailParam.getSpaceId());
        milogLogtailDo.setStoreId(milogLogtailParam.getStoreId());
        milogLogtailDo.setMilogAppId(milogLogtailParam.getMilogAppId());
        milogLogtailDo.setAppId(milogLogtailParam.getMilogAppId());
        milogLogtailDo.setAppName(milogLogtailParam.getAppName());
        milogLogtailDo.setEnvId(milogLogtailParam.getEnvId());
        milogLogtailDo.setEnvName(milogLogtailParam.getEnvName());
        milogLogtailDo.setMachineType(milogLogtailParam.getMachineType());
        List<String> list = milogLogtailParam.getIps();
        Integer appType = milogLogtailParam.getAppType();
        milogLogtailDo.setAppType(appType);
        milogLogtailDo.setIps(list);
        milogLogtailDo.setParseType(milogLogtailParam.getParseType());
        milogLogtailDo.setParseScript(StringUtils.isEmpty(milogLogtailParam.getParseScript()) ? DEFAULT_TAIL_SEPARATOR : milogLogtailParam.getParseScript());
        milogLogtailDo.setLogPath(milogLogtailParam.getLogPath().trim());
        milogLogtailDo.setLogSplitExpress((StringUtils.isNotEmpty(milogLogtailParam.getLogSplitExpress()) ?
                milogLogtailParam.getLogSplitExpress().trim() : ""));
        milogLogtailDo.setValueList(milogLogtailParam.getValueList());
        FilterDefine filterDefine = FilterDefine.consRateLimitFilterDefine(milogLogtailParam.getTailRate());
        if (filterDefine != null) {
            milogLogtailDo.setFilter(Arrays.asList(filterDefine));
        }
        milogLogtailDo.setDeployWay(milogLogtailParam.getDeployWay());
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
        if (list != null) {
            milogTailDTO.setIps(new ArrayList<String>(list));
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
        return milogTailDTO;
    }

    @Override
    public Result<List<SimpleAppEnvDTO>> getRegionZonesByAppId(Long milogAppId, String machineRoom) {
        if (null == milogAppId) {
            return Result.failParam("appId不能为空");
        }
        if (StringUtils.isEmpty(machineRoom) || null == MachineRegionEnum.queryCnByEn(machineRoom)) {
            return Result.failParam("machineRoom不能为空或者错误");
        }
        List<SimpleAppEnvDTO> simpleAppEnvDTOs = Lists.newArrayList();
//        MilogAppTopicRelDO milogAppTopicRel = milogAppTopicRelDao.queryById(milogAppId);
        AppBaseInfo milogAppTopicRel = heraAppService.queryById(milogAppId);
        SimpleAppEnvDTO simpleAppEnvDTO = new SimpleAppEnvDTO();
        simpleAppEnvDTO.setNameEn(machineRoom);
        simpleAppEnvDTO.setNameCn(MachineRegionEnum.queryCnByEn(machineRoom));
        LinkedHashMap<String, List<String>> nodeIPs = milogAppTopicRel.getNodeIPs();
        if (null != nodeIPs && nodeIPs.size() > 0) {
            simpleAppEnvDTO.setNodeIps(nodeIPs.get(machineRoom));
        }
        if (null != milogAppTopicRel && CollectionUtils.isNotEmpty(milogAppTopicRel.getTreeIds())) {
            List<String> treeIds = milogAppTopicRel.getTreeIds().stream().map(String::valueOf).collect(Collectors.toList());
            List<RegionDTO> neoAppInfos = neoAppInfoService.getNeoAppInfo(treeIds);
            simpleAppEnvDTO.setPodDTOList(regionDTOTransferSimpleAppDTOs(neoAppInfos, MachineRegionEnum.queryRegionByEn(machineRoom)));
        }
        simpleAppEnvDTOs.add(simpleAppEnvDTO);
        return Result.success(simpleAppEnvDTOs);
    }

    @Override
    public List<PodDTO> regionDTOTransferSimpleAppDTOs(List<RegionDTO> neoAppInfos, MachineRegionEnum
            machineRoom) {
        List<PodDTO> podDTOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(neoAppInfos)) {
            neoAppInfos.forEach(regionDTO ->
                    regionDTO.getZoneDTOList()
                            .stream().forEach(zoneDTO -> {
                                String zoneNameEN = zoneDTO.getZoneNameEN();
                                MachineRegionEnum machineInfoByZone = MachineRegionEnum.queryMchineRegionByZone(zoneNameEN);
                                if (null != machineInfoByZone && machineInfoByZone == machineRoom) {
                                    podDTOList.addAll(zoneDTO.getPodDTOList());
                                }
                            }));
        }
        return podDTOList;
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
                tailApp.setNameCn(MachineRegionEnum.queryCnByEn(nameEn));
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
    public void handleK8sTopicTail(K8sMachineChangeDTO machineChangeDTO) {
        log.info("handleK8sTopicTail:{}", gson.toJson(machineChangeDTO));
        List<String> chainContainResource = Lists.newArrayList(ProjectSourceEnum.ONE_SOURCE.getSource(),
                ProjectSourceEnum.TWO_SOURCE.getSource());
        Integer appTypeCode = ProjectTypeEnum.MIONE_TYPE.getCode();
        List<MilogAppTopicRelDO> appContents = milogAppTopicRelDao.queryAppInfoByChinaCondition(machineChangeDTO.getAppId(), null);
        if (CollectionUtils.isNotEmpty(appContents)) {
            Integer finalAppTypeCode = appTypeCode;
            appContents.stream()
                    .filter(appContent -> chainContainResource.contains(appContent.getSource()))
                    .forEach(appContent -> {
                        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao
                                .queryByMilogAppAndEnvK8s(appContent.getId(), machineChangeDTO.getEnvId(), finalAppTypeCode);
                        for (MilogLogTailDo milogLogtailDo : milogLogtailDos) {
                            List<String> changedMachines = machineChangeDTO.getChangedMachines();
                            AppBaseInfo appBaseInfo = heraAppService.queryById(milogLogtailDo.getMilogAppId());
                            //2.发送配置
                            List<String> podNamePrefix = machineChangeDTO.getDeletingMachines()
                                    .stream()
                                    .map(podName -> String.format("%s%s%s", LOG_PATH_PREFIX, "/", podName))
                                    .collect(Collectors.toList());
                            k8sPodIpsSend(milogLogtailDo.getId(), changedMachines, podNamePrefix, appBaseInfo.getPlatformType());
                            //更新tail
                            milogLogtailDo.setIps(changedMachines);
                            milogLogtailDo.setUtime(Instant.now().toEpochMilli());
                            milogLogtailDo.setUpdater(Constant.DEFAULT_OPERATOR);
                            milogLogtailDao.update(milogLogtailDo);
                        }
                    });
        }

    }

    @Override
    public void k8sPodIpsSend(Long tailId, List<String> podIps, List<String> podNamePrefix, Integer appType) {
        HeraEnvIpService heraEnvIpService = heraEnvIpServiceFactory.getHeraEnvIpServiceByAppType(appType);
        Map<String, List<LogAgentListBo>> agentIpMap = heraEnvIpService.queryAgentIpByPodIps(podIps);
        if (!agentIpMap.isEmpty()) {
            for (Map.Entry<String, List<LogAgentListBo>> stringListEntry : agentIpMap.entrySet()) {
                milogAgentService.configIssueAgentK8s(tailId, stringListEntry.getKey(), stringListEntry.getValue(), podNamePrefix);
            }
        }
    }

    @Override
    public Result<List<QuickQueryVO>> quickQueryByApp(Long milogAppId) {
        if (null == milogAppId) {
            return Result.failParam("milogAppId不能为空");
        }
        List<MilogLogTailDo> milogLogTailDos = milogLogtailDao.queryByAppId(milogAppId);
        List<QuickQueryVO> quickQueryVOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(milogLogTailDos)) {
            quickQueryVOS = milogLogTailDos.stream().map(
                            LogTailServiceImpl::applyQueryVO)
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

    /**
     * 比较机器列表并发送消息
     * 1.找到配置的log-agent的机器列表
     * 2.查询到最新的
     * 比较最新的是否比库中的多
     * 如果多，修改库，发送消息
     * 否则只修改库
     */
    @Override
    public void casOttMachines(String source) {
        BaseMilogRpcConsumerService consumerService = queryConsumerService(source);
        List<MilogLogStoreDO> storeDOS = logstoreDao.queryByLogType(LogTypeEnum.OPENTELEMETRY.getType());
        List<MilogAppTopicRelDO> appTopicRelDOS = milogAppTopicRelDao.queryAppbyNameSource(AppTypeEnum.LOG_AGENT.getName(), source);
        if (CollectionUtils.isNotEmpty(appTopicRelDOS) && CollectionUtils.isNotEmpty(storeDOS)) {
            try {
                List<String> liveMachines = Lists.newArrayList();
                MilogAppTopicRelDO milogAppTopicRelDO = appTopicRelDOS.get(appTopicRelDOS.size() - 1);
                List<Long> storeIds = storeDOS.stream().map(MilogLogStoreDO::getId).collect(Collectors.toList());
                List<MilogLogTailDo> logTailDos = milogLogtailDao.queryTailsByAppAndStores(milogAppTopicRelDO.getId(), storeIds);
                //过滤掉k8s的--miline调用的是线上环境，本地和线上不通，会报连接异常
                logTailDos = logTailDos.stream().filter(milogLogTailDo -> {
//                    PipelineDeployDto pipelineDeployDto = consumerService.qryDeployInfo(milogAppTopicRelDO.getAppId(), milogLogTailDo.getEnvId());
//                    return DeployTypeEnum.K8S.getId() != pipelineDeployDto.getDeployType();
                    return true;
                }).collect(Collectors.toList());
                for (MilogLogTailDo logTailDo : logTailDos) {
                    List<String> ips = logTailDo.getIps();
                    if (!CollectionUtils.isEqualCollection(liveMachines, ips)) {
                        logTailDo.setIps(liveMachines);
                        milogLogtailDao.update(logTailDo);
                        compareIpToHandle(ips, liveMachines);
                    }
                }
            } catch (Exception e) {
                log.error("casOttMachines error,source:{}", source, e);
            }
        }
    }

    @Override
    public BaseMilogRpcConsumerService queryConsumerService(String source) {
        return null;
    }

    @Override
    public void machineIpChange(HeraEnvIpVo heraEnvIpVo) {
        List<MilogLogTailDo> logTailDos = milogLogtailDao.queryByMilogAppAndEnvId(heraEnvIpVo.getHeraAppId(), heraEnvIpVo.getEnvId());
        if (CollectionUtils.isNotEmpty(logTailDos)) {
            log.info("动态扩容当前环境下的配置，heraAppEnvVo:{}，logTailDos:{}",
                    gson.toJson(heraEnvIpVo), gson.toJson(logTailDos));
            for (MilogLogTailDo milogLogtailDo : logTailDos) {
                List<String> exitIps = milogLogtailDo.getIps();
                List<String> newIps = heraEnvIpVo.getIpList();
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
}
