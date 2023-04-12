/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.cron.test;

import com.xiaomi.youpin.cron.CronExpression;
import org.junit.Test;
import org.junit.Assert;

import java.text.ParseException;
import java.util.Date;

public class CronExpressionTest {

    @Test
    public void test1() throws ParseException {
        System.out.println("test1");
        CronExpression expression = new CronExpression("0 47 11 ? * *");
        System.out.println(expression.getNextValidTimeAfter(new Date()));
        Assert.assertNotNull(expression);
    }

    @Test
    public void test2() throws ParseException {
        System.out.println("test2");
        CronExpression expression = new CronExpression("0 0 0 * * ? *");
        System.out.println(expression.getNextValidTimeAfter(new Date()));
        Assert.assertNotNull(expression);
    }


    @Test
    public void test3() throws ParseException {
        CronExpression expression = new CronExpression("0/5 * * * * ?");
        System.out.println(expression.getNextValidTimeAfter(new Date()));
        Assert.assertNotNull(expression);
    }


    @Test
    public void test4() throws ParseException {
        CronExpression expression = new CronExpression("0/30 * * * * ?");
        System.out.println(expression.getNextValidTimeAfter(new Date()));
        Assert.assertNotNull(expression);
    }

}
