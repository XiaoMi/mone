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

import com.dianping.cat.Cat;
import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.JavaDocData;
import com.xiaomi.youpin.gwdash.dao.model.ProjectJavaDoc;
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
public class JavaDocHandler {

    @Autowired
    private Dao dao;

    @Value("${rocket.tag.javadoc}")
    private String javaDocTag;

    public static String tag;

    @PostConstruct
    public void init() {
        tag = javaDocTag;
    }

    public void consumeMessage(MessageExt it) {
        log.info("JavaDocHandler#consumeMessage: {}", it.getMsgId());
        Gson gson = new Gson();
        JavaDocData javaDocData = gson.fromJson(new String(it.getBody()), JavaDocData.class);
        long projectId = javaDocData.getProjectId();
        String javaDoc = javaDocData.getDoc();
        try {
            ProjectJavaDoc projectJavaDoc = dao.fetch(ProjectJavaDoc.class, Cnd.where("project_id", "=", projectId));
            if (projectJavaDoc == null) {
                projectJavaDoc = new ProjectJavaDoc();
                projectJavaDoc.setProjectId(projectId);
                projectJavaDoc.setDoc(javaDoc);
                dao.insert(projectJavaDoc);
            } else {
                projectJavaDoc.setDoc(javaDoc);
                dao.update(projectJavaDoc);
            }
        } catch (Exception e) {
            log.error("error when saving java doc with projectId: " + projectId, e);
            Cat.logError("error when saving java doc with projectId: " + projectId, e);
        }
    }
}
