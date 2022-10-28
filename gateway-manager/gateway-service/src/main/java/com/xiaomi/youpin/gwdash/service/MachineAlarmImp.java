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
//package com.xiaomi.youpin.gwdash.service;
//
//import com.xiaomi.youpin.gwdash.dao.model.Machine;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.nutz.dao.pager.Pager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author zhangjunyi
// * created on 2020/3/11 11:18 上午
// */
//@Service
//public class MachineAlarmImp {
//
//    @Autowired
//    Dao dao;
//
//    @Autowired
//    private FeiShuService feiShuService;
//
//    private ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
//
//    @PostConstruct
//    public void init() {
//        pool.scheduleAtFixedRate(() -> {
//            this.checkMachineAgent();
//        }, 0, 5, TimeUnit.MINUTES);
//    }
//
//    public void checkMachineAgent() {
//        long now = System.currentTimeMillis();
//        long tenMin = 600 * 1000;
//        List<Machine> machines = dao.query(Machine.class, Cnd.where("utime", "<", now - tenMin), new Pager(1, 20));
//        if (null != machines && machines.size() > 0) {
//            feiShuService.sendMsg("", "机器agent异常:\n" + machines.stream().map(it -> it.getIp()).collect(Collectors.toList()));
//        }
//    }
//}
