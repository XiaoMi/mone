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
//package com.xiaomi.youpin.gwdash.service.impl;
//
//import com.xiaomi.youpin.gwdash.service.InspectionService;
//import com.xiaomi.youpin.gwdash.service.OnSiteInspectionService;
//import org.apache.dubbo.config.annotation.Service;
//import org.springframework.beans.factory.annotation.Autowired;
//
///**
// * @author zhangjunyi
// * created on 2020/7/30 10:56 上午
// */
//@Service(interfaceClass = InspectionService.class, retries = 0, group = "${dubbo.group}")
//public class InspectionServiceImpl implements InspectionService {
//    @Autowired
//    private OnSiteInspectionService onsiteInspectionService;
//
//    @Override
//    public int cleanUsageRecord() {
//        return onsiteInspectionService.deleteFromUsageRecord();
//    }
//}