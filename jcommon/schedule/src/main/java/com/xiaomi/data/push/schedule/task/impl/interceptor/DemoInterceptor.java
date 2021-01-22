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

package com.xiaomi.data.push.schedule.task.impl.interceptor;

import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskInterceptor;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author zhangzhiyong
 * @date 10/06/2018
 */
@Component
public class DemoInterceptor implements TaskInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DemoInterceptor.class);

    @Override
    public void beforeTask(TaskParam param, TaskContext context) {
        logger.info("before task param:{}", param);
    }

    @Override
    public void afterTask(TaskResult result) {
        logger.info("after task result:{}", result);
    }

    @Override
    public void onSuccess(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        
    }

    @Override
    public void onFailure(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {

    }

}
