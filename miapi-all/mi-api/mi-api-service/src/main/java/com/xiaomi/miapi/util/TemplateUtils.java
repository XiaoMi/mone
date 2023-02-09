package com.xiaomi.miapi.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public class TemplateUtils {

    public static String processTemplate(String template, Map<String, String> params) {
        Matcher m = Pattern.compile("\\$\\{\\w+\\}").matcher(template);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String param = m.group();
            String value = params.get(param.substring(2, param.length() - 1));
            m.appendReplacement(sb, value == null ? "" : value);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
