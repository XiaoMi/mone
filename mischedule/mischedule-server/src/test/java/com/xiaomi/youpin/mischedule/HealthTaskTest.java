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

package com.xiaomi.youpin.mischedule;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.mischedule.api.service.bo.HealthParam;
import com.xiaomi.youpin.mischedule.api.service.bo.PipelineInfo;
import com.xiaomi.youpin.mischedule.task.HealthyTask;
import com.xiaomi.youpin.mischedule.task.test.common.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HealthTaskTest extends BaseTest {

    @Autowired
    private HealthyTask healthyTask;


    @Test
    public void testTask() {
        TaskParam param = new TaskParam();
        HealthParam healthParam = new HealthParam();

        PipelineInfo info = new PipelineInfo();
        info.setProjectId(1L);
        info.setPipelineId(2L);
        info.setEnvId(3L);
        healthParam.setPipelineInfo(info);

        List<String> urls = Lists.newArrayList(
//                "dubbo://xxxx/com.xiaomi.youpin.markting.act.api.LegoWeiXinApi//health",
                "http://www.baidu.com"
        );
        healthParam.setUrls(urls);

        param.put(TaskParam.PARAM,new Gson().toJson(healthParam));
        TaskContext context = new TaskContext();
        healthyTask.execute(param, context);
    }


    @Test
    public void testGetDubboServiceStatusByGeneric() {
        healthyTask.getDubboServiceStatusByGeneric("xxxx",20880,"com.xiaomi.youpin.zzytest.api.service.DubboHealthService","dev","health");
    }

}
