package com.xiaomi.mone.log.parse;

import cn.hutool.core.collection.CollectionUtil;
import com.google.gson.Gson;
import com.xiaomi.mone.log.utils.IndexUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: wtt
 * @Date: 2021/12/28 21:59
 * @Description: 自定义表达式解析
 */
@Data
@NoArgsConstructor
@Slf4j
public class CustomLogParser implements LogParser {

    private boolean isParsePattern;

    private Map<Integer, List<String>> mapPattern;

    private Gson gson = new Gson();

    private LogParserData parserData;

    private String keyValueList;

    private List<String> logPerComments;

    public CustomLogParser(LogParserData parserData) {
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
        String originData = logData;
        if (logData == null) {
            return null;
        }
        try {
            if (!isParsePattern) {
                parsePatter(parserData.getParseScript());
            }
            if (logData.length() == 0) {
                return ret;
            }
            String originLog = logData;
            if (StringUtils.isBlank(keyValueList) && CollectionUtil.isEmpty(logPerComments)) {
                ret.put(esKeyMap_MESSAGE, logData);
                return ret;
            }
            int startIndex = 0;
            for (int i = 0; i < logPerComments.size(); i++) {
                String parsedData = "";
                List<String> list = mapPattern.get(i);
                if (StringUtils.isNotEmpty(list.get(0)) && StringUtils.isNotEmpty(list.get(1))) {
                    parsedData = StringUtils.substringBetween(logData, list.get(0), list.get(1));
                } else {
                    parsedData = logData;
                }
                if (null == parsedData) {
                    ret.put(logPerComments.get(i), logData);
                    break;
                }
                if (parsedData.length() > MESSAGE_MAX_SIZE) {
                    parsedData = StringUtils.substring(parsedData, 0, MESSAGE_MAX_SIZE);
                }
                ret.put(logPerComments.get(i), parsedData);
                if (i == logPerComments.size() - 1) {
                    break;
                }
                if (StringUtils.isNotEmpty(list.get(0)) && StringUtils.isNotEmpty(list.get(1))) {
                    startIndex = list.get(0).length() + parsedData.length() + list.get(1).length();
                } else {
                    startIndex = 0;
                }
                logData = StringUtils.substring(logData, startIndex).trim();
            }
            if (ret.values().stream().map(String::valueOf).anyMatch(StringUtils::isEmpty)) {
                if (originLog.length() > MESSAGE_MAX_SIZE) {
                    originLog = StringUtils.substring(originLog, 0, MESSAGE_MAX_SIZE);
                }
                ret.put(esKeyMap_logSource, originLog);
            }
            /**
             * 兜底 不包含 esKeyMap_timestamp，esKeyMap_topic ，esKeyMap_tag ，esKeyMap_logstoreName
             */
            if (ret.containsKey(esKeyMap_timestamp)) {
                Long time = getTimestampFromString(ret.get(esKeyMap_timestamp).toString(), collectStamp);
                ret.put(esKeyMap_timestamp, time);
            }
        } catch (Exception e) {
            ret.put(esKeyMap_logSource, originData);
        }
        return ret;
    }

    private void parsePatter(String pattern) {
        mapPattern = new HashMap<>();
        String[] split = StringUtils.split(pattern, "-");
        for (int i = 0; i < split.length; i++) {
            String[] split1 = split[i].split("%s");
            if (split1.length == 2) {
                mapPattern.put(i, Arrays.asList(split1[0], split1[1]));
            } else {
                mapPattern.put(i, Arrays.asList("", ""));
            }
        }

        keyValueList = IndexUtils.getKeyValueList(parserData.getKeyList(), parserData.getValueList());
        logPerComments = Arrays.stream(StringUtils.split(keyValueList, ",")).collect(Collectors.toList());

        isParsePattern = true;
    }

}
