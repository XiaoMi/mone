package com.xiaomi.youpin.tesla.ip.util;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author goodjava@qq.com
 * @date 2024/1/31 11:27
 */
public class MarkdownFilter {


    private static final Pattern START_PATTERN = Pattern.compile("```");
    private static final Pattern JAVA_PATTERN = Pattern.compile("\\bjava\\b|\\bgo\\b");
    private static final Pattern END_PATTERN = Pattern.compile("```$");
    private final Consumer<String> codeConsumer;
    private boolean inCodeBlock = false;
    private boolean checkForJava = false;

    public MarkdownFilter(Consumer<String> codeConsumer) {
        this.codeConsumer = codeConsumer;
    }

    public void accept(String str) {
        if (inCodeBlock) {
            Matcher endMatcher = END_PATTERN.matcher(str);
            if (endMatcher.find()) {
                inCodeBlock = false;
                accept(str.substring(endMatcher.end()));
            } else {
                codeConsumer.accept(str);
            }
        } else if (checkForJava) {
            Matcher javaMatcher = JAVA_PATTERN.matcher(str);
            if (javaMatcher.lookingAt()) {
                inCodeBlock = true;
                // 处理java标记之后的内容
                accept(str.substring(javaMatcher.end()));
            } else {
                // 如果不是java，则发送之前缓存的```和当前字符串
                codeConsumer.accept("```" + str);
            }
            checkForJava = false;
        } else {
            Matcher startMatcher = START_PATTERN.matcher(str);
            if (startMatcher.find()) {
                checkForJava = true;
                // 发送开始标记之前的内容
                codeConsumer.accept(str.substring(0, startMatcher.start()));
                // 检查剩余的字符串是否以java开始
                String remaining = str.substring(startMatcher.end());
                if (!remaining.isEmpty()) {
                    accept(remaining);
                }
            } else {
                codeConsumer.accept(str);
            }
        }
    }

}
