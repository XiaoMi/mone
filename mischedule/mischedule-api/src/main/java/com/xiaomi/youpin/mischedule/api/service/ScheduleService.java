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

package com.xiaomi.youpin.mischedule.api.service;

import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.mischedule.api.service.bo.RequestBo;
import com.xiaomi.youpin.mischedule.api.service.bo.Task;
import com.xiaomi.youpin.mischedule.api.service.bo.TaskDo;
import com.xiaomi.youpin.mischedule.api.service.bo.TaskStepDo;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
public interface ScheduleService {

    /**
     * 健康监测
     *
     * @return
     */
    Result<String> health();

    /**
     * 提交任务
     *
     * @param compileRequest
     * @return
     */
    Result<Boolean> submitTask(RequestBo compileRequest);

    /**
     * debug 任务
     * 只调取一次 不存入表中
     */
    // Result<Boolean> debugTask(TaskParam taskParam);

    /**
     * 提交任务
     *
     * @param taskParam
     * @return
     */
    Result<Integer> submitTask(TaskParam taskParam);

    /**
     * 注册任务(用户需要在本地指定)
     *
     * @param task
     * @return
     */
    Result<Integer> regTask(List<Task> task);

    /**
     * 删除任务
     *
     * @param id
     * @return
     */
    Result<Boolean> delTask(int id);

    /**
     * 获取参数
     *
     * @param bid
     * @return
     */
    Result<Task> getParamsByBid(String bid);
    /**
     * 停止任务
     *
     * @param id
     * @return
     */
    Result<Boolean> pause(int id);


    /**
     * 启动任务
     *
     * @param id
     * @return
     */
    Result<Boolean> start(int id);

    /**
     * 获取任务
     *
     * @param taskId
     * @return
     */
    Result<Task>  getTask(int taskId);

    /**
     * 获取任务 对应数据库的字段
     * @param taskId
     * @return
     */
    Result<TaskDo> getTask2(int taskId);

    /**
     * 修改任务参数
     * @param id
     * @param params
     * @return
     */
    Result<Boolean> modify(Integer id, String params);

    Result<List<TaskStepDo>> getTaskStepList(int taskId, int limit);
}