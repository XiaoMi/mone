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

import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskDefBean;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
import com.xiaomi.youpin.gwdash.rocketmq.CompileHandler;
import com.xiaomi.youpin.mischedule.STaskDef;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tsingfu
 */
@Service
@Slf4j
public class ProjectCompilationService {

    @Autowired
    private Dao dao;

    @Autowired
    private MyScheduleService myScheduleService;

    private final String suffix = ".git";

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    public ProjectCompileRecord startCloudCompile(CompileParam compileParam) {
        long now = System.currentTimeMillis();
        ProjectCompileRecord projectCompilation = new ProjectCompileRecord();
        projectCompilation.setCtime(now);
        projectCompilation.setUtime(now);
        projectCompilation.setStatus(TaskStatus.running.ordinal());
        // 生成id，关联云编译
        dao.insert(projectCompilation);

        String gitUrl = compileParam.getGitUrl();
        if (null != gitUrl && !gitUrl.endsWith(suffix)) {
            compileParam.setGitUrl(gitUrl + suffix);
        }
        compileParam.setId(projectCompilation.getId());
        compileParam.setTags(CompileHandler.tag);
        TaskParam taskParam = new TaskParam();
        taskParam.setTaskDef(new TaskDefBean(STaskDef.CloudCompileTask));
        taskParam.put("param", new Gson().toJson(compileParam));

        try {
            myScheduleService.submitTask(taskParam);
            executorService.schedule(() -> {
                ProjectCompileRecord projectCompileRecord = dao.fetch(ProjectCompileRecord.class, Cnd.where("id","=", projectCompilation.getId()));
                if (null != projectCompileRecord
                        && (projectCompileRecord.getStatus() == TaskStatus.running.ordinal())) {
                    projectCompileRecord.setStatus(TaskStatus.failure.ordinal());
                    dao.update(projectCompileRecord);
                }
            }, 5, TimeUnit.MINUTES);
        } catch (RpcException e) {
            log.info("{}", e);
            projectCompilation.setStatus(TaskStatus.failure.ordinal());
        } finally {
            dao.update(projectCompilation);
        }
        return projectCompilation;
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
