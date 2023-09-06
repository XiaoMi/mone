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

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.mone.file.*;
import com.xiaomi.mone.log.agent.channel.file.InodeFileComparator;
import com.xiaomi.mone.log.agent.channel.file.MonitorFile;
import com.xiaomi.mone.log.agent.channel.memory.AgentMemoryService;
import com.xiaomi.mone.log.agent.channel.memory.ChannelMemory;
import com.xiaomi.mone.log.agent.common.ChannelUtil;
import com.xiaomi.mone.log.agent.common.ExecutorUtil;
import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.agent.filter.FilterChain;
import com.xiaomi.mone.log.agent.input.Input;
import com.xiaomi.mone.log.api.enums.K8sPodTypeEnum;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.model.meta.FilterConf;
import com.xiaomi.mone.log.api.model.msg.LineMessage;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.common.PathUtils;
import com.xiaomi.mone.log.utils.NetUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;
import static com.xiaomi.mone.log.common.PathUtils.PATH_WILDCARD;
import static com.xiaomi.mone.log.common.PathUtils.SEPARATOR;

/**
 * @author shanwb
 * @date 2021-07-20
 */
@Slf4j
public class ChannelServiceImpl extends AbstractChannelService {

    private AgentMemoryService memoryService;

    private MsgExporter msgExporter;

    private ChannelDefine channelDefine;

    private ChannelMemory channelMemory;

    @Getter
    private final ConcurrentHashMap<String, LogFile> logFileMap = new ConcurrentHashMap<>();

    @Getter
    private final ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<>();

    private Set<String> delFileCollList = new CopyOnWriteArraySet<>();

    private final Map<String, Long> reOpenMap = new HashMap<>();
    private final Map<String, Long> fileReadMap = new ConcurrentHashMap<>();

    private final Map<String, Pair<MLog, AtomicReference<ReadResult>>> resultMap = new ConcurrentHashMap<>();

    private ScheduledFuture<?> lastFileLineScheduledFuture;

    private Gson gson = Constant.GSON;

    private List<LineMessage> lineMessageList = new ArrayList<>();

    private byte[] lock = new byte[0];

    private long lastSendTime = System.currentTimeMillis();

    private long logCounts = 0;

    private ScheduledFuture<?> scheduledFuture;

    /**
     * collect once flag
     */
    private boolean collectOnce;

    private FilterChain chain;

    /**
     * The file path to monitor
     */
    private List<MonitorFile> monitorFileList;

    private LogTypeEnum logTypeEnum;

    private String logPattern;

    private String logSplitExpress;

    private String linePrefix;

    public ChannelServiceImpl(MsgExporter msgExporter, AgentMemoryService memoryService, ChannelDefine channelDefine, FilterChain chain) {
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
                    futureMap.get(filePath).cancel(false);
                    log.warn("channel:{} stop file:{} success", channelDefine.getChannelId(), filePath);
                    ChannelMemory.FileProgress fileProgress = fileProgressMap.get(filePath);
                    //Refresh the memory record to prevent the agent from restarting and recollect the file.
                    if (null != fileProgress) {
                        fileProgress.setFinished(true);
                    }
                    it.remove();
                }
            }
        }
    }

    @Override
    public void start() {
        Long channelId = channelDefine.getChannelId();
        Input input = channelDefine.getInput();

        this.logPattern = input.getLogPattern();
        this.logSplitExpress = input.getLogSplitExpress();
        this.linePrefix = input.getLinePrefix();

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

        startCollectFile(channelId, input, patterns);

        startExportQueueDataThread();
        memoryService.refreshMemory(channelMemory);
        log.warn("channelId:{}, channelInstanceId:{} start success! channelDefine:{}", channelId, instanceId(), gson.toJson(this.channelDefine));
    }

    @Override
    public void cleanCollectFiles() {
        for (String path : delFileCollList) {
            delCollFile(path);
        }
    }

    @Override
    public void deleteCollFile(String directory) {
        log.info("channelId:{},deleteCollFile,directory:{}", channelDefine.getChannelId(), directory);
        for (Map.Entry<String, LogFile> logFileEntry : logFileMap.entrySet()) {
            if (logFileEntry.getKey().contains(directory)) {
                delFileCollList.add(logFileEntry.getKey());
                log.info("channelId:{},delFileCollList:{}", channelDefine.getChannelId(), gson.toJson(delFileCollList));
            }
        }
    }

    private void startExportQueueDataThread() {
        scheduledFuture = ExecutorUtil.scheduleAtFixedRate(() -> {
            // If the mq message is not sent for more than 10 seconds, it will be sent asynchronously.
            if (System.currentTimeMillis() - lastSendTime < 10 * 1000) {
                return;
            }
            synchronized (lock) {
                this.doExport(lineMessageList);
            }
        }, 10, 7, TimeUnit.SECONDS);
    }

    private void startCollectFile(Long channelId, Input input, List<String> patterns) {
        for (int i = 0; i < patterns.size(); i++) {
            log.info("startCollectFile,total file:{},start:{},remain:{}", patterns.size(), i + 1, patterns.size() - (i + 1));
            readFile(input.getPatternCode(), getTailPodIp(patterns.get(i)), patterns.get(i), channelId);
            InodeFileComparator.addFile(patterns.get(i));
        }
        lastLineRemainSendSchedule(input.getPatternCode());
    }


    private void handleAllFileCollectMonitor(String patternCode, String newFilePath, Long channelId) {
        String ip = getTailPodIp(newFilePath);

        if (logFileMap.keySet().stream().anyMatch(key -> Objects.equals(newFilePath, key))) {
            log.info("collectOnce open file:{}", newFilePath);
            logFileMap.get(newFilePath).setReOpen(true);
        } else {
            readFile(patternCode, ip, newFilePath, channelId);
        }
    }

    /**
     * 1.logSplitExpress:/home/work/log/log-agent/server.log.* realFilePaths: ["/home/work/log/log-agent/server.log"]
     * 2.logSplitExpress:/home/work/log/log-agent/(server.log.*|error.log.*) realFilePaths: ["/home/work/log/log-agent/server.log","/home/work/log/log-agent/server.log"]
     * 2.logSplitExpress:/home/work/log/(log-agent|log-stream)/server.log.* realFilePaths: ["/home/work/log/log-agent/server.log","/home/work/log/log-stream/server.log"]
     * The real file does not exist, it should also listen
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
                 * Compatible with the current file, it can be monitored when it is created.
                 */
                perFilePathExpress = String.format("(%s|%s)", perFilePathExpress, String.format("%s.*", realFilePaths.get(i)));
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
            fileProgress.setPodType(channelDefine.getPodType());
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
            long ct = System.currentTimeMillis();
            readResult.get().getLines().stream().forEach(l -> {
                String logType = channelDefine.getInput().getType();
                LogTypeEnum logTypeEnum = LogTypeEnum.name2enum(logType);
                // Multi-line application log type and opentelemetry type are used to determine the exception stack
                if (LogTypeEnum.APP_LOG_MULTI == logTypeEnum || LogTypeEnum.OPENTELEMETRY == logTypeEnum) {
                    l = mLog.append2(l);
                } else {
                    // tail single line mode
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
        resultMap.put(pattern, Pair.of(mLog, readResult));
        return listener;
    }

    private void lastLineRemainSendSchedule(String patternCode) {
        /**
         * Collect all data in the last row of data that has not been sent for more than 10 seconds.
         */
        lastFileLineScheduledFuture = ExecutorUtil.scheduleAtFixedRate(() -> SafeRun.run(() -> {
            for (Map.Entry<String, Pair<MLog, AtomicReference<ReadResult>>> referenceEntry : resultMap.entrySet()) {
                MLog mLog = referenceEntry.getValue().getKey();
                String pattern = referenceEntry.getKey();
                Long appendTime = mLog.getAppendTime();
                if (null != appendTime && Instant.now().toEpochMilli() - appendTime > 10 * 1000) {
                    String remainMsg = mLog.takeRemainMsg2();
                    if (null != remainMsg) {
                        synchronized (lock) {
                            log.info("start send last line,pattern:{},patternCode:{},data:{}", pattern, patternCode, remainMsg);
                            wrapDataToSend(remainMsg, referenceEntry.getValue().getValue(), pattern, patternCode, getTailPodIp(pattern), appendTime);
                        }
                    }
                }
            }
        }), 30, 30, TimeUnit.SECONDS);
    }

    private void wrapDataToSend(String lineMsg, AtomicReference<ReadResult> readResult, String pattern, String patternCode, String localIp, long ct) {
        LineMessage lineMessage = new LineMessage();
        lineMessage.setMsgBody(lineMsg);
        lineMessage.setPointer(readResult.get().getPointer());
        lineMessage.setLineNumber(readResult.get().getLineNumber());
        lineMessage.setFileName(pattern);
        lineMessage.setProperties(LineMessage.KEY_MQ_TOPIC_TAG, patternCode);
        lineMessage.setProperties(LineMessage.KEY_IP, localIp);
        lineMessage.setProperties(LineMessage.KEY_COLLECT_TIMESTAMP, String.valueOf(ct));
        String logType = channelDefine.getInput().getType();
        LogTypeEnum logTypeEnum = LogTypeEnum.name2enum(logType);
        if (null != logTypeEnum) {
            lineMessage.setProperties(LineMessage.KEY_MESSAGE_TYPE, logTypeEnum.getType().toString());
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
        fileProgress.setPodType(channelDefine.getPodType());
        fileProgress.setCtTime(ct);
        lineMessageList.add(lineMessage);

        fileReadMap.put(pattern, ct);

        int batchSize = msgExporter.batchExportSize();
        if (lineMessageList.size() > batchSize) {
            List<LineMessage> subList = lineMessageList.subList(0, batchSize);
            doExport(subList);
        }
    }

    private void readFile(String patternCode, String ip, String filePath, Long channelId) {
        MLog mLog = new MLog();
        if (StringUtils.isNotBlank(this.linePrefix)) {
            mLog.setCustomLinePattern(this.linePrefix);
        }
        String usedIp = StringUtils.isBlank(ip) ? NetUtil.getLocalIp() : ip;

        ReadListener listener = initFileReadListener(mLog, patternCode, usedIp, filePath);
        Map<String, ChannelMemory.FileProgress> fileProgressMap = channelMemory.getFileProgressMap();
        log.info("fileProgressMap:{}", gson.toJson(fileProgressMap));
        LogFile logFile = getLogFile(filePath, listener, fileProgressMap);
        if (null == logFile) {
            log.warn("file:{} marked stop to collect", filePath);
            return;
        }
        //Determine whether the file exists
        if (FileUtil.exist(filePath)) {
            stopOldCurrentFileThread(filePath);
            log.info("start to collect file,channelId:{},fileName:{}", channelId, filePath);
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

    private void stopOldCurrentFileThread(String filePath) {
        LogFile logFile = logFileMap.get(filePath);
        if (null != logFile) {
            logFile.setStop(true);
        }
        Future future = futureMap.get(filePath);
        if (null != future) {
            future.cancel(false);
        }
    }

    private LogFile getLogFile(String filePath, ReadListener listener, Map<String, ChannelMemory.FileProgress> fileProgressMap) {
        long pointer = 0L;
        long lineNumber = 0L;
        ChannelMemory.FileProgress fileProgress = fileProgressMap.get(filePath);
        if (fileProgress != null) {
            if (null != fileProgress.getFinished() && fileProgress.getFinished()) {
                /**
                 * Stateful pods in k8s do not need to be judged by finished
                 */
                if (StringUtils.isNotBlank(channelDefine.getPodType())) {
                    if (K8sPodTypeEnum.valueOf(channelDefine.getPodType().toUpperCase()) != K8sPodTypeEnum.STATEFUL) {
                        return null;
                    }
                }
            }
            pointer = fileProgress.getPointer();
            lineNumber = fileProgress.getCurrentRowNum();
            //Compare whether the inode value changes, and read from the beginning if the change
            ChannelMemory.UnixFileNode memoryUnixFileNode = fileProgress.getUnixFileNode();
            if (null != memoryUnixFileNode && null != memoryUnixFileNode.getSt_ino()) {
                log.info("memory file inode info,filePath:{},:{}", filePath, gson.toJson(memoryUnixFileNode));
                //Get current file inode information
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
            //Current limiting processing
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
        log.info("Delete the current collection task,channelId:{}", getChannelId());
        //1.Stop log capture
        for (Map.Entry<String, LogFile> fileEntry : logFileMap.entrySet()) {
            fileEntry.getValue().setStop(true);
            InodeFileComparator.removeFile(fileEntry.getKey());
        }
        //2. stop exporting
        this.msgExporter.close();
        //3. refresh cache
        memoryService.refreshMemory(channelMemory);
        // stop task
        if (null != scheduledFuture) {
            scheduledFuture.cancel(false);
        }
        if (null != lastFileLineScheduledFuture) {
            lastFileLineScheduledFuture.cancel(false);
        }
        for (Future future : futureMap.values()) {
            future.cancel(false);
        }
        log.info("stop file monitor,fileName:", logFileMap.keySet().stream().collect(Collectors.joining(SYMBOL_COMMA)));
        lineMessageList.clear();
        reOpenMap.clear();
        fileReadMap.clear();
        resultMap.clear();
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
    public synchronized void reOpen(String filePath) {
        //Judging the number of openings, it can only be reopened once within 10 seconds.
        if (reOpenMap.containsKey(filePath) && Instant.now().toEpochMilli() - reOpenMap.get(filePath) < 10 * 1000) {
            log.info("The file has been opened too frequently.Please try again in 10 seconds.fileName:{}," +
                    "last time opening time.:{}", filePath, reOpenMap.get(filePath));
            return;
        }
        reOpenMap.put(filePath, Instant.now().toEpochMilli());
        log.info("reOpen file:{}", filePath);
        if (collectOnce) {
            handleAllFileCollectMonitor(channelDefine.getInput().getPatternCode(), filePath, getChannelId());
            return;
        }
        LogFile logFile = logFileMap.get(filePath);
        String tailPodIp = getTailPodIp(filePath);
        String ip = StringUtils.isBlank(tailPodIp) ? NetUtil.getLocalIp() : tailPodIp;
        if (null == logFile) {
            // Add new log file
            readFile(channelDefine.getInput().getPatternCode(), ip, filePath, getChannelId());
            log.info("watch new file create for channelId:{},ip:{},path:{}", getChannelId(), filePath, ip);
        } else {
            // Normal log segmentation
            try {
                //Delay 5 seconds to split files, todo @shanwb Ensure that the files are collected before switching
                TimeUnit.SECONDS.sleep(5);
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

    /**
     * A file that has not been written to for more than 10 minutes.
     *
     * @return
     */
    @Override
    public Map<String, Long> getExpireFileMap() {
        Map<String, Long> expireMap = new HashMap();
        for (Map.Entry<String, Long> entry : fileReadMap.entrySet()) {
            if (Instant.now().toEpochMilli() - entry.getValue() > TimeUnit.MINUTES.toMillis(10)) {
                expireMap.put(entry.getKey(), entry.getValue());
            }
        }
        return expireMap;
    }

    @Override
    public void cancelFile(String file) {
        log.info("cancelFile,file:{}", file);
        for (Map.Entry<String, LogFile> logFileEntry : logFileMap.entrySet()) {
            if (file.equals(logFileEntry.getKey())) {
                delFileCollList.add(logFileEntry.getKey());
            }
        }
    }

    /**
     * Delete the specified directory collection, receive the delete event and no data is written in for more than 1 minute.
     *
     * @param path
     */
    private void delCollFile(String path) {
        boolean shouldRemovePath = false;
        if (logFileMap.containsKey(path) && fileReadMap.containsKey(path)) {
            if ((Instant.now().toEpochMilli() - fileReadMap.get(path)) > TimeUnit.MINUTES.toMillis(1)) {
                cleanFile(path::equals);
                shouldRemovePath = true;
                log.info("stop coll file:{}", path);
            }
        } else {
            shouldRemovePath = true;
        }
        if (shouldRemovePath) {
            log.info("channelId:{},delCollFile remove file:{}", channelDefine.getChannelId(), path);
            delFileCollList.removeIf(data -> StringUtils.equals(data, path));
        }
    }

    private void cleanFile(Predicate<String> filter) {
        List<String> delFiles = Lists.newArrayList();
        for (Map.Entry<String, LogFile> logFileEntry : logFileMap.entrySet()) {
            if (filter.test(logFileEntry.getKey())) {
                InodeFileComparator.removeFile(logFileEntry.getKey());
                logFileEntry.getValue().setStop(true);
                delFiles.add(logFileEntry.getKey());
                log.info("cleanFile,stop file:{}", logFileEntry.getKey());
            }
        }
        for (String delFile : delFiles) {
            logFileMap.remove(delFile);
        }
        delFiles.clear();
        for (Map.Entry<String, Future> futureEntry : futureMap.entrySet()) {
            if (filter.test(futureEntry.getKey())) {
                futureEntry.getValue().cancel(false);
                delFiles.add(futureEntry.getKey());
            }
        }
        for (String delFile : delFiles) {
            futureMap.remove(delFile);
        }
        delFiles.clear();
        delFiles = reOpenMap.keySet().stream()
                .filter(filePath -> filter.test(filePath))
                .collect(Collectors.toList());
        for (String delFile : delFiles) {
            reOpenMap.remove(delFile);
        }

        delFiles = fileReadMap.keySet().stream()
                .filter(filePath -> filter.test(filePath))
                .collect(Collectors.toList());
        for (String delFile : delFiles) {
            fileReadMap.remove(delFile);
        }

        delFiles = resultMap.keySet().stream()
                .filter(filePath -> filter.test(filePath))
                .collect(Collectors.toList());
        for (String delFile : delFiles) {
            resultMap.remove(delFile);
        }
    }

    @Override
    public Long getLogCounts() {
        return this.logCounts;
    }


}
