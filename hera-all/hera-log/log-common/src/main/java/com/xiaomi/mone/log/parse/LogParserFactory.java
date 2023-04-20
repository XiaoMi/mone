package com.xiaomi.mone.log.parse;

import lombok.Getter;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/5 10:27
 */
public class LogParserFactory {

    private LogParserFactory() {
    }

    public static LogParser getLogParser(Integer parseType, String keyList, String valueList, String parseScript) {
        return LogParserFactory.getLogParser(parseType, keyList, valueList, parseScript, "", "", "", "");
    }

    public static LogParser getLogParser(Integer parseType, String keyList, String valueList, String parseScript,
                                         String topicName, String tailName, String mqTag, String logStoreName) {
        LogParserData logParserData = LogParserData.builder().keyList(keyList)
                .valueList(valueList)
                .parseScript(parseScript)
                .topicName(topicName)
                .tailName(tailName)
                .mqTag(mqTag)
                .logStoreName(logStoreName).build();
        if (LogParserEnum.CUSTOM_PARSE.getCode().equals(parseType)) {
            return new CustomLogParser(logParserData);
        }
        if (LogParserEnum.REGEX_PARSE.getCode().equals(parseType)) {
            return new RegexLogParser(logParserData);
        }
        if (LogParserEnum.JSON_PARSE.getCode().equals(parseType)) {
            return new JsonLogParser(logParserData);
        }
        if (LogParserEnum.NGINX_PARSE.getCode().equals(parseType)) {
            return new NginxLogParser(logParserData);
        }
        return new SeparatorLogParser(logParserData);
    }

    @Getter
    public enum LogParserEnum {

        SEPARATOR_PARSE(2, "分割符"),
        CUSTOM_PARSE(5, "自定义脚本"),
        REGEX_PARSE(6, "正则表达式"),
        JSON_PARSE(7, "JSON解析"),
        NGINX_PARSE(8, "Nginx解析");

        private Integer code;
        private String name;

        LogParserEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
