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

package com.xiaomi.data.push.schedule.task.impl;

import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import org.springframework.stereotype.Component;

/**
 * Created by zhangzhiyong on 29/05/2018.
 * <p>
 * demo 任务
 */
@Component
public class DemoTask extends AbstractTask {

    @Override
    public TaskResult execute(TaskParam TaskParam, TaskContext taskContext) {
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            throw new RuntimeException("time out");
//        }
        try {
            System.out.println("demoTask");
            int count = taskContext.getInt("count");
            count = count + 1;
            if (count >= 3) {
                return TaskResult.Success();
            }
            taskContext.putInt("count", count);
            return TaskResult.Retry();
        } catch (Throwable throwable) {
            TaskResult failure = TaskResult.Failure();
            failure.setData(throwable.getMessage());
            return failure;
        }

    }
}
