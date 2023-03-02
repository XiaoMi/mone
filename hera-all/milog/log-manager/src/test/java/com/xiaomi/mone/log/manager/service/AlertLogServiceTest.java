package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.api.model.vo.AlarmPattern;
import com.xiaomi.mone.log.manager.model.bo.alert.AlertMsgPattern;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: wtt
 * @date: 2022/6/2 10:57
 * @description:
 */
@Slf4j
public class AlertLogServiceTest {

    final String regex = "^.*\\|ERROR\\|(?!.*BizException|StockServiceImpl|AuditSuccessConsumerImpl ).*$";
    final String message = ""
            + "err_msg: \"your chosen goods 31956 stock not enough\"";

    @Test
    public void testPattern() {

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            System.out.println("Full match: " + matcher.group(0));

            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println("Group " + i + ": " + matcher.group(i));
            }
        }
    }

    @Test
    public void test() {
    }
}
