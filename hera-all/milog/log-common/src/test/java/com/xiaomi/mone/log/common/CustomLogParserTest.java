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
        String keyList = "timestamp:date,level:keyword,traceId:keyword,threadName:text,className:text,line:keyword,methodName:keyword,message:text,podName:keyword,failed:text,taskId:text,apiType:text,uri:text,method:text,rt:text,code:text,sceneId:text,serialId:text,reportId:text,apiId:text,params:text,result:text,errorInfo :text,reqHeaders:text,respHeaders:text,logstore:keyword,logsource:keyword,mqtopic:keyword,mqtag:keyword,logip:keyword,tail:keyword,linenumber:long";
        String valueList = "0,-1,16,-1,-1,-1,-1,-1,-1,1,2,3,4,5,6,7,8,9,10,11,13,12,17,14,15";
        String parseScript = "|";
        String logData = "";
        String ip = "127.0.0.1";
        Long currentStamp = Instant.now().toEpochMilli();
        Integer parserType = LogParserFactory.LogParserEnum.SEPARATOR_PARSE.getCode();
        LogParser customParse = LogParserFactory.getLogParser(parserType, keyList, valueList, parseScript, topicName, tailName, tag, logStoreName);
        Map<String, Object> parse = customParse.parse(logData, ip, 1l, currentStamp, "");
        System.out.println(parse);

        System.out.println(customParse.getTimestampFromString("2021-12-28 18:04:51.23", currentStamp));
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
