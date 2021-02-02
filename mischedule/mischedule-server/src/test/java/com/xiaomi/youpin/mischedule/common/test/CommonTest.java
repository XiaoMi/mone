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
import com.xiaomi.data.push.schedule.task.impl.shell.ProcessUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CommonTest {


    @Test
    public void testParam() {
        TaskParam param = new TaskParam();
        TaskDefBean taskDefBean = new TaskDefBean();
        taskDefBean.setErrorRetryNum(Integer.MAX_VALUE);
        param.setTaskDef(taskDefBean);
        System.out.println(new Gson().toJson(param));
    }


    @Test
    public void testIp() {
        String url = "http://127.0.0.1:80/health";
        String[] ss = url.split("://|:");
        System.out.println(Arrays.toString(ss));

        //dubbo, 127.0.0.1, com.xiaomi.Test, group, health
        String url2 = "dubbo://127.0.0.1/com.xiaomi.Test/group/health";
        String[] ss2 = url2.split("://|/");
        System.out.println(Arrays.toString(ss2));
    }


    @Test
    public void testProcess() {
        String goBinPath = "go";
        Pair<Integer, List<String>> data = ProcessUtils.process("/tmp", new String[]{goBinPath + " env"});
        System.out.println(data);

        System.out.println(ProcessUtils.process("/tmp/", new String[]{goBinPath + " version"}));
    }

}
