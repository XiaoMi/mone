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

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangjuan
 * @version 1.0
 * @description
 */
@Slf4j
@NoArgsConstructor
public class NginxLogParser implements LogParser {

    private RegexLogParser regexLogParser;

    public NginxLogParser(LogParserData parserData) {
        String regexParseScript = generateRegexFromNginxScript(parserData.getParseScript());
        parserData.setParseScript(regexParseScript);
        this.regexLogParser = new RegexLogParser(parserData);
    }

    @Override
    public Map<String, Object> parse(String logData, String ip, Long lineNum, Long collectStamp, String fileName) {
        return this.regexLogParser.parse(logData, ip, lineNum, collectStamp, fileName);
    }

    @Override
    public Map<String, Object> parseSimple(String logData, Long collectStamp) {
        return this.regexLogParser.parseSimple(logData, collectStamp);
    }

    public String generateRegexFromNginxScript(String nginxFormatStr) {
        Pattern regexSpace = Pattern.compile("\\s");
        Pattern regexSpaceChar = Pattern.compile("\\\\[tnvfr]");
        Pattern regexSpacePlus = Pattern.compile("\\s+");
        Pattern regexVar = Pattern.compile("\\$\\{?[a-zA-Z0-9_]+\\}?");
        // Matches columns that may contain spaces (that is, columns that need to be enclosed in quotation marks in the nginx log format)
        Pattern regexSVar = Pattern.compile("([\\[\\{\\(\"])(\\$\\{?[a-zA-Z0-9_]+\\}?)([\\]\\}\\)\"])");
        Pattern regexNginxConfBodyGroup = Pattern.compile("'(.*?)'");

        // Placeholder for columns that do not contain spaces
        String varPlaceHolder = "__VAR_PLACE_HOLDER__";
        // Placeholders for columns that may contain spaces
        String svarPlaceholder = "__SVAR_PLACE_HOLDER__";

        String bodyStr = "";
        Matcher bodyMatcher = regexNginxConfBodyGroup.matcher(nginxFormatStr);
        while (bodyMatcher.find()) {
            for (int i = 1; i <= bodyMatcher.groupCount(); i++) {
                bodyStr += bodyMatcher.group(i);
            }
        }

        String valueRegex = "";
        // 1.  Replaces all spaces
        valueRegex = regexSpace.matcher(bodyStr).replaceAll(" ");
        // 2. Replaces all tabs that represent spaces
        valueRegex = regexSpaceChar.matcher(valueRegex).replaceAll(" ");
        // 3. Replace columns that contain spaces with placeholders
        valueRegex = regexSVar.matcher(valueRegex).replaceAll("$1" + svarPlaceholder + "$3");
        // 4. Replace all variables with placeholders
        valueRegex = regexVar.matcher(valueRegex).replaceAll(varPlaceHolder);
        valueRegex = escapeExprSpecialWord(valueRegex);
        // 5. Replace all spaces with regular expression syntax
        valueRegex = regexSpacePlus.matcher(valueRegex).replaceAll("\\\\s+");
        // 6. Replace the placeholders with the corresponding regular expression groupings
        valueRegex = valueRegex.replaceAll(svarPlaceholder, "(.*?)");
        valueRegex = valueRegex.replaceAll(varPlaceHolder, "(\\\\S*)");
        // 7. Matches the extra line breaks at the end
        valueRegex += "\\s*$";
        return valueRegex;
    }

    /**
     * Escape special characters
     *
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    @Override
    public List<String> parseLogData(String logData) throws Exception {
        return this.regexLogParser.parseLogData(logData);
    }
}
