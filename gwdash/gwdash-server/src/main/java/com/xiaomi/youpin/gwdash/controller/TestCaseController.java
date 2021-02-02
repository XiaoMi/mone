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

package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.bo.TestCaseParam;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.TestCaseSetting;
import com.xiaomi.youpin.gwdash.service.TestCaseService;
import com.xiaomi.youpin.gwdash.service.TestWorldService;
import com.xiaomi.youpin.tw.bo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author tsingfu
 * @author goodjava@qq.com
 */
@RestController
@Slf4j
@RequestMapping("/api/test/case")
public class TestCaseController {

    @Autowired
    private TestWorldService testWorldService;

    @Autowired
    private TestCaseService testCaseService;

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public Result<String> getVersion(HttpServletRequest request) {
        return Result.success(testWorldService.version());
    }

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public Result<List<String>> getSevices(HttpServletRequest request) {
        return Result.success(testWorldService.services());
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Result<List<String>> test(
            HttpServletRequest request,
            @RequestParam(required = true) String serviceName) {
        return Result.success(testWorldService.test(serviceName).getData());
    }

    @RequestMapping(value = "/set-setting", method = RequestMethod.POST)
    public Result<Boolean> setSetting(
            HttpServletRequest request,
            @RequestBody TestCaseSetting testCaseSetting) {
        return testCaseService.setParam(testCaseSetting);
    }

    @RequestMapping(value = "/get-setting", method = RequestMethod.GET)
    public Result<TestCaseParam> getSetting(
            HttpServletRequest request,
            @RequestParam(required = true) String serviceName,
            @RequestParam(required = true) String methodName) {
        return Result.success(testCaseService.getTestCaseParam(serviceName, methodName));
    }

    /**
     * 获取相应测试服务的 provider 列表信息
     * @param request
     * @param serviceName
     * @return
     */
    @RequestMapping(value = "/provider-list", method = RequestMethod.GET)
    public Result<List<ProviderInfo>> providerList(
            HttpServletRequest request,
            @RequestParam(required = true) String serviceName,
            @RequestParam(required = true) String methodName
    ) {
        TwRequest tr = new TwRequest();
        tr.setServiceName(serviceName);
        tr.setMethodName(methodName);
        return Result.success(testWorldService.prividerListInfo(tr).getData());
    }

    @RequestMapping(value = "/test-method", method = RequestMethod.GET)
    public Result<String> testMethod(
            HttpServletRequest request,
            @RequestParam(required = true) String serviceName,
            @RequestParam(required = true) String methodName
    ) {
        TestCaseParam testCaseParam = testCaseService.getTestCaseParam(serviceName, methodName);
        TwRequest twRequest = new TwRequest();
        twRequest.setServiceName(serviceName);
        twRequest.setMethodName(methodName);
        twRequest.setArgs("");
        twRequest.setType(RequestType.normal);
        twRequest.setCallType(CallType.dubbo);
        if (null != testCaseParam) {
            twRequest.setArgs(testCaseParam.getArgs());
            twRequest.setCallType(CallType.valueOf(testCaseParam.getCallType()));
            twRequest.setType(RequestType.valueOf(testCaseParam.getType()));
            List<ProviderInfo> list = testCaseParam.getProviders();
            if (null != list && list.size() > 0) {
                twRequest.setIp(list.get(0).getIp());
                twRequest.setPort(list.get(0).getPort());
            }
        }
        return Result.success(testWorldService.testMethod(twRequest).getData());
    }

    @RequestMapping(value = "/methods", method = RequestMethod.GET)
    public Result<MethodInfos> getMethod(
            HttpServletRequest request,
            @RequestParam(required = true) String serviceName) {
        return Result.success(testWorldService.methods(serviceName));
    }
}
