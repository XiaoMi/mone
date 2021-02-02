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

package com.xiaomi.data.push.schedule.task;

/**
 * Created by zhangzhiyong on 10/06/2018.
 * 任务拦截器,任何一个任务都可以添加
 */
public interface TaskInterceptor {

    void beforeTask(TaskParam param, TaskContext context);

    void afterTask(TaskResult result);

    /**
     * 任务成功
     */
    void onSuccess(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult);

    /**
     * 任务失败
     */
    void onFailure(int id, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult);


}
