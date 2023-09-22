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
package com.xiaomi.mone.log.common;

import com.google.common.base.Stopwatch;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.mone.log.parse.LogParserFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

import java.time.Instant;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/29 14:25
 */
@Slf4j
public class CustomLogParserTest {

    String topicName = "test";
    String tailName = "test";
    String tag = "test";
    String logStoreName = "test";

    @Test
    public void test1() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String keyList = "timestamp:date,podName:keyword,level:keyword,threadName:text,className:text,line:keyword,methodName:keyword,traceId:keyword,message:text,ip:ip,logstore:keyword,logsource:keyword,mqtopic:keyword,mqtag:keyword,logip:keyword,tail:keyword,linenumber:long";
        String valueList = "0,-1,16,-1,-1,-1,-1,-1,-1,1,2,3,4,5,6,7,8,9,10,11,13,12,17,14,15";
        String parseScript = "|";
        String logData = "";
        String ip = "127.0.0.1";
        Long currentStamp = Instant.now().toEpochMilli();
        Integer parserType = LogParserFactory.LogParserEnum.SEPARATOR_PARSE.getCode();
        LogParser customParse = LogParserFactory.getLogParser(parserType, keyList, valueList, parseScript, topicName, tailName, tag, logStoreName);
        Map<String, Object> parse = customParse.parse(logData, ip, 1l, currentStamp, "");
        System.out.println(parse);

        System.out.println(customParse.getTimestampFromString("2023-08-25 10:46:09.239", currentStamp));
        stopwatch.stop();
        log.info("cost time:{}", stopwatch.elapsed().toMillis());
    }

    @Test
    public void test2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String keyList = "message:text,logstore:keyword,logsource:keyword,mqtopic:keyword,mqtag:keyword,logip:keyword,tail:keyword,linenumber:long";
        String valueList = "0";
        String parseScript = "|";
        String logData = "";
        String ip = "127.0.0.1";
        Long currentStamp = Instant.now().toEpochMilli();
        Integer parserType = LogParserFactory.LogParserEnum.CUSTOM_PARSE.getCode();
        LogParser customParse = LogParserFactory.getLogParser(parserType, keyList, valueList, parseScript, topicName, tailName, tag, logStoreName);
        Map<String, Object> parse = customParse.parse(logData, ip, 1l, currentStamp, "");
        System.out.println(parse);
        stopwatch.stop();
        log.info("cost time:{}", stopwatch.elapsed().toMillis());
    }

    @Test
    public void test() {
        System.out.println("1.647590227174E12".length());
        System.out.println(String.valueOf(Instant.now().toEpochMilli()).length());
        DateParser dateFormat1 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat1.getPattern().length());
    }

    @Test
    public void testGetTime() {
        String msg = "";
        String substring = msg.substring(0, 20);
        System.out.println("1.647590227174E12".length());
        System.out.println(String.valueOf(Instant.now().toEpochMilli()).length());
        DateParser dateFormat1 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat1.getPattern().length());
    }
}
