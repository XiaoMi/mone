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

//package com.xiaomi.youpin.tesla.agent.state.impl;
//
//import com.github.dockerjava.api.model.Container;
//import com.google.common.collect.Lists;
//import com.xiaomi.youpin.docker.YpDockerClient;
//import com.xiaomi.youpin.tesla.agent.po.DockerReq;
//import com.xiaomi.youpin.tesla.agent.po.NotifyMsg;
//import com.xiaomi.youpin.tesla.agent.state.Fsm;
//import com.xiaomi.youpin.tesla.agent.state.State;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author goodjava@qq.com
// */
//public class RollbackState extends State {
//
//    private Fsm fsm;
//
//    @Override
//    public void execute(DockerReq req) {
//        String containerName = req.getImageName();
//        List<Container> oldList = YpDockerClient.ins().listContainers(Lists.newArrayList(), true, (containerName));
//        if (oldList.size() == 0) {
//            //need create
//            fsm.changeState();
//            return;
//        }
//
//        //停止旧的container
//        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), false, req.getImageName().split("-")[0]);
//        list.stream().forEach(it -> YpDockerClient.ins().stopContainer(it.getId()));
//        notifyServer(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 0, "stop", "[INFO] stop container finish", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));
//
//
//
//
//    }
//}
