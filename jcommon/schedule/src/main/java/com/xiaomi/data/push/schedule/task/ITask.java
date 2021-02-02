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
 * @author goodjava@qq.com
 */
public interface ITask {

    void beforeTask(TaskParam var1, TaskContext var2);

    TaskResult execute(TaskParam var1, TaskContext var2);

    void afterTask(TaskContext var1, TaskResult var2);

    void onRetry(int var1, TaskParam var2, TaskContext var3, TaskResult var4);

    void onSuccess(int var1, TaskParam var2, TaskContext var3, TaskResult var4);

    void onFailure(int var1, TaskParam var2, TaskContext var3, TaskResult var4);
}
