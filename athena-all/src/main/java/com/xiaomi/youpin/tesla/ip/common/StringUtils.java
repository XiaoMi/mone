package com.xiaomi.youpin.tesla.ip.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author goodjava@qq.com
 * @date 2023/5/18 22:26
 */
public abstract class StringUtils {

    public static Pattern endingKeywordPattern = Pattern.compile("(\\(|（|\\\\)(class|method|project|module)(\\)|）|\\\\)?$");


    public static String convertToCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        String[] words = input.split("_");
        result.append(words[0]);
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            String capitalizedWord = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            result.append(capitalizedWord);
        }
        return result.toString();
    }


    //写一个正则，从结尾提取出我期望的字符（class、method、project、module），接下来描述时我用x代表目标字符，结尾场景有这三种情况：(x)、（x）、\x，不满足提取诉求的返回空
	public static String extractEndingKeyword(String input) {
	    if (org.apache.commons.lang3.StringUtils.isEmpty(input)) {
	        return "";
	    }
        input = extractMultilineComment(input);
        Matcher matcher = endingKeywordPattern.matcher(input);
	    if (matcher.find()) {
	        return matcher.group(2);
	    }
	    return "";
	}

    //写一个方法，输入一个字符串，判断是否是多行注释，如果是多行注释，请把注释符号全去掉，只返回注释文本
	public static String extractMultilineComment(String input) {
	    if (input == null || !input.startsWith("/*") || !input.endsWith("*/")) {
	        return input;
	    }
	    return input.substring(2, input.length() - 2).replaceAll("\\*", "").trim();
	}



}
