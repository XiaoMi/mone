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
//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.service.AutoScalingService;
//import com.xiaomi.youpin.gwdash.service.ConfigService;
//import com.xiaomi.youpin.quota.bo.UtilizationStats;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@Slf4j
//public class AutoScalingController {
//
//    @Autowired
//    ConfigService configService;
//
//    @Autowired
//    AutoScalingService autoScalingService;
//
//    @RequestMapping(value = "/api/autoscaling/turnon", method = {RequestMethod.GET})
//    public Result<Boolean> turnon() {
//        configService.enableAutoScaling();
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/autoscaling/turnoff", method = {RequestMethod.GET})
//    public Result<Boolean> turnoff() {
//        configService.disableAutoScaling();
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/autoscaling/status", method = {RequestMethod.GET})
//    public Result<Boolean> status() {
//        return Result.success(configService.isAutoScalingEnabled());
//    }
//
//    @RequestMapping(value = "/api/scaledown/enable", method = {RequestMethod.GET})
//    public Result<Boolean> enableQuotaServerScaleDown() {
//        autoScalingService.enableQuotaServerScaleDown();
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/scaledown/disable", method = {RequestMethod.GET})
//    public Result<Boolean> disableQuotaServerScaleDown() {
//        autoScalingService.disableQuotaServerScaleDown();
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/scaledown/status", method = {RequestMethod.GET})
//    public Result<Boolean> isScaleDownEnabled() {
//        return Result.success(autoScalingService.isScaleDownEnabled());
//    }
//
//    @RequestMapping(value = "/api/cluster/utilization/current", method = {RequestMethod.GET})
//    public Result<Double> getClusterUtilization() {
//        return Result.success(autoScalingService.getClusterUtilization());
//    }
//
//    @RequestMapping(value = "/api/cluster/utilization/daily", method = {RequestMethod.GET})
//    public Result<List<UtilizationStats>> getDailyUtilizationStats() {
//        return Result.success(autoScalingService.getDailyUtilizationStats());
//    }
//}
