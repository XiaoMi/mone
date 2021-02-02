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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xiaomi.data.push.schedule.task.impl.http;

import com.google.gson.Gson;
import com.xiaomi.data.push.annotation.Task;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.client.HttpClientV2.HttpResult;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.data.push.service.EmailCommonService;
import com.xiaomi.data.push.service.FeiShuCommonService;
import com.xiaomi.data.push.service.TaskHistoryService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpTask extends AbstractTask {
    private static final Logger log = LoggerFactory.getLogger(HttpTask.class);
    @Autowired
    private FeiShuCommonService feiShuService;
    @Autowired
    private TaskHistoryService taskHistoryService;
    @Value("${server.type}")
    private String serverType;

    public HttpTask() {
    }

    @Task(
            name = "HttpTask"
    )
    public TaskResult execute(TaskParam param, TaskContext context) {
        log.info("HttpTask TaskParam  param:{}", param.getParam().toString());
        log.info("HttpTask TaskParam  email:{}", param.getParam().get("email"));
        log.info("HttpTask TaskParam  responseCode:{}", param.getParam().get("responseCode"));
        log.info("HttpTask TaskParam  statusCode:{}", param.getParam().get("statusCode"));
        log.info("HttpTask TaskParam  feishu:{}", param.getParam().get("feishu"));
        log.info("HttpTask TaskParam  Creator:{}", param.getCreator());
        String email = (String)param.getParam().get("email");
        String feishu = (String)param.getParam().get("feishu");
        String responseCode = (String)param.getParam().get("responseCode");
        String statusCode = (String)param.getParam().get("statusCode");
        String creator = param.getCreator();
        long begin = System.currentTimeMillis();
        HttpTaskParam httpTaskParam = (HttpTaskParam)(new Gson()).fromJson(param.get("param"), HttpTaskParam.class);
//        System.out.println(httpTaskParam.getMethodType());
//        System.out.println(param.getTaskId());
        TaskResult result = TaskResult.Success();
        String res = "";
        log.info("httpTaskParam info:{}", httpTaskParam.toString());
        if (httpTaskParam.getMethodType().equals("get")) {
            HttpResult httpRes = HttpClientV2.httpGet(httpTaskParam.getUrl(), httpTaskParam.getHeaders());

            if (httpRes.code != 200) {
                // 失败
                result.setCode(TaskStatus.Failure.code);


                if (responseCode != null) {
                    if (email != null && email.equals("true")) {
                        log.info("任务失败发邮件taskid:{}", param.getTaskId());
                        this.sendEmail(param, context);
                    }

                    if (feishu != null && feishu.equals("true")) {
                        log.info("任务失败发飞书taskid:{}", param.getTaskId());
                        this.sendFeishu(param, context);
                    }
                    // 记录失败
                    this.taskHistoryService.taskFailHistory(param.getTaskId(), httpRes.content, creator);
                }

                if (statusCode != null) {
                    Gson gson = new Gson();
                    Map map = (Map)gson.fromJson(res, Map.class);
                    int code = Integer.valueOf(map.get("code").toString());
                    if (code != Integer.valueOf(statusCode)) {
                        result.setCode(TaskStatus.Failure.code);
                        if (email != null && email.equals("true")) {
                            log.info("任务失败发邮件taskid:{}", param.getTaskId());
                            this.sendEmail(param, context);
                        }

                        if (feishu != null && feishu.equals("true")) {
                            log.info("任务失败发飞书taskid:{}", param.getTaskId());
                            this.sendFeishu(param, context);
                        }
                        // 记录失败
                        this.taskHistoryService.taskFailHistory(param.getTaskId(), httpRes.content, creator);
                    }
                }
            } else {
                // 成功
                if (email != null && email.equals("true")) {
                    log.info("任务成功发邮件taskid:{}", param.getTaskId());
                    this.sendEmail(param, context);
                }

                if (feishu != null && feishu.equals("true")) {
                    log.info("任务成功发飞书taskid:{}", param.getTaskId());
                    this.sendFeishu(param, context);
                }
                this.taskHistoryService.taskSuccessHistory(param.getTaskId(), result.toString(), creator);
            }
            res = httpRes.content;

        } else if (httpTaskParam.getMethodType().equals("post")) {
            log.info("http post info:{}", res);

            try {
                res = HttpClientV2.post(httpTaskParam.getUrl(), httpTaskParam.getBody(), httpTaskParam.getHeaders());
            } catch (Exception var17) {
                if (email != null && email.equals("true")) {
                    log.info("任务失败发邮件taskid:{}", param.getTaskId());
                    this.sendEmail(param, context);
                }

                if (feishu != null && feishu.equals("true")) {
                    log.info("任务失败发飞书taskid:{}", param.getTaskId());
                    this.sendFeishu(param, context);
                }
                // 记录失败
                this.taskHistoryService.taskFailHistory(param.getTaskId(), var17.getMessage(), creator);
            }
        }

        result.setData(res);
        result.setUseTime(System.currentTimeMillis() - begin);
        return result;
    }

    private  void sendEmail(TaskParam taskParam, TaskContext taskResult){
        log.info("sendEmail taskParam:{}", taskParam);
        log.info("sendEmail taskResult:{}", taskResult);
        String detailUrl = "";
        if (serverType.equals("c3")||serverType.equals("c4")||serverType.equals("online")) {
            detailUrl += "online: 点击 http://xxxx/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        } else {
            detailUrl += "st: 点击 http://xxxxx/#/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        }
        String name = null;
        if (taskParam.getTaskDef().getName() == null) {
            name = "mischedule邮件";
        } else {
            name = taskParam.getTaskDef().getName();
        }

        String creator = taskParam.getCreator();
        String param = taskParam.getParam().toString();
        String result = taskResult.toString();
        String body =  detailUrl+"\nparams:"+param+"\nresult"+result;
        EmailCommonService.send(creator+"@xxxxx.com", "[MiSchedule] - " + name, body);

    }

    private  void sendFeishu(TaskParam taskParam, TaskContext taskResult){
        String detailUrl = "";
        if (serverType.equals("c3")||serverType.equals("c4")||serverType.equals("online")) {
            detailUrl += "online: 点击 http://xxxxx/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        } else {
            detailUrl += "st: 点击 http://xxxxx/#/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        }
        String name = taskParam.getTaskDef().getName();
        String creator = taskParam.getCreator();
        String param = taskParam.getParam().toString();
        String result = taskResult.toString();
        String body =  detailUrl+"\nparams:"+param+"\nresult"+result;

        this.feiShuService.sendMsg2Person(creator, name + body);
        //EmailCommonService.send(creator+"@xxxx.com", "[MiSchedule] - " + name, body);

    }

}
