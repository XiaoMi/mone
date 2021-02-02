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

package com.xiaomi.youpin.mischedule.interceptor;

import com.google.gson.Gson;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskInterceptor;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.youpin.mischedule.api.service.bo.TaskStepDo;
import com.xiaomi.youpin.mischedule.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangjunyi
 * created on 2020/9/22 10:46 上午
 */
@Component("DefaultInterceptor")
@Slf4j
public class DefaultInterceptor implements TaskInterceptor {

    @Autowired
    private Dao dao;

    @Autowired
    private Redis redis;

    @Value("${server.type}")
    private String serverType;

    @Value("${failure_alert_max}")
    private int FAILURE_ALERT_MAX;

    private static final String REDIS_KEY_PREFIX_ALERT = "mischedule_alert_map";

    @Override
    public void beforeTask(TaskParam taskParam, TaskContext taskContext) {

    }

    @Override
    public void afterTask(TaskResult taskResult) {

    }

    @Override
    public void onSuccess(int i, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        log.info("onsuccess i: {}", i);
        // send email

//        sendEmail(taskParam,taskResult);

        TaskWithBLOBs taskData = taskContext.getTaskData();

        if (!StringUtils.isEmpty(taskData.getResult())) {

        }

        long now = System.currentTimeMillis();

        log.info("onsuccess task: {} taskData: {}, {} ,{}", i, taskData, taskData.getSuccessNum(), taskData.getFailureNum());

        TaskStepDo taskStepDo = new TaskStepDo();
        taskStepDo.setTaskId(taskData.getId());
        taskStepDo.setUpdated(now);
        taskStepDo.setSuccessNum(taskData.getSuccessNum() + 1);
        taskStepDo.setFailureNum(taskData.getFailureNum());

        dao.insert(taskStepDo);

        log.info("onsuccess task:{} taskData: {}, type {} success", i, serverType, taskData);
    }

    @Override
    public void onFailure(int taskId, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        /**
         * 报警机制
         * 单次报警默认FAILURE_ALERT_MAX 5
         */
        String REDIS_KEY_ALERT = REDIS_KEY_PREFIX_ALERT + "_" + taskId;
        String cache = redis.get(REDIS_KEY_ALERT);
        int oneDay = (int) TimeUnit.DAYS.toMillis(1L);
        if (!StringUtils.isEmpty(cache)) {
            int alertCount = Integer.valueOf(cache);
            if (++alertCount <= FAILURE_ALERT_MAX) {
                sendEmail(taskParam, taskResult);
                redis.set(REDIS_KEY_ALERT, String.valueOf(alertCount));
            } else {
                log.info("task:{},fail alert max", taskId);
            }
        } else {
            redis.set(REDIS_KEY_ALERT, String.valueOf(1), oneDay);
            sendEmail(taskParam, taskResult);
        }

        TaskWithBLOBs taskData = taskContext.getTaskData();

        long now = System.currentTimeMillis();

        TaskStepDo taskStepDo = new TaskStepDo();
        taskStepDo.setTaskId(taskData.getId());
        taskStepDo.setUpdated(now);
        taskStepDo.setSuccessNum(taskData.getSuccessNum());
        taskStepDo.setFailureNum(taskData.getFailureNum() + 1);

        dao.insert(taskStepDo);

        log.info("task:{} taskData: {} failure", taskId, taskData);
    }

    private void sendEmail(TaskParam taskParam, TaskResult taskResult) {
        String detailUrl = "";
        if (serverType == "c3" || serverType == "c4" || serverType == "online") {
            detailUrl += "online: 点击 http://xxxx/detail?id=" + taskParam.getTaskId() + " 查看 <b/>";
        } else {
            detailUrl += "st: 点击 http://xxxx/#/detail?id=" + taskParam.getTaskId() + " 查看 <b/>";
        }
        String name = taskParam.getTaskDef().getName();
        String creator = taskParam.getCreator();
        String param = taskParam.getParam().toString();
        String result = taskResult.toString();
        String body = detailUrl + "<b/>params:" + param + "<b/>result" + result;
        log.info("defaultInteceptor:params:user:{},name:{},body:{}", creator, name, body);
         EmailService.send(creator+"@xxxx.com", "[MiSchedule] - " + name, body);

    }
}

