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

package com.xiaomi.data.push.service;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.common.RpcTaskReq;
import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.dao.mapper.TaskMapper;
import com.xiaomi.data.push.dao.model.TaskExample;
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.schedule.TaskCacheUpdater;
import com.xiaomi.data.push.schedule.TaskManager;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskManager taskManager;


    @Autowired(required = false)
    private RpcClient rpcClient;


    @Autowired
    private ServerContext serverContext;


    @Autowired
    private TaskCacheUpdater taskCacheUpdater;

    public TaskService() {
    }

    public int insertTask(int parentId, String params, String context, String name, TaskStatus status, int group) {
        TaskWithBLOBs task = new TaskWithBLOBs();
        task.setParams(params);
        task.setContext(context);
        task.setParentId(parentId);
        task.setVersion(0);
        task.setNextRetryTime(0L);
        task.setErrorRetryNum(0);
        task.setFailureNum(0);
        task.setSuccessNum(0);
        task.setRetryNum(0);
        long now = System.currentTimeMillis();
        task.setCreated(now);
        task.setUpdated(now);
        task.setName(name);
        task.setScheduleGroup(group);
        task.setStatus(status.code);
        this.taskMapper.insert(task);
        return task.getId();
    }


    public int batchUpdateStatus(int status, long now, int micheduleGroup) {
        TaskWithBLOBs blob = new TaskWithBLOBs();
        TaskExample taskExample = new TaskExample();
        taskExample.createCriteria()
                .andScheduleGroupEqualTo(micheduleGroup)
                .andStatusEqualTo(TaskStatus.Retry.code).andNextRetryTimeLessThan(now);
        blob.setStatus(status);
        return this.taskMapper.updateByExampleSelective(blob, taskExample);
    }


    private static final int maxUpdateNum = 3;


    public boolean updateTask(int id, Function<TaskWithBLOBs, Boolean> function) {
        return updateTask(id, function, null, null, null);
    }


    public boolean updateTask(int id, Function<TaskWithBLOBs, Boolean> function, TaskContext context, TaskResult result, Integer status) {
        int n = 0;
        //调度系统优化,不是直接刷入数据库,而是先刷入内存
        if (null != context && context.getString(TaskContext.CACHE).equals("true")) {
            if (serverContext.isLeader()) {
                //自己就可以完成处理
                taskCacheUpdater.updateTask(id, new Gson().toJson(result), new Gson().toJson(context),
                        status);
            } else {
                //发送给leader,让leader进行处理
                RemotingCommand req = RemotingCommand.createRequestCommand(3000);
                RpcTaskReq rtr = new RpcTaskReq();
                rtr.setCmd("modify");
                rtr.setTaskId(id);
                Map<String, String> attachements = Maps.newHashMap();
                attachements.put("result", new Gson().toJson(result));
                attachements.put("context", new Gson().toJson(context));
                attachements.put("status", status.toString());
                rtr.setAttachments(attachements);
                req.setBody(new Gson().toJson(rtr).getBytes());
                logger.info("updateTask notify leader:{}", id);
                this.rpcClient.sendMessage(req);
            }
            return true;
        }


        do {
            try {
                TaskWithBLOBs task = this.taskMapper.selectByPrimaryKey(id);
                if (null == task) {
                    logger.error("task is null id:{}", id);
                    return false;
                }

                boolean b = function.apply(task);
                if (!b) {
                    return false;
                }

                int oldVersion = task.getVersion();

                if (null == task.getGid()) {
                    task.setGid(0);
                }

                task.setVersion(task.getVersion() + 1);
                task.setUpdated(System.currentTimeMillis());
                TaskExample taskExample = new TaskExample();
                taskExample.createCriteria().andIdEqualTo(id).andVersionEqualTo(oldVersion);
                int i = this.taskMapper.updateByExampleWithBLOBs(task, taskExample);
                if (i > 0) {
                    return true;
                }

                try {
                    TimeUnit.MILLISECONDS.sleep((long) ((new Random()).nextInt(10)));
                } catch (InterruptedException var10) {
                }
            } catch (Throwable ex) {
                logger.warn("updateTask id:{} error:{}", id, ex.getMessage());
            }

            ++n;
        } while (n < maxUpdateNum);

        logger.warn("updateTask failure n>={} taskId:{}", maxUpdateNum, id);
        return false;
    }

    public TaskWithBLOBs findTask(int id) {
        return this.taskMapper.selectByPrimaryKey(id);
    }
}
