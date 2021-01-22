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

package com.xiaomi.youpin.gwdash.rocketmq;

import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.DataMessage;
import com.xiaomi.youpin.gwdash.bo.VulcanusData;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.gwdash.service.LogService;
import com.xiaomi.youpin.gwdash.service.PipelineService;
import com.xiaomi.youpin.gwdash.service.ProjectCompilationService;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileEnum;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class CompileHandler {

    @Autowired
    private LogService logService;

    @Autowired
    private Dao dao;

    @Value("${rocket.tag.compile}")
    private String compileTag;

    public static String tag;

    public static final String DOCKER_BUILD_LOGS = "docker-build-logs";

    public static final String COMPILE_LOGS = "compile-logs";

    public static final String COMPILE_STATUS = "compile-status";

    @PostConstruct
    public void init() {
        tag = compileTag;
    }

    public void consumeMessage(MessageExt it) {
        log.info("CompileHandler#consumeMessage: {}", it.getMsgId());
        String topic = it.getTopic();
        if (topic.equals(CompileEnum.CompileLog.getName())) {
            handleCompileLog(it);
        } else if (topic.equals(CompileEnum.CompileStatus.getName())) {
            handleCompileStatus(it);
        }
    }

    private void handleCompileLog (MessageExt it) {
        VulcanusData vulcanusData =  new Gson().fromJson(new String(it.getBody()), VulcanusData.class);
        logService.saveLog(LogService.ProjectCompilation, vulcanusData.getId(), vulcanusData.getMessage());
    }

    private void handleCompileStatus (MessageExt it) {
        VulcanusData vulcanusData = new Gson().fromJson(new String(it.getBody()), VulcanusData.class);

        ProjectCompileRecord projectCompilation = dao.fetch(ProjectCompileRecord.class, vulcanusData.getId());
        if (null != projectCompilation) {
            projectCompilation.setUtime(System.currentTimeMillis());
            projectCompilation.setStep(vulcanusData.getStep());
            projectCompilation.setStatus(vulcanusData.getStatus());
            projectCompilation.setTime(vulcanusData.getTime());
            projectCompilation.setUrl(vulcanusData.getUrl());
            String keyStr = vulcanusData.getKey();
            if (null != keyStr) {
                projectCompilation.setJarKey(keyStr);
                projectCompilation.setJarName(keyStr);
            }
            dao.update(projectCompilation,
                    Cnd.where("step", "<", vulcanusData.getStep())
                            .or(Cnd.exps("step", "=", vulcanusData.getStep())
                                    .and("status", "<", vulcanusData.getStatus())));

            DataMessage message = new DataMessage();
            message.setData(new String(it.getBody()));
            message.setMsgType(COMPILE_STATUS);


            ProjectCompilationService.pushMsg(vulcanusData.getId(), new Gson().toJson(message));

        } else {
            log.error("ProjectCompilationService.defaultMQPushConsumer:" +
                    "找不到对应的编译记录 {}", vulcanusData);
        }
    }
}
