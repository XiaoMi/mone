package com.xiaomi.data.push.schedule.task.impl;

import com.google.gson.Gson;
import com.xiaomi.data.push.common.ScheduleCmd;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.TaskExample;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.schedule.TaskManager;
import com.xiaomi.data.push.schedule.task.*;
import com.xiaomi.data.push.service.TaskService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangzhiyong on 31/05/2018.
 * 需要并行执行的任务
 * 会分配任务到work 机器上执行
 */
public abstract class ConcurrentTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentTask.class);

    @Autowired
    private RpcServer rpcServer;

    @Autowired
    private TaskMapper taskMapper;


    @Autowired
    private TaskService taskService;


    @Autowired
    private TaskManager taskManager;

    @Value("${michedule_group}")
    private int micheduleGroup;

    @Override
    public TaskResult execute(TaskParam param, TaskContext context) {
        int concurrentNum = param.getInt("concurrentNum");
        int step = context.getInt("step");
        context.put("beginTime", String.valueOf(System.currentTimeMillis()));//存储下任务开始时间

        if (-1 == concurrentNum && step == 0) {//在全部work 机器上执行  execute

            int clientNum = rpcServer.clientNum();
            logger.info("ConcurrentTask clientNum:{}", clientNum);

            if (clientNum == 0) {
                return TaskResult.Retry();
            }

            ArrayList<Channel> clients = rpcServer.clients();

            if (clients.size() == 0) {//没有足够的客户端可执行,先跳过此次执行
                logger.info("skip current execute client size=0");
                return TaskResult.Retry();
            }

            try {
                concurrentExecute(param, param.getTaskId(), clients);
            } catch (IOException e) {
                e.printStackTrace();
            }

            context.putInt("step", 1);
        } else if (step == 1) {//check
            TaskExample example = new TaskExample();
            example.createCriteria().andParentIdEqualTo(param.getTaskId()).andStatusNotEqualTo(1);

            List<TaskWithBLOBs> tasks = taskMapper.selectByExampleWithBLOBs(example);
            for (TaskWithBLOBs task : tasks) {
                //如果任务超时,尝试在master重新拉起服务
                if (task.getStatus() == TaskStatus.Running.code && task.getUpdated() - task.getCreated() >= TimeUnit.HOURS.toMinutes(1)) {
                    boolean success = taskService.updateTask(task.getId(), t -> {
                        t.setCreated(System.currentTimeMillis());
                        t.setStatus(TaskStatus.Retry.code);
                        return true;
                    });
                    if (success) {
                        logger.info("ConcurrentTask recover task:{}", task.getId());
                        Gson gson = new Gson();
                        TaskParam p = gson.fromJson(task.getParams(), TaskParam.class);
                        p.setTaskId(task.getId());
                        TaskContext c = gson.fromJson(task.getContext(), TaskContext.class);
                        taskManager.doTask(p, c);
                    }
                }
            }
            if (tasks.size() == 0) {//都成功了
                executeFinish(param);
                TaskResult result = TaskResult.Success();
                long endTime = System.currentTimeMillis();
                long beginTime = Long.valueOf(context.get("beginTime"));
                result.setData("useTime:" + String.valueOf(endTime - beginTime));
                return result;
            }
        }

        return TaskResult.Retry();
    }


    public abstract void executeFinish(TaskParam param);


    public abstract void concurrentExecute(TaskParam param, int taskId, ArrayList<Channel> clients) throws IOException;


    public void sendTaskToClient(int taskId, Channel c, TaskDef taskDef, Map<String, String> params) {
        TaskParam taskParam = new TaskParam();
        taskParam.setTaskDef(new TaskDefBean(taskDef));
        taskParam.putParams(params);
        int id = taskService.insertTask(taskId, new Gson().toJson(taskParam), new Gson().toJson(new TaskContext()), taskDef.name(), TaskStatus.Running, micheduleGroup);
        taskParam.setTaskId(id);
        logger.info("sendTaskToClient taskId:{}", id);

        //work 服务器执行
        RemotingCommand r = RemotingCommand.createRequestCommand(ScheduleCmd.doTaskReq);
        r.setBody(new Gson().toJson(taskParam).getBytes());
        rpcServer.sendMessage(c, r);
    }

}
