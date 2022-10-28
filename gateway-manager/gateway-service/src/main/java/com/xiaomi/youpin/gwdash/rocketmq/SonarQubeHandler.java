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
//import com.dianping.cat.Cat;
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.service.SonarQubeService;
//import com.xiaomi.youpin.mischedule.api.service.bo.SonarQubeData;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
///**
// * @author zheng.xucn@outlook.com
// * <p>
// * SonarQube RocketMQ consumer
// */
//
//@Component
//@Slf4j
//public class SonarQubeHandler {
//
//    @Value("${rocket.tag.sonarqube}")
//    private String sonarQubeTag;
//
//    public static String tag;
//
//    @Autowired
//    private SonarQubeService sonarQubeService;
//
//    @PostConstruct
//    public void init() {
//        tag = sonarQubeTag;
//    }
//
//
//    public void consumeMessage(MessageExt it) {
//        try {
//            log.info("SonarQubeHandler#consumeMessage: {}", it.getMsgId());
//            Gson gson = new Gson();
//            SonarQubeData sonarQubeData = gson.fromJson(new String(it.getBody()), SonarQubeData.class);
//            long projectId = sonarQubeData.getProjectId();
//            String projectKey = sonarQubeData.getProjectKey();
//            sonarQubeService.addPermission(projectId, projectKey);
//        } catch (Exception e) {
//            log.error(e.toString());
//            Cat.logError(e);
//        }
//    }
//}
