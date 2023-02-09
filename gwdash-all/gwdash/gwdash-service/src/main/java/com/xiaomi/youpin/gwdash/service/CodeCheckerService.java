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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.data.push.schedule.task.TaskDefBean;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.ProjectCodeCheckRecord;
import com.xiaomi.youpin.mischedule.STaskDef;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tsingfu
 */
@Service
@Slf4j
public class CodeCheckerService {

    @Autowired
    private Dao dao;

    @Autowired
    private MyScheduleService myScheduleService;

    public Result<ProjectCodeCheckRecord> startCodeChecker(TaskParam taskParam) {
        long now = System.currentTimeMillis();
        ProjectCodeCheckRecord projectCodeCheckRecord = new ProjectCodeCheckRecord();
        projectCodeCheckRecord.setStep(0);
        projectCodeCheckRecord.setStatus(TaskStatus.running.ordinal());
        projectCodeCheckRecord.setCtime(now);
        projectCodeCheckRecord.setUtime(now);
        dao.insert(projectCodeCheckRecord);
        taskParam.setNotify("mqNotify");
        taskParam.setTaskDef(new TaskDefBean(STaskDef.CodeCheckTask));
        taskParam.getParam().put("id", String.valueOf(projectCodeCheckRecord.getId()));
        try {
            myScheduleService.submitTask(taskParam);
            projectCodeCheckRecord.setStep(1);
        } catch (RpcException e) {
            projectCodeCheckRecord.setStatus(TaskStatus.failure.ordinal());
            log.info("CodeCheckerService, {}", e);
        } finally {
            dao.update(projectCodeCheckRecord);
        }
        return Result.success(projectCodeCheckRecord);
    }

    public static final ConcurrentMap<Long, CopyOnWriteArraySet<WebSocketSession>> subscriber = new ConcurrentHashMap<>();

    private static ReentrantLock reentrantLock = new ReentrantLock(true);

    public static void pushMsg(Long pId, String message) {
        CopyOnWriteArraySet<WebSocketSession> sessions = subscriber.get(pId);
        if (null != sessions) {
            sessions.forEach(it -> {
                reentrantLock.lock();
                try {
                    log.info("push msg :{}", message);
                    it.sendMessage(new TextMessage(message));
                } catch (Throwable e) {
                    log.error("WsSubscriber#pushMsg:" + e.getMessage(), e);
                } finally {
                    reentrantLock.unlock();
                }
            });
        }
    }

    public static void addWebSocketSession(Long pId, WebSocketSession webSocketSession) {
        CopyOnWriteArraySet set = subscriber.get(pId);
        if (null == set) {
            set = new CopyOnWriteArraySet<>();
            set.add(webSocketSession);
            subscriber.put(pId, set);
        } else {
            set.add(webSocketSession);
        }
    }

    public static void removeWebSocketSession(WebSocketSession webSocketSession) {
        Set<Map.Entry<Long, CopyOnWriteArraySet<WebSocketSession>>> set = subscriber.entrySet();
        set.stream().forEach(it -> {
            it.getValue().remove(webSocketSession);
        });
        set = subscriber.entrySet();
        set.stream().forEach(it -> {
            it.getValue().remove(webSocketSession);
        });
    }
}
