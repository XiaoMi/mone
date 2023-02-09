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

package com.xiaomi.youpin.mischedule.common.test;

import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskDefBean;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.mischedule.STaskDef;
import org.junit.Test;

public class JsonTest {

    @Test
    public void testFromJson() {
        String str = "{\n" +
                " \"taskDef\": \"CloudCompileTask\",\n" +
                " \"taskId\": 1,\n" +
                " \"param\": {\n" +
                "  \"param\": \"{\\\"gitUrl\\\":\\\"http://test.com/youpin-gateway/mock-data-filter.git\\\",\\\"branch\\\":\\\"094c67c3f3082200c831ebb59878e6625cfb8afc\\\",\\\"profile\\\":\\\"\\\",\\\"tags\\\":\\\"CompileProject\\\"}\"\n" +
                " },\n" +
                " \"beginTime\": 0\n" +
                "}";

        Gson gson = new Gson();
        TaskParam tp = gson.fromJson(str, TaskParam.class);
        System.out.println(tp);
    }


    @Test
    public void testTaskParam() {
        TaskParam tp = new TaskParam();
        TaskDefBean bean = new TaskDefBean(STaskDef.MiTestTask);
        tp.setTaskDef(bean);
        tp.setCron("0/10 * * * * ? *");
        System.out.println(new Gson().toJson(tp));
    }


    @Test
    public void testTaskParam2() {
        TaskParam tp = new TaskParam();
        tp.put("path","/tmp/");
        tp.put("cmd","uname -a");
        TaskDefBean bean = new TaskDefBean(STaskDef.ShellTask);
        tp.setTaskDef(bean);
        tp.setCron("0/10 * * * * ? *");
        System.out.println(new Gson().toJson(tp));
    }


    @Test
    public void testTaskParam3() {
        TaskParam tp = new TaskParam();
        tp.put("task_config","{\n" +
                "  \"taskList\": [\n" +
                "    {\n" +
                "      \"index\": 0,\n" +
                "      \"taskDef\": {\"name\":\"SimpleTask\"},\n" +
                "      \"taskParam\": {\n" +
                "        \"taskDef\": {\"name\":\"SimpleTask\",\"type\": \"simpleTask\"},\n" +
                "        \"taskId\": 0,\n" +
                "        \"param\": {\n" +
                "          \"key\": \"1\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"taskId\": 0,\n" +
                "      \"dependList\": [],\n" +
                "      \"status\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"index\": 1,\n" +
                "      \"taskDef\": {\"name\":\"SimpleTask\"},\n" +
                "      \"taskParam\": {\n" +
                "        \"taskDef\": {\"name\":\"SimpleTask\",\"type\": \"simpleTask\"},\n" +
                "        \"taskId\": 0,\n" +
                "        \"param\": {\n" +
                "          \"key\": \"$_0_java.util.Map_result{key}\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"taskId\": 0,\n" +
                "      \"dependList\": [],\n" +
                "      \"status\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"index\": 2,\n" +
                "      \"taskDef\": {\"name\":\"SimpleTask\"},\n" +
                "      \"taskParam\": {\n" +
                "        \"taskDef\": {\"name\":\"SimpleTask\",\"type\": \"simpleTask\"},\n" +
                "        \"taskId\": 0,\n" +
                "        \"param\": {\n" +
                "          \"key\": \"3\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"taskId\": 0,\n" +
                "      \"dependList\": [],\n" +
                "      \"status\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"dependList\": [\n" +
                "    {\n" +
                "      \"from\": 0,\n" +
                "      \"to\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"from\": 0,\n" +
                "      \"to\": 2\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        TaskDefBean bean = new TaskDefBean(STaskDef.GraphTask);
        tp.setTaskDef(bean);
        System.out.println(new Gson().toJson(tp));
    }
}
