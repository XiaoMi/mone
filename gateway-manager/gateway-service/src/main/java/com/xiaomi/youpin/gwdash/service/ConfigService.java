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
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 动态配置(会动态更新)
// */
//@Service
//public class ConfigService {
//    @NacosValue(value = "${needRefreshNginxUpstreamName:false}", autoRefreshed = true)
//    private boolean needRefreshNginxUpstreamName = false;
//
//    public boolean isNeedRefreshNginxUpstreamName() {
//        return needRefreshNginxUpstreamName;
//    }
//
//
//    private volatile boolean autoScalingEnable = true;
//
//    public boolean isAutoScalingEnabled() {
//        return autoScalingEnable;
//    }
//
//    public void enableAutoScaling() {
//        autoScalingEnable = true;
//    }
//
//    public void disableAutoScaling() {
//        autoScalingEnable = false;
//    }
//
//}
