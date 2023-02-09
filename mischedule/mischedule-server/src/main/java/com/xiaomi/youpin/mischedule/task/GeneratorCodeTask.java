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

package com.xiaomi.youpin.mischedule.task;

import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.youpin.codegen.DoceanProGen;
import com.xiaomi.youpin.codegen.FilterGen;
import com.xiaomi.youpin.codegen.PluginGen;
import com.xiaomi.youpin.codegen.SpringBootProGen;
import com.xiaomi.youpin.gitlab.Gitlab;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.mischedule.bo.ProjectGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 * <p>
 * 生成项目
 */
@Slf4j
@Component
public class GeneratorCodeTask extends AbstractTask {

    private static final int SUCCESS = 0;

    @Override
    public TaskResult execute(TaskParam taskParam, TaskContext taskContext) {
        log.info("generator code task");
        String json = taskParam.get("param");
        ProjectGen param = new Gson().fromJson(json, ProjectGen.class);

        if (StringUtils.isEmpty(param.getType())) {
            param.setType("spring");
        }

        com.xiaomi.youpin.infra.rpc.Result<String> genRes = getGenRes(param);

        if (null == genRes) {
            log.error("generator code error:null");
            return TaskResult.Failure();
        }

        if (genRes.getCode() != SUCCESS) {
            log.error("generator code error:{}", genRes.getCode());
            return TaskResult.Failure();
        }

        String path = genRes.getData();

        try {
            Gitlab.createNewProject(param.getGitAddress(), path, param.getGitUser(), param.getGitToken());
        } catch (Exception e) {
            return TaskResult.Failure(e.getMessage());
        }

        return TaskResult.Success();
    }

    /**
     * 生成代码
     * @param param
     * @return
     */
    private Result<String> getGenRes(ProjectGen param) {
        switch (param.getType()) {
            case "spring": {
                SpringBootProGen springBootProGen = new SpringBootProGen();
                return springBootProGen.generateAndZip("/tmp/gencode",
                        param.getProjectName(), param.getGroupId(), param.getPackageName(), param.getAuthor(), "1.0", param.isNeedTomcat(), param.getDubboVersion());
            }
            case "filter": {
                FilterGen filterGen = new FilterGen();
                return filterGen.generateAndZip("/tmp/gencode", param.getProjectName(), param.getGroupId(), param.getPackageName(), param.getAuthor(), param.getVersionId(), param.getFilterOrder(), param.getParams(), param.getCname(), param.getDesc(), param.getGitAddress(), param.getIsSystem());
            }
            case "plugin": {
                PluginGen pluginGen = new PluginGen();
                return pluginGen.generateAndZip("/tmp/gencode", param.getProjectName(), param.getGroupId(), param.getPackageName(), param.getAuthor(), param.getVersionId(), param.getUrl());
            }
            case "docean": {
                DoceanProGen doceanProGen = new DoceanProGen();
                return doceanProGen.generateAndZip("/tmp/gencode",
                        param.getProjectName(), param.getGroupId(), param.getPackageName(), param.getAuthor(),"1.0");
            }
        }
        return null;
    }
}
