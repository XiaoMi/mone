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

package com.xiaomi.mone.file;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/9 11:33
 */
public class MLogTest {
    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("^20[0-9]{2}");
    private static final Pattern ERROR_LINE_PATTERN = Pattern.compile(".*ERROR|.*(WARN|INFO).*Exception");

    @Test
    public void test1() {
        MLog log = new MLog();
        log.setCustomLinePattern("^error");
        System.out.println(log.append2("2021|ERROR"));
        System.out.println(log.append2("error"));
        System.out.println(log.append2("error2"));
        System.out.println(log.append2("2021 2|ERROR"));
        System.out.println(log.append2("error a"));
        System.out.println(log.append2("error b"));
        System.out.println(log.append2("error c"));
        System.out.println(log.append2("2021 4|INFO"));
        System.out.println(log.append2("2021 5|INFO"));
    }

    @Test
    public void test2() {
        System.out.println(NEW_LINE_PATTERN.matcher("2021").find());
        System.out.println(NEW_LINE_PATTERN.matcher("2022").find());
        System.out.println(NEW_LINE_PATTERN.matcher("20225").find());
        System.out.println(NEW_LINE_PATTERN.matcher("201x").find());
        System.out.println(NEW_LINE_PATTERN.matcher("2112").find());
        System.out.println(NEW_LINE_PATTERN.matcher("1012").find());
        System.out.println(NEW_LINE_PATTERN.matcher("a012").find());
        System.out.println("=======");

        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|ERROR||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{}").find());
        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|ERROR||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{},2021-10-14 14:56:48,260|ERROR||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{}").find());
        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|WARN||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{}").find());
        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|INF1O1||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{}").find());
        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|WARN||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport exception:{}").find());

    }

    @Test
    public void test3() {
    }
}
