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
import com.xiaomi.youpin.gwdash.bo.DockerResData;
import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
import com.xiaomi.youpin.gwdash.service.DockerfileService;
import com.xiaomi.youpin.gwdash.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class DockerfileHandler {

    @Autowired
    private Dao dao;

    @Autowired
    private LogService logService;

    @Autowired
    private DockerfileService dockerDeploymentService;

    @Value("${rocket.tag.dockerfile}")
    private String dockerfileTag;

    public static String tag;

    public static final String DOCKER_BUILD_INFO = "docker-build-info";

    @PostConstruct
    public void init() {
        tag = dockerfileTag;
    }

    public void consumeMessage(MessageExt it) {
        log.info("DockerfileHandler#consumeMessage: {}", it.getMsgId());
        DockerResData dockerResData = new Gson().fromJson(new String(it.getBody()), DockerResData.class);
        long id = dockerResData.getId();
        ProjectCompileRecord compileRecord = dao.fetch(ProjectCompileRecord.class, id);
        if (null == compileRecord) {
            log.warn("DockerfileHandler#consumeMessage record is null: {}", id);
            return;
        }
        int step = dockerResData.getStep();
        int status = dockerResData.getStatus();
        compileRecord.setStep(step);
        compileRecord.setStatus(status);
        compileRecord.setJarName(dockerResData.getImageTags());
        compileRecord.setTime(dockerResData.getTime());
        compileRecord.setUtime(System.currentTimeMillis());
        dao.update(compileRecord,
                Cnd.where("step", "<=", step)
                        .and("status", "<=", status));

        DataMessage message = new DataMessage();
        message.setData(new String(it.getBody()));
        message.setMsgType(DOCKER_BUILD_INFO);
        DockerfileService.pushMsg(dockerResData.getId(), new Gson().toJson(message));
        logService.saveLog(LogService.ProjectDockerBuild, id, dockerResData.getMsg());
    }
}
