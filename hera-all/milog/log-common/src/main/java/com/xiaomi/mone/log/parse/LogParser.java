package com.xiaomi.mone.log.parse;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateFormat;

import java.time.Instant;
import java.util.Map;

/**
 * @Author: wtt
 * @Date: 2021/12/28 21:57
 * @Description:
 */
public interface LogParser {

    String LOG_PREFIX = "[";
    String LOG_SUFFFIX = "]";
    Integer TIME_STAMP_MILLI_LENGTH = 13;

    Integer MESSAGE_MAX_SIZE = 25000;

    DateParser dateFormat1 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    DateParser dateFormat2 = FastDateFormat.getInstance("yy-MM-dd HH:mm:ss");
    DateParser dateFormat3 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");
    DateParser dateFormat4 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss,SSS");

    Integer specialTimeLength = dateFormat1.getPattern().length();

    String specialTimePrefix = "20";

    String esKeyMap_timestamp = "timestamp";
    String esKeyMap_Date = "Date";
    String esKeyMap_topic = "mqtopic";
    String esKeyMap_tag = "mqtag";
    String esKeyMap_logstoreName = "logstore";
    String esKeyMap_logSource = "logsource";
    String esKeyMap_MESSAGE = "message";
    String esKeyMap_tail = "tail";
    String esKeyMap_logip = "logip";
    String esKeyMap_lineNumber = "linenumber";
    String esKyeMap_fileName = "filename";

    /**
     * 兼容22-10-19 11:14:29这种
     *
     * @param logTime
     * @param collectStamp
     * @return
     */
    default Long getTimestampFromString(String logTime, Long collectStamp) {
        Long timeStamp;
        try {
            timeStamp = DateUtil.parse(logTime).getTime();
        } catch (Exception e) {
            try {
                logTime = String.format("%s%s", String.valueOf(DateUtil.thisYear()).substring(0, 2), logTime);
                timeStamp = DateUtil.parse(logTime).getTime();
            } catch (Exception ex) {
                timeStamp = collectStamp;
            }
        }
        return (null != timeStamp && timeStamp.toString().length() == TIME_STAMP_MILLI_LENGTH) ? timeStamp : Instant.now().toEpochMilli();
    }

    Map<String, Object> parse(String logData, String ip, Long lineNum, Long collectStamp, String fileName);

    Map<String, Object> parseSimple(String logData, Long collectStamp);

    default void wrapMap(Map<String, Object> ret, LogParserData parserData, String ip,
                         Long lineNum, String fileName, Long collectStamp) {
        ret.putIfAbsent(esKeyMap_timestamp, null == collectStamp ? getTimestampFromString("", collectStamp) : collectStamp);
        ret.putIfAbsent(esKeyMap_topic, parserData.getTopicName());
        ret.putIfAbsent(esKeyMap_tag, parserData.getMqTag());
        ret.putIfAbsent(esKeyMap_logstoreName, parserData.getLogStoreName());
        ret.putIfAbsent(esKeyMap_tail, parserData.getTailName());
        ret.putIfAbsent(esKeyMap_logip, ip);
        ret.putIfAbsent(esKeyMap_lineNumber, lineNum);
        ret.putIfAbsent(esKyeMap_fileName, fileName);
    }

    /**
     * if message not exist,add it,全量message不能和logsource同时存在，减少数据量的存储
     */
    default void checkMessageExist(Map<String, Object> ret, String originData) {
        if (!ret.containsKey(esKeyMap_MESSAGE)) {
            ret.put(esKeyMap_MESSAGE, originData.length() > MESSAGE_MAX_SIZE ? originData.substring(0, MESSAGE_MAX_SIZE) : originData);
            ret.remove(esKeyMap_logSource);
        }
    }

    /**
     * 时间提取
     */
    default void extractTimeStamp(Map<String, Object> ret, String logData, Long collectStamp) {
        /**
         * 提取文本中第一个[2022XXXX]这种，第一个默认是时间处理
         */
        if (!ret.containsKey(esKeyMap_timestamp) && logData.startsWith(LOG_PREFIX)) {
            String timeStamp = StringUtils.substringBetween(logData, LOG_PREFIX, LOG_SUFFFIX);
            Long time = getTimestampFromString(timeStamp, collectStamp);
            ret.put(esKeyMap_timestamp, time);
        }
        /**
         * 特殊处理，只会提取文件中以日期开始且yyyy-MM-dd HH:mm:ss这样的日期
         */
        if (!ret.containsKey(esKeyMap_timestamp) && logData.startsWith(specialTimePrefix)) {
            String timeStamp = StringUtils.substring(logData, 0, specialTimeLength);
            Long time = getTimestampFromString(timeStamp, collectStamp);
            ret.put(esKeyMap_timestamp, time);
        }
    }

    public static void main(String[] args) {
        LogParser logParser = new SeparatorLogParser();
        String msg = "[22-10-19 10:19:35] [mi.com.i18n.mi_com_i18n] [] [NOTICE] [923973969695] Controller[Registration] Action[Index] App_local[hk] sesion_server_init[0] newxmuuid[true] MiRedisProxy::_init[1|3536] MiRedisProxy::get[1|8014] GUESTID[XMGUEST-20CBCA55-2E94-1697-51DB-DC787EF9055A] HOSTNAME[sgp1-b2c-mishop-order-web03.alisgp] Request Finished";
        String timeStamp = StringUtils.substringBetween(msg, LOG_PREFIX, LOG_SUFFFIX);
        Long time = logParser.getTimestampFromString(timeStamp, Instant.now().toEpochMilli());
        System.out.println(time);
    }
}
