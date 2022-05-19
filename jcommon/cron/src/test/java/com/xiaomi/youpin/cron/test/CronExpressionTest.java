package com.xiaomi.youpin.cron.test;

import com.xiaomi.youpin.cron.CronExpression;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class CronExpressionTest {

    @Test
    public void test1() throws ParseException {
        System.out.println("test1");
        CronExpression expression = new CronExpression("0 47 11 ? * *");
        System.out.println(expression.getNextValidTimeAfter(new Date()));
    }

    @Test
    public void test2() throws ParseException {
        System.out.println("test1");
        CronExpression expression = new CronExpression("0 0 0 * * ? *");
        System.out.println(expression.getNextValidTimeAfter(new Date()));
    }


    @Test
    public void test3() throws ParseException {
        CronExpression expression = new CronExpression("0/5 * * * * ?");
        System.out.println(expression.getNextValidTimeAfter(new Date()));
    }


    @Test
    public void test4() throws ParseException {
        CronExpression expression = new CronExpression("0/30 * * * * ?");
        System.out.println(expression.getNextValidTimeAfter(new Date()));
    }

}
