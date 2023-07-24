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
package com.xiaomi.mone.log.agent.common;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.log.agent.channel.memory.ChannelMemory;
import com.xiaomi.mone.log.common.PathUtils;
import com.xiaomi.mone.log.utils.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;
import static com.xiaomi.mone.log.common.PathUtils.PATH_WILDCARD;
import static com.xiaomi.mone.log.common.PathUtils.SEPARATOR;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/6/23 14:55
 */
@Slf4j
public class ChannelUtil {

    private Gson gson = GSON;

    private ChannelUtil() {

    }

    public static String queryCurrentCorrectIp(Map<String, String> ipPath, String pattern, List<String> ips) {
        if (ips.size() == 1) {
            return ips.get(ips.size() - 1);
        }
        String ip = "";
        try {
            if (!ipPath.isEmpty()) {
                String correctIpKey = ipPath.keySet().stream()
                        .filter(data -> pattern.contains(data))
                        .findFirst().get();
                ip = ipPath.get(correctIpKey);
            }
        } catch (Exception e) {
            ip = NetUtil.getLocalIp();
        }
        return ip;
    }

    public static void buildConnectionBetweenAppIp(String logPattern, Map<String, String> ipPath, List<String> ips, boolean collectOnce) {
        List<String> directoryWithMultiple = PathUtils.findDirectoryWithMultiple(logPattern, collectOnce);
        ipPath.clear();
        String ip;
        for (int i = 0; i < directoryWithMultiple.size(); i++) {
            try {
                ip = ips.get(i);
            } catch (Exception e) {
                ip = NetUtil.getLocalIp();
            }
            ipPath.put(directoryWithMultiple.get(i), ip);
        }
    }

    public static List<String> buildLogExpressList(String logPattern) {
        List<String> pathList = Lists.newArrayList();
        for (String filePath : logPattern.split(SYMBOL_COMMA)) {
            String filePrefix = StringUtils.substringBeforeLast(filePath, SEPARATOR);
            String multipleFileNames = StringUtils.substringAfterLast(filePath, SEPARATOR);
            if (filePath.contains(PATH_WILDCARD) && !filePath.contains(SYMBOL_MULTI)) {
                logPattern = logPattern.replaceAll("\\*", SYMBOL_MULTI);
            } else {
                logPattern = Arrays.stream(multipleFileNames.split("\\|"))
                        .map(s -> filePrefix + SEPARATOR + s + SYMBOL_MULTI)
                        .collect(Collectors.joining(DEFAULT_TAIL_SEPARATOR));
            }
            if (!logPattern.endsWith(SYMBOL_MULTI)) {
                logPattern = logPattern + SYMBOL_MULTI;
            }
            pathList.add(logPattern);
        }
        return pathList;
    }

    public static String buildSingleTimeExpress(String filePath) {
        String filePrefix = StringUtils.substringBeforeLast(filePath, SEPARATOR);
        String multipleFileName = StringUtils.substringAfterLast(filePath, SEPARATOR);
        if (!multipleFileName.contains(SYMBOL_MULTI)) {
            multipleFileName = multipleFileName.replaceAll("\\*", SYMBOL_MULTI);
        }
        if (multipleFileName.startsWith(PATH_WILDCARD)) {
            multipleFileName = multipleFileName.replaceFirst("\\*", SYMBOL_MULTI);
        }
        return String.format("%s%s%s", filePrefix, SEPARATOR, multipleFileName);
    }

    /**
     * unix系统文件才可以获取到值，否则为空对象
     *
     * @param filePath
     * @return
     */
    public static ChannelMemory.UnixFileNode buildUnixFileNode(String filePath) {
        try {
            BasicFileAttributes fileAttributes = Files.readAttributes(Paths.get(filePath), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            if (null != fileAttributes.fileKey()) {
                ChannelMemory.UnixFileNode unixFileNode = GSON.fromJson(GSON.toJson(fileAttributes.fileKey()), ChannelMemory.UnixFileNode.class);
                log.debug("origin file path:{},fileNode unixFileNode:{}", filePath, GSON.toJson(unixFileNode));
                return unixFileNode;
            }
        } catch (IOException e) {
            log.info("buildUnixFileNode error,filePath:{}", filePath, e);
        }
        return new ChannelMemory.UnixFileNode();
    }

}
