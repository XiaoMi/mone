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

package com.xiaomi.youpin.mischedule.task.test;

import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.youpin.mischedule.MethodInfo;
import com.xiaomi.youpin.mischedule.task.DubboTask;
import com.xiaomi.youpin.mischedule.task.test.common.BaseTest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Data
class HomePageRecommendRequest implements Serializable {
    private static final long serialVersionUID = -4152146844413977449L;
    private Long uid;
    private Integer count;
    private Integer offset;
    private Object extra;
}

/**
 * 需要带环境测试
 */
@Slf4j
public class DubboTaskTest extends BaseTest {

    @Autowired
    private DubboTask dubboTask;

    @Test
    public void testTask() {
        TaskParam taskParam = new TaskParam();
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setServiceName("com.xiaomi.youpin.metis.service.MetisDubboService");
        methodInfo.setMethodName("homePageRecommend");
        methodInfo.setParameterTypes(new String[]{"com.xiaomi.youpin.metis.service.bean.HomePageRecommendRequest"});
//        methodInfo.setGroup("staging");
        /*HomePageRecommendRequest homePageRecommendRequest = new HomePageRecommendRequest();
        homePageRecommendRequest.setUid(0L);
        homePageRecommendRequest.setCount(40);
        homePageRecommendRequest.setOffset(0);*/

        String j = "{\"uid\": 0, \"count\": 40, \"offset\": 0}";
        Object homePageRecommendRequest = new Gson().fromJson(j, Object.class);

        Object[] homePageRecommendRequests = {homePageRecommendRequest};
        methodInfo.setArgs(homePageRecommendRequests);
        taskParam.put("param",new Gson().toJson(methodInfo));
        TaskContext taskContext = new TaskContext();
        TaskResult res = dubboTask.execute(taskParam, taskContext);
        log.info("res:{}",res);
    }


    @Test
    public void testTask5() {
        TaskParam taskParam = new TaskParam();
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setServiceName("com.xiaomi.youpin.diamond.dashboard.api.service.PushPlanService");
        methodInfo.setMethodName("setDoToDone");
        methodInfo.setGroup("staging");
        methodInfo.setParameterTypes(new String[]{});
        Object[] homePageRecommendRequests = new Object[]{};
        methodInfo.setArgs(homePageRecommendRequests);
        taskParam.put("param",new Gson().toJson(methodInfo));
        TaskContext taskContext = new TaskContext();
        TaskResult res = dubboTask.execute(taskParam, taskContext);
        log.info("res:{}",res);
    }


    @Test
    public void testTask2() {
        TaskParam taskParam = new TaskParam();
        MethodInfo methodInfo = new MethodInfo();
        // methodInfo.setServiceName("com.mi.youpin.TestService");
        // methodInfo.setMethodName("error");
        methodInfo.setServiceName("com.xiaomi.youpin.markting.act.admin.task.LegoActDataJob");
        methodInfo.setMethodName("testMiScheduleJob");
        methodInfo.setParameterTypes(new String[]{});
        methodInfo.setArgs(new Object[]{});
        taskParam.put("param",new Gson().toJson(methodInfo));

        String str = new Gson().toJson(taskParam);
        System.out.println(str);

        TaskContext taskContext = new TaskContext();
        TaskResult res = dubboTask.execute(taskParam, taskContext);
        log.info("res:{}",res);
    }


    @Test
    public void testTask3() throws InterruptedException {
        IntStream.range(0,100000).forEach(i->{
            IntStream.range(0,10).parallel().forEach(it->{
                try {
                    TaskParam taskParam = new TaskParam();
                    MethodInfo methodInfo = new MethodInfo();
                    methodInfo.setServiceName("com.xiaomi.youpin.zzytest.api.service.DubboHealthService");
                    methodInfo.setMethodName("version");
                    methodInfo.setGroup("staging");
                    methodInfo.setParameterTypes(new String[]{});
                    methodInfo.setArgs(new Object[]{});
                    taskParam.put("param", new Gson().toJson(methodInfo));

                    String str = new Gson().toJson(taskParam);
                    System.out.println(str);

                    TaskContext taskContext = new TaskContext();
                    TaskResult res = dubboTask.execute(taskParam, taskContext);
                    log.info("res:{}", res);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            });

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });



        TimeUnit.SECONDS.sleep(40000);
    }

}
