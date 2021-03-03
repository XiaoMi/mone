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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskDefBean;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskStatus;
import com.xiaomi.data.push.schedule.task.impl.http.HttpTaskParam;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.mischedule.STaskDef;
import com.xiaomi.youpin.mischedule.api.service.AbService;
import com.xiaomi.youpin.mischedule.api.service.ScheduleService;
import com.xiaomi.youpin.mischedule.api.service.bo.HttpParams;
import com.xiaomi.youpin.mischedule.api.service.bo.Task;
import com.xiaomi.youpin.mischedule.bo.TaskDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author gaoyibo
 * 压测 测试用例
 */
@Slf4j
@Service(timeout = 1000, interfaceClass = AbService.class, group = "${dubbo.group}")
public class AbServiceImpl implements AbService {

    @Reference(check = false, group = "${dubbo.group}", interfaceClass = ScheduleService.class)
    private ScheduleService scheduleService;

    @Autowired
    private Dao dao;

    private void httpExcecute(HttpParams httpParams, String cron) {
        TaskParam taskParam = new TaskParam();

        HttpTaskParam httpTaskParam = new HttpTaskParam();
        httpTaskParam.setMethodType(httpParams.getMethod());
        httpTaskParam.setUrl(httpParams.getUrl());
        httpTaskParam.setHeaders(Maps.newHashMap());
        httpTaskParam.setBody(httpParams.getBody());

        taskParam.setCron(cron);
        taskParam.setTaskDef(new TaskDefBean(STaskDef.HttpTask));
        taskParam.setUrl(httpParams.getUrl());
        taskParam.put("param", new Gson().toJson(httpTaskParam));

        scheduleService.submitTask(taskParam);
    }

    @Override
    public Result<Boolean> abTest(Integer count, String cron, String password, HttpParams httpParams) {
        log.info("mischedule test: ab");

        if (password != ".!.") {
            return Result.success(false);
        }

        for (int i = 0; i < count; i++) {
            httpExcecute(httpParams, cron);
        }

        return Result.success(true);
    }

    @Override
    public Result<Boolean> abTestSql(Integer count) {
        long now = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            TaskDo taskDo = new TaskDo();
            taskDo.setName("abTest");
            taskDo.setCreated(now);
            taskDo.setUpdated(now);
            taskDo.setParams("{\"taskDef\":{\"type\":\"miTestTask\",\"retryNum\":1,\"errorRetryNum\":100,\"timeOut\":10000,\"name\":\"MiTestTask\"}," +
                    "\"param\":{}," +
                    "\"beginTime\":0,\"cron\":\"0/5 * * * * ?\",\"timeout\":0}");
            taskDo.setStatus(TaskStatus.Retry.ordinal());
            taskDo.setNextRetryTime(0L);
            taskDo.setContext("{}");
            taskDo.setErrorRetryNum(0);

            dao.insert(taskDo);
        }

        return Result.success(true);
    }

    @Override
    public Result<Boolean> canAbTest() {
        Sql sql = Sqls.create("DELETE FROM task");
        dao.execute(sql);
        return Result.success(true);
    }

    @Override
    public List<Task> getList() {
        return dao.query(Task.class, Cnd.orderBy().desc("id"));
    }
}
