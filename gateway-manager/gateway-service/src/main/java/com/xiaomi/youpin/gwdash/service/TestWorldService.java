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
//
//import com.xiaomi.youpin.infra.rpc.Result;
//import com.xiaomi.youpin.tw.bo.MethodInfos;
//import com.xiaomi.youpin.tw.bo.ProviderInfo;
//import com.xiaomi.youpin.tw.bo.TwRequest;
//
//import java.security.Provider;
//import java.util.List;
//
///**
// * @author goodjava@qq.com
// */
//public interface TestWorldService {
//
//    String version();
//
//    Result<List<String>> test(String serviceName);
//
//    List<String> services();
//
//    MethodInfos methods(String serviceName);
//
//    Result<String> testMethod(TwRequest request);
//
//    Result<List<ProviderInfo>> prividerListInfo(TwRequest request);
//
//}
