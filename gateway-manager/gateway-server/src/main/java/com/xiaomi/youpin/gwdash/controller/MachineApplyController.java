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
//import com.xiaomi.data.push.micloud.bo.response.CatalystResponse;
//import com.xiaomi.data.push.micloud.bo.response.OrderDetail;
//import com.xiaomi.youpin.gwdash.bo.ApplyMachineParam;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.service.ApplyMachineService;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.util.Map;
//
///**
// * @author zhangjunyi
// * created on 2020/6/24 11:36 上午
// */
//@Slf4j
//@RestController
//public class MachineApplyController {
//
//    @Autowired
//    private ApplyMachineService applyMachineService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @RequestMapping(value = "/api/applyMachine/list", method = RequestMethod.GET)
//    public Result<Map<String, Object>> list(
//            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
//            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize
//    ){
//        return Result.success(applyMachineService.list(page, pageSize));
//    }
//
//    @RequestMapping(value = "/api/applyMachine/detail", method = RequestMethod.GET)
//    public Result<OrderDetail> applyMachineDetail(
//            @RequestParam(value = "id") int id
//    ) throws IOException {
//        return Result.success(applyMachineService.orderDetail(id));
//    }
//
//    @RequestMapping(value = "/api/applyMachine/application", method = RequestMethod.POST)
//    public Result<Boolean> applyMachine(
//            HttpServletRequest request,
//            @RequestBody ApplyMachineParam param) {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return Result.success(applyMachineService.applyMachine(account.getUsername(), param));
//    }
//
//    @RequestMapping(value = "/api/applyMachine/init", method = RequestMethod.GET)
//    public Result<CatalystResponse> initMachine(
//            @RequestParam(value = "id") int id
//    ) {
//        return Result.success(applyMachineService.initMachine(id));
//    }
//
//    @RequestMapping(value="/api/applyMachine/info",method = RequestMethod.GET)
//    public Result<CatalystResponse> getInitMachineInfo(
//            @RequestParam(value = "id") int id
//    ){
//        return Result.success(applyMachineService.getInitMachineInfo(id));
//    }
//}
