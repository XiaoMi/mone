package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.meta.*;
import com.xiaomi.mone.log.api.model.vo.LogCmd;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.dao.*;
import com.xiaomi.mone.log.manager.domain.LogProcess;
import com.xiaomi.mone.log.manager.model.bo.MilogAgentIpParam;
import com.xiaomi.mone.log.manager.model.dto.AgentLogProcessDTO;
import com.xiaomi.mone.log.manager.model.dto.MotorRoomDTO;
import com.xiaomi.mone.log.manager.model.dto.PodDTO;
import com.xiaomi.mone.log.manager.model.pojo.*;
import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;
import com.xiaomi.mone.log.manager.service.MilogAgentService;
import com.xiaomi.mone.log.manager.service.env.HeraEnvIpService;
import com.xiaomi.mone.log.manager.service.env.HeraEnvIpServiceFactory;
import com.xiaomi.mone.log.manager.service.path.LogPathMapping;
import com.xiaomi.mone.log.manager.service.path.LogPathMappingFactory;
import com.xiaomi.mone.log.utils.NetUtil;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COLON;

@Service
@Slf4j
public class MilogAgentServiceImpl implements MilogAgentService {

    @Resource
    private LogPathMappingFactory logPathMappingFactory;

    @Resource
    private HeraEnvIpServiceFactory heraEnvIpServiceFactory;

    @Resource
    private RpcServer rpcServer;

    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private LogstoreDao logstoreDao;

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

    private static final ThreadPoolExecutor THREADPOOL_EXECUTOR;

    private static final Integer K8S_DEPLOYED = 5;

    private static final String IGNORE_APP_NAME = "milog-agent";

    static {
        THREADPOOL_EXECUTOR = new ThreadPoolExecutor(6, 20,
                1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(200),
                new NamedThreadFactory("coll-base-data-start", true),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        THREADPOOL_EXECUTOR.allowCoreThreadTimeOut(true);
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

    public Result<String> configIssueAgentK8s(Long tailId, String agentIp,
                                              List<LogAgentListBo> logAgentListBos, List<String> podNames) {
        if (StringUtils.isEmpty(agentIp)) {
            return Result.failParam("agentIp不能为空");
        }
        // 1.查询该agent机器下下全量的配置
        LogCollectMeta logCollectMeta = queryMilogAgentConfigK8s(tailId, agentIp, logAgentListBos);
        logCollectMeta.setPodNames(podNames);
        logCollectMeta.setSingleMetaData(true);
        log.info("{},this k8sip config data:{}", agentIp, gson.toJson(logCollectMeta));
        List<String> ipAddress = new ArrayList<>(getAgentChannelMap().keySet());
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
    private void sengConfigToAgent(final String agentIp, LogCollectMeta logCollectMeta) {
        // 放在线程池中 执行
        THREADPOOL_EXECUTOR.execute(() -> {
            int count = 1;
            while (count < 10) {
                Map<String, AgentChannel> logAgentMap = getAgentChannelMap();
                String agentCurrentIp = queryCurrentDockerAgentIP(agentIp, logAgentMap);
                if (logAgentMap.containsKey(agentCurrentIp)) {
                    String sendStr = gson.toJson(logCollectMeta);
                    if (CollectionUtils.isNotEmpty(logCollectMeta.getAppLogMetaList())) {
                        RemotingCommand req = RemotingCommand.createRequestCommand(LogCmd.logReq);
                        req.setBody(sendStr.getBytes());
                        log.info("发送配置：agent ip:{},配置信息:{}", agentCurrentIp, sendStr);
                        Stopwatch started = Stopwatch.createStarted();
                        RemotingCommand res = rpcServer.sendMessage(logAgentMap.get(agentCurrentIp), req, 10000);
                        started.stop();
                        String response = new String(res.getBody());
                        log.info("配置发送成功---->{},时长：{}s,agentIp:{}", response, started.elapsed().getSeconds(), agentCurrentIp);
                        if (Objects.equals(response, "ok")) {
                            break;
                        }
                    }
                } else {
                    log.info("当前agent ip没有连接，ip:{},配置数据：{}", agentIp, gson.toJson(logCollectMeta));
                }
                //重试策略-重试10次，每次休眠5s
                try {
                    TimeUnit.SECONDS.sleep(5L);
                } catch (final InterruptedException ignored) {
                }
                count++;
            }
        });
    }

    @Nullable
    private String queryCurrentDockerAgentIP(String agentIp, Map<String, AgentChannel> logAgentMap) {
        if (Objects.equals(agentIp, NetUtil.getLocalIp())) {
            //for docker 处理当前机器上有agent
            final String tempIp = agentIp;
            List<String> ipList = getAgentChannelMap().keySet()
                    .stream().filter(ip -> ip.startsWith("172"))
                    .collect(Collectors.toList());
            Optional<String> optionalS = ipList.stream()
                    .filter(ip -> Objects.equals(logAgentMap.get(ip).getIp(), tempIp))
                    .findFirst();
            if (optionalS.isPresent()) {
                String correctIp = optionalS.get();
                log.info("origin ip:{},set agent ip:{}", agentIp, correctIp);
                agentIp = correctIp;
            }
        }
        return agentIp;
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
        MilogLogTailDo milogLogtailDo = milogLogtailDao.queryById(tailId);
        AppBaseInfo appBaseInfo = heraAppService.queryById(milogAppId);
        //根据应用和环境信息查询应用是用k8s部署还是非k8s部署,且k8s部署下业务使用的log-agent
        //是采用sidecar部署的还是deamonset部署
        HeraEnvIpService heraEnvIpService = heraEnvIpServiceFactory.getHeraEnvIpServiceByAppType(appBaseInfo.getAppType());
        Map<String, List<LogAgentListBo>> agentK8sMap = heraEnvIpService.queryAgentIpByPodIps(ips);
        if (null == agentK8sMap || agentK8sMap.isEmpty()) {
            log.info("physical machine config,milogAppId:{}", milogAppId);
            ips.forEach(ip -> {
                AppLogMeta appLogMeta = assembleSingleConfig(milogAppId, queryLogPattern(milogAppId, ip, appBaseInfo.getPlatformType()));
                LogCollectMeta logCollectMeta = new LogCollectMeta();
                logCollectMeta.setAgentIp(ip);
                logCollectMeta.setAppLogMetaList(Arrays.asList(appLogMeta));
                AgentDefine agentDefine = new AgentDefine();
                agentDefine.setFilters(new ArrayList<>());
                logCollectMeta.setAgentDefine(agentDefine);
                log.info("push agent config data:{}", gson.toJson(logCollectMeta));
                sengConfigToAgent(ip, logCollectMeta);
            });
        } else {
            log.info("k8s machine config:{}", milogAppId);
            logTailService.k8sPodIpsSend(milogLogtailDo.getId(), ips, Collections.EMPTY_LIST, appBaseInfo.getAppType());
        }
    }

    @NotNull
    private Map<String, AgentChannel> getAgentChannelMap() {
        Map<String, AgentChannel> logAgentMap = new HashMap<>();
        AgentContext.ins().map.forEach((k, v) -> logAgentMap.put(StringUtils.substringBefore(k, SYMBOL_COLON), v));
        return logAgentMap;
    }

    private void printMangerInfo() {
        List<String> remoteAddress = Lists.newArrayList();
        List<String> ipAddress = Lists.newArrayList();
        AgentContext.ins().map.entrySet().forEach(agentChannelEntry -> {
                    String key = agentChannelEntry.getKey();
                    remoteAddress.add(key);
                    ipAddress.add(StringUtils.substringBefore(key, SYMBOL_COLON));
                }
        );
        log.info("连接的agent机器远程地址集合为:{}", gson.toJson(remoteAddress));
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
            appBaseInfos = heraAppService.queryByIds(logTailDos.stream().map(MilogLogTailDo::getMilogAppId).collect(Collectors.toList()));
        }
        logCollectMeta.setAppLogMetaList(appBaseInfos.stream()
                .map(appBaseInfo -> assembleSingleConfig(appBaseInfo.getId().longValue(), queryLogPattern(appBaseInfo.getId().longValue(), agentIp, appBaseInfo.getPlatformType())))
                .filter(appLogMeta -> CollectionUtils.isNotEmpty(appLogMeta.getLogPatternList()))
                .collect(Collectors.toList()));
        return logCollectMeta;
    }

    public LogCollectMeta queryMilogAgentConfigK8s(Long tailId, String agentIp, List<LogAgentListBo> logAgentListBos) {
        LogCollectMeta logCollectMeta = buildLogCollectMeta(agentIp);
        List<Long> appIds = Lists.newArrayList();
        appIds.add(milogLogtailDao.queryById(tailId).getMilogAppId());

        logCollectMeta.setAppLogMetaList(appIds.stream()
                .map(appId -> assembleSingleConfig(appId, queryLogPattern(tailId, logAgentListBos)))
                .collect(Collectors.toList()));
        return logCollectMeta;
    }

    public LogCollectMeta queryMilogAgentConfigK8s(String agentIp, List<LogAgentListBo> podIps) {
        LogCollectMeta logCollectMeta = buildLogCollectMeta(agentIp);
        Map<MilogLogTailDo, List<LogAgentListBo>> milogLogtailDoListMap = wrapTailAndAgentRel(podIps);
        List<MilogLogTailDo> logtailDoList = milogLogtailDoListMap.keySet().stream().collect(Collectors.toList());
        Map<MilogAppTopicRelDO, List<MilogLogTailDo>> appTopicRelListMap = warpAppAdnTailRel(logtailDoList);

        List<MilogAppTopicRelDO> milogAppTopicRels = appTopicRelListMap.keySet().stream().collect(Collectors.toList());

        logCollectMeta.setAppLogMetaList(milogAppTopicRels.stream()
                .map(milogAppTopicRel -> assembleSingleConfig(milogAppTopicRel.getId(), queryLogPattern(appTopicRelListMap.get(milogAppTopicRel), milogLogtailDoListMap)))
                .filter(appLogMeta -> CollectionUtils.isNotEmpty(appLogMeta.getLogPatternList()))
                .collect(Collectors.toList()));
        return logCollectMeta;
    }

    private Map<MilogAppTopicRelDO, List<MilogLogTailDo>> warpAppAdnTailRel(List<MilogLogTailDo> logtailDoList) {
        Map<MilogAppTopicRelDO, List<MilogLogTailDo>> appTopicRelListMap = Maps.newHashMap();
        logtailDoList.forEach(milogLogtailDo -> {
            MilogAppTopicRelDO appTopicRel = milogAppTopicRelDao.queryById(milogLogtailDo.getMilogAppId());
            appTopicRelListMap.putIfAbsent(appTopicRel, Lists.newArrayList());
            appTopicRelListMap.get(appTopicRel).add(milogLogtailDo);
        });
        return appTopicRelListMap;
    }

    private Map<MilogLogTailDo, List<LogAgentListBo>> wrapTailAndAgentRel(List<LogAgentListBo> podIps) {
        Map<LogAgentListBo, List<MilogLogTailDo>> listBoListMap = Maps.newConcurrentMap();
        Map<MilogLogTailDo, List<LogAgentListBo>> logTailDoListMap = Maps.newConcurrentMap();
        podIps.parallelStream().forEach(logAgentBo -> listBoListMap.put(logAgentBo, milogLogtailDao.queryByIp(logAgentBo.getPodIP())));
        for (Map.Entry<LogAgentListBo, List<MilogLogTailDo>> entry : listBoListMap.entrySet()) {
            entry.getValue().forEach(miLogLogTailDo -> {
                logTailDoListMap.putIfAbsent(miLogLogTailDo, Lists.newArrayList());
                logTailDoListMap.get(miLogLogTailDo).add(entry.getKey());
            });
        }
        return logTailDoListMap;
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
//            MilogAppMiddlewareRel milogAppMiddlewareRel = null;
//            if (ProjectTypeEnum.MIS_TYPE.getCode().equals(milogLogtailDo.getAppType())) {
//                milogAppMiddlewareRel = handleMisAppMiddlewareConfigRel(milogLogtailDo);
//            } else {
//                List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(milogLogtailDo.getMilogAppId(), null, milogLogtailDo.getId());
//                if (CollectionUtils.isEmpty(milogAppMiddlewareRels)) {
//                    //取默认的配置
//                    MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryDefaultMiddlewareConfig();
//                    return mqConfig;
//                }
//                milogAppMiddlewareRel = milogAppMiddlewareRels.get(0);
//            }
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

    private List<LogPattern> queryLogPattern(List<MilogLogTailDo> milogLogtailDos, Map<MilogLogTailDo, List<LogAgentListBo>> milogLogtailDoListMap) {
        List<LogPattern> logPatternList = Lists.newArrayList();
        for (MilogLogTailDo milogLogtailDo : milogLogtailDos) {
            logPatternList.addAll(queryLogPattern(milogLogtailDo.getId(), milogLogtailDoListMap.get(milogLogtailDo)));
        }
        return logPatternList;
    }

    private List<LogPattern> queryLogPattern(Long tailId, List<LogAgentListBo> logAgentListBos) {
        MilogLogTailDo logTailDo = milogLogtailDao.queryById(tailId);
        if (null != logTailDo) {
            LogPattern logPattern = generateLogPattern(logTailDo);
            try {
                LogPathMapping logPathMapping = logPathMappingFactory.queryLogPathMappingByAppType(logTailDo.getAppType());
                HeraEnvIpService heraEnvIpService = heraEnvIpServiceFactory.getHeraEnvIpServiceByAppType(logTailDo.getAppType());
                logPattern.setLogPattern(logPathMapping.getLogPath(logTailDo.getLogPath(), logAgentListBos));
                logPattern.setLogSplitExpress(logPathMapping.getLogPath(logTailDo.getLogSplitExpress(), logAgentListBos));
                logPattern.setIps(heraEnvIpService.queryActualIps(logTailDo.getIps()));
            } catch (Exception e) {
                log.error("assemble log path data error:", e);
            }
            //设置mq配置
            MQConfig mqConfig = decorateMQConfig(logTailDo);
            logPattern.setMQConfig(mqConfig);
            return Lists.newArrayList(logPattern);
        }
        return Collections.emptyList();
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
                    logPattern.setIps(heraEnvIpService.queryActualIps(milogLogtailDo.getIps()));
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

    public String generateMisTailLogPath(String logPath, String agentIp, List<MotorRoomDTO> motorRooms) {
        if (StringUtils.isBlank(logPath)) {
            return StringUtils.EMPTY;
        }
        String logPathPrefix = "/home/work/logs";
        String logPathMiddle = "/neo-logs/";
        String logPathSuffix = StringUtils.substringAfter(logPath, logPathPrefix);
        String nodeNames = motorRooms.stream()
                .flatMap(motorRoomDTO -> motorRoomDTO.getPodDTOList().stream())
                .filter(podDTO -> StringUtils.equalsIgnoreCase(agentIp, podDTO.getNodeIP()))
                .sorted(Comparator.comparing(PodDTO::getPodIP))
                .map(PodDTO::getPodName).collect(Collectors.joining("|"));
        return new StringBuilder().append(logPathPrefix)
                .append(logPathMiddle)
                .append(String.format(nodeNames.split("\\|").length > 1 ? "(%s)" : "%s", nodeNames))
                .append(logPathSuffix).toString();
    }

    public List<String> generateMisTailIps(String agentIp, List<MotorRoomDTO> motorRooms) {
        if (StringUtils.isBlank(agentIp)) {
            return Collections.EMPTY_LIST;
        }
        return motorRooms.stream().flatMap(motorRoomDTO -> motorRoomDTO.getPodDTOList().stream())
                .filter(podDTO -> Objects.equals(agentIp, podDTO.getNodeIP()))
                .map(PodDTO::getPodIP)
                .sorted()
                .collect(Collectors.toList());
    }


    public List<String> generateK8sTailIps(String agentIp, List<LogAgentListBo> agentListBos) {
        if (StringUtils.isBlank(agentIp)) {
            return Collections.EMPTY_LIST;
        }
        return agentListBos.stream()
                .filter(podDTO -> Objects.equals(agentIp, podDTO.getAgentIP()))
                .map(LogAgentListBo::getPodIP)
                .sorted()
                .collect(Collectors.toList());
    }

}
