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
//import com.xiaomi.youpin.gwdash.common.AppDeployStatus;
//import com.xiaomi.youpin.gwdash.dao.model.Machine;
//import lombok.Data;
//
//import java.util.List;
//
///**
// * @author goodjava@qq.com
// */
//@Data
//public class DeployMachine extends Machine {
//
//    /**
//     * 步骤
//     */
//    private int step;
//
//    /**
//     * 状态 0:正常  2:下线  3:poweroff(关机)
//     */
//    private int status;
//
//    /**
//     * 部署时间
//     */
//    private long time;
//
//    /**
//     * 应用部署状态
//     *
//     * @see AppDeployStatus
//     */
//    private int appDeployStatus;
//
//    /**
//     * 记录应用健康监测连续失败次数
//     */
//    private int failNum = 0;
//
//
//    private List<Integer> cpuCore;
//}
