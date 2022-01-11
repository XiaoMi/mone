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

package com.xiaomi.data.push.schedule;

import com.google.gson.Gson;
import com.xiaomi.data.push.common.PushService;
import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.TaskExample;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskData;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import com.xiaomi.data.push.service.TaskService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 任务寻找,然后拉起来重试
 */
@Service
@Slf4j
public class TaskFinder implements PushService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private TaskService taskService;


    @Autowired
    private TaskCacheUpdater taskCacheUpdater;

    @Autowired
    private ServerContext serverContext;

    @Autowired(required = false)
    private RpcServer rpcServer;

    @Value("${michedule_group}")
    private int micheduleGroup;

    private volatile boolean shutdown = false;


    public TaskFinder() {
    }

    public void schedule() {
        this.findTasks();
    }


    public void findTasks() {
        if (!this.shutdown && this.serverContext.isLeader()) {
            long now = System.currentTimeMillis();

            TaskExample taskExample = new TaskExample();
            taskExample.createCriteria()
                    .andScheduleGroupEqualTo(micheduleGroup)
                    .andStatusEqualTo(TaskStatus.Retry.code)
                    .andNextRetryTimeLessThan(now);
            List<TaskWithBLOBs> tasks = this.taskMapper.selectByExampleWithBLOBs(taskExample);
            if (tasks.size() > 0) {
                log.info("id:{} need retry task size:{} use time:{}", now, tasks.size(), (System.currentTimeMillis() - now));
            }


            long updateBegin = System.currentTimeMillis();
            int updateNum = this.taskService.batchUpdateStatus(TaskStatus.Running.code, now, micheduleGroup);
            log.info("id:{} findTasks size:{} update num:{} useTime:{}", now, tasks.size(), updateNum, System.currentTimeMillis() - updateBegin);

            long invokeTaskBegin = System.currentTimeMillis();
            tasks.forEach((task) -> {

                try {
                    if (null == task.getContext()) {
                        task.setContext("{}");
                    }
                    log.info("id:{} task find id:{}", now, task.getId());
                    TaskContext context = (new Gson()).fromJson(task.getContext(), TaskContext.class);

                    if (null == context) {
                        context = new TaskContext();
                    }

                    //是否缓存任务
                    context.put(TaskContext.CACHE, String.valueOf(false));

                    //让其他work机器执行
                    if (!serverContext.getType().equals(ServerContext.STANDALONE)) {
                        ArrayList<Channel> clients = this.rpcServer.clients();
                        if (clients.size() > 0) {
                            Random r = new Random();
                            int i = r.nextInt(clients.size());
                            Channel c = clients.get(i);
                            //同步任务过去
                            try {
                                this.sendTaskToWorker(task.getId(), c, task.getParams(), context);
                            } catch (Throwable ex) {
                                log.warn("sendTaskToWoker failure id:{} error:{}", task.getId(), ex.getMessage());
                                this.taskManager.retryTask(task);
                            }
                        } else {
                            //本地执行
                            this.taskManager.retryTask(task, false);
                        }
                    } else {
                        //本地执行
                        this.taskManager.retryTask(task, false);
                    }
                } catch (Exception ex) {
                    log.error("id: " + now + "retry task:" + task.getId() + " error:" + ex.getMessage(), ex);
                }

            });

            long end = System.currentTimeMillis();
            log.info("id:{} find task size:{} invoke use time:{} total time:{}", now, tasks.size(), end - invokeTaskBegin, end - now);
        }
    }

    public void shutdown() {
        log.info("taskFinder shutdown begin");
        this.shutdown = true;
        log.info("taskFinder shutdown finish");
    }

    public void sendTaskToWorker(Integer taskId, Channel c, String param, TaskContext context) {
        TaskParam taskParam = new Gson().fromJson(param, TaskParam.class);
        TaskData taskData = new TaskData();
        taskParam.setTaskId(taskId);
        taskData.setParam(taskParam);
        taskData.setTaskContext(context);
        log.info("sendTaskToWorker c:{} id:{} param:{}", c, taskId, param);
        RemotingCommand r = RemotingCommand.createRequestCommand(4000);
        r.setBody((new Gson()).toJson(taskData).getBytes());
        this.rpcServer.sendMessage(c, r, TimeUnit.SECONDS.toMillis(10));
    }
}
