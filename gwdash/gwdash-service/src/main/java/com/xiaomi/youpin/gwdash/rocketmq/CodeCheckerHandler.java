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
import com.xiaomi.youpin.codecheck.po.CheckResult;
import com.xiaomi.youpin.gwdash.bo.CodeCheckData;
import com.xiaomi.youpin.gwdash.bo.DataMessage;
import com.xiaomi.youpin.gwdash.dao.model.ProjectCodeCheckRecord;
import com.xiaomi.youpin.gwdash.service.CodeCheckerService;
import com.xiaomi.youpin.gwdash.service.LogService;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class CodeCheckerHandler {

    @Autowired
    private Dao dao;

    @Autowired
    private LogService logService;


    @Value("${rocket.tag.codechecker}")
    private String codeCheckerTag;

    public static String tag;

    public static final String CODE_CHECK_UPDATE = "code-check-update";

    public static final String CODE_CHECK_LOGS = "code-check-logs";

    @PostConstruct
    public void init() {
        tag = codeCheckerTag;
    }

    public void consumeMessage(MessageExt it) {
        log.info("CodeCheckerHandler#content: {}", it.getMsgId());
        Gson gson = new Gson();
        CodeCheckData codeCheckData = gson.fromJson(new String(it.getBody()), CodeCheckData.class);

        long id = codeCheckData.getId();
        ProjectCodeCheckRecord projectCodeCheckRecord = dao.fetch(ProjectCodeCheckRecord.class, id);
        if (null != projectCodeCheckRecord) {
            projectCodeCheckRecord.setTime(codeCheckData.getTime());
            Map<String, List<CheckResult>> checkRes = codeCheckData.getCheckRes();
            StringBuffer sb = new StringBuffer();
            AtomicInteger status = new AtomicInteger(TaskStatus.success.ordinal());
            checkRes.entrySet().stream().forEach(it1 -> {
                sb.append(it1.getKey() + "\n");
                it1.getValue().stream().forEach(it2 -> {
                    if (it2.getLevel().equals(CheckResult.LEVEL_ERROR)) {
                        status.set(TaskStatus.failure.ordinal());
                    }
                    sb.append(it2.getLevel() + it2.getName() + ":\t" + it2.getChineseDesc() + "\n\t\t" + it2.getDetailDesc() + "\n");
                });
                sb.append("\n");
            });

            projectCodeCheckRecord.setStep(2);
            projectCodeCheckRecord.setStatus(status.get());

            dao.update(projectCodeCheckRecord);

            DataMessage message = new DataMessage();
            message.setMsgType(CODE_CHECK_UPDATE);
            message.setData(gson.toJson(projectCodeCheckRecord));
            CodeCheckerService.pushMsg(id, gson.toJson(message));

            logService.saveLog(LogService.ProjectCodeCheck, id, sb.toString());
        }
    }
}
