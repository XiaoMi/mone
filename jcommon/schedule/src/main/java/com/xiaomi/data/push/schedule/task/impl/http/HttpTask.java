//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xiaomi.data.push.schedule.task.impl.http;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.annotation.Task;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.client.HttpClientV2.HttpResult;
import com.xiaomi.data.push.common.TaskHistoryData;
import com.xiaomi.data.push.dto.TaskExecuteContentDTO;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.data.push.service.EmailCommonService;
import com.xiaomi.data.push.service.FeiShuCommonService;
import com.xiaomi.data.push.service.TaskExecuteHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class HttpTask extends AbstractTask {
    private static final Logger log = LoggerFactory.getLogger(HttpTask.class);
    @Autowired
    private FeiShuCommonService feiShuService;
    @Autowired
    private TaskExecuteHistoryService taskExecuteHistoryService;
    @Value("${server.type}")
    private String serverType;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public HttpTask() {
    }

    @Task(name = "HttpTask")
    public TaskResult execute(TaskParam param, TaskContext context) {
        String email = param.getParam().get("email");
        String feishu = param.getParam().get("feishu");
        String responseCode = param.getParam().get("responseCode");
        String statusCode = param.getParam().get("statusCode");
        String history = param.getParam().get("history");
        int taskId = param.getTaskId();
        String creator = param.getCreator();
        long begin = System.currentTimeMillis();
        HttpTaskParam httpTaskParam = (new Gson()).fromJson(param.get("param"), HttpTaskParam.class);

        String result = "";
        boolean success = true;
        Long timeout = Math.min(param.getTimeout(), 10000);
        log.info("httpTaskParam info:{}", httpTaskParam.toString());
        if (httpTaskParam.getMethodType().equalsIgnoreCase("get")){
            HttpResult httpRes = HttpClientV2.httpGet(httpTaskParam.getUrl(), Lists.newArrayList(), Maps.newHashMap(),"UTF-8", timeout.intValue());
            result = httpRes.content;
            success = httpRes.code == 200;

        }else if(httpTaskParam.getMethodType().equalsIgnoreCase("post")){
            try {
                String body = StringUtils.isBlank(httpTaskParam.getBody())?"{}":httpTaskParam.getBody();
                //简陋版支持post
                Map<String,String> headers = httpTaskParam.getHeaders()==null?new HashMap<>():httpTaskParam.getHeaders();
                headers.putIfAbsent("content-type", "application/json; charset=utf-8");
                result = HttpClientV2.post(httpTaskParam.getUrl(), body, headers, timeout.intValue());
            }catch (Exception e){
                success = false;
                log.error("HttpTask.post {}", e);
            }
        }
        TaskResult taskResult = success?TaskResult.Success():TaskResult.Failure();
        String finalResult = result;
        threadPoolExecutor.execute(()->{
            if(history != null && history.equals("true")){
                saveTaskExecuteContent(taskId,taskResult.getCode(), finalResult,creator, param.getInt("triggerType"));
            }
            if (email != null && email.equals("true")) {
                log.info("任务失败发邮件taskid:{}", taskId);
                this.sendEmail(param, context);
            }

            if (feishu != null && feishu.equals("true")) {
                log.info("任务失败发飞书taskid:{}", taskId);
                this.sendFeishu(param, context);
            }
        });
        taskResult.setData(result);
        taskResult.setUseTime(System.currentTimeMillis() - begin);
        return taskResult;
    }

    private void saveTaskExecuteContent(Integer taskId,Integer resultCode,String result,String executor, int triggerType){
        if(taskId == null || taskId <=0){
            log.error("saveTaskExecuteContent 失败，taskId 无效");
            return;
        }
        TaskHistoryData historyData = new TaskHistoryData();
        historyData.setMessage(result);
        historyData.setResult(Objects.equals(TaskStatus.Success.code,resultCode));
        historyData.setTime(System.currentTimeMillis());
        taskExecuteHistoryService.addHistory(new TaskExecuteContentDTO(taskId, JSON.toJSONString(historyData),executor, triggerType));
    }
    private  void sendEmail(TaskParam taskParam, TaskContext taskResult){
        log.info("sendEmail taskParam:{}", taskParam);
        log.info("sendEmail taskResult:{}", taskResult);
        String detailUrl = "";
        if (serverType.equals("c3")||serverType.equals("c4")||serverType.equals("online")) {
            detailUrl += "online: 点击 http://mischedule.youpin.mi.srv/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        } else {
            detailUrl += "st: 点击 http://st.mischedule.youpin.mi.srv/#/detail?id=" + taskParam.getTaskId() + " 查看 \n";
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
        EmailCommonService.send(creator+"@xiaomi.com", "[MiSchedule] - " + name, body);

    }

    private  void sendFeishu(TaskParam taskParam, TaskContext taskResult){
        String detailUrl = "";
        if (serverType.equals("c3")||serverType.equals("c4")||serverType.equals("online")) {
            detailUrl += "online: 点击 http://mischedule.youpin.mi.srv/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        } else {
            detailUrl += "st: 点击 http://st.mischedule.youpin.mi.srv/#/detail?id=" + taskParam.getTaskId() + " 查看 \n";
        }
        String name = taskParam.getTaskDef().getName();
        String creator = taskParam.getCreator();
        String param = taskParam.getParam().toString();
        String result = taskResult.toString();
        String body =  detailUrl+"\nparams:"+param+"\nresult"+result;

        this.feiShuService.sendMsg2Person(creator, name + body);
        //EmailCommonService.send(creator+"@xiaomi.com", "[MiSchedule] - " + name, body);

    }

}
