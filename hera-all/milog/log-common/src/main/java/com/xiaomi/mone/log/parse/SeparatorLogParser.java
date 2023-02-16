package com.xiaomi.mone.log.parse;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 */
@Slf4j
@NoArgsConstructor
public class SeparatorLogParser implements LogParser {

    private LogParserData parserData;

    public SeparatorLogParser(LogParserData parserData) {
        this.parserData = parserData;
    }

    @Override
    public Map<String, Object> parse(String logData, String ip, Long lineNum, Long collectStamp, String fileName) {
        Map<String, Object> ret = parseSimple(logData, collectStamp);
        extractTimeStamp(ret, logData, collectStamp);
        wrapMap(ret, parserData, ip, lineNum, fileName, collectStamp);
        checkMessageExist(ret, logData);
        return ret;
    }

    @Override
    public Map<String, Object> parseSimple(String logData, Long collectStamp) {
        Map<String, Object> ret = new HashMap<>();
        if (logData == null) {
            return ret;
        }
        if (logData.length() == 0) {
            return ret;
        }
        try {
            String[] keysAndTypes = StringUtils.split(parserData.getKeyList(), ",");
            String[] values = StringUtils.split(parserData.getValueList(), ",");

            int maxLength = Arrays.stream(values).filter(s -> !s.equals("-1")).collect(Collectors.toList()).size();

            String[] logArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(logData, parserData.getParseScript(), maxLength);
            if (0 == maxLength) {
                ret.put(esKeyMap_MESSAGE, logData);
                return ret;
            }
            if (values.length == 1 && logArray.length == 1 && maxLength == 1) {
                String[] ktSplit = keysAndTypes[0].split(":");
                String keysAndType = ktSplit[0];
                ret.put(keysAndType, logArray);
                return ret;
            }

            int count = 0;
            int valuecount = 0;
            /**
             * 正常解析
             */
            for (int i = 0; i < keysAndTypes.length; i++) {
                String[] kTsplit = keysAndTypes[i].split(":");
                if (kTsplit.length != 2) {
                    continue;
                }
                if (kTsplit[0].equals(esKeyMap_topic)) {
                    count++;
                    ret.put(esKeyMap_topic, parserData.getTopicName());
                    continue;
                } else if (kTsplit[0].equals(esKeyMap_tag)) {
                    count++;
                    ret.put(esKeyMap_tag, parserData.getMqTag());
                    continue;
                } else if (kTsplit[0].equals(esKeyMap_logstoreName)) {
                    count++;
                    ret.put(esKeyMap_logstoreName, parserData.getLogStoreName());
                    continue;
                } else if (kTsplit[0].equals(esKeyMap_tail)) {
                    count++;
                    ret.put(esKeyMap_tail, parserData.getTailName());
                    continue;
                } else if (kTsplit[0].equals(esKeyMap_logSource)) {
                    count++;
                    continue;
                }
                String value = null;
                int num = -1;
                try {
                    num = new Integer(values[i]);
                    if (num == -1) {
                        valuecount++;
                        continue;
                    }
                } catch (Exception e) {
                    continue;
                }
                if (num < logArray.length && num > -1) {
                    value = logArray[num];
//                    if (StringUtils.startsWith(value, LOG_PREFIX) && StringUtils.endsWith(value, LOG_SUFFFIX)) {
//                        value = value.substring(1, value.length() - 1);
//                    }
                } else {
                    value = "";
                }
                if (kTsplit[0].equals(esKeyMap_timestamp) || kTsplit[1].equalsIgnoreCase(esKeyMap_Date)) {
                    Long time = getTimestampFromString(value, collectStamp);
                    ret.put(esKeyMap_timestamp, time);
                } else {
                    if (value.length() > MESSAGE_MAX_SIZE) {
                        value = StringUtils.substring(value, 0, MESSAGE_MAX_SIZE);
                    }
                    ret.put(kTsplit[0], value);
                }
            }

            /**
             * 字段配置错误
             * esKeyMap_topic,esKeyMap_tag,esKeyMap_logstoreName,esKeyMap_logSource 对用户不可见，即不存在于values，logArray
             */
            if (ret.values().stream().filter(Objects::nonNull).map(String::valueOf).anyMatch(StringUtils::isEmpty)) {
                if (logData.length() > MESSAGE_MAX_SIZE) {
                    logData = StringUtils.substring(logData, 0, MESSAGE_MAX_SIZE);
                }
                ret.put(esKeyMap_logSource, logData);
            }
        } catch (Exception e) {
            ret.put(esKeyMap_logSource, logData);
        }
        return ret;
    }
}
