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
import com.xiaomi.youpin.gwdash.dao.model.ErrorContent;
import com.xiaomi.youpin.gwdash.dao.model.MError;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.mischedule.api.service.bo.HealthResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NukeHandler {

    @Autowired
    private Dao dao;

    @Value("${rocket.tag.nuke}")
    private String nukeTag;

    public static String tag;

    @PostConstruct
    public void init() {
        tag = nukeTag;
    }

    public void consumeMessage(MessageExt it) {
        log.info("NukeHandler#consumeMessage: {}", it.getMsgId());
        try {
            long now = System.currentTimeMillis();
            byte[] body = it.getBody();
            HealthResult hr = new Gson().fromJson(new String(body), HealthResult.class);

            long envId = hr.getPipelineInfo().getEnvId();

            Project project = dao.fetch(Project.class, hr.getPipelineInfo().getProjectId());
            if (null == project) {
                return;
            }


            List<String> ips = hr.getServiceInfoList().stream().map(info -> info.getIp()).collect(Collectors.toList());
            if (ips.size() > 0) {
                MError error = new MError();
                error.setCtime(now);
                error.setUtime(now);
                error.setKey(String.valueOf(hr.getPipelineInfo().getEnvId()));
                error.setType(MError.ErrorType.Nuke.ordinal());
                ErrorContent content = new ErrorContent();

                MError merror = dao.fetch(MError.class, Cnd.where("key", "=", hr.getPipelineInfo().getEnvId()).and("type", "=", MError.ErrorType.Nuke.ordinal()));

                if (null == merror) {
                    content.setIps(ips);
                    content.setName(project.getGitName());
                    error.setServiceName(envId + " 需要nuke");
                    error.setContent(content);
                    dao.insert(error);
                } else {
                    if (merror.getUtime() + TimeUnit.MINUTES.toMillis(1) < now) {
                        content.setIps(ips);
                        merror.setUtime(now);
                        merror.setStatus(0);
                        dao.update(merror);
                    }
                }
            }
        } catch (Throwable ex) {
            log.warn("error:{}", ex.getMessage());
        }
    }
}
