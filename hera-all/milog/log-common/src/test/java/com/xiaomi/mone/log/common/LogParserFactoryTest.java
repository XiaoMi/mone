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
        String tailName = "发射点发射点";
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
