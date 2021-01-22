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

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.youpin.codecheck.DocCheck;
import com.xiaomi.youpin.mischedule.bo.JavaDocData;
import com.xiaomi.youpin.mischedule.util.RepoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author 
 * <p>
 * 负责读项目的java doc
 */
@Component
@Slf4j
public class JavaDocTask extends AbstractTask {
    @Value("${git.base.path}")
    private String BASE_GIT_PATH;

    private Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    public TaskResult execute(TaskParam taskParam, TaskContext taskContext) {
        Transaction t = Cat.newTransaction("JavaDockTask", "execute");
        try {
            t.setStatus(Transaction.SUCCESS);
            String tag = taskParam.param.get("tag");
            String gitUrl = taskParam.param.get("gitUrl");
            String branch = taskParam.param.get("branch");
            String gitUser = taskParam.param.get("gitUser");
            String gitPw = taskParam.param.get("gitPw");
            long projectId = Long.parseLong(taskParam.param.get("projectId"));

            String tempGitPath = BASE_GIT_PATH + gitUrl + File.separator + branch;
            File tempGitDir = RepoUtil.delTemp(tempGitPath);
            Pair<Boolean, String> result = RepoUtil.cloneRepository(gitUrl, branch, tempGitDir, gitUser, gitPw);

            if (!result.getKey()) {
                t.setStatus("RepoUtil.cloneRepository returned false");
                return TaskResult.Failure(result.getValue());
            }

            String doc = readJavaDoc(tempGitPath);
            taskContext.notifyMsg(tag, new Gson().toJson(new JavaDocData(projectId, doc)));

            RepoUtil.delTemp(tempGitPath);

            return TaskResult.Success();
        } catch (Exception e) {
            log.error(e.toString());
            Cat.logError(e);
            t.setStatus(e);
            return TaskResult.Failure(e.toString());
        } finally {
            t.complete();
        }
    }

    private String readJavaDoc(String tempGitPath) {
        try {
            if (StringUtils.isEmpty(tempGitPath)) {
                throw new InvalidParameterException("tempGitPath: " + tempGitPath);
            }

            DocCheck dockCheck = new DocCheck();
            Map<String, String> map = dockCheck.getDoc(tempGitPath);

            Map<String, String> formattedMap = new HashMap<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.length() > tempGitPath.length()) {
                    key = key.substring(tempGitPath.length());
                    formattedMap.put(key, value);
                }
            }
            String doc = gson.toJson(formattedMap);
            return doc;
        } catch (Exception e) {
            log.error("error when loading java doc", e);
            Cat.logError(e);
            return "";
        }
    }
}
