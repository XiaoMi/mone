///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.rocketmq;
//
//import com.google.gson.Gson;
//import com.xiaomi.data.push.redis.Redis;
//import com.xiaomi.youpin.codecheck.po.CheckResult;
//import com.xiaomi.youpin.gwdash.bo.AutoStartContextDTO;
//import com.xiaomi.youpin.gwdash.bo.CodeCheckData;
//import com.xiaomi.youpin.gwdash.bo.DataMessage;
//import com.xiaomi.youpin.gwdash.bo.VulcanusData;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectCodeCheckRecord;
//import com.xiaomi.youpin.gwdash.service.LogService;
//import com.xiaomi.youpin.gwdash.service.PipelineService;
//import com.xiaomi.youpin.gwdash.ws.CiCdWebSocketHandler;
//import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Component
//@Slf4j
//public class CodeCheckerHandler {
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private LogService logService;
//
//    @Autowired
//    private ExecutorService executorService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private Redis redis;
//
//    @Autowired
//    private Environment environment;
//
//    @Value("${rocket.tag.codechecker}")
//    private String codeCheckerTag;
//
//    public static String tag;
//
//    public static final String CODE_CHECK_UPDATE = "code-check-status";
//
//    public static final String CODE_CHECK_LOGS = "code-check-logs";
//
//    @PostConstruct
//    public void init() {
//        tag = codeCheckerTag;
//    }
//
//    public void consumeMessage(MessageExt it) {
//        log.info("CodeCheckerHandler#content: {}", it.getMsgId());
//        Gson gson = new Gson();
//        CodeCheckData codeCheckData = gson.fromJson(new String(it.getBody()), CodeCheckData.class);
//
//        long id = codeCheckData.getId();
//        ProjectCodeCheckRecord projectCodeCheckRecord = dao.fetch(ProjectCodeCheckRecord.class, id);
//        String pushMsgId = codeCheckData.getPushMsgId();
//        if (null != projectCodeCheckRecord) {
//            projectCodeCheckRecord.setTime(codeCheckData.getTime());
//            Map<String, List<CheckResult>> checkRes = codeCheckData.getCheckRes();
//            StringBuffer sb = new StringBuffer();
//            AtomicInteger status = new AtomicInteger(TaskStatus.success.ordinal());
//            checkRes.forEach((key, value) -> {
//                sb.append(key).append("\n");
//                value.forEach(it2 -> {
//                    if (it2.getLevel().equals(CheckResult.LEVEL_ERROR)) {
//                        status.set(TaskStatus.failure.ordinal());
//                    }
//                    sb.append(it2.getLevel()).append(it2.getName()).append(":\t").append(it2.getChineseDesc()).append("\n\t\t").append(it2.getDetailDesc()).append("\n");
//                });
//                sb.append("\n");
//            });
//
//            projectCodeCheckRecord.setStep(2);
//            projectCodeCheckRecord.setStatus(status.get());
//
//            dao.update(projectCodeCheckRecord);
//            autoStartCompile(status.get() == TaskStatus.success.ordinal(),pushMsgId);
//            DataMessage message = new DataMessage();
//            message.setMsgType(CODE_CHECK_UPDATE);
//            message.setStage(CiCdWebSocketHandler.CODE_CHECK_STAGE);
//            message.setData(gson.toJson(projectCodeCheckRecord));
//            logService.saveLog(LogService.ProjectCodeCheck, id, sb.toString());
//            if (StringUtils.isNotEmpty(pushMsgId)) {
//                CiCdWebSocketHandler.pushMsg(pushMsgId, gson.toJson(message));
//                pushLog(pushMsgId, sb.toString());
//            }
//        }
//    }
//
//    private void pushLog (final String pushMsgId, final String msg) {
//        Gson gson = new Gson();
//        DataMessage msgLog = new DataMessage();
//        msgLog.setMsgType(CodeCheckerHandler.CODE_CHECK_LOGS);
//        msgLog.setStage(CiCdWebSocketHandler.CODE_CHECK_STAGE);
//        msgLog.setData(gson.toJson(VulcanusData.builder().id(0).message(msg).build()));
//        CiCdWebSocketHandler.pushMsg(pushMsgId, new Gson().toJson(msgLog));
//    }
//
//    private void autoStartCompile(boolean checkSuccess,String pushMsgId){
//        String serverEnv = environment.getProperty("server.serverEnv");
//        if(("staging".equals(serverEnv) || "dev".equals(serverEnv)) && checkSuccess){
//            executorService.execute(()->{
//                AutoStartContextDTO autoStartContext = redis.get(Consts.AUTO_START_PREFIX + pushMsgId, AutoStartContextDTO.class);
//                if(autoStartContext != null && autoStartContext.contextEnough() && Objects.equals("true",autoStartContext.getAutoBuild())){
//                    pipelineService.startCompile(autoStartContext.getSessionAccount().getUsername(),autoStartContext.getProjectId(),autoStartContext.getProjectEnvId());
//                }
//            });
//        }
//    }
//}
