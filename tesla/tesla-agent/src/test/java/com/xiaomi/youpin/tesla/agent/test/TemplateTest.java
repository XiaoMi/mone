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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.youpin.tesla.agent.common.TemplateUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateTest {


    @Test
    public void testTemplate() {
        String tml = TemplateUtils.getTemplate("docker_file.tml");
        Map<String, Object> m = Maps.newHashMap();
        m.put("project_path", "xxxx/mischedule/");
        m.put("log_path", "xxxx/log/mischedule/");
        m.put("jar_name", "mischedule.jar");
        m.put("java_heap", "256");
        String str = TemplateUtils.renderTemplate(tml, m);
        System.out.println(str);
    }


    @Test
    public void test2() {
        List<String> p = Lists.newArrayList("a", ",", "b", "c");
        String tml = "[<%for(p in params)print(p);%>]";
        Map<String, Object> m = Maps.newHashMap();
        m.put("params", p);
        String str = TemplateUtils.renderTemplate(tml, m);
        System.out.println(str);
    }

    @Test
    public void testJoin() {
        List<String> li = Lists.newArrayList("a", "b", "c");
        System.out.println(li.stream().map(it -> "\"" + it + "\"").collect(Collectors.joining(",")));
    }


    @Test
    public void modifyString() throws IOException {
        String str = new String(Files.readAllBytes(Paths.get("/Users/zhangzhiyong/IdeaProjects/tesla/tesla-agent/src/main/resources/template/docker_file.tml")));
        System.out.println(str);

        int inedex = str.indexOf("ENTRYPOINT");
        System.out.println(str.substring(0, inedex)+"${entrypoint}");

    }
}
