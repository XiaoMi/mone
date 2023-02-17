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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.mone.file.*;
import com.xiaomi.mone.log.agent.channel.file.MonitorFile;
import com.xiaomi.mone.log.agent.channel.memory.AgentMemoryService;
import com.xiaomi.mone.log.agent.channel.memory.ChannelMemory;
import com.xiaomi.mone.log.agent.common.ChannelUtil;
import com.xiaomi.mone.log.agent.common.ExecutorUtil;
import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.agent.filter.FilterChain;
import com.xiaomi.mone.log.agent.input.Input;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.model.meta.FilterConf;
import com.xiaomi.mone.log.api.model.msg.LineMessage;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.common.PathUtils;
import com.xiaomi.mone.log.utils.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.PathUtils.PATH_WILDCARD;
import static com.xiaomi.mone.log.common.PathUtils.SEPARATOR;

/**
 * @author shanwb
 * @date 2021-07-20
 */
@Slf4j
public class ChannelServiceImpl implements ChannelService {

    private AgentMemoryService memoryService;

    private MsgExporter msgExporter;

    private ChannelDefine channelDefine;

    private ChannelMemory channelMemory;

    private ConcurrentHashMap<String, LogFile> logFileMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<>();

    private Gson gson = Constant.GSON;

    private List<LineMessage> lineMessageList = new ArrayList<>();

    private byte[] lock = new byte[0];

    private long lastSendTime = System.currentTimeMillis();

    private long logCounts = 0;

    private ScheduledFuture<?> scheduledFuture;

    private ScheduledFuture<?> lastFileLineScheduledFuture;
    /**
     * collect once flag
     */
    private boolean collectOnce;

    private FilterChain chain;

    private String instanceId = UUID.randomUUID().toString();
    /**
     * 监听的文件路径
     */
    private List<MonitorFile> monitorFileList;
    /**
     * 实际采集的文件对应的机器Ip(兼容k8s 一个node下多个pod的问题)
     */
    Map<String, String> ipPath = new ConcurrentHashMap<>();

    private LogTypeEnum logTypeEnum;

    private String logPattern;

    private String logSplitExpress;

    public ChannelServiceImpl(MsgExporter msgExporter, AgentMemoryService memoryService,
                              ChannelDefine channelDefine, FilterChain chain) {
        this.memoryService = memoryService;
        this.msgExporter = msgExporter;
        this.channelDefine = channelDefine;
        this.chain = chain;
        this.monitorFileList = Lists.newArrayList();
    }

    @Override
    public void refresh(ChannelDefine channelDefine, MsgExporter msgExporter) {
        this.channelDefine = channelDefine;
        if (null != msgExporter) {
            synchronized (this.lock) {
                this.msgExporter.close();
                this.msgExporter = msgExporter;
            }
        }
    }

    @Override
    public void stopFile(List<String> filePrefixList) {
        Map<String, ChannelMemory.FileProgress> fileProgressMap = channelMemory.getFileProgressMap();
        if (null == fileProgressMap) {
            fileProgressMap = new HashMap<>();
        }

        for (Iterator<Map.Entry<String, LogFile>> it = logFileMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, LogFile> entry = it.next();
            String filePath = entry.getKey();
            for (String filePrefix : filePrefixList) {
                if (filePath.startsWith(filePrefix)) {
                    entry.getValue().setStop(true);
                    log.warn("channel:{} stop file:{} success", channelDefine.getChannelId(), filePath);
                    ChannelMemory.FileProgress fileProgress = fileProgressMap.get(filePath);
                    //刷新内存记录，防止agent重启，重新采集该文件
                    if (null != fileProgress) {
                        fileProgress.setFinished(true);
                    }
                    it.remove();
                    continue;
                }
            }
        }
    }

    @Override
    public ChannelState state() {
        ChannelState channelState = new ChannelState();

        channelState.setTailId(this.channelDefine.getChannelId());
        channelState.setTailName(this.channelDefine.getTailName());
        channelState.setAppId(this.channelDefine.getAppId());
        channelState.setAppName(this.channelDefine.getAppName());
        channelState.setLogPattern(this.channelDefine.getInput().getLogPattern());
        channelState.setLogPatternCode(this.channelDefine.getInput().getPatternCode());
        channelState.setIpList(ipPath.values().stream().distinct().collect(Collectors.toList()));

        channelState.setCollectTime(this.channelMemory.getCurrentTime());

        if (channelState.getStateProgressMap() == null) {
            channelState.setStateProgressMap(new HashMap<>(256));
        }
        channelMemory.getFileProgressMap().forEach((pattern, fileProcess) -> {
            if (null != fileProcess.getFinished() && fileProcess.getFinished()) {
                return;
            }
            ChannelState.StateProgress stateProgress = new ChannelState.StateProgress();
            stateProgress.setCurrentFile(pattern);
            stateProgress.setIp(getTailPodIp(pattern));
            stateProgress.setCurrentRowNum(fileProcess.getCurrentRowNum());
            stateProgress.setPointer(fileProcess.getPointer());
            stateProgress.setFileMaxPointer(fileProcess.getFileMaxPointer());
            channelState.getStateProgressMap().put(pattern, stateProgress);
        });

        channelState.setTotalSendCnt(this.logCounts);
        return channelState;
    }

    private String getTailPodIp(String pattern) {
        String tailPodIp = ipPath.get(pattern);
        if (StringUtils.isBlank(tailPodIp)) {
            Optional<String> ipOptional = ipPath.keySet().stream().filter(path -> pattern.startsWith(path)).findFirst();
            String ipKey = ipPath.keySet().stream().findFirst().get();
            if (ipOptional.isPresent()) {
                ipKey = ipOptional.get();
            }
            tailPodIp = ipPath.get(ipKey);
        }
        return tailPodIp;
    }

    @Override
    public String instanceId() {
        return instanceId;
    }

    @Override
    public void start() {
        Long channelId = channelDefine.getChannelId();
        Input input = channelDefine.getInput();
        List<String> ips = channelDefine.getIps();

        this.logPattern = input.getLogPattern();
        this.logSplitExpress = input.getLogSplitExpress();

        String logType = channelDefine.getInput().getType();
        logTypeEnum = LogTypeEnum.name2enum(logType);
        collectOnce = StringUtils.substringAfterLast(logPattern, SEPARATOR).contains(PATH_WILDCARD);

        List<String> patterns = PathUtils.parseLevel5Directory(logPattern);
        if (CollectionUtils.isEmpty(patterns)) {
            log.info("config pattern:{},current files not exist", logPattern);
        }
        log.info("channel start, logPattern:{}，fileList:{}, channelId:{}, instanceId:{}", logPattern, patterns, channelId, instanceId());
        // disassembly monitor file
        logMonitorPathDisassembled(logSplitExpress, patterns, logPattern);

        channelMemory = memoryService.getMemory(channelId);
        if (null == channelMemory) {
            channelMemory = initChannelMemory(channelId, input, patterns);
        }
        memoryService.cleanChannelMemoryContent(channelId, patterns);
        // handle all * file ip
        ChannelUtil.buildConnectionBetweenAppIp(logPattern, ipPath, ips, collectOnce);

        startCollectFile(channelId, input, ips, patterns);

        startExportQueueDataThread();
        memoryService.refreshMemory(channelMemory);
        delayDeletionFinishedFile();
        log.warn("channelId:{}, channelInstanceId:{} start success! channelDefine:{}", channelId, instanceId, gson.toJson(this.channelDefine));
    }

    /**
     * 担心没采集完，延迟2min后停止(机器重启的时候且不是单个配置过来的时候执行)
     */
    private void delayDeletionFinishedFile() {
        List<String> usedFilePaths = channelDefine.getPodNames();
        if (CollectionUtils.isNotEmpty(usedFilePaths) && LogTypeEnum.OPENTELEMETRY == logTypeEnum
                && (null == channelDefine.getSingleMetaData() || !channelDefine.getSingleMetaData())) {
            log.info("usedFilePaths:{},collecting filePaths:{}", gson.toJson(usedFilePaths), gson.toJson(logFileMap.keys()));
            Iterator<Map.Entry<String, LogFile>> entryIterator = logFileMap.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, LogFile> fileEntry = entryIterator.next();
                String fileName = fileEntry.getKey();
                if (!usedFilePaths.stream().anyMatch(fileName::contains)) {
                    log.info("delete file stop job:{}", fileName);
                    CompletableFuture.runAsync(() -> {
                        try {
                            TimeUnit.MINUTES.sleep(2L);
                        } catch (InterruptedException e) {
                            log.error("TimeUnit.MINUTES.sleep error,fileName:{}", fileName, e);
                        }
                        log.warn("delayDeletionFinishedFile channel:{} stop file:{} success", channelDefine.getChannelId(), fileName);
                        ChannelMemory.FileProgress fileProgress = channelMemory.getFileProgressMap().get(fileName);
                        //刷新内存记录，防止agent重启，重新采集该文件
                        if (null != fileProgress) {
                            fileProgress.setFinished(true);
                        }
                        fileEntry.getValue().setStop(true);
                        entryIterator.remove();
                    });
                }
            }
        }
    }

    private void startExportQueueDataThread() {
        scheduledFuture = ExecutorUtil.scheduleAtFixedRate(() -> {
            // 超过10s 未发送mq消息，才进行异步发送
            if (System.currentTimeMillis() - lastSendTime < 10 * 1000) {
                return;
            }
            synchronized (lock) {
                this.doExport(lineMessageList);
            }
        }, 10, 7, TimeUnit.SECONDS);
    }

    private void startCollectFile(Long channelId, Input input, List<String> ips, List<String> patterns) {
        Map<String, String> ipPathDireMap = new HashMap<>();
        BeanUtil.copyProperties(ipPath, ipPathDireMap);

        for (int i = 0; i < patterns.size(); i++) {
            String ip = ChannelUtil.queryCurrentCorrectIp(ipPathDireMap, patterns.get(i), ips);

            readFile(input.getPatternCode(), ip, patterns.get(i), channelId);
            if (!collectOnce) {
                ipPath.put(patterns.get(i), ip);
            }
        }
    }


    private void handleAllFileCollectMonitor(String patternCode, String newFilePath, Long channelId, Map<String, String> ipPath) {
        String ip = ChannelUtil.queryCurrentCorrectIp(ipPath, newFilePath, Collections.EMPTY_LIST);

        List<String> collectKeys = logFileMap.keySet().stream().collect(Collectors.toList());

        readFile(patternCode, ip, newFilePath, channelId);

        // close old
        collectKeys.forEach(key -> {
            CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.MINUTES.sleep(2L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogFile logFile = logFileMap.get(key);
                logFile.shutdown();
                logFileMap.remove(key);
            });
        });
    }

    /**
     * 1.logSplitExpress:/home/work/log/log-agent/server.log.* realFilePaths: ["/home/work/log/log-agent/server.log"]
     * 2.logSplitExpress:/home/work/log/log-agent/(server.log.*|error.log.*) realFilePaths: ["/home/work/log/log-agent/server.log","/home/work/log/log-agent/server.log"]
     * 2.logSplitExpress:/home/work/log/(log-agent|log-stream)/server.log.* realFilePaths: ["/home/work/log/log-agent/server.log","/home/work/log/log-stream/server.log"]
     * 真实的文件不存在，也应该监听
     *
     * @param logSplitExpress
     * @param realFilePaths
     */
    private void logMonitorPathDisassembled(String logSplitExpress, List<String> realFilePaths, String configPath) {
        List<String> cleanedPathList = Lists.newArrayList();
        if (StringUtils.isNotBlank(logSplitExpress)) {
            PathUtils.dismantlingStrWithSymbol(logSplitExpress, cleanedPathList);
        }
        if (LogTypeEnum.OPENTELEMETRY == logTypeEnum) {
            opentelemetryMonitor(configPath);
            return;
        }
        if (collectOnce) {
            collectOnceFileMonitor(configPath);
            return;
        }
        for (int i = 0; i < realFilePaths.size(); i++) {
            String perFilePathExpress;
            try {
                perFilePathExpress = cleanedPathList.get(i);
                /**
                 * 兼容当前文件创建的时候可以监听到
                 */
                perFilePathExpress = String.format("(%s|%s)", perFilePathExpress,
                        String.format("%s.*", realFilePaths.get(i)));
            } catch (Exception e) {
                perFilePathExpress = String.format("%s.*", realFilePaths.get(i));
            }
            monitorFileList.add(MonitorFile.of(realFilePaths.get(i), perFilePathExpress, logTypeEnum, collectOnce));
        }
    }

    private void collectOnceFileMonitor(String configPath) {
        String singleTimeExpress = ChannelUtil.buildSingleTimeExpress(configPath);
        monitorFileList.add(MonitorFile.of(configPath, singleTimeExpress, logTypeEnum, collectOnce));
    }

    private void opentelemetryMonitor(String configPath) {
        List<String> cleanedPathList = ChannelUtil.buildLogExpressList(configPath);
        monitorFileList.add(MonitorFile.of(configPath, cleanedPathList.get(0), logTypeEnum, collectOnce));
    }


    private ChannelMemory initChannelMemory(Long channelId, Input input, List<String> patterns) {
        channelMemory = new ChannelMemory();
        channelMemory.setChannelId(channelId);
        channelMemory.setInput(input);
        HashMap<String, ChannelMemory.FileProgress> fileProgressMap = Maps.newHashMap();
        for (String pattern : patterns) {
            ChannelMemory.FileProgress fileProgress = new ChannelMemory.FileProgress();
            fileProgress.setPointer(0L);
            fileProgress.setCurrentRowNum(0L);
            fileProgress.setUnixFileNode(ChannelUtil.buildUnixFileNode(pattern));
            fileProgressMap.put(pattern, fileProgress);
        }
        channelMemory.setFileProgressMap(fileProgressMap);
        channelMemory.setCurrentTime(System.currentTimeMillis());
        channelMemory.setVersion(ChannelMemory.DEFAULT_VERSION);
        return channelMemory;
    }

    private ReadListener initFileReadListener(MLog mLog, String patternCode, String ip, String pattern) {
        AtomicReference<ReadResult> readResult = new AtomicReference<>();
        ReadListener listener = new DefaultReadListener(event -> {
            readResult.set(event.getReadResult());
            if (null == readResult.get()) {
                log.info("empty data");
                return;
            }
            String ct = String.valueOf(System.currentTimeMillis());
            readResult.get().getLines().stream()
                    .forEach(l -> {
                        String logType = channelDefine.getInput().getType();
                        LogTypeEnum logTypeEnum = LogTypeEnum.name2enum(logType);
                        // 多行应用日志类型和opentelemetry类型才判断异常堆栈
                        if (LogTypeEnum.APP_LOG_MULTI == logTypeEnum || LogTypeEnum.OPENTELEMETRY == logTypeEnum) {
                            l = mLog.append2(l);
                        } else {
                            // tail 单行模式
                        }
                        if (null != l) {
                            synchronized (lock) {
                                wrapDataToSend(l, readResult, pattern, patternCode, ip, ct);
                            }
                        } else {
                            log.debug("biz log channelId:{}, not new line:{}", channelDefine.getChannelId(), l);
                        }
                    });

        });

        /**
         * 采集最后一行数据内存中超3分钟没有发送的数据
         */
        lastFileLineScheduledFuture = ExecutorUtil.scheduleAtFixedRate(() -> {
            Long appendTime = mLog.getAppendTime();
            if (null != appendTime && Instant.now().toEpochMilli() - appendTime > 3 * 60 * 1000) {
                String remainMsg = mLog.takeRemainMsg2();
                if (null != remainMsg) {
                    synchronized (lock) {
                        log.info("start send last line,pattern:{},patternCode:{},data:{}", pattern, patternCode, remainMsg);
                        wrapDataToSend(remainMsg, readResult, pattern, patternCode, ip, String.valueOf(Instant.now().toEpochMilli()));
                    }
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
        return listener;
    }

    private void wrapDataToSend(String lineMsg, AtomicReference<ReadResult> readResult, String pattern, String patternCode, String localIp, String ct) {
        LineMessage lineMessage = new LineMessage();
        lineMessage.setMsgBody(lineMsg);
        lineMessage.setPointer(readResult.get().getPointer());
        lineMessage.setLineNumber(readResult.get().getLineNumber());
        lineMessage.setFileName(pattern);
        lineMessage.setProperties(LineMessage.KEY_MQ_TOPIC_TAG, patternCode);
        lineMessage.setProperties(LineMessage.KEY_IP, localIp);
        lineMessage.setProperties(LineMessage.KEY_COLLECT_TIMESTAMP, ct);
        String logType = channelDefine.getInput().getType();
        LogTypeEnum logTypeEnum = LogTypeEnum.name2enum(logType);
        if (logTypeEnum == LogTypeEnum.OPENTELEMETRY) {
            lineMessage.setProperties(LineMessage.KEY_MESSAGE_TYPE,
                    String.valueOf(LogTypeEnum.OPENTELEMETRY.getType()));
        }

        ChannelMemory.FileProgress fileProgress = channelMemory.getFileProgressMap().get(pattern);
        if (null == fileProgress) {
            fileProgress = new ChannelMemory.FileProgress();
            channelMemory.getFileProgressMap().put(pattern, fileProgress);
            channelMemory.getInput().setLogPattern(logPattern);
            channelMemory.getInput().setType(logTypeEnum.name());
            channelMemory.getInput().setLogSplitExpress(logSplitExpress);
        }
        fileProgress.setCurrentRowNum(readResult.get().getLineNumber());
        fileProgress.setPointer(readResult.get().getPointer());
        if (null != readResult.get().getFileMaxPointer()) {
            fileProgress.setFileMaxPointer(readResult.get().getFileMaxPointer());
        }
        fileProgress.setUnixFileNode(ChannelUtil.buildUnixFileNode(pattern));
        lineMessageList.add(lineMessage);

        int batchSize = msgExporter.batchExportSize();
        if (lineMessageList.size() > batchSize) {
            List<LineMessage> subList = lineMessageList.subList(0, batchSize);
            doExport(subList);
        }
    }

    private void readFile(String patternCode, String ip, String filePath, Long channelId) {
        MLog mLog = new MLog();
        String usedIp = StringUtils.isBlank(ip) ? NetUtil.getLocalIp() : ip;

        ReadListener listener = initFileReadListener(mLog, patternCode, usedIp, filePath);
        Map<String, ChannelMemory.FileProgress> fileProgressMap = channelMemory.getFileProgressMap();
        log.info("fileProgressMap:{}", fileProgressMap);
        LogFile logFile = getLogFile(filePath, listener, fileProgressMap);
        if (null == logFile) {
            log.warn("file:{} marked stop to collect", filePath);
            return;
        }
        //判断文件是否存在
        if (FileUtil.exist(filePath)) {
            log.info("start to collect file,fileName:{}", filePath);
            logFileMap.put(filePath, logFile);
            Future<?> future = ExecutorUtil.submit(() -> {
                try {
                    logFile.readLine();
                } catch (Exception e) {
                    log.error("logFile read line err,channelId:{},localIp:{},file:{},patternCode:{}", channelId, usedIp, fileProgressMap, patternCode, e);
                }
            });
            futureMap.put(filePath, future);
        } else {
            log.info("file not exist,file:{}", filePath);
        }
    }

    private LogFile getLogFile(String filePath, ReadListener listener, Map<String, ChannelMemory.FileProgress> fileProgressMap) {
        long pointer = 0L;
        long lineNumber = 0L;
        ChannelMemory.FileProgress fileProgress = fileProgressMap.get(filePath);
        if (fileProgress != null) {
            if (null != fileProgress.getFinished() && fileProgress.getFinished()) {
                return null;
            }

            pointer = fileProgress.getPointer();
            lineNumber = fileProgress.getCurrentRowNum();
            //比较inode值是否变化，变化则从头开始读
            ChannelMemory.UnixFileNode memoryUnixFileNode = fileProgress.getUnixFileNode();
            if (null != memoryUnixFileNode && null != memoryUnixFileNode.getSt_ino()) {
                log.info("memory file inode info,filePath:{},:{}", filePath, gson.toJson(memoryUnixFileNode));
                //获取当前文件inode信息
                ChannelMemory.UnixFileNode currentUnixFileNode = ChannelUtil.buildUnixFileNode(filePath);
                if (null != currentUnixFileNode && null != currentUnixFileNode.getSt_ino()) {
                    log.info("current file inode info,filePath:{},file node info:{}", filePath, gson.toJson(currentUnixFileNode));
                    if (!Objects.equals(memoryUnixFileNode.getSt_ino(), currentUnixFileNode.getSt_ino())) {
                        pointer = 0L;
                        lineNumber = 0L;
                        log.info("read file start from head,filePath:{},memory:{},current:{}", filePath, gson.toJson(memoryUnixFileNode), gson.toJson(currentUnixFileNode));
                    }
                }
            }
        }
        return new LogFile(filePath, listener, pointer, lineNumber);
    }

    private void doExport(List<LineMessage> subList) {
        try {
            if (CollectionUtils.isEmpty(subList)) {
                return;
            }
            //限流处理
            chain.doFilter();

            long current = System.currentTimeMillis();
            msgExporter.export(subList);
            logCounts += subList.size();
            lastSendTime = System.currentTimeMillis();
            channelMemory.setCurrentTime(lastSendTime);

            log.info("doExport channelId:{}, send {} message, cost:{}, total send:{}, instanceId:{},", channelDefine.getChannelId(), subList.size(), lastSendTime - current, logCounts, instanceId());
        } catch (Exception e) {
            log.error("doExport Exception:{}", e);
        } finally {
            subList.clear();
        }
    }

    @Override
    public void close() {
        log.info("删除当前采集任务，channelId:{}", getChannelId());
        //1.停止日志抓取
        for (LogFile logFile : logFileMap.values()) {
            logFile.setStop(true);
        }
        //2. 停止export
        this.msgExporter.close();
        //3. 刷新缓存
        memoryService.refreshMemory(channelMemory);
        // 停止任务
        if (null != scheduledFuture) {
            scheduledFuture.cancel(true);
        }
        if (null != lastFileLineScheduledFuture) {
            lastFileLineScheduledFuture.cancel(true);
        }
        log.info("stop file monitor");
        lineMessageList.clear();
    }

    public Long getChannelId() {
        return channelDefine.getChannelId();
    }

    public MsgExporter getMsgExporter() {
        return msgExporter;
    }

    @Override
    public void filterRefresh(List<FilterConf> confs) {
        try {
            this.chain.loadFilterList(confs);
            this.chain.reset();
        } catch (Exception e) {
            log.error("filter refresh err,new conf:{}", confs, e);
        }
    }

    @Override
    public void reOpen(String filePath) {
        log.info("reOpen file:{}", filePath);
        if (collectOnce) {
            handleAllFileCollectMonitor(channelDefine.getInput().getPatternCode(), filePath, getChannelId(), ipPath);
            return;
        }
        LogFile logFile = logFileMap.get(filePath);
        String ip = StringUtils.isBlank(ipPath.get(filePath)) ? NetUtil.getLocalIp() : ipPath.get(filePath);
        if (null == logFile) {
            // 新增日志文件
            readFile(channelDefine.getInput().getPatternCode(), ip, filePath, getChannelId());
            log.info("watch new file create for chnnelId:{},ip:{},path:{}", getChannelId(), filePath, ip);
        } else {
            // 正常日志切分
            try {
                //延迟7s切分文件, todo @shanwb 保证文件采集完再切换
                TimeUnit.SECONDS.sleep(7);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logFile.setReOpen(true);
            log.info("file reOpen: channelId:{},ip:{},path:{}", getChannelId(), ip, filePath);
        }
    }

    @Override
    public List<MonitorFile> getMonitorPathList() {
        return monitorFileList;
    }

    public ChannelDefine getChannelDefine() {
        return channelDefine;
    }

    public ChannelMemory getChannelMemory() {
        return channelMemory;
    }


}
