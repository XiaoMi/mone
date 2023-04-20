package com.xiaomi.mone.log.manager.service.extension.agent;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.meta.*;
import com.xiaomi.mone.log.api.model.vo.AgentLogProcessDTO;
import com.xiaomi.mone.log.api.service.PublishConfigService;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.dao.*;
import com.xiaomi.mone.log.manager.domain.LogProcess;
import com.xiaomi.mone.log.manager.model.bo.MilogAgentIpParam;
import com.xiaomi.mone.log.manager.model.pojo.*;
import com.xiaomi.mone.log.manager.service.env.HeraEnvIpService;
import com.xiaomi.mone.log.manager.service.env.HeraEnvIpServiceFactory;
import com.xiaomi.mone.log.manager.service.impl.HeraAppServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.mone.log.manager.service.path.LogPathMapping;
import com.xiaomi.mone.log.manager.service.path.LogPathMappingFactory;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.NamedThreadFactory;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COLON;
import static com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentService.DEFAULT_AGENT_EXTENSION_SERVICE_KEY;

@Service(name = DEFAULT_AGENT_EXTENSION_SERVICE_KEY)
@Slf4j
public class MilogAgentServiceImpl implements MilogAgentService {

    @Resource
    private LogPathMappingFactory logPathMappingFactory;

    @Resource
    private HeraEnvIpServiceFactory heraEnvIpServiceFactory;

    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private MilogLogstoreDao logstoreDao;

    private Gson gson = Constant.GSON;

    @Resource
    private LogProcess logProcess;

    @Resource
    private MilogAppMiddlewareRelDao milogAppMiddlewareRelDao;
    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    @Resource
    private HeraAppServiceImpl heraAppService;
    @Resource
    private LogTailServiceImpl logTailService;

    @Reference(interfaceClass = PublishConfigService.class, group = "$dubbo.env.group", check = false, timeout = 14000)
    private PublishConfigService publishConfigService;

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;


    private static final AtomicInteger COUNT_INCR = new AtomicInteger(0);

    static {
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(6, 20,
                1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(200),
                new NamedThreadFactory("coll-base-data-start", true),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);
    }

    @Override
    public Result<List<AgentLogProcessDTO>> process(String ip) {
        List<AgentLogProcessDTO> dtoList = logProcess.getAgentLogProcess(ip);
        return Result.success(dtoList);
    }

    @Override
    public Result<String> configIssueAgent(String agentId, String agentIp, String agentMachine) {
        if (StringUtils.isEmpty(agentIp)) {
            return Result.failParam("agentIp不能为空");
        }
        // 1.查询该物理机下全量的配置
        LogCollectMeta logCollectMeta = queryMilogAgentConfig(agentId, agentIp, agentMachine);
        log.info("{},this ip config data:{}", agentIp, gson.toJson(logCollectMeta));
        String k8sNodeIP = queryNodeIpByPodIp(agentIp);
        if (StringUtils.isNotEmpty(k8sNodeIP)) {
            log.info("query k8s ip succeed,ip:{},k8sNodeIP:{}", agentIp, k8sNodeIP);
            agentIp = k8sNodeIP;
        }
        List<String> ipAddress = Lists.newArrayList();
        AgentContext.ins().map.entrySet().forEach(agentChannelEntry -> {
                    String key = agentChannelEntry.getKey();
                    ipAddress.add(StringUtils.substringBefore(key, SYMBOL_COLON));
                }
        );
        log.info("agent ip list:{}", gson.toJson(ipAddress));
        //2.下发配置
        sengConfigToAgent(agentIp, logCollectMeta);
        return Result.success("success");
    }

    /**
     * 下发配置到指定的ip
     *
     * @param logCollectMeta
     * @param agentIp
     */
    public void sengConfigToAgent(final String agentIp, LogCollectMeta logCollectMeta) {
        if (CollectionUtils.isEmpty(logCollectMeta.getAppLogMetaList()) || logCollectMeta.getAppLogMetaList()
                .stream().allMatch(appLogMeta -> CollectionUtils.isEmpty(appLogMeta.getLogPatternList()))) {
            return;
        }
        // 放在线程池中 执行
        THREAD_POOL_EXECUTOR.execute(() -> {
            publishConfigService.sengConfigToAgent(agentIp, logCollectMeta);
        });
    }

    /**
     * 发送增量的配置
     * 1.找到部署这台app的所有物理机ip
     * 2.这条新增的配置推送给这些物理机或者容器
     *
     * @param milogAppId
     * @param ips
     */
    @Override
    public void publishIncrementConfig(Long tailId, Long milogAppId, List<String> ips) {
        log.info("push agent params,milogAppId:{},ips:{}", milogAppId, ips);
        if (CollectionUtils.isEmpty(ips)) {
            return;
        }
        printMangerInfo();
        AppBaseInfo appBaseInfo = heraAppService.queryById(milogAppId);
        ips.forEach(ip -> {
            AppLogMeta appLogMeta = assembleSingleConfig(milogAppId, queryLogPattern(milogAppId, ip, appBaseInfo.getPlatformType()));
            LogCollectMeta logCollectMeta = new LogCollectMeta();
            logCollectMeta.setAgentIp(ip);
            logCollectMeta.setAppLogMetaList(Arrays.asList(appLogMeta));
            AgentDefine agentDefine = new AgentDefine();
            agentDefine.setFilters(new ArrayList<>());
            logCollectMeta.setAgentDefine(agentDefine);
            log.info("push agent config data,ip:{},{}", ip, gson.toJson(logCollectMeta));
            sengConfigToAgent(ip, logCollectMeta);
        });
    }

    @NotNull
    private Map<String, AgentChannel> getAgentChannelMap() {
        Map<String, AgentChannel> logAgentMap = new HashMap<>();
        AgentContext.ins().map.forEach((k, v) -> logAgentMap.put(StringUtils.substringBefore(k, SYMBOL_COLON), v));
        return logAgentMap;
    }

    private void printMangerInfo() {
        List<String> remoteAddress = publishConfigService.getAllAgentList();
        if (COUNT_INCR.getAndIncrement() % 200 == 0) {
            log.info("连接的agent机器远程地址集合为:{}", gson.toJson(remoteAddress));
        }
    }

    public String queryNodeIpByPodIp(String ip) {
        return ip;
    }


    @Override
    public void publishIncrementDel(Long tailId, Long milogAppId, List<String> ips) {
        log.info("删除配置同步到 logAgent,tailId:{},milogAppId:{},ips:{}", tailId, milogAppId, gson.toJson(ips));
        AppLogMeta appLogMeta = new AppLogMeta();
        LogPattern logPattern = new LogPattern();
        MilogAppTopicRelDO appTopicRel = milogAppTopicRelDao.queryById(milogAppId);
        logPattern.setLogtailId(tailId);
        logPattern.setOperateEnum(OperateEnum.DELETE_OPERATE);
        appLogMeta.setAppId(appTopicRel.getAppId());
        appLogMeta.setAppName(appTopicRel.getAppName());
        appLogMeta.setLogPatternList(Arrays.asList(logPattern));
        ips.forEach(ip -> {
            LogCollectMeta logCollectMeta = new LogCollectMeta();
            logCollectMeta.setAgentIp(ip);
            logCollectMeta.setAgentMachine("");
            logCollectMeta.setAgentId("");
            logCollectMeta.setAppLogMetaList(Arrays.asList(appLogMeta));
            // todo 获取并设置agent全局filter
            AgentDefine agentDefine = new AgentDefine();
            agentDefine.setFilters(new ArrayList<>());
            logCollectMeta.setAgentDefine(agentDefine);
            sengConfigToAgent(ip, logCollectMeta);
        });
    }

    @Override
    public Result<String> agentOfflineBatch(MilogAgentIpParam agentIpParam) {
        if (null == agentIpParam || CollectionUtils.isEmpty(agentIpParam.getIps())) {
            return Result.failParam("ip不能为空");
        }
        return null;
    }

    @Override
    public LogCollectMeta getLogCollectMetaFromManager(String ip) {
        return queryMilogAgentConfig("", ip, "");
    }

    /**
     * 查询该物理机IP下的全量配置
     *
     * @param agentId
     * @param agentIp      物理机IP
     * @param agentMachine
     * @return
     */
    public LogCollectMeta queryMilogAgentConfig(String agentId, String agentIp, String agentMachine) {
        LogCollectMeta logCollectMeta = buildLogCollectMeta(agentIp);
        List<AppBaseInfo> appBaseInfos = Lists.newArrayList();
        List<MilogLogTailDo> logTailDos = milogLogtailDao.queryByIp(agentIp);
        if (CollectionUtils.isNotEmpty(logTailDos)) {
            appBaseInfos = heraAppService.queryByIds(
                    logTailDos.stream().map(MilogLogTailDo::getMilogAppId)
                            .distinct()
                            .collect(Collectors.toList())
            );
        }
        logCollectMeta.setAppLogMetaList(appBaseInfos.stream()
                .map(appBaseInfo -> assembleSingleConfig(appBaseInfo.getId().longValue(), queryLogPattern(appBaseInfo.getId().longValue(), agentIp, appBaseInfo.getPlatformType())))
                .filter(appLogMeta -> CollectionUtils.isNotEmpty(appLogMeta.getLogPatternList()))
                .collect(Collectors.toList()));
        return logCollectMeta;
    }

    private LogCollectMeta buildLogCollectMeta(String agentIp) {
        LogCollectMeta logCollectMeta = new LogCollectMeta();
        logCollectMeta.setAgentIp(agentIp);
        logCollectMeta.setAgentMachine("");
        logCollectMeta.setAgentId("");
        return logCollectMeta;
    }

    /**
     * 组装增量配置
     *
     * @param milogAppId
     * @param logPatternList
     * @return
     */
    private AppLogMeta assembleSingleConfig(Long milogAppId, List<LogPattern> logPatternList) {
        MilogAppTopicRelDO appInfo = milogAppTopicRelDao.queryById(milogAppId);
        AppLogMeta appLogMeta = new AppLogMeta();
        appLogMeta.setAppId(appInfo.getAppId());
        appLogMeta.setAppName(appInfo.getAppName());
        appLogMeta.setLogPatternList(logPatternList);
        return appLogMeta;
    }

    private MQConfig decorateMQConfig(MilogLogTailDo milogLogtailDo) {
        MQConfig mqConfig = new MQConfig();
        try {
            Long mqResourceId = logstoreDao.queryById(milogLogtailDo.getStoreId()).getMqResourceId();

            List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(milogLogtailDo.getMilogAppId(), mqResourceId, milogLogtailDo.getId());

            if (CollectionUtils.isEmpty(milogAppMiddlewareRels)) {
                milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(milogLogtailDo.getMilogAppId(), null, milogLogtailDo.getId());
            }

            MilogAppMiddlewareRel milogAppMiddlewareRel = milogAppMiddlewareRels.get(milogAppMiddlewareRels.size() - 1);

            MilogMiddlewareConfig middlewareConfig = milogMiddlewareConfigDao.queryById(milogAppMiddlewareRel.getMiddlewareId());
            if (MiddlewareEnum.ROCKETMQ.getCode().equals(middlewareConfig.getType())) {
                mqConfig.setClusterInfo(middlewareConfig.getNameServer());
                fillMqConfigData(mqConfig, MiddlewareEnum.ROCKETMQ.getName(), middlewareConfig, milogAppMiddlewareRel.getConfig());
            }
        } catch (Exception e) {
            log.error("组装mq配置信息异常,data:{}", gson.toJson(milogLogtailDo), e);
        }
        return mqConfig;
    }

    private void fillMqConfigData(MQConfig mqConfig, String typeName, MilogMiddlewareConfig middlewareConfig, MilogAppMiddlewareRel.Config config) {
        mqConfig.setType(typeName);
        mqConfig.setAk(middlewareConfig.getAk());
        mqConfig.setProducerGroup(config.getConsumerGroup());
        mqConfig.setSk(middlewareConfig.getSk());
        mqConfig.setTopic(config.getTopic());
        mqConfig.setTag(config.getTag());
        mqConfig.setPartitionCnt(config.getPartitionCnt());
        mqConfig.setEsConsumerGroup(config.getEsConsumerGroup());
        mqConfig.setBatchSendSize(config.getBatchSendSize());
    }

    private List<LogPattern> queryLogPattern(Long milogAppId, String agentIp, Integer type) {
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryByAppIdAgentIp(milogAppId, agentIp);
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            return milogLogtailDos.stream().map(milogLogtailDo -> {
                log.info("assemble data:{}", gson.toJson(milogAppId));
                LogPattern logPattern = generateLogPattern(milogLogtailDo);
                logPattern.setIps(Lists.newArrayList(agentIp));
                LogPathMapping logPathMapping = logPathMappingFactory.queryLogPathMappingByAppType(type);
                HeraEnvIpService heraEnvIpService = heraEnvIpServiceFactory.getHeraEnvIpServiceByAppType(type);
                try {
                    logPattern.setLogPattern(logPathMapping.getLogPath(milogLogtailDo.getLogPath(), null));
                    logPattern.setLogSplitExpress(logPathMapping.getLogPath(milogLogtailDo.getLogSplitExpress(), null));
                    logPattern.setIps(heraEnvIpService.queryActualIps(milogLogtailDo.getIps(), agentIp));
                } catch (Exception e) {
                    log.error("assemble log path data error:", e);
                }
                //设置mq配置
                MQConfig mqConfig = decorateMQConfig(milogLogtailDo);
                logPattern.setMQConfig(mqConfig);
                return logPattern;
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private LogPattern generateLogPattern(MilogLogTailDo milogLogtailDo) {
        LogPattern logPattern = new LogPattern();
        MilogLogStoreDO milogLogstoreDO = logstoreDao.queryById(milogLogtailDo.getStoreId());
        logPattern.setLogtailId(milogLogtailDo.getId());
        logPattern.setTailName(milogLogtailDo.getTail());
        logPattern.setLogPattern(milogLogtailDo.getLogPath());
        logPattern.setLogSplitExpress(milogLogtailDo.getLogSplitExpress());
        logPattern.setFilters(milogLogtailDo.getFilter());
        logPattern.setFirstLineReg(milogLogtailDo.getFirstLineReg());
        if (null != milogLogstoreDO && null != milogLogstoreDO.getLogType()) {
            logPattern.setLogType(milogLogstoreDO.getLogType());
            if (LogTypeEnum.NGINX.getType().equals(milogLogstoreDO.getLogType())) {
                logPattern.setLogPattern(milogLogtailDo.getLogPath());
            }
        }
        String tag = Utils.createTag(milogLogtailDo.getSpaceId(), milogLogtailDo.getStoreId(), milogLogtailDo.getId());
        logPattern.setPatternCode(tag);
        return logPattern;
    }
}
