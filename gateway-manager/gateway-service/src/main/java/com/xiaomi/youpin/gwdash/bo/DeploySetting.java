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
//package com.xiaomi.youpin.gwdash.bo;
//
//import com.xiaomi.youpin.gwdash.dao.model.HttpService;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author tsingfu
// */
//@Setter
//@Getter
//public class DeploySetting {
//
//    // serviceName
//    private String serviceName = "none";
//
//    // 测试用列相关
//
//    private String testService;
//
//    // 构建相关配置-start
//
//    private String buildPath;
//
//    private String buildJarPath;
//
//    private String javaHome;
//
//    private String customParams;
//
//    private int xmlSetting;
//
//    // 部署策略相关-start
//
//    private String policyDeployment;
//
//    private String policyStop;
//
//    private int policyBatchNum;
//
//    // nginx治理相关相关
//
//    private HttpService httpService;
//
//    // 部署配置相关-start
//
//    private String deploySettingPath;
//
//    private long deploySettingHeapSize;
//
//    // 机器相关配置-start
//
//    List<EnvMachineBo> envMachineBo;
//
//    // 部署类型-start
//
//    private int deployType;
//
//    private String envGroup;
//
//    private String mvnProfile;
//
//    private long lastPipelineId;
//
//    // 日志相关
//    private boolean jaeger;
//
//    // docker相关配置
//
//    private int dockerCup;
//
//    private long dockerMem;
//
//    private long dockerReplicate;
//
//    private long minDockerReplicate;
//
//    private long maxDockerReplicate;
//
//    private String dockerLogPath;
//
//    private String dockerfilePath;
//
//    private String dockerLabels;
//
//    private String dockerVolume;
//
//    private Map<String, String> dockerParams;
//
//    // 项目相关配置-start
//
//    private String gitUrl;
//
//    private String gitGroup;
//
//    private String gitName;
//
//    private String branch;
//
//    private String commitId;
//
//    // 项目pipeline节点-start
//
//    private List<String> pipelineNode;
//
//    private String langType;
//
//    private String deploymentAuthorityName;
//
//    private String jvmParams;
//
//    /**
//     * 健康监测的地址
//     * support http dubbo
//     * http://19.3.3.4:8080/health
//     * dubbo://com.xiaomi.demoservice/staging/health
//     */
//    private String healthCheckUrl;
//
//    /**
//     * 健康监测的任务id
//     */
//    private int healthCheckTaskId;
//
//}
