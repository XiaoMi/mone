package com.xiaomi.mone.log.parse;

import com.gliwka.hyperscan.util.PatternFilter;
import com.google.common.collect.Lists;
import com.xiaomi.mone.log.utils.IndexUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangjuan
 * @version 1.0
 * @description
 */
@Slf4j
@NoArgsConstructor
public class RegexLogParser implements LogParser {

    private LogParserData parserData;
    private PatternFilter filter;

    public RegexLogParser(LogParserData parserData) {
        this.parserData = parserData;
        Pattern pattern = Pattern.compile(parserData.getParseScript(), Pattern.MULTILINE);
        List<Pattern> patterns = Lists.newArrayList();
        patterns.add(pattern);
        try {
            this.filter = new PatternFilter(patterns);
        } catch (Exception e) {
            this.filter = null;
        }
    }

    @Override
    public Map<String, Object> parse(String logData, String ip, Long lineNum, Long collectStamp, String fileName) {
        Map<String, Object> ret = parseSimple(logData, collectStamp);
        validRet(ret, logData);
        extractTimeStamp(ret, logData, collectStamp);
        wrapMap(ret, parserData, ip, lineNum, fileName, collectStamp);
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
            // 按正则提取出的内容列表
            List<String> logArray = parseLogData(logData);
            // 索引列名列表
            List<String> keyNameList = IndexUtils.getKeyListSlice(parserData.getKeyList());
            // 每个索引列名对应的内容在正则提取出的内容列表的索引值的数组
            int[] valueIndexList = Arrays.stream(parserData.getValueList().split(",")).mapToInt(Integer::parseInt).toArray();
            for (int i = 0; i < keyNameList.size(); i++) {
                // 如果 key 的索引超出 value 的范围，或 value 对应索引为 -1，则跳过当前 key
                if (i >= valueIndexList.length || valueIndexList[i] == -1) {
                    continue;
                }
                // value 的索引未超出正则解析后的内容数组，则该 key 有对应的解析值，否则为""
                String value = "";
                if (valueIndexList[i] < logArray.size()) {
                    value = logArray.get(valueIndexList[i]);
                }
                ret.put(keyNameList.get(i), value);
            }
            validTimestamp(ret, logData, collectStamp);
        } catch (Exception e) {
            // 如果出现异常则保留原始日志到 logsource 字段
            ret.put(esKeyMap_logSource, logData);
        }
        return ret;
    }

    @Override
    public List<String> parseLogData(String logData) throws Exception {
        List<String> ret = new ArrayList<>();
        if (filter == null) {
            throw new Exception("compile failed, empty filter");
        }
        List<Matcher> matchers = filter.filter(logData);
        if (matchers.size() > 0) {
            Matcher matcher = matchers.get(0);
            if (matcher.find()) {
                // matcher.groupCount() 获取的是 matcher 对象当前有多少个捕获组，不包括 group(0)，所以 groupCount 与 group(i) 索引是不对应的
                // group(0) 不支持用户获取，用户使用时，valueList 顺序仍从 0 开始即可
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    ret.add(matcher.group(i));
                }
            }
        }
        return ret;
    }
}
