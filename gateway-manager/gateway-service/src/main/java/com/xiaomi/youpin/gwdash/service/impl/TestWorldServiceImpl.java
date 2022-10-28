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
//import com.xiaomi.youpin.gwdash.service.TestWorldService;
//import com.xiaomi.youpin.infra.rpc.Result;
//import com.xiaomi.youpin.tw.bo.MethodInfos;
//import com.xiaomi.youpin.tw.bo.ProviderInfo;
//import com.xiaomi.youpin.tw.bo.TwRequest;
//import com.xiaomi.youpin.tw.service.TestManagerService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * @author goodjava@qq.com
// */
//@Slf4j
//@Service
//public class TestWorldServiceImpl implements TestWorldService {
//
//
//    @Reference(interfaceClass = TestManagerService.class, check = false, timeout = 30000, retries = 0)
//    private TestManagerService service;
//
//    @Override
//    public String version() {
//        return service.version().getData();
//    }
//
//    @Override
//    public Result<List<String>> test(String serviceName) {
//        log.info("test serviceName:{}", serviceName);
//        TwRequest request = new TwRequest();
//        request.setServiceName(serviceName);
//        return Result.success(service.test(request).getData());
//    }
//
//    @Override
//    public Result<String> testMethod(TwRequest request) {
//        log.info("testMethod: {}", request);
//        return service.testForMethod(request);
//    }
//
//    @Override
//    public Result<List<ProviderInfo>> prividerListInfo(TwRequest request) {
//        return service.providerListInfo(request);
//    }
//
//    @Override
//    public MethodInfos methods(String serviceName) {
//        TwRequest twRequest = new TwRequest();
//        twRequest.setServiceName(serviceName);
//        return service.methodInfos(twRequest).getData();
//    }
//
//
//    @Override
//    public List<String> services() {
//        return service.services().getData();
//    }
//}
