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

package run.mone.mimeter.engine.service.test;

import run.mone.mimeter.engine.service.BenchEngineService;
import org.junit.Test;
import run.mone.mimeter.engine.agent.bo.task.Context;
import run.mone.mimeter.engine.agent.bo.data.HttpData;
import run.mone.mimeter.engine.agent.bo.task.Task;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
public class EngineTest {


    @Test
    public void testEngine() throws InterruptedException {
        BenchEngineService engineService = new BenchEngineService();
        Context context = new Context();
        Task task = new Task();
        task.setId(1);
        task.setQps(1000);
        task.setTime(10);
        HttpData data = new HttpData();
        data.setUrl("http://www.baidu.com");
        task.setHttpData(data);
        engineService.submitTask(context, task);

    }
}
