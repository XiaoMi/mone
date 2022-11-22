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

import com.dianping.cat.Cat;
import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskDefBean;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.GitlabAccessToken;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.ProjectJavaDoc;
import com.xiaomi.youpin.gwdash.rocketmq.JavaDocHandler;
import com.xiaomi.youpin.gwdash.rocketmq.RocketMQConsumer;
import com.xiaomi.youpin.mischedule.STaskDef;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * @author xuzheng5
 */
@Service
@Slf4j
public class JavaDocService {

    @Autowired
    private Dao dao;

    @Autowired
    private MyScheduleService myScheduleService;

    @Autowired
    ProjectService projectService;

    @Autowired
    private GitlabService gitlabService;

    private static final String GIT_BRANCH_MASTER = "master";

    public Result createJavaDoc(long projectId, SessionAccount account) {

        Result<Project> projectResult = projectService.getProjectById(projectId);
        if (projectResult == null || projectResult.getData() == null) {
            return new Result<>(1, "project不存在", null);
        }

        Project project = projectResult.getData();
        String gitUrl = project.getGitAddress() + ".git";
        String branch = GIT_BRANCH_MASTER;


        GitlabAccessToken gitlabAccessToken =
            gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(2, "需授权access token", null);
        }


        TaskParam taskParam = new TaskParam();
        Map<String, String> param = new HashMap<>();
        param.put("gitUrl", gitUrl);
        param.put("branch", branch);
        param.put("gitUser", gitlabAccessToken.getName());
        param.put("gitPw", gitlabAccessToken.getToken());
        param.put("tag", JavaDocHandler.tag);
        param.put("projectId", String.valueOf(projectId));
        param.put("param", new Gson().toJson(param));
        taskParam.setParam(param);
        taskParam.setNotify("mqNotify");
        taskParam.setTaskDef(new TaskDefBean(STaskDef.JavaDocTask));

        try {
            myScheduleService.submitTask(taskParam);
            return Result.success(null);
        } catch (RpcException e) {
            log.info("CodeCheckerService, {}", e);
            Cat.logError(e);
            return new Result(1, e.getMessage(), null);
        } catch (Exception e) {
            log.info("CodeCheckerService, {}", e);
            Cat.logError(e);
            return new Result(1, e.getMessage(), null);
        }
    }


    public ProjectJavaDoc getJavaDoc(long projectId) {
        return dao.fetch(ProjectJavaDoc.class, Cnd.where("project_id", "=", projectId));
    }


}
