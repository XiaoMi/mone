package com.xiaomi.mone.log.stream;

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.model.msg.LineMessage;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.mone.log.parse.LogParserFactory;
import com.xiaomi.mone.log.stream.plugin.loki.LokiResponse;
import com.xiaomi.mone.log.stream.plugin.loki.impl.HttpLokiClient;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HttpLokiClientTest {
    String tailName = "testTail";
    long tailId = 1;
    String logStoreName = "testLogStore";
    long logStoreId = 1;
    long logSpaceId = 1;
    String tenantId = "test";

    String topicName = "testTopic";
    String tag = "testTag";
    String keyList = "timestamp:date,level:keyword,traceId:keyword,threadName:text,className:text,line:text,appName:text,code:text,message:keyword,logstore:keyword,logsource:keyword,mqtopic:keyword,mqtag:keyword,logip:text,tail:keyword\",";
    String valueList = "0,3,-1,2,-1,-1,1,4,5,-1";
    String parseScript = "[%s]-[%s]-[%s]-[%s]-[%s]-%s";
    Integer parserType = LogParserFactory.LogParserEnum.CUSTOM_PARSE.getCode();
    LogParser customParse = LogParserFactory.getLogParser(parserType, keyList, valueList, parseScript, topicName, tailName, tag, logStoreName);
    String dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date());
    String dateFormatOld = "2022-01-17 15:03:27";

    @Test
    public void testNormalHttpLokiClient() {
        HttpLokiClient lokiClient = new HttpLokiClient();

        String msg = String.format("{\"lineNumber\":1,\"pointer\":326750437,\"msgBody\":\"[%s] [i18n_infra_demo_grpc] [i18n-infra-demo-grpc-stable-6f666bd7c4-k4drk] [NOTICE] [] {\\\"level\\\":\\\"info\\\",\\\"ts\\\":1640691156.8477683,\\\"msg\\\":\\\"calling SayHello\\\"}\",\"extMap\":{\"ct\":\"1640692920359\",\"ip\":\"127.0.0.1\",\"tag\":\"tags_14_30022_30093\"}}",
                dateFormat);

        String msg_copy = String.format("{\"lineNumber\":1,\"pointer\":326750437,\"msgBody\":\"[%s] [copy_i18n_infra_demo_grpc] [i18n-infra-demo-grpc-stable-6f666bd7c4-k4drk] [NOTICE] [] {\\\"level\\\":\\\"info\\\",\\\"ts\\\":1640691156.8477683,\\\"msg\\\":\\\"calling SayHello\\\"}\",\"extMap\":{\"ct\":\"1640692920359\",\"ip\":\"127.0.0.1\",\"tag\":\"tags_14_30022_30093\"}}",
                FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new DateTime().minusSeconds(15).toDate()));

        LineMessage lineMessage = new Gson().fromJson(msg, LineMessage.class);
        LineMessage lineMessage_copy = new Gson().fromJson(msg_copy, LineMessage.class);
        String ip = lineMessage.getProperties(LineMessage.KEY_IP);
        Long currentStamp = Instant.now().toEpochMilli();
        Map<String, Object> msgMap = customParse.parse(lineMessage.getMsgBody(), ip, 1l, currentStamp, "");
        Map<String, Object> msgMap_copy = customParse.parse(lineMessage_copy.getMsgBody(), ip, 1l, currentStamp, "");

        LokiResponse resp = lokiClient.send(msgMap, lokiClient.buildFixedTags(tailId, logStoreId, logSpaceId), tenantId);
        assertNull(resp);
        resp = lokiClient.send(msgMap_copy, lokiClient.buildFixedTags(tailId, logStoreId, logSpaceId), tenantId);
        assertNull(resp);

        resp = lokiClient.flushRequest();
        assertEquals(resp.status, 204);

        Exception e = null;
        try {
            lokiClient.close();
        } catch (Exception exp) {
            e = exp;
        }
        assertNull(e);
    }

    @Test
    public void testOldTsHttpLokiClient() {
        HttpLokiClient lokiClient = new HttpLokiClient();

        String msg = String.format("{\"lineNumber\":1,\"pointer\":326750437,\"msgBody\":\"[%s] [i18n_infra_demo_grpc] [i18n-infra-demo-grpc-stable-6f666bd7c4-k4drk] [NOTICE] [] {\\\"level\\\":\\\"info\\\",\\\"ts\\\":1640691156.8477683,\\\"msg\\\":\\\"calling SayHello\\\"}\",\"extMap\":{\"ct\":\"1640692920359\",\"ip\":\"127.0.0.1\",\"tag\":\"tags_14_30022_30093\"}}",
                dateFormatOld);

        LineMessage lineMessage = new Gson().fromJson(msg, LineMessage.class);
        String ip = lineMessage.getProperties(LineMessage.KEY_IP);
        Long currentStamp = Instant.now().toEpochMilli();
        Map<String, Object> msgMap = customParse.parse(lineMessage.getMsgBody(), ip, 1l, currentStamp, "");

        LokiResponse resp = lokiClient.send(msgMap, lokiClient.buildFixedTags(tailId, logStoreId, logSpaceId), tenantId);
        assertNull(resp);

        Exception e = null;
        try {
            lokiClient.close();
        } catch (Exception exp) {
            e = exp;
        }
        assertNull(e);
    }
}