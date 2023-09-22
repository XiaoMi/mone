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
package com.xiaomi.mone.log.parse;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateFormat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: wtt
 * @Date: 2021/12/28 21:57
 * @Description:
 */
public interface LogParser {

    String LOG_PREFIX = "[";
    String LOG_SUFFFIX = "]";
    Integer TIME_STAMP_MILLI_LENGTH = 13;

//    Integer MESSAGE_MAX_SIZE = 25000;

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
     * Compatible with 22-10-19 11:14:29 of this kind
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

    List<String> parseLogData(String logData) throws Exception;

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
     * if message not exist,add it,Full messages cannot exist at the same time as logsource, reducing the storage of data volume
     */
    default void checkMessageExist(Map<String, Object> ret, String originData) {
        if (!ret.containsKey(esKeyMap_MESSAGE)) {
            ret.put(esKeyMap_MESSAGE, originData);
            ret.remove(esKeyMap_logSource);
        }
    }

    /**
     * Time extraction
     */
    default void extractTimeStamp(Map<String, Object> ret, String logData, Long collectStamp) {
        /**
         * The first [2022 XXXX] in extracted text, the first default is time processing
         */
        if (!ret.containsKey(esKeyMap_timestamp) && logData.startsWith(LOG_PREFIX)) {
            String timeStamp = StringUtils.substringBetween(logData, LOG_PREFIX, LOG_SUFFFIX);
            Long time = getTimestampFromString(timeStamp, collectStamp);
            ret.put(esKeyMap_timestamp, time);
        }
        /**
         * Special handling, only dates starting with a date in the file such as yyyy-mm-dd HH:mm:ss will be extracted
         */
        if (!ret.containsKey(esKeyMap_timestamp) && logData.startsWith(specialTimePrefix)) {
            String timeStamp = StringUtils.substring(logData, 0, specialTimeLength);
            Long time = getTimestampFromString(timeStamp, collectStamp);
            ret.put(esKeyMap_timestamp, time);
        }
    }

    default void validTimestamp(Map<String, Object> ret, String logData, Long collectStamp) {
        /**
         * If the user configures the parse timestamp field, the time format is checked to be correct, and the incorrect time is set to the current time
         */
        if (ret.containsKey(esKeyMap_timestamp)) {
            Long time = getTimestampFromString(ret.get(esKeyMap_timestamp).toString(), collectStamp);
            ret.put(esKeyMap_timestamp, time);
        }
    }

    /**
     * Field configuration error check If the value corresponding to the key is empty in the result,
     * indicating that the corresponding key has not been extracted, the complete log is retained
     */
    default void validRet(Map<String, Object> ret, String logData) {
        if (ret.values().stream().filter(Objects::nonNull).map(String::valueOf).anyMatch(StringUtils::isEmpty)) {
            ret.put(esKeyMap_logSource, logData);
        }
    }
}
