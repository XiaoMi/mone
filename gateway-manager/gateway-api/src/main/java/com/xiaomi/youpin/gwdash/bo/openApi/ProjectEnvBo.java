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
//package com.xiaomi.youpin.gwdash.bo.openApi;
//
//
//import lombok.Data;
//
//import java.io.Serializable;
//
//@Data
//public class ProjectEnvBo implements Serializable {
//
//    private long id;
//
//    private String name;
//
//    private long projectId;
//
//    private String projectName;
//
//    private String group;
//
//    private int deployType;
//
//    /**
//     * 部署权限
//     * 参考DeploymentAuthorityEnum
//     */
//    private int authority;
//
//    private String branch;
//
//    private String profile;
//
//    /**
//     * 记录当前环境上线成功pipeline
//     */
//    private long pipelineId;
//
//    private int status;
//
//
//    private long lastAutoScaleTime;
//
//    /**
//     * 健康监测的任务id
//     * 没有的话是0
//     */
//    private int healthCheckTaskId;
//
//}
