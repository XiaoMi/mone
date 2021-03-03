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

package com.xiaomi.youpin.mischedule.service;

import com.google.gson.Gson;
import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.schedule.TaskDeleter;
import com.xiaomi.data.push.schedule.TaskManager;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskDefBean;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import com.xiaomi.youpin.cron.CronExpression;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.service.AccountService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.mischedule.STaskDef;
import com.xiaomi.youpin.mischedule.api.service.ScheduleService;
import com.xiaomi.youpin.mischedule.api.service.bo.ExecuteType;
import com.xiaomi.youpin.mischedule.api.service.bo.RequestBo;
import com.xiaomi.youpin.mischedule.api.service.bo.Task;
import com.xiaomi.youpin.mischedule.api.service.bo.TaskStepDo;
import com.xiaomi.youpin.mischedule.bo.QTask;
import com.xiaomi.youpin.mischedule.bo.TaskDo;
import com.xiaomi.youpin.mischedule.cloudcompile.CloudCompileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.sql.OrderBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service(timeout = 1000, interfaceClass = ScheduleService.class, group = "${dubbo.group}")
public class ScheduleServiceImpl implements ScheduleService {


    @Autowired
    private TaskManager taskManager;

    @Autowired
    private TaskDeleter taskDeleter;

    @Autowired
    private CloudCompileService cloudCompileService;

    @Autowired
    private Dao dao;


    @Autowired
    private ServerContext serverContext;


    @Autowired
    private RpcClient rpcClient;

    @Autowired
    private ElectionServiceImpl electionService;


    @Autowired
    private QueueService queueService;

    @Value("${rpc.server.port}")
    private int port;

    @Override
    public Result<String> health() {
        return Result.success("ok: " + System.currentTimeMillis());
    }

    @Reference(check = false, interfaceClass = AccountService.class, retries = 0, group = "${ref.hermes.group}")
    private AccountService accountService;

    /**
     * 提交任务接口
     *
     * @param request
     * @return
     */
    @Override
    public Result<Boolean> submitTask(RequestBo request) {
        TaskContext context = getContext();
        invokeTask(request, context);
        return Result.success(true);
    }


    private TaskContext getContext() {
        TaskContext context = new TaskContext();
        context.put("beginTime", String.valueOf(System.currentTimeMillis()));
        return context;
    }

    @Override
    public Result<Integer> submitTask(TaskParam taskParam) {
        log.info("taskParam: [{}]", taskParam);
        String defaultInterceptor = "DefaultInterceptor";
        /**
         * 修改任务任务参数
         */
        if (0 != taskParam.getTaskId()) {
            log.info("modify task:{} param:{}", taskParam.getTaskId(), taskParam);
            taskManager.modifyParam(taskParam.getTaskId(), new Gson().toJson(taskParam));
            return Result.success(taskParam.getTaskId());
        }

        TaskContext context = getContext();
        String interceptor = taskParam.getParam().get(TaskContext.INTERCEPTOR);
        if (!StringUtils.isEmpty(interceptor)) {
            context.put(TaskContext.INTERCEPTOR, interceptor);
        } else {
            context.put(TaskContext.INTERCEPTOR, defaultInterceptor);
        }

        //是否立刻执行
        boolean run = isRun(taskParam);
        //是否只执行一次
        boolean once = isOnce(taskParam);


        if (taskParam.getExecuteTime() != 0) {
            run = false;
        }

        log.info("run:{} onec:{}", run, once);

        context.setOnce(once);

        //是否需要异步处理
        if (needAsync(taskParam)) {
            QTask qTask = new QTask();
            qTask.setParam(taskParam);
            qTask.setContext(context);
            qTask.setRun(run);
            boolean res = queueService.offer(qTask);
            if (!res) {
                log.error("offer error:{}", taskParam.getTaskDef().getName());
            }
            taskParam.setTaskId(-1);
        } else {
            taskManager.submitTask(taskParam, context, run);
        }

        return Result.success(taskParam.getTaskId());
    }

    /**
     * 是否需要异步
     * 这些任务都是2c的
     *
     * @param taskParam
     * @return
     */
    private boolean needAsync(TaskParam taskParam) {
        try {
            String name = taskParam.getTaskDef().getName().trim();
            if (name.equals("orderCancelCallback") || name.equals("sendPushTask2")) {
                return true;
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
        return false;
    }

    private boolean isOnce(TaskParam taskParam) {
        String onceStr = taskParam.get("once");
        if (StringUtils.isNotEmpty(onceStr)) {
            return Boolean.parseBoolean(onceStr);
        }
        return false;
    }

    private boolean isRun(TaskParam taskParam) {
        boolean run = true;

        try {
            String runStr = taskParam.getParam().get("run");
            if (StringUtils.isNotEmpty(runStr)) {
                run = Boolean.parseBoolean(runStr);
            }
            return run;
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    /**
     * 注册过来task
     * 需要check version
     *
     * @param taskList
     * @return
     */
    @Override
    public Result<Integer> regTask(List<Task> taskList) {
        long now = System.currentTimeMillis();
        taskList.stream().forEach(it -> {

            String params = "";
            TaskParam taskParam = new TaskParam();

            if (it.getExecuteType().equals(ExecuteType.http)) {
                taskParam.setTaskDef(new TaskDefBean(STaskDef.HttpTask));
                taskParam.setCron(it.getCron());
                taskParam.put("param", it.getParam());
            } else if (it.getExecuteType().equals(ExecuteType.dubbo)) {
                taskParam.setTaskDef(new TaskDefBean(STaskDef.DubboTask));
                taskParam.setCron(it.getCron());
                taskParam.put("param", it.getParam());
            }
            params = new Gson().toJson(taskParam);


            long nextRetryTime = 0L;
            if (!it.getCron().equals("")) {
                try {
                    CronExpression cronExpression = new CronExpression(it.getCron());
                    nextRetryTime = cronExpression.getNextValidTimeAfter(new Date()).getTime();
                } catch (ParseException e) {
                    log.warn("get nextRetryTime ex:{}", e.getMessage());
                }
            }

            TaskDo task = dao.fetch(TaskDo.class, Cnd.where("name", "=", it.getName()));

            if (null == task) {
                TaskDo taskDo = new TaskDo();
                taskDo.setName(it.getName());
                taskDo.setCreated(now);
                taskDo.setUpdated(now);
                taskDo.setParams(params);
                taskDo.setStatus(TaskStatus.Retry.ordinal());
                taskDo.setNextRetryTime(nextRetryTime);
                taskDo.setContext("{}");
                taskDo.setErrorRetryNum(0);
                try {
                    dao.insert(taskDo);
                } catch (Throwable ex) {
                    //ignore
                }
            } else {
                TaskDo taskDo = new TaskDo();
                taskDo.setUpdated(now);
                taskDo.setParams(params);
                taskDo.setStatus(TaskStatus.Retry.ordinal());
                taskDo.setNextRetryTime(nextRetryTime);
                dao.update(taskDo);
            }
        });

        return Result.success(0);
    }

    @Override
    public Result<Boolean> delTask(int id) {
        pause(id);
        int count = taskDeleter.deleteTaskById(id);
        Boolean flag = false;
        if (count > 0) {
            flag = true;
        }
        return Result.success(flag);
    }

    private void invokeTask(RequestBo request, TaskContext context) {
        STaskDef taskDef = STaskDef.valueOf(request.getTaskName());
        if (null == taskDef) {
            log.warn("taskDef is null:{}", request.getTaskName());
        } else {
            TaskParam param = new TaskParam();
            param.setCron(request.getCron());
            TaskDefBean tdb = new TaskDefBean(taskDef);
            if (request.getErrorRetryNum() > 0) {
                tdb.setErrorRetryNum(request.getErrorRetryNum());
            }
            param.setTaskDef(tdb);
            param.put("param", request.getParam());
            taskManager.submitTask(param, context, true);
        }
    }

    @Override
    public Result<Task> getParamsByBid(String bid) {
        Task task = dao.fetch(Task.class, Cnd.where("bid", "=", bid));
        return Result.success(task);
    }

    @Override
    public Result<Boolean> pause(int id) {
        log.info("pause id:{}", id);
        taskManager.pause(id);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> start(int id) {
        log.info("start id:{}", id);
        taskManager.start(id);
        return Result.success(true);
    }

    @Override
    public Result<Task> getTask(int taskId) {
        return Result.success(dao.fetch(Task.class, taskId));
    }

    @Override
    public Result<com.xiaomi.youpin.mischedule.api.service.bo.TaskDo> getTask2(int taskId) {
        return Result.success(dao.fetch(com.xiaomi.youpin.mischedule.api.service.bo.TaskDo.class, taskId));
    }

    @Override
    public Result<Boolean> modify(Integer id, String params) {
        log.info("modify id:{} params:{}", id, params);
        taskManager.modifyParam(id, params);
        return Result.success(true);
    }

    @Override
    public Result<List<TaskStepDo>> getTaskStepList(int taskId, int limit) {
        OrderBy cnd = Cnd.where("task_id", "=", taskId)
                .limit(1, limit)
                .desc("id");

        List<TaskStepDo> taskStepList = dao.query(TaskStepDo.class, cnd);
        return Result.success(taskStepList);
    }
}
