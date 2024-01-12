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

package run.mone.mimeter.engine.client.impl;

import com.xiaomi.youpin.docean.anno.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.mimeter.engine.agent.bo.data.DemoData;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.Result;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.client.base.IClient;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2022/5/23
 * <p>
 * 用来测试代码逻辑
 */
@Component(name = "demoClient")
public class DemoClient implements IClient {

    private static final Logger log = LoggerFactory.getLogger(DemoClient.class);

    public Result<String> call(Task task, TaskContext context, CommonReqInfo commonReqInfo, SceneTotalCountContext totalCountContext) {
        log.info("call task id:{} begin", task.getId());
        DemoData demoData = task.getDemoData();
        int i = 0;

        Result<String> result = new Result<>();
        result.setCode(0);

        if (task.isDebug()) {
            result.setData(String.valueOf(i + 1));
        }
        result.setMessage("msg:" + task.getId());
        if (task.getAttachments().containsKey("sleep")) {
            int time = Integer.valueOf(task.getAttachments().get("sleep"));
            if (time > 0) {
                try {
                    TimeUnit.SECONDS.sleep(time);
                } catch (InterruptedException e) {
                    log.error("[DemoClient] call time sleep exception", e);
                }
            }
        }
        log.info("call demo task:{} context:{} result:{} success", task.getId(), context, result);
        return result;
    }


}
