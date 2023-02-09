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

package com.xiaomi.youpin.gwdash.service.factory.impl;

import com.xiaomi.data.push.micloud.bo.request.Disk;
import com.xiaomi.data.push.micloud.bo.request.OrderInfo;
import com.xiaomi.youpin.gwdash.service.factory.IApplyMachineParam;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AliOnlineApplyMachineParam implements IApplyMachineParam {

    @Override
    public List<OrderInfo> getOrderInfo(String suitId, String siteId) {
        List<OrderInfo> orderinfos = new ArrayList<>(1);
        OrderInfo info = new OrderInfo();
        info.setNodegroup("cop.xiaomi_owt.id_pdl.mijia_servicegroup.base_service.mione_jobgroup.online_job.mione_cluster.ali-bj");
        info.setTypeId("aliyun");
        info.setSuitId(suitId);
        // info.setSiteId("bjsali");
        info.setSiteId(siteId);
        info.setImageId("m-2ze43q53vjr3e5zztmgr");
        info.setZones(new LinkedList<>());
        info.getZones().add("cn-beijing-e");
        info.getZones().add("cn-beijing-f");
        info.getZones().add("cn-beijing-g");
        info.getZones().add("cn-beijing-h");
        info.setNetId("ali-bjs-vpc-online");
        info.setMachineNum(new LinkedList<>());
        info.getMachineNum().add(0);
        info.getMachineNum().add(1);
        info.getMachineNum().add(0);
        info.getMachineNum().add(0);
        info.setUsername("mione");
        info.setCoverTime(1);
        info.setPerformanceCalculation("");
        info.setProjectContext("mione");
        info.setServiceName("mione");
        info.setManagerName("zhangzhiyong1");
        info.setManagerEmail("zhangzhiyong1@xxxx.com");
        info.setDisk(new Disk());
        info.getDisk().setType("cloud_ssd");
        info.getDisk().setNumber(0);
        info.getDisk().setSize(0);
        orderinfos.add(info);
        return orderinfos;
    }
}
