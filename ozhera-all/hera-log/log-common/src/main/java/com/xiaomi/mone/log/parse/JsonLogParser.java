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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.xiaomi.mone.log.utils.IndexUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author liyandi
 */
@Slf4j
@NoArgsConstructor
public class JsonLogParser implements LogParser {

    private LogParserData parserData;

    public JsonLogParser(LogParserData parserData) {
        this.parserData = parserData;
    }

    @Override
    public Map<String, Object> parse(String logData, String ip, Long lineNum, Long collectStamp, String fileName) {
        Map<String, Object> ret = parseSimple(logData, collectStamp);
        //字段配置错误校验
        validRet(ret, logData);
        //timestamp
        extractTimeStamp(ret, logData, collectStamp);
        //default labels
        wrapMap(ret, parserData, ip, lineNum, fileName, collectStamp);
        //add default message
        checkMessageExist(ret, logData);
        return ret;
    }

    @Override
    public Map<String, Object> parseSimple(String logData, Long collectStamp) {
        Map<String, Object> ret = new HashMap<>();
        if (logData == null || logData.length() == 0) {
            return ret;
        }
        try {
            Map<String, Object> rawLogMap = JSON.parseObject(logData);
            // 索引列名全集
            List<String> keyNameList = IndexUtils.getKeyListSlice(parserData.getKeyList());
            // 索引子集，标记对应位置的索引列名是否在当前 tail 中被引用
            int[] valueIndexList = Arrays.stream(parserData.getValueList().split(",")).mapToInt(Integer::parseInt).toArray();
            for (int i = 0; i < keyNameList.size(); i++) {
                // 跳过未被引用的 key
                if (i >= valueIndexList.length || valueIndexList[i] == -1) {
                    continue;
                }
                String currentKey = keyNameList.get(i);
                String value = rawLogMap.getOrDefault(currentKey, "").toString();
                ret.put(currentKey, StringUtils.isNotEmpty(value) ? value.trim() : value);
            }
            /* 不需要考虑跳过了所有的key的情况，后面会对 esKeyMap_MESSAGE 做判断 :
            if (ret.size()==0){
                ret.put(esKeyMap_MESSAGE, logData);
                return ret;
            }
            */
            //timestamp
            validTimestamp(ret, logData, collectStamp);
        } catch (Exception e) {
            // 如果出现异常则保留原始日志到 logsource 字段
            ret.put(esKeyMap_logSource, logData);
        }
        return ret;
    }

    @Override
    public List<String> parseLogData(String logData) throws Exception {
        Map<String, Object> rawLogMap = JSON.parseObject(logData, Feature.OrderedField);
        List<String> parsedLogs = new ArrayList<>();
        for (String key : rawLogMap.keySet()) {
            parsedLogs.add(rawLogMap.getOrDefault(key, "").toString());
        }
        return parsedLogs;
    }
}
