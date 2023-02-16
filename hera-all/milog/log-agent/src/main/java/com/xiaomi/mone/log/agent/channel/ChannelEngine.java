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

package com.xiaomi.mone.log.agent.channel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.agent.channel.comparator.*;
import com.xiaomi.mone.log.agent.channel.listener.DefaultFileMonitorListener;
import com.xiaomi.mone.log.agent.channel.listener.FileMonitorListener;
import com.xiaomi.mone.log.agent.channel.locator.ChannelDefineJsonLocator;
import com.xiaomi.mone.log.agent.channel.locator.ChannelDefineLocator;
import com.xiaomi.mone.log.agent.channel.locator.ChannelDefineRpcLocator;
import com.xiaomi.mone.log.agent.channel.memory.AgentMemoryService;
import com.xiaomi.mone.log.agent.channel.memory.AgentMemoryServiceImpl;
import com.xiaomi.mone.log.agent.common.ExecutorUtil;
import com.xiaomi.mone.log.agent.exception.AgentException;
import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.agent.export.Output;
import com.xiaomi.mone.log.agent.export.RmqOutput;
import com.xiaomi.mone.log.agent.export.TalosOutput;
import com.xiaomi.mone.log.agent.export.impl.RmqExporter;
import com.xiaomi.mone.log.agent.filter.FilterChain;
import com.xiaomi.mone.log.agent.input.Input;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.utils.NetUtil;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author shanwb
 * @date 2021-07-20
 */
@Service
@Slf4j
public class ChannelEngine {

    /**
     * 按nameSrvAddr 初始化MQProducer
     */
    private ConcurrentHashMap<String, DefaultMQProducer> producerMap;

    //private ConcurrentHashMap<String, TalosProducer> talosProducerMap;

    private AgentMemoryService agentMemoryService;

    private ChannelDefineLocator channelDefineLocator;
    /**
     * 服务启动时全量拉取的配置
     */
    private List<ChannelDefine> channelDefineList = Lists.newArrayList();

    private volatile List<ChannelService> channelServiceList;
    /**
     * 文件监听器
     */
    private FileMonitorListener fileMonitorListener;

    private static byte[] lock = new byte[0];

    private Gson gson = Constant.GSON;

    @Getter
    private volatile boolean initComplete;

    private String opentelemetryTopicOld;
    private String opentelemetryTopicNew;


    public void init() {
        List<Long> failedChannelId = Lists.newArrayList();
        try {
            Config config = Ioc.ins().getBean(Config.class.getName());
            String memoryBasePath = config.get("agent.memory.path", AgentMemoryService.DEFAULT_BASE_PATH);
            opentelemetryTopicOld = config.get("talos.opentelemetry.topic.old", null);
            opentelemetryTopicNew = config.get("talos.opentelemetry.topic.new", null);
            producerMap = new ConcurrentHashMap<>(128);
            //talosProducerMap = new ConcurrentHashMap<>(512);

            channelDefineLocator = getChannelDefineLocator(config);
            channelDefineList = new CopyOnWriteArrayList<>(channelDefineLocator.getChannelDefine());
            log.info("current agent all config meta:{}", gson.toJson(channelDefineList));
            agentMemoryService = new AgentMemoryServiceImpl(memoryBasePath);
            fileMonitorListener = new DefaultFileMonitorListener();

            log.info("query channelDefineList:{}", gson.toJson(channelDefineList));
            channelServiceList = channelDefineList.stream()
                    .map(channelDefine -> {
                        ChannelService channelService = this.channelServiceTrans(channelDefine);
                        if (null == channelService) {
                            failedChannelId.add(channelDefine.getChannelId());
                        }
                        return channelService;
                    }).filter(channelService -> null != channelService)
                    .collect(Collectors.toList());
            // 删除失败的channel
            deleteFailedChannel(failedChannelId, this.channelDefineList, this.channelServiceList);
            channelServiceList = new CopyOnWriteArrayList<>(channelServiceList);
            // 启动channel
            channelStart(channelServiceList);
            //关机-回调操作
            graceShutdown();
            //10s一次 上报channel进度
            exportChannelState();
            log.info("current channelDefineList:{},current channelServiceList:{}",
                    gson.toJson(this.channelDefineList), gson.toJson(this.channelServiceList.stream().map(ChannelService::instanceId).collect(Collectors.toList())));

        } catch (Exception e) {
            log.error("ChannelEngine init exception", e);
        } finally {
            initComplete = true;
        }
    }

    private ChannelDefineLocator getChannelDefineLocator(Config config) {
        String locatorType = config.get("agent.channel.locator", "rpc");
        log.warn("locatorType: {}", locatorType);
        switch (locatorType) {
            case "json":
                return new ChannelDefineJsonLocator();
            default:
                return new ChannelDefineRpcLocator();
        }
    }

    private void exportChannelState() {
        ExecutorUtil.scheduleAtFixedRate(() -> {
            List<ChannelState> channelStateList = channelServiceList.stream()
                    .map(c -> c.state())
                    .collect(Collectors.toList());
            // 发送收集进度
            sendCollectionProgress(channelStateList);
        }, 10, 10, TimeUnit.SECONDS);
    }

    private List<Long> channelStart(List<ChannelService> channelServiceList) {
        List<Long> failedChannelIds = Lists.newArrayList();
        List<Long> successChannelIds = Lists.newArrayList();
        // 启动channel
        for (ChannelService channelService : channelServiceList) {
            ChannelServiceImpl realChannelService = (ChannelServiceImpl) channelService;
            log.info("realChannelService,id:{}", realChannelService.getChannelId());
            try {
                channelService.start();
                fileMonitorListener.addChannelService(realChannelService);
                successChannelIds.add(realChannelService.getChannelId());
            } catch (Exception e) {
                Long channelId = ((ChannelServiceImpl) channelService).getChannelId();
                failedChannelIds.add(channelId);
                log.error("start channel exception,channelId:{}", channelId, e);
            }
        }
        deleteFailedChannel(failedChannelIds, this.channelDefineList, this.channelServiceList);
        return successChannelIds;
    }

    private void deleteFailedChannel(List<Long> failedChannelId, List<ChannelDefine> defineList, List<ChannelService> serviceList) {
        if (CollectionUtils.isNotEmpty(failedChannelId)) {
            //处理从当前队列中摘掉
            for (Long delChannelId : failedChannelId) {
                defineList.removeIf(channelDefine -> channelDefine.getChannelId().equals(delChannelId));
                serviceList.removeIf(channelService -> ((ChannelServiceImpl) channelService).getChannelId().equals(delChannelId));
            }
        }
    }

    private void graceShutdown() {
        //关闭操作
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("shutdown hook begin!");
            for (ChannelService c : channelServiceList) {
                try {
                    c.close();
                } catch (Exception e) {
                    log.error("shutdown channel exception:{}", e);
                }
            }
            log.info("shutdown hook end!");
        }));
    }

    private ChannelService channelServiceTrans(ChannelDefine channelDefine) {
        try {
            preCheckChannelDefine(channelDefine);
            Output output = channelDefine.getOutput();
            MsgExporter exporter = exporterTrans(output);
            if (null == exporter) {
                throw new IllegalArgumentException("cant not trans to MsgExporter, output:" + gson.toJson(output));
            }
            FilterChain filterChain = new FilterChain();
            filterChain.loadFilterList(channelDefine.getFilters());
            filterChain.reset();
            ChannelService channelService = new ChannelServiceImpl(exporter, agentMemoryService, channelDefine, filterChain);
            return channelService;
        } catch (Throwable e) {
            log.error("channelServiceTrans exception, channelDefine:{}, exception:{}", gson.toJson(channelDefine), e);
        }
        return null;
    }

    private void preCheckChannelDefine(ChannelDefine channelDefine) {
        Preconditions.checkArgument(null != channelDefine, "channelDefine can not be null");
        Preconditions.checkArgument(null != channelDefine.getInput(), "channelDefine.input can not be null");
        Preconditions.checkArgument(null != channelDefine.getOutput(), "channelDefine.output can not be null");
        Preconditions.checkArgument(null != channelDefine.getChannelId(), "channelDefine.channelId can not be null");
        preCheckOutput(channelDefine.getOutput());

        Input input = channelDefine.getInput();
        String logPattern = input.getLogPattern();
        Preconditions.checkArgument(null != logPattern, "channelDefine.logPattern can not be null");

    }

    private void preCheckOutput(Output output) {
        Preconditions.checkArgument(StringUtils.isNotBlank(output.getOutputType()), "outputType can not be null");
        switch (output.getOutputType()) {
            case Output.OUTPUT_ROCKETMQ:
                RmqOutput rmqOutput = (RmqOutput) output;
                Preconditions.checkArgument(null != rmqOutput.getClusterInfo(), "rmqOutput.getClusterInfo can not be null");
                Preconditions.checkArgument(null != rmqOutput.getTopic(), "rmqOutput.getTopic can not be null");
                Preconditions.checkArgument(null != rmqOutput.getProducerGroup(), "rmqOutput.getProducerGroup can not be null");
                break;
            case Output.OUTPUT_TALOS:
                TalosOutput talosOutput = (TalosOutput) output;
                Preconditions.checkArgument(null != talosOutput.getClusterInfo(), "talosOutput.getClusterInfo can not be null");
                Preconditions.checkArgument(null != talosOutput.getTopic(), "talosOutput.getTopic can not be null");
                break;
            default:
                break;
        }
    }

    private MsgExporter exporterTrans(Output output) {
        if (null == output) {
            return null;
        }

        String outputType = output.getOutputType();
        switch (outputType) {
            case Output.OUTPUT_ROCKETMQ:
                RmqOutput rmqOutput = (RmqOutput) output;
                String nameSrvAddr = rmqOutput.getClusterInfo();
                DefaultMQProducer mqProducer = producerMap.get(nameSrvAddr);
                if (null == mqProducer) {
                    mqProducer = initMqProducer(rmqOutput);
                    producerMap.put(String.valueOf(nameSrvAddr), mqProducer);
                }

                RmqExporter rmqExporter = new RmqExporter(mqProducer);
                rmqExporter.setRmqTopic(rmqOutput.getTopic());
                rmqExporter.setBatchSize(rmqOutput.getBatchExportSize());

                return rmqExporter;
            case Output.OUTPUT_TALOS:
                return null;
            default:
                break;
        }
        return null;
    }

    private DefaultMQProducer initMqProducer(RmqOutput rmqOutput) {
        DefaultMQProducer producer = null;
        producer = new DefaultMQProducer(rmqOutput.getProducerGroup() + "x", true);
        producer.setNamesrvAddr(rmqOutput.getClusterInfo());
        try {
            producer.start();
            return producer;
        } catch (MQClientException e) {
            log.error("ChannelBootstrap.initMqProducer error, RocketmqConfig: {}", rmqOutput, e);
            throw new AgentException("initMqProducer exception", e);
        }
    }

    /**
     * 刷新配置(增量配置和全量配置来时刷新已经存在的配置)
     *
     * @param channelDefines
     */
    public void refresh(List<ChannelDefine> channelDefines) {
        log.info("[config change],changed data:{},origin data:{}", gson.toJson(channelDefines),
                gson.toJson(channelDefineList));
        try {
            if (CollectionUtils.isNotEmpty(channelDefines) &&
                    !CollectionUtils.isEqualCollection(channelDefines, channelDefineList)) {
                // 新增配置
                addConfig(channelDefines, false);
                // 修改的的更新
                updateConfig(channelDefines);
                /**
                 * 单项配置处理 不删除
                 */
                if (channelDefines.size() == 1 && channelDefines.get(0).getSingleMetaData() != null
                        && channelDefines.get(0).getSingleMetaData()) {
                    return;
                }
                // 删除的配置
                deleteConfig(channelDefines, false);
            }
        } catch (Exception e) {
            log.error("refresh error,[config change],changed data:{},origin data:{}",
                    gson.toJson(channelDefines), gson.toJson(channelDefineList), e);
        }
    }

    /**
     * 停止指定前缀的文件采集，对#refresh 方法进行补充
     * 适用于容器环境下pod的删除，停止对应的文件采集
     *
     * @param filePrefixList
     */
    public void stopChannelFile(List<String> filePrefixList) {
        log.warn("stop pod file craw:{}", gson.toJson(filePrefixList));
        if (CollectionUtils.isEmpty(filePrefixList)) {
            return;
        }

        for (ChannelService c : channelServiceList) {
            try {
                c.stopFile(filePrefixList);
            } catch (Exception e) {
                log.warn("stopFile exception", e);
            }
        }
    }

    /**
     * 新增配置
     *
     * @param channelDefines
     */
    private void addConfig(List<ChannelDefine> channelDefines, boolean directAdd) {
        try {
            // 新增的，进行初始化
            List<ChannelDefine> channelDefinesDifference = differenceSet(channelDefines, channelDefineList);
            if (directAdd) {
                channelDefinesDifference = channelDefines;
            }
            if (directAdd || CollectionUtils.isNotEmpty(channelDefinesDifference)) {
                log.info("[add config]data:{}", gson.toJson(channelDefinesDifference));
                initIncrement(channelDefinesDifference);
            }
        } catch (Exception e) {
            log.error("addConfig error,source channelDefines:{},origin channelDefines:{},directAdd:{}",
                    gson.toJson(channelDefines), gson.toJson(channelDefineList), directAdd, e);
        }
    }

    /**
     * 更新配置(
     * 1.找到变化了的配置
     * 2.删除原配置
     * 3.重新增加配置
     * )
     *
     * @param channelDefines
     */
    private void updateConfig(List<ChannelDefine> channelDefines) {
        List<ChannelDefine> channelDefinesIntersection = intersection(channelDefines, channelDefineList);
        if (CollectionUtils.isNotEmpty(channelDefinesIntersection)) {
            List<ChannelDefine> changedDefines = Lists.newArrayList();
            log.info("have exist config:{}", new Gson().toJson(channelDefineList));
            Iterator<ChannelDefine> iterator = channelDefinesIntersection.iterator();
            while (iterator.hasNext()) {
                ChannelDefine newChannelDefine = iterator.next();
                // 旧channelDefine
                Long channelId = newChannelDefine.getChannelId();
                ChannelDefine oldChannelDefine = channelDefineList.stream()
                        .filter(channelDefine -> channelDefine.getChannelId().equals(channelId))
                        .findFirst().orElse(null);
                if (null != oldChannelDefine) {
                    // 比较器
                    SimilarComparator appSimilarComparator = new AppSimilarComparator(oldChannelDefine.getAppId());
                    SimilarComparator inputSimilarComparator = new InputSimilarComparator(oldChannelDefine.getInput());
                    SimilarComparator outputSimilarComparator = new OutputSimilarComparator(oldChannelDefine.getOutput());
                    FilterSimilarComparator filterSimilarComparator = new FilterSimilarComparator(oldChannelDefine.getFilters());
                    // 将opentelemetry的老topic替换成新的，为了平滑过渡，不用过渡配置
                    TalosOutput talosOutput = (TalosOutput) newChannelDefine.getOutput();
                    if (opentelemetryTopicOld.equals(talosOutput.getTopic())) {
                        talosOutput.setTopic(opentelemetryTopicNew);
//                        log.info("set topic:{},newChannelDefine:{}", opentelemetryTopicNew, gson.toJson(newChannelDefine));
                    }
                    if (appSimilarComparator.compare(newChannelDefine.getAppId())
                            && inputSimilarComparator.compare(newChannelDefine.getInput())
                            && outputSimilarComparator.compare(newChannelDefine.getOutput())) {
                        if (!filterSimilarComparator.compare(newChannelDefine.getFilters())) {
                            channelServiceList.stream()
                                    .filter(channelService -> ((ChannelServiceImpl) channelService).getChannelId().equals(channelId))
                                    .findFirst()
                                    .ifPresent(channelService -> channelService.filterRefresh(newChannelDefine.getFilters()));
                        }
                    } else {
                        log.info("config changed,old:{},new:{}", gson.toJson(oldChannelDefine), gson.toJson(newChannelDefine));
                        changedDefines.add(newChannelDefine);
                        deleteConfig(Arrays.asList(newChannelDefine), true);
                        addConfig(Arrays.asList(newChannelDefine), true);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(changedDefines)) {
                log.info("[update config]data:{}", gson.toJson(changedDefines));
            }
        }
    }

    /**
     * 删除配置
     *
     * @param channelDefines
     */
    private void deleteConfig(List<ChannelDefine> channelDefines, boolean directDel) {
        List<ChannelDefine> channelDels = channelDefines.stream()
                .filter(channelDefine -> null != channelDefine.getOperateEnum()
                        && channelDefine.getOperateEnum().getCode().equals(OperateEnum.DELETE_OPERATE.getCode()))
                .collect(Collectors.toList());
        if (directDel) {
            channelDels = channelDefines;
        }
        try {
            if (directDel || CollectionUtils.isNotEmpty(channelDels)) {
                log.info("[delete config]data:{}", gson.toJson(channelDels));
                List<Long> channelIdDels = channelDels.stream().map(ChannelDefine::getChannelId).collect(Collectors.toList());
                List<ChannelService> tempChannelServiceList = Lists.newArrayList();
                channelServiceList.forEach(channelService -> {
                    Long channelId = ((ChannelServiceImpl) channelService).getChannelId();
                    if (channelIdDels.contains(channelId)) {
                        log.info("[delete config]channelService:{}", channelId);
                        channelService.close();
                        fileMonitorListener.removeChannelService(channelService);
                        tempChannelServiceList.add(channelService);
                        this.channelDefineList.removeIf(channelDefine -> {
                            if (channelDefine.getChannelId().equals(channelId)) {
                                //删除mq
                                Output output = channelDefine.getOutput();
                                if (output.getOutputType().equals(Output.OUTPUT_TALOS)) {
                                }
                                if (output.getOutputType().equals(Output.OUTPUT_ROCKETMQ)) {
                                    RmqOutput rmqOutput = (RmqOutput) output;
                                    String nameSrvAddr = rmqOutput.getClusterInfo();
//                                    producerMap.remove(nameSrvAddr);
                                }
                                return true;
                            }
                            return false;
                        });
                    }
                });
                if (CollectionUtils.isNotEmpty(tempChannelServiceList)) {
                    channelServiceList.removeAll(tempChannelServiceList);
                }
            }
        } catch (Exception e) {
            log.error(String.format("delete config exception,config:%s", gson.toJson(channelDels)), e);
        }
    }

    /**
     * 差集
     *
     * @param origin 新老的配置
     * @param source 旧的配置
     * @return
     */
    private List<ChannelDefine> differenceSet(List<ChannelDefine> origin, List<ChannelDefine> source) {
        if (CollectionUtils.isEmpty(source)) {
            return origin;
        }
        List<Long> sourceIds = source.stream().map(ChannelDefine::getChannelId).collect(Collectors.toList());
        return origin.stream().filter(channelDefine -> !sourceIds.contains(
                channelDefine.getChannelId()) && OperateEnum.DELETE_OPERATE != channelDefine.getOperateEnum())
                .collect(Collectors.toList());
    }


    /**
     * 交集
     *
     * @param origin
     * @param source
     * @return
     */
    private List<ChannelDefine> intersection(List<ChannelDefine> origin, List<ChannelDefine> source) {
        List<Long> sourceIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(source)) {
            sourceIds = source.stream().map(ChannelDefine::getChannelId).collect(Collectors.toList());
        }
        List<Long> finalSourceIds = sourceIds;
        return origin.stream().filter(channelDefine -> finalSourceIds.contains(
                channelDefine.getChannelId()) && OperateEnum.DELETE_OPERATE != channelDefine.getOperateEnum())
                .collect(Collectors.toList());
    }

    /**
     * 新增配置初始化
     *
     * @param definesIncrement
     */
    public void initIncrement(List<ChannelDefine> definesIncrement) {
        List<Long> failedChannelId = Lists.newArrayList();
        List<ChannelService> channelServices = definesIncrement.stream()
                .filter(Objects::nonNull)
                .map(channelDefine -> {
                    ChannelService channelService = channelServiceTrans(channelDefine);
                    if (null == channelService) {
                        failedChannelId.add(channelDefine.getChannelId());
                    }
                    return channelService;
                }).filter(channelService -> null != channelService)
                .collect(Collectors.toList());
        deleteFailedChannel(failedChannelId, definesIncrement, channelServices);
        List<Long> successChannelIds = channelStart(channelServices);
        if (CollectionUtils.isNotEmpty(successChannelIds)) {
            this.channelServiceList.addAll(channelServices.stream()
                    .filter(channelService -> successChannelIds.contains(((ChannelServiceImpl) channelService).getChannelId()))
                    .collect(Collectors.toList()));
            this.channelDefineList.addAll(definesIncrement.stream().
                    filter(channelDefine -> successChannelIds.contains(channelDefine.getChannelId()))
                    .collect(Collectors.toList()));
        }
        log.info("[add config] after current channelDefineList:{},channelServiceList:{}",
                gson.toJson(this.channelDefineList), gson.toJson(gson.toJson(channelServiceList.stream().map(ChannelService::instanceId).collect(Collectors.toList()))));
    }


    /**
     * 发送采集进度
     *
     * @param
     */
    private void sendCollectionProgress(List<ChannelState> channelStateList) {
        SafeRun.run(() -> {
            if (CollectionUtils.isEmpty(channelStateList)) {
                return;
            }
            UpdateLogProcessCmd processCmd = assembleParam(channelStateList);
            RpcClient rpcClient = Ioc.ins().getBean(RpcClient.class);
            RemotingCommand req = RemotingCommand.createRequestCommand(Constant.RPCCMD_AGENT_CODE);
            req.setBody(new Gson().toJson(processCmd).getBytes());
            rpcClient.sendMessage(req);
            log.debug("send collect progress,data:{}", gson.toJson(processCmd));
        });
    }

    private UpdateLogProcessCmd assembleParam(List<ChannelState> channelStateList) {
        UpdateLogProcessCmd cmd = new UpdateLogProcessCmd();
        try {
            cmd.setIp(NetUtil.getLocalIp());
            List<UpdateLogProcessCmd.CollectDetail> collects = Lists.newArrayList();
            List<UpdateLogProcessCmd.CollectDetail> finalCollects = collects;
            channelStateList.forEach(channelState -> {

                UpdateLogProcessCmd.CollectDetail collectDetail = new UpdateLogProcessCmd.CollectDetail();
                collectDetail.setTailId(channelState.getTailId().toString());
                collectDetail.setAppId(channelState.getAppId());
                collectDetail.setTailName(channelState.getTailName());
                collectDetail.setAppName(channelState.getAppName());
                collectDetail.setIpList(channelState.getIpList());
                collectDetail.setPath(channelState.getLogPattern());

                List<UpdateLogProcessCmd.FileProgressDetail> progressDetails = channelState.getStateProgressMap()
                        .entrySet().stream()
                        .map(entry -> UpdateLogProcessCmd.FileProgressDetail
                                .builder()
                                .fileRowNumber(entry.getValue().getCurrentRowNum())
                                .collectTime(channelState.getCollectTime())
                                .pointer(entry.getValue().getPointer())
                                .fileMaxPointer(entry.getValue().getFileMaxPointer())
                                .collectPercentage(getPercent(entry.getValue().getPointer(), entry.getValue().getFileMaxPointer()))
                                .configIp(entry.getValue().getIp())
                                .pattern(entry.getKey())
                                .build()).collect(Collectors.toList());
                collectDetail.setFileProgressDetails(progressDetails);
                finalCollects.add(collectDetail);
            });
            //进度去重
            collects = collects.stream().distinct().collect(Collectors.toList());
            cmd.setCollectList(collects);
            return cmd;
        } catch (Exception e) {
            log.error("send collect data progress wrap data error", e);
        }
        return cmd;
    }

    private String getPercent(Long pointer, Long maxPointer) {
        if (null == pointer || pointer == 0 || null == maxPointer || maxPointer == 0) {
            return 0 + "%";
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(((float) pointer / (float) maxPointer) * 100) + "%";
    }
}
