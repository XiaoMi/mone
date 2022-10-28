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
//import com.xiaomi.youpin.gwdash.bo.SwitchBo;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.service.SwitchService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * @author tsingfu
// */
//@RestController
//@Slf4j
//@RequestMapping("/api")
//public class SwitchController {
//
//    @Autowired
//    private SwitchService switchService;
//
//    @RequestMapping(value = "/switch/config", method = RequestMethod.GET)
//    public Result<SwitchBo> getConfig (HttpServletRequest request) {
//        return Result.success(switchService.getConfig());
//    }
//
//    @RequestMapping(value = "/switch/release", method = RequestMethod.POST)
//    public Result<Boolean> isRelease (HttpServletRequest request, @RequestBody SwitchBo switchBo) {
//        switchService.update(switchBo);
//        return Result.success(true);
//    }
//}
