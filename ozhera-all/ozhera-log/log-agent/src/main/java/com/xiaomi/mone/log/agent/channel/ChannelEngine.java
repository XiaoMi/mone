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
package com.xiaomi.mone.log.agent.channel;

import cn.hutool.core.lang.Pair;
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
import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.agent.factory.OutPutServiceFactory;
import com.xiaomi.mone.log.agent.filter.FilterChain;
import com.xiaomi.mone.log.agent.input.Input;
import com.xiaomi.mone.log.agent.output.Output;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.utils.NetUtil;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.GSON;

/**
 * @author shanwb
 * @date 2021-07-20
 */
@Service
@Slf4j
public class ChannelEngine {
    private AgentMemoryService agentMemoryService;

    private ChannelDefineLocator channelDefineLocator;
    /**
     * The configuration pulled in full when the service starts.
     */
    private List<ChannelDefine> channelDefineList = Lists.newArrayList();

    private volatile List<ChannelService> channelServiceList = Lists.newArrayList();
    /**
     * File listener
     */
    private FileMonitorListener fileMonitorListener;

    private static byte[] lock = new byte[0];

    private Gson gson = GSON;

    @Getter
    private volatile boolean initComplete;

    public void init() {
        List<Long> failedChannelId = Lists.newArrayList();
        try {
            Config config = Ioc.ins().getBean(Config.class.getName());
            String memoryBasePath = config.get("agent.memory.path", AgentMemoryService.DEFAULT_BASE_PATH);
            //talosProducerMap = new ConcurrentHashMap<>(512);

            channelDefineLocator = getChannelDefineLocator(config);
            channelDefineList = new CopyOnWriteArrayList<>(channelDefineLocator.getChannelDefine());
            log.info("current agent all config meta:{}", gson.toJson(channelDefineList));
            agentMemoryService = new AgentMemoryServiceImpl(memoryBasePath);
            fileMonitorListener = new DefaultFileMonitorListener();

            log.info("query channelDefineList:{}", gson.toJson(channelDefineList));
            channelServiceList = channelDefineList.stream().map(channelDefine -> {
                ChannelService channelService = this.channelServiceTrans(channelDefine);
                if (null == channelService) {
                    failedChannelId.add(channelDefine.getChannelId());
                }
                return channelService;
            }).filter(channelService -> null != channelService).collect(Collectors.toList());
            // Delete failed channel
            deleteFailedChannel(failedChannelId, this.channelDefineList, this.channelServiceList);
            channelServiceList = new CopyOnWriteArrayList<>(channelServiceList);
            // start channel
            channelStart(channelServiceList);
            //Shutdown - callback action
            graceShutdown();
            //Report channel progress once every 10 seconds
            exportChannelState();
            log.info("current channelDefineList:{},current channelServiceList:{}", gson.toJson(this.channelDefineList), gson.toJson(this.channelServiceList.stream().map(ChannelService::instanceId).collect(Collectors.toList())));
            monitorFilesClean();
            executorFileClean();
        } catch (Exception e) {
            log.error("ChannelEngine init exception", e);
        } finally {
            initComplete = true;
        }
    }

    /**
     * Thread pool cleaning, many wasted files don't need to keep wasting threads, they should be cleaned up directly.
     */
    private void executorFileClean() {
        ExecutorUtil.scheduleAtFixedRate(() -> {
            SafeRun.run(() -> {
                List<Pair<AbstractChannelService, Pair<String, Long>>> serviceTimeList = Lists.newArrayList();
                for (ChannelService channelService : channelServiceList) {
                    AbstractChannelService service = (AbstractChannelService) channelService;
                    Map<String, Long> fileReadTime = service.getExpireFileMap();
                    if (!fileReadTime.isEmpty()) {
                        for (Map.Entry<String, Long> entry : fileReadTime.entrySet()) {
                            serviceTimeList.add(Pair.of(service, Pair.of(entry.getKey(), entry.getValue())));
                        }
                    }
                }
                if (serviceTimeList.size() > 500) {
                    serviceTimeList = serviceTimeList.stream().sorted(Comparator.comparing(o -> o.getValue().getValue())).collect(Collectors.toList());
                    for (int i = 0; i < serviceTimeList.size(); i++) {
                        if (i < 100) {
                            serviceTimeList.get(i).getKey().cancelFile(serviceTimeList.get(i).getValue().getKey());
                        }
                    }
                }
            });
        }, 1, 10, TimeUnit.MINUTES);
    }

    /**
     * Clean up deleted file events
     */
    private void monitorFilesClean() {
        ExecutorUtil.scheduleAtFixedRate(() -> {
            for (ChannelService channelService : channelServiceList) {
                try {
                    channelService.cleanCollectFiles();
                } catch (Exception e) {
                    log.error("monitorFilesClean error", e);
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
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
            SafeRun.run(() -> {
                List<ChannelState> channelStateList = channelServiceList.stream().map(c -> c.state()).collect(Collectors.toList());
                // Send the collection progress
                sendCollectionProgress(channelStateList);
            });
        }, 10, 10, TimeUnit.SECONDS);
    }

    private List<Long> channelStart(List<ChannelService> channelServiceList) {
        List<Long> failedChannelIds = Lists.newArrayList();
        List<Long> successChannelIds = Lists.newArrayList();
        for (ChannelService channelService : channelServiceList) {
            ChannelServiceImpl realChannelService = (ChannelServiceImpl) channelService;
            log.info("realChannelService,id:{}", realChannelService.getChannelId());
            try {
                channelService.start();
                fileMonitorListener.addChannelService(realChannelService);
                successChannelIds.add(realChannelService.getChannelId());
            } catch (RejectedExecutionException e) {
                log.error("The thread pool is full.id:{}", realChannelService.getChannelId(), e);
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
            //Processing is removed from the current queue
            for (Long delChannelId : failedChannelId) {
                defineList.removeIf(channelDefine -> Objects.equals(delChannelId, channelDefine.getChannelId()));
                serviceList.removeIf(channelService -> Objects.equals(delChannelId, ((ChannelServiceImpl) channelService).getChannelId()));
            }
        }
    }

    private void graceShutdown() {
        //Close operation
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
            if (null == agentMemoryService) {
                agentMemoryService = new AgentMemoryServiceImpl(com.xiaomi.mone.log.common.Config.ins().get("agent.memory.path", AgentMemoryService.DEFAULT_BASE_PATH));
            }
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
        OutPutServiceFactory.getOutPutService(output.getServiceName()).preCheckOutput(output);
    }

    private MsgExporter exporterTrans(Output output) throws Exception {
        if (null == output) {
            return null;
        }
        return OutPutServiceFactory.getOutPutService(output.getServiceName()).exporterTrans(output);
    }


    /**
     * Refresh configuration (refresh existing configuration when incremental configuration and full configuration come)
     * There are deletion events, indicating that it is not a full configuration, and it goes directly to the stop event.
     *
     * @param channelDefines
     */
    public void refresh(List<ChannelDefine> channelDefines) {
        log.info("[config change],changed data:{},origin data:{}", gson.toJson(channelDefines), gson.toJson(channelDefineList));
        try {
            if (CollectionUtils.isNotEmpty(channelDefines) && !CollectionUtils.isEqualCollection(channelDefines, channelDefineList)) {
                if (channelDefines.stream().allMatch(channelDefine -> null != channelDefine.getOperateEnum() && channelDefine.getOperateEnum().getCode().equals(OperateEnum.DELETE_OPERATE.getCode()))) {
                    // Collect and delete files in the specified directory
                    log.info("delSpecialFileColl,config:{}", gson.toJson(channelDefines));
                    delSpecialFileColl(channelDefines);
                    return;
                }
                log.info("refresh,config:{}", gson.toJson(channelDefines));
                // add config
                addConfig(channelDefines, false);
                // update config
                updateConfig(channelDefines);
                /**
                 * Single configuration processing without deletion.
                 */
                if (channelDefines.size() == 1 && channelDefines.get(0).getSingleMetaData() != null && channelDefines.get(0).getSingleMetaData()) {
                    return;
                }
                // delete config
                deleteConfig(channelDefines, false);
            }
        } catch (Exception e) {
            log.error("refresh error,[config change],changed data:{},origin data:{}", gson.toJson(channelDefines), gson.toJson(channelDefineList), e);
        }
    }

    /**
     * New configuration
     *
     * @param channelDefines
     */
    private void addConfig(List<ChannelDefine> channelDefines, boolean directAdd) {
        try {
            // Newly added, initialize
            List<ChannelDefine> channelDefinesDifference = differenceSet(channelDefines, channelDefineList);
            if (directAdd) {
                channelDefinesDifference = channelDefines;
            }
            if (directAdd || CollectionUtils.isNotEmpty(channelDefinesDifference)) {
                log.info("[add config]data:{}", gson.toJson(channelDefinesDifference));
                initIncrement(channelDefinesDifference);
            }
        } catch (Exception e) {
            log.error("addConfig error,source channelDefines:{},origin channelDefines:{},directAdd:{}", gson.toJson(channelDefines), gson.toJson(channelDefineList), directAdd, e);
        }
    }

    /**
     * Update configuration(
     * 1. Find the changed configuration
     * 2. Delete the original configuration
     * 3. Add the configuration again
     * )
     *
     * @param channelDefines
     */
    private void updateConfig(List<ChannelDefine> channelDefines) {
        List<ChannelDefine> channelDefinesIntersection = intersection(channelDefines, channelDefineList);
        if (CollectionUtils.isNotEmpty(channelDefinesIntersection)) {
            List<ChannelDefine> changedDefines = Lists.newArrayList();
            log.info("have exist config:{}", GSON.toJson(channelDefineList));
            Iterator<ChannelDefine> iterator = channelDefinesIntersection.iterator();
            while (iterator.hasNext()) {
                ChannelDefine newChannelDefine = iterator.next();
                // old channelDefine
                Long channelId = newChannelDefine.getChannelId();
                ChannelDefine oldChannelDefine = channelDefineList.stream().filter(channelDefine -> channelDefine.getChannelId().equals(channelId)).findFirst().orElse(null);
                if (null != oldChannelDefine) {
                    // Comparator
                    SimilarComparator appSimilarComparator = new AppSimilarComparator(oldChannelDefine.getAppId());
                    SimilarComparator inputSimilarComparator = new InputSimilarComparator(oldChannelDefine.getInput());
                    SimilarComparator outputSimilarComparator = new OutputSimilarComparator(oldChannelDefine.getOutput());
                    FilterSimilarComparator filterSimilarComparator = new FilterSimilarComparator(oldChannelDefine.getFilters());
                    if (appSimilarComparator.compare(newChannelDefine.getAppId()) && inputSimilarComparator.compare(newChannelDefine.getInput()) && outputSimilarComparator.compare(newChannelDefine.getOutput())) {
                        if (!filterSimilarComparator.compare(newChannelDefine.getFilters())) {
                            channelServiceList.stream().filter(channelService -> ((ChannelServiceImpl) channelService).getChannelId().equals(channelId)).findFirst().ifPresent(channelService -> channelService.filterRefresh(newChannelDefine.getFilters()));
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
     * Delete configuration
     *
     * @param channelDefines
     */
    private void deleteConfig(List<ChannelDefine> channelDefines, boolean directDel) {
        // The entire file is collected and deleted.
        delTailFileColl(channelDefines, directDel);
    }

    private void delTailFileColl(List<ChannelDefine> channelDefines, boolean directDel) {
        List<ChannelDefine> channelDels = channelDefines.stream().filter(channelDefine -> null != channelDefine.getOperateEnum() && channelDefine.getOperateEnum().getCode().equals(OperateEnum.DELETE_OPERATE.getCode()) && StringUtils.isEmpty(channelDefine.getDelDirectory())).collect(Collectors.toList());
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
                                //delete mq
                                Output output = channelDefine.getOutput();
                                OutPutServiceFactory.getOutPutService(output.getServiceName()).removeMQ(output);
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
     * Delete log collection under a specific directory.
     *
     * @param channelDefines
     */
    private void delSpecialFileColl(List<ChannelDefine> channelDefines) {
        //Find out the pods that need to be deleted when a machine goes offline
        List<ChannelDefine> delSpecialFiles = channelDefines.stream().filter(channelDefine -> null != channelDefine.getOperateEnum() && channelDefine.getOperateEnum().getCode().equals(OperateEnum.DELETE_OPERATE.getCode()) && StringUtils.isNotEmpty(channelDefine.getDelDirectory())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(delSpecialFiles)) {
            try {
                for (ChannelService channelService : channelServiceList) {
                    CompletableFuture.runAsync(() -> {
                        AbstractChannelService abstractChannelService = (AbstractChannelService) channelService;
                        Long channelId = abstractChannelService.getChannelDefine().getChannelId();

                        List<ChannelDefine> defineList = delSpecialFiles.stream().filter(channelDefine -> Objects.equals(channelDefine.getChannelId(), channelId)).collect(Collectors.toList());

                        for (ChannelDefine channelDefine : defineList) {
                            log.info("deleteConfig,deleteCollFile,channelDefine:{}", gson.toJson(channelDefine));
                            channelService.deleteCollFile(channelDefine.getDelDirectory());
                        }
                        //Also need to delete opentelemetry logs.
                        if (LogTypeEnum.OPENTELEMETRY == abstractChannelService.getLogTypeEnum()) {
                            for (ChannelDefine channelDefine : delSpecialFiles) {
                                log.info("deleteConfig OPENTELEMETRY,deleteCollFile,channelDefine:{}", gson.toJson(channelDefine));
                                channelService.deleteCollFile(channelDefine.getDelDirectory());
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.error("delSpecialFileColl error,delSpecialFiles:{}", gson.toJson(channelDefines), e);
            }
        }
    }

    /**
     * Difference
     *
     * @param origin New and old configuration
     * @param source Old configuration
     * @return
     */
    private List<ChannelDefine> differenceSet(List<ChannelDefine> origin, List<ChannelDefine> source) {
        if (CollectionUtils.isEmpty(source)) {
            return origin;
        }
        List<Long> sourceIds = source.stream().map(ChannelDefine::getChannelId).collect(Collectors.toList());
        return origin.stream().filter(channelDefine -> !sourceIds.contains(channelDefine.getChannelId()) && OperateEnum.DELETE_OPERATE != channelDefine.getOperateEnum()).collect(Collectors.toList());
    }


    /**
     * Intersection
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
        return origin.stream().filter(channelDefine -> finalSourceIds.contains(channelDefine.getChannelId()) && OperateEnum.DELETE_OPERATE != channelDefine.getOperateEnum()).collect(Collectors.toList());
    }

    /**
     * New configuration initialization
     *
     * @param definesIncrement
     */
    public void initIncrement(List<ChannelDefine> definesIncrement) {
        List<Long> failedChannelId = Lists.newArrayList();
        List<ChannelService> channelServices = definesIncrement.stream().filter(Objects::nonNull).map(channelDefine -> {
            ChannelService channelService = channelServiceTrans(channelDefine);
            if (null == channelService) {
                failedChannelId.add(channelDefine.getChannelId());
            }
            return channelService;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        deleteFailedChannel(failedChannelId, definesIncrement, channelServices);
        List<Long> successChannelIds = channelStart(channelServices);
        if (CollectionUtils.isNotEmpty(successChannelIds)) {
            this.channelServiceList.addAll(channelServices.stream().filter(channelService -> successChannelIds.contains(((ChannelServiceImpl) channelService).getChannelId())).collect(Collectors.toList()));
            this.channelDefineList.addAll(definesIncrement.stream().filter(channelDefine -> successChannelIds.contains(channelDefine.getChannelId())).collect(Collectors.toList()));
        }
        log.info("[add config] after current channelDefineList:{},channelServiceList:{}", gson.toJson(this.channelDefineList), gson.toJson(gson.toJson(channelServiceList.stream().map(ChannelService::instanceId).collect(Collectors.toList()))));
    }


    /**
     * Send collection progress.
     *
     * @param
     */
    private void sendCollectionProgress(List<ChannelState> channelStateList) {
        if (CollectionUtils.isEmpty(channelStateList)) {
            return;
        }
        UpdateLogProcessCmd processCmd = assembleLogProcessData(channelStateList);
        RpcClient rpcClient = Ioc.ins().getBean(RpcClient.class);
        RemotingCommand req = RemotingCommand.createRequestCommand(Constant.RPCCMD_AGENT_CODE);
        req.setBody(GSON.toJson(processCmd).getBytes());
        rpcClient.sendMessage(req);
        log.debug("send collect progress,data:{}", gson.toJson(processCmd));
    }

    private UpdateLogProcessCmd assembleLogProcessData(List<ChannelState> channelStateList) {
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

                List<UpdateLogProcessCmd.FileProgressDetail> progressDetails = channelState.getStateProgressMap().entrySet().stream().map(entry -> UpdateLogProcessCmd.FileProgressDetail.builder().fileRowNumber(entry.getValue().getCurrentRowNum()).collectTime(entry.getValue().getCtTime()).pointer(entry.getValue().getPointer()).fileMaxPointer(entry.getValue().getFileMaxPointer()).collectPercentage(getPercent(entry.getValue().getPointer(), entry.getValue().getFileMaxPointer())).configIp(entry.getValue().getIp()).pattern(entry.getKey()).build()).collect(Collectors.toList());
                collectDetail.setFileProgressDetails(progressDetails);
                finalCollects.add(collectDetail);
            });
            //Progress deduplication
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
