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

package com.xiaomi.youpin.mischedule.task;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.gson.Gson;
import com.site.lookup.util.StringUtils;
import com.xiaomi.data.push.micloud.MiCloud;
import com.xiaomi.data.push.micloud.bo.response.ControlResponse;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.youpin.mischedule.api.service.bo.PowerOnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2020/7/22
 * 开机任务
 */
@Slf4j
@Component
public class PowerOnTask extends AbstractTask {


    private static final String TAG = "PowerOn";
    private static final String SERVER_RUNNING = "正在运行";
    private static final String STATUS = "status";
    private static final String STEP = "step";

    private static final String serviceName = "tesla_agent";


    @Autowired
    private MiCloud miCloud;

    @Autowired
    private NacosNaming nacosNaming;

    @Override
    public TaskResult execute(TaskParam taskParam, TaskContext taskContext) {
        log.info("PowerOnTask param:{}", new Gson().toJson(taskParam));

        String accessKey = taskParam.param.get("accessKey");
        String secretKey = taskParam.param.get("secretKey");
        String hostname = taskParam.param.get("hostname");
        String userName = taskParam.param.get("userName");
        String envId = taskParam.param.get("envId");
        if (org.apache.commons.lang3.StringUtils.isEmpty(userName)) {
            userName = "";
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(envId)) {
            envId = "-1";
        }

        String ip = taskParam.param.get("ip");
        int step = taskContext.getInt(STEP);

        //调用云平台启动机器接口
        if (0 == step) {
            int code = callPowerOn(accessKey, secretKey, hostname);
            notifyApiServer(envId, userName, ip, taskContext, "", 0, "call power on:" + code + ":" + ip);
            if (0 == code) {
                taskContext.getMap().put(STEP, "1");
                step = 1;
            }
        }
        //获取服务器状态
        if (1 == step) {
            int code = getStatus(taskParam.getTaskId(), accessKey, secretKey, hostname, taskContext);
            notifyApiServer(envId, userName, ip, taskContext, "", 1, "get status:" + code + ":" + ip);
            if (code == 0 && isServerRunning(taskContext.get(STATUS))) {
                taskContext.getMap().put(STEP, "2");
            }
        }

        //check agent 是否已经启动
        if (2 == step) {
            try {
                List<Instance> instance = nacosNaming.getAllInstances(serviceName);
                boolean find = instance.stream().filter(it -> it.getIp().equals(ip)).findAny().isPresent();
                log.info("PowerOnTask:{} find:{}", taskParam.getTaskId(), find);
                notifyApiServer(envId, userName, ip, taskContext, "", 2, "get agent status:" + find + ":" + ip);
                if (find) {
                    step = 3;
                    taskContext.getMap().put(STEP, "3");
                }
            } catch (NacosException e) {
                log.error(e.getMessage());
            }
        }


        //通知apiserver(dashboard)
        if (3 == step) {
            String status = taskContext.get(STATUS);
            int code = notifyApiServer(envId, userName, ip, taskContext, status, 3, "notify dashboard");
            if (code == 0) {
                return TaskResult.Success();
            }
        }
        return TaskResult.Retry();
    }

    /**
     * 通知apiserver 机器已经开启
     *
     * @return
     */
    private int notifyApiServer(String envId, String userName, String ip, TaskContext taskContext, String status, int step, String message) {
        PowerOnResult result = new PowerOnResult();
        result.setData(status);
        result.setIp(ip);
        result.setStep(step);
        result.setMessage(message);
        result.setUserName(userName);
        result.setEnvId(Long.valueOf(envId));
        String str = new Gson().toJson(result);
        log.info("notify api server res:{}", str);
        taskContext.notifyMsg(TAG, str);
        return 0;
    }

    private boolean isServerRunning(String status) {
        return StringUtils.isNotEmpty(status) && status.equals(SERVER_RUNNING);
    }

    /**
     * 查询机器状态
     *
     * @return
     */
    private int getStatus(int taskId, String accessKey, String secretKey, String hostname, TaskContext taskContext) {
        ControlResponse response = miCloud.getStatus(accessKey, secretKey, new String[]{hostname});
        log.info("taskId:{} get status:{}", taskId, new Gson().toJson(response));
        int code = response != null && response.getCode() == 0 && response.getData() != null && response.getData().size() >= 1 &&
                response.getData().get(0).isSuccess() ? 0 : 1;
        if (code == 0) {
            taskContext.put(STATUS, response.getData().get(0).getMessage());
        }
        return code;
    }

    /**
     * 调用云平台接口,开机
     *
     * @return
     */
    private int callPowerOn(String accessKey, String secretKey, String hostname) {
        ControlResponse response = miCloud.powerOn(accessKey, secretKey, "poweron", new String[]{
                hostname
        });
        log.info("call power on:{}", new Gson().toJson(response));
        return response != null && response.getCode() == 0 && response.getData() != null && response.getData().size() >= 1 && response.getData().get(0).isSuccess() ? 0 : 1;
    }
}
