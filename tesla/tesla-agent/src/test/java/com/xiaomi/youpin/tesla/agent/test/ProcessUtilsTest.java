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

package com.xiaomi.youpin.tesla.agent.test;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.tesla.agent.common.ProcessUtils;
import com.xiaomi.youpin.tesla.agent.common.Tail;
import com.xiaomi.youpin.tesla.agent.common.TemplateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProcessUtilsTest {


    @Test
    public void test2() {
        Pair<Integer, List<String>> res = ProcessUtils.process("/tmp/", "ps -ef|grep java|wc -l");
        String v = res.getValue().get(0);
        System.out.println(v);
    }


    @Test
    public void test() {
        Pair<Integer, List<String>> res = ProcessUtils.process("/tmp/", "touch eeeeee");
        System.out.println(res.getKey() + ":" + res.getValue());
    }


    @Test
    public void testTail() {
        ProcessUtils.process("/tmp/", it -> {
            System.out.println(it);
        }, false, "tail", "-f", "/var/log/system.log");
    }


    @Test
    public void testUptime2() {
        Pair<Integer, List<String>> uptime = ProcessUtils.process("/tmp/", "uptime");
        System.out.println(uptime);
    }


    @Test
    public void testTail3() throws InterruptedException, IOException {
        Tail tail = new Tail("/var/log/system.log", System.out::println);
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tail.stop();
        }).start();
        tail.tail();
    }


    @Test
    public void testTail2() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("tail", "-f", "/var/log/system.log");
        Process process = builder.start();

        new Thread(() -> {
            new BufferedReader(new InputStreamReader(process.getInputStream())).lines()
                    .forEach(System.out::println);

            System.out.println("finish");
        }).start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            process.destroy();
        }).start();

        int code = process.waitFor();
        System.out.println(code);
        TimeUnit.HOURS.sleep(1);

    }


    @Test
    public void testBatch() {
        System.out.println(ProcessUtils.process("/tmp/", "mkdir zzy"));
        System.out.println(ProcessUtils.process("/tmp/", "cp /Users/zhangzhiyong/IdeaProjects/tesla/tesla-agent/target/tesla-agent-1.0.0-SNAPSHOT.jar /tmp/zzy/"));
        System.out.println(ProcessUtils.process("/tmp/zzy", "touch start.sh"));

        String tml = TemplateUtils.getTemplate("start.tml");
        Map<String, Object> m = Maps.newHashMap();
        m.put("jar", "tesla-agent-1.0.0-SNAPSHOT.jar");
        String cmd = TemplateUtils.renderTemplate(tml, m);

        System.out.println(ProcessUtils.process("/tmp/zzy", cmd));
        System.out.println(ProcessUtils.process("/tmp/zzy", "chmod 777 start.sh"));
        System.out.println(ProcessUtils.process("/tmp/zzy", "./start.sh"));
    }


    @Test
    public void testKill() {

        ProcessUtils.process("/tmp/zzy/", "kill `cat pid`");
        ProcessUtils.process("/tmp/zzy/", "rm pid");

        String tml = TemplateUtils.getTemplate("stop.tml");
        Map<String, Object> m = Maps.newHashMap();
        m.put("jar", "tesla-agent-1.0.0-SNAPSHOT");
        String cmd = TemplateUtils.renderTemplate(tml, m);

        List<String> list = ProcessUtils.process("/tmp/", cmd).getValue();

        if (list.size() > 0) {
            String pid = list.get(0);
            System.out.println(pid);
            System.out.println(ProcessUtils.process("/tmp/", "kill " + pid));
        }

    }


    @Test
    public void testKill2() {
        String tml = TemplateUtils.getTemplate("stop.tml");
        Map<String, Object> m = Maps.newHashMap();
        m.put("jar", "Launcher");
        String cmd = TemplateUtils.renderTemplate(tml, m);
        System.out.println(cmd);
        List<String> list = ProcessUtils.process("/tmp/", cmd).getValue();
        System.out.println(list);
    }


    @Test
    public void testUptime() {
        List<String> list = ProcessUtils.process("/tmp/", "uptime").getValue();
        String info = list.get(0);
        //最近1分钟
        System.out.println(Double.parseDouble(info.split("averages:")[1].trim().split("\\s")[0].trim()));
    }


    @Test
    public void testDockerStats() {
        List<String> list = ProcessUtils.process("/tmp/", "docker stats --no-stream").getValue();
        list.stream().filter(it->it.contains("keen_sammet")).findAny().ifPresent(it->{
            String[] array = it.split("\\s+");
            System.out.println(Arrays.toString(array));
            System.out.println(array[2] + "---" + array[6]);
        });

    }
}
