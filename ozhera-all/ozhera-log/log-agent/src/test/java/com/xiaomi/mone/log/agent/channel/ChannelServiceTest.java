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

import com.google.common.collect.Lists;
import com.xiaomi.mone.file.LogFile;
import com.xiaomi.mone.log.utils.SimilarUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/2/16 11:10
 */
@Slf4j
public class ChannelServiceTest {

    @Test
    public void monitorTest() {
        String logPattern = "/home/work/logs/neo-logs/jump-game-stable-74bbdbcf9d-sf2vx/applogs/i18n-shop-jump-game/log_debug.log|log_error.log|log_info.log|log_warn.log|sys.log";
        String changedFilePath = "/home/work/logs/neo-logs/jump-game-stable-74bbdbcf9d-sf2vx/applogs/i18n-shop-jump-game/info/log-info.2023443";
        String logSplitExpress = "/home/work/logs/neo-logs/jump-game-stable-74bbdbcf9d-sf2vx/applogs/i18n-shop-jump-game/(debug/log-debug.*|error/log-error.*|info/log-info.*|warn/log-warn.*|sys.*)";
        List<String> patterns = Arrays.asList("/home/work/logs/neo-logs/jump-game-stable-74bbdbcf9d-sf2vx/applogs/i18n-shop-jump-game/log_debug.log",
                "/home/work/logs/neo-logs/jump-game-stable-74bbdbcf9d-sf2vx/applogs/i18n-shop-jump-game/log_error.log",
                "/home/work/logs/neo-logs/jump-game-stable-74bbdbcf9d-sf2vx/applogs/i18n-shop-jump-game/log_info.log",
                "/home/work/logs/neo-logs/jump-game-stable-74bbdbcf9d-sf2vx/applogs/i18n-shop-jump-game/log_warn.log",
                "/home/work/logs/neo-logs/jump-game-stable-74bbdbcf9d-sf2vx/applogs/i18n-shop-jump-game/sys.log");
        ConcurrentHashMap<String, LogFile> logFileMap = new ConcurrentHashMap<>();
        String channelId = "";
        String localIp = "";
        String separator = "/";
        String baseFileName = logPattern.substring(logPattern.lastIndexOf(separator) + 1);
        log.warn("#### watch consumer accept file:{},logPattern:{}", changedFilePath, logPattern);
        String changeFileName = StringUtils.substringAfterLast(changedFilePath, separator);
        Pattern pattern = makeLogPattern(logPattern, logSplitExpress);
        String originFileName = StringUtils.substringAfterLast(logPattern, separator);
        boolean ifTo = true;
        if (changeFileName.contains("wf")) {
            String changeFilePrefix = StringUtils.substringBeforeLast(changeFileName, "-");
            ifTo = changeFilePrefix.equals(originFileName);
        }
        if (pattern.matcher(changedFilePath).matches() && ifTo) {
            List<String> fileNames = Arrays.stream(baseFileName.split("\\|")).collect(Collectors.toList());
            String fileName = SimilarUtils.findHighestSimilarityStr(changeFileName, fileNames);
            String newFilePath = changedFilePath.substring(0, changedFilePath.lastIndexOf(separator)) + separator + fileName;
            if (StringUtils.isNotEmpty(logSplitExpress)) {
                String finalFileName = fileName;
                newFilePath = patterns.stream().filter(logPath -> logPath.contains(finalFileName)).findFirst().get();
            }
            log.warn("newFilePath:{}", newFilePath);
            LogFile logFile = logFileMap.get(newFilePath);
            if (null == logFile) {
//                readFile(input.getPatternCode(), localIp, newFilePath, channelId);
                log.info("watch new file create for chnnelId:{},ip:{},path:{}", channelId, localIp, newFilePath);
            } else {
                try {
                    TimeUnit.SECONDS.sleep(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logFile.setReOpen(true);
                log.info("file reOpen: chnnelId:{},ip:{},path:{}", channelId, localIp, newFilePath);
            }
        } else {
            log.info("invalid file create event, logPattern:{}, changedFilePath:{}", logPattern, changedFilePath);
        }
    }

    public Pattern makeLogPattern(String logPattern, String logSplitExpress) {
        if (StringUtils.isNotEmpty(logSplitExpress)) {
            return Pattern.compile(logSplitExpress);
        }
        String separator = FileSystems.getDefault().getSeparator();
        List<String> pathList = Lists.newArrayList();
        for (String filePath : logPattern.split(",")) {
            String filePrefix = StringUtils.substringBeforeLast(filePath, separator);
            String multipleFileNames = StringUtils.substringAfterLast(filePath, separator);
            if (filePath.contains("*") && !filePath.contains(".*")) {
                logPattern = logPattern.replaceAll("\\*", ".*");
            } else {
                logPattern = Arrays.stream(multipleFileNames.split("\\|"))
                        .map(s -> filePrefix + separator + s + ".*")
                        .collect(Collectors.joining("|"));
            }
            if (!logPattern.endsWith(".*")) {
                logPattern = logPattern + ".*";
            }
            pathList.add(logPattern);
        }
        log.warn("logPattern -> regex:{}", logPattern);
        return Pattern.compile(logPattern);
    }
}
