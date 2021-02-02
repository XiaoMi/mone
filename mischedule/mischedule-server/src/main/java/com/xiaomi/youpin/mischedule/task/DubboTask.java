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

import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.data.push.service.EmailCommonService;
import com.xiaomi.data.push.service.FeiShuCommonService;
import com.xiaomi.data.push.service.TaskHistoryService;
import com.xiaomi.youpin.mischedule.MethodInfo;
import com.xiaomi.youpin.mischedule.service.EmailService;
import com.xiaomi.youpin.mischedule.service.TaskHistoryDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 * 支持远程调用dubbo服务
 * <p>
 * 可以设置任务远程调用对方dubbo接口
 */
@Component
@Slf4j
public class DubboTask extends AbstractTask {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private RegistryConfig registryConfig;

    @Autowired
    private FeiShuCommonService feiShuService;

    @Autowired
    private TaskHistoryDubboService taskHistoryDubboService;

    @Value("${server.type}")
    private String serverType;

    @Override
    public TaskResult execute(TaskParam taskParam, TaskContext taskContext) {
        log.info("execute dubbo task {}:{}", taskParam.getTaskId(), taskParam.get("param"));

        String email = taskParam.getParam().get("email");
        String feishu = taskParam.getParam().get("feishu");
        String responseCode = taskParam.getParam().get("responseCode");
        String creator = taskParam.getCreator();

        MethodInfo methodInfo = new Gson().fromJson(taskParam.get("param"), MethodInfo.class);
        Object res = null;
        TaskResult tr = null;
        try {
            res = call(methodInfo);
            tr = TaskResult.Success();
        } catch (Exception e) {
            log.error("DubboTask execute error", e);
            if (responseCode != null) {
                if (email != null && email.equals("true")) {
                    log.info("任务失败发邮件,taskid:{}", taskParam.getTaskId());
                    this.sendEmail(taskParam, taskContext);
                }
                if (feishu != null && feishu.equals("true")) {
                    log.info("任务失败发飞书,taskid:{}", taskParam.getTaskId());
                    this.sendFeishu(taskParam, taskContext);
                }
            }
            // 任务失败记录
            taskHistoryDubboService.taskFailHistory(taskParam.getTaskId(), tr.getData(), creator);
        }

        tr.setData(null == res ? "" : res.toString());

        if (tr.getData().equals("")) {
            if (responseCode != null) {
                if (email != null && email.equals("true")) {
                    log.info("任务失败发邮件,taskid:{}", taskParam.getTaskId());
                    this.sendEmail(taskParam, taskContext);
                }
                if (feishu != null && feishu.equals("true")) {
                    log.info("任务失败发飞书,taskid:{}", taskParam.getTaskId());
                    this.sendFeishu(taskParam, taskContext);
                }
            }
            // 任务失败记录
            taskHistoryDubboService.taskFailHistory(taskParam.getTaskId(), tr.getData(), creator);
        } else {
            // 任务成功记录
            taskHistoryDubboService.taskSuccessHistory(taskParam.getTaskId(), tr.getData(), creator);
        }

        return tr;
    }


    private Object call(MethodInfo methodInfo) {
        RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(methodInfo.getTimeout()));
        String key = ReferenceConfigCache.getKey(methodInfo.getServiceName(), methodInfo.getGroup(), methodInfo.getVersion());
        GenericService genericService = ReferenceConfigCache.getCache().get(key);
        boolean create = false;
        if (null == genericService) {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
            reference.setInterface(methodInfo.getServiceName());
            reference.setGeneric(true);
            reference.setCheck(false);
            reference.setGroup(methodInfo.getGroup());
            reference.setVersion(methodInfo.getVersion());
            reference.setTimeout(methodInfo.getTimeout());
            ReferenceConfigCache cache = ReferenceConfigCache.getCache();
            genericService = cache.get(reference);
            create = true;
        }
        log.info("call key:{} {} {}", key, genericService,create);

        /**
         * 定向调用
         */
        if (StringUtils.isNotEmpty(methodInfo.getAddr())) {
            String[] ss = methodInfo.getAddr().split(":");
            RpcContext.getContext().setAttachment(Constants.MUST_PROVIDER_IP_PORT, "true");
            RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, ss[0]);
            RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, ss[1]);
        }

        Object res = null;
        try {
            res = genericService.$invoke(methodInfo.getMethodName(), methodInfo.getParameterTypes(), methodInfo.getArgs());
        } catch (Exception e) {
            log.error("DubboTask call error", e);
        } finally {
            RpcContext.getContext().clearAttachments();
        }

        return res;
    }

    private void sendEmail(TaskParam taskParam, TaskContext taskResult) {
        String detailUrl = "";
        if (serverType.equals("c3") || serverType.equals("c4") || serverType.equals("online")) {
            detailUrl += "online: 点击 http://xxxx/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        } else {
            detailUrl += "st: 点击 http://xxxx/#/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        }
        String name = taskParam.getTaskDef().getName();
        String creator = taskParam.getCreator();
        String param = taskParam.getParam().toString();
        String result = taskResult.toString();
        String body = detailUrl + "\nparams:" + param + "\nresult" + result;
        EmailService.send(creator + "@xxxx.com", "[MiSchedule] - " + name, body);

    }

    private void sendFeishu(TaskParam taskParam, TaskContext taskResult) {
        String detailUrl = "";
        if (serverType.equals("c3") || serverType.equals("c4") || serverType.equals("online")) {
            detailUrl += "online: 点击 http://xxxx/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        } else {
            detailUrl += "st: 点击 http://xxxxx/#/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        }
        String name = taskParam.getTaskDef().getName();
        String creator = taskParam.getCreator();
        String param = taskParam.getParam().toString();
        String result = taskResult.toString();
        String body = detailUrl + "\nparams:" + param + "\nresult" + result;

        this.feiShuService.sendMsg2Person(creator, name + body);

    }

}