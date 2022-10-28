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
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author goodjava@qq.com
// * <p>
// * 发布信息
// */
//@Data
//@Slf4j
//public class DeployInfo implements Serializable {
//
//    /**
//     * 部署批次相关
//     */
//    private List<DeployBatch> deployBatches;
//
//    /**
//     * 上次部署机器列表中本次部署已经下线列表
//     */
//    private List<DeployMachine> lastDeployOfflineMachine;
//
//
//    /**
//     * 部署的docker机器
//     */
//    private List<DeployMachine> dockerMachineList;
//
//
//    /**
//     * 0 还没有发布
//     * 1 发布中
//     * 2 发布结束-成功
//     * 3 发布结束-失败
//     */
//    private int status;
//
//    private int step;
//
//    private long time;
//
//    private long utime;
//
//    private long ctime;
//
//    private String kcDeployToken;
//
//    /**
//     * 健康监测的状态 (0:正常  1:里边的url都被清空了)
//     */
//    private int healthCheckStatus;
//
//
//    public void offlineDockerMachine(String ip) {
//        if (null == dockerMachineList) {
//            log.info("dockerMachineList is null");
//            return;
//        }
//        this.dockerMachineList = dockerMachineList.stream().filter(it -> !it.getIp().equals(ip)).collect(Collectors.toList());
//    }
//
//    public void onlineDockerMachine(DeployMachine machine) {
//        dockerMachineList.add(machine);
//    }
//
//}
