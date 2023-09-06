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
package com.xiaomi.mone.log.agent.rpc.processor.service;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.log.agent.channel.ChannelDefine;
import com.xiaomi.mone.log.agent.channel.locator.ChannelDefineRpcLocator;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/9 10:36
 */
@Slf4j
public class ChannelDefineRpcLocatorTest {

    private Gson gson = new Gson();

    @Test
    public void testDataTrans() {
        Ioc.ins().init("com.xiaomi");
        ChannelDefineRpcLocator channelDefineRpcLocator = Ioc.ins().getBean(ChannelDefineRpcLocator.class);
        System.out.println(channelDefineRpcLocator);
        String str = "";
        LogCollectMeta logCollectMeta = JSONUtil.toBean(str, LogCollectMeta.class);
        List<ChannelDefine> channelDefine = ChannelDefineRpcLocator.agentTail2ChannelDefine(channelDefineRpcLocator.logCollectMeta2ChannelDefines(logCollectMeta));
        log.info("Returns data：{}", channelDefine);
        Assert.assertNotNull(channelDefine);
    }

    @Test
    public void testPattern() {
        String logPattern = "/home/work/log/log/error.log";
        String changedFilePath = "/home/work/log/log/error.log.202323";
        logPattern = makePattern(logPattern);
        log.warn("logPattern -> regex:{}", logPattern);
        Pattern pattern = Pattern.compile(logPattern);
        Assert.assertEquals(true, pattern.matcher(changedFilePath).matches());

    }

    @Test
    public void testPattern1() {
        String logPattern = "/home/work/log/log/server.log,/home/work/log/test/server.log";
        String changedFilePath = "/home/work/log/test/server.log.202323";
        logPattern = makePattern(logPattern);
        log.warn("logPattern -> regex:{}", logPattern);
        Pattern pattern = Pattern.compile(logPattern);
        Assert.assertEquals(true, pattern.matcher(changedFilePath).matches());

    }

    @Test
    public void testPattern11() {
        String logPattern = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log";
        String changedFilePath = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log.wf-2021122714.gz";
        logPattern = makePattern(logPattern);
        log.warn("logPattern -> regex:{}", logPattern);
        Pattern pattern = Pattern.compile(logPattern);
        Assert.assertEquals(true, pattern.matcher(changedFilePath).matches());

    }

    @Test
    public void test123() {
        String logPattern = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log";
        String separator = "/";
        String changedFilePath = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log-2021122715.gz";
        String changeFileName = StringUtils.substringAfterLast(changedFilePath, separator);
        String originFileName = StringUtils.substringAfterLast(logPattern, separator);
        boolean ifTo = true;
        if (changeFileName.startsWith(originFileName + "-")) {
            String changeFilePrefix = StringUtils.substringBefore(changeFileName, "-");
            ifTo = changeFilePrefix.equals(originFileName);
        }
        Assert.assertEquals(true, ifTo);
    }

    @Test
    public void test1234() {
        String logPattern = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log";
        String separator = "/";
        String changedFilePath = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log-2021122715.gz";
        String changeFileName = StringUtils.substringAfterLast(changedFilePath, separator);
        String originFileName = StringUtils.substringAfterLast(logPattern, separator);
        boolean ifTo = true;
        if (changeFileName.contains("wf")) {
            String changeFilePrefix = StringUtils.substringBefore(changeFileName, "-");
            ifTo = changeFilePrefix.equals(originFileName);
        }
        Assert.assertEquals(true, ifTo);
    }

    @Test
    public void test12345() {
        String logPattern = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log.wf";
        String separator = "/";
        String changedFilePath = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log.wf-2021122715.gz";
        String changeFileName = StringUtils.substringAfterLast(changedFilePath, separator);
        String originFileName = StringUtils.substringAfterLast(logPattern, separator);
        boolean ifTo = true;
        if (changeFileName.contains("wf")) {
            String changeFilePrefix = StringUtils.substringBefore(changeFileName, "-");
            ifTo = changeFilePrefix.equals(originFileName);
        }
        Assert.assertEquals(true, ifTo);
    }

    @Test
    public void test12346() {
        String logPattern = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log";
        String separator = "/";
        String changedFilePath = "/home/work/logs/applogs/cn_xm_stock_soa/cn_xm_stock_soa.log.wf-2021121210.gz";
        String changeFileName = StringUtils.substringAfterLast(changedFilePath, separator);
        String originFileName = StringUtils.substringAfterLast(logPattern, separator);
        boolean ifTo = true;
        if (changeFileName.contains("wf")) {
            String changeFilePrefix = StringUtils.substringBefore(changeFileName, "-");
            ifTo = changeFilePrefix.equals(originFileName);
        }
        Assert.assertEquals(false, ifTo);
    }
    @Test
    public void test12347() {
        String logPattern = "/home/work/logs/applogs/cn_xm_stock_soa/server.log";
        String separator = "/";
        String changedFilePath = "/home/work/logs/applogs/cn_xm_stock_soa/server.log.2021-12-27-07";
        String changeFileName = StringUtils.substringAfterLast(changedFilePath, separator);
        String originFileName = StringUtils.substringAfterLast(logPattern, separator);
        boolean ifTo = true;
        if (changeFileName.contains("wf")) {
            String changeFilePrefix = StringUtils.substringBefore(changeFileName, "-");
            ifTo = changeFilePrefix.equals(originFileName);
        }
        Assert.assertEquals(true, ifTo);
    }

    @Test
    public void testPattern2() {
        String logPattern = "/home/work/log/*/trace.log";
        String changedFilePath = "/home/work/log/log-agent/trace.log.2021-12-23-16";
        logPattern = makePattern(logPattern);
        log.warn("logPattern -> regex:{}", logPattern);
        Pattern pattern = Pattern.compile(logPattern);
        Assert.assertEquals(true, pattern.matcher(changedFilePath).matches());

    }

    private String makePattern(String logPattern) {
        String separator = "/";
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
        return pathList.stream().collect(Collectors.joining("|"));
    }

    @Test
    public void test2() {
        String logPattern = "/home/work/log/log/server.log|error.log";
        String changedFilePath = "/home/work/log/log/server.log.2021-12-23-16";
        String suffix = ".log";
        String separator = "/";
        String baseFileName = logPattern.substring(logPattern.lastIndexOf(separator) + 1);
        System.out.println();
        System.out.println(baseFileName);

    }
}
