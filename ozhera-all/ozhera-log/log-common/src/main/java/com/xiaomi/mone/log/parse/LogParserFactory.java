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

        SEPARATOR_PARSE(2, "Separator Parsing"),
        CUSTOM_PARSE(5, "Custom scripts Parsing"),
        REGEX_PARSE(6, "Regular Expression Parsing"),
        JSON_PARSE(7, "JSON Parsing"),
        NGINX_PARSE(8, "Nginx Parsing");

        private Integer code;
        private String name;

        LogParserEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
