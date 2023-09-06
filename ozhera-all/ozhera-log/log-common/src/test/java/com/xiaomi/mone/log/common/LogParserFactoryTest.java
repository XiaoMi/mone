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

import com.google.gson.Gson;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.mone.log.parse.LogParserFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Instant;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/5 10:33
 */
@Slf4j
public class LogParserFactoryTest {

    private Gson gson = new Gson();

    @Test
    public void test() {
        Integer parseType = 2;
        String keyList = "message:text,logstore:date,logsource:keyword,mqtopic:keyword,mqtag:text,logip:text,tail:keyword,linenumber:keyword";
        String valueList = "0";
        String parseScript = "@##$%";
        String topicName = "3424";
        String tailName = "test name";
        String mqTag = "fsfsd";
        String logStoreName = "testet";
        String message = "{}";
        LogParser logParser = LogParserFactory.getLogParser(parseType, keyList, valueList, parseScript, topicName, tailName, mqTag, logStoreName);

        LineMessage lineMessage = Constant.GSON.fromJson(message, LineMessage.class);
        String ip = lineMessage.getProperties(LineMessage.KEY_IP);
        Long lineNumber = lineMessage.getLineNumber();
        Map<String, Object> parseSimple = logParser.parse(lineMessage.getMsgBody(), ip, lineNumber, Instant.now().toEpochMilli(), lineMessage.getFileName());

        log.info("simple data:{}", gson.toJson(parseSimple));
    }
}
