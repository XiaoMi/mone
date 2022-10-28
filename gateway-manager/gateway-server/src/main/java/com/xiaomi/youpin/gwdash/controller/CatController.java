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
//import com.xiaomi.youpin.gwdash.common.HttpResult;
//import com.xiaomi.youpin.gwdash.common.HttpUtils;
//import com.xiaomi.youpin.gwdash.common.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@Slf4j
//public class CatController {
//
//    @Value("${cat.base.url}")
//    private String catBaseUrl;
//
//    @Value("${cat.tesla:tesla}")
//    private String tesla;
//
//    @RequestMapping(value = "/api/cat/history", method = RequestMethod.GET)
//    public Result<HttpResult> getHistory(@RequestParam String reportType, @RequestParam int topk) {
//        HttpResult result = HttpUtils.get("http://" + catBaseUrl + "/cat/r/t?op=historyDashboard&reportType=" + reportType
//                + "&topk=" + topk,
//            null,
//            null,
//            100000);
//
//        return Result.success(result);
//    }
//
//
//    @RequestMapping(value = "/api/cat/domains", method = RequestMethod.GET)
//    public Result<HttpResult> getAllDomains() {
//        HttpResult result = HttpUtils.get("http://" + catBaseUrl + "/cat/r/top?getAllDomains=true",
//            null,
//            null,
//            100000);
//
//        return Result.success(result);
//    }
//
//
//    @RequestMapping(value = "/api/cat/list", method = RequestMethod.GET)
//    public Result<HttpResult> catList() {
//        HttpResult result = HttpUtils.get("http://" + catBaseUrl + "/cat/r/t?ip=All&queryname=&domain=" + tesla + "&type=tesla&export=true",
//            null,
//            null,
//            100000);
//
//        return Result.success(result);
//    }
//
//    @RequestMapping(value = "/api/cat/topkDomains", method = RequestMethod.GET)
//    public Result<HttpResult> topkAvailability(@RequestParam int topk) {
//        HttpResult result = HttpUtils.get("http://" + catBaseUrl + "/cat/r/t?op=dashboard&domain=ALL&topk=" + topk,
//            null,
//            null,
//            10000);
//
//        return Result.success(result);
//    }
//
//    @RequestMapping(value = "/api/cat/domain", method = RequestMethod.GET)
//    public Result<HttpResult> domainAvailability(@RequestParam String domain) {
//        HttpResult result = HttpUtils.get("http://" + catBaseUrl + "/cat/r/t?op=dashboard&domain=" + domain,
//            null,
//            null,
//            10000);
//
//        return Result.success(result);
//    }
//
//}
