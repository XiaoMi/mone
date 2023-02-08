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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.TestCaseParam;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.TestCaseSetting;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestCaseService {

    @Autowired
    private Dao dao;

    public Result<Boolean> setParam(TestCaseSetting testCaseSettingBo) {
        TestCaseSetting testCaseSetting = dao.fetch(TestCaseSetting.class,
                Cnd.where("service_name", "=", testCaseSettingBo.getServiceName())
                        .and("method", "=", testCaseSettingBo.getMethod()));
        long now = System.currentTimeMillis();
        if (null == testCaseSetting) {
            testCaseSettingBo.setCtime(now);
            testCaseSettingBo.setUtime(now);
            dao.insert(testCaseSettingBo);
        } else {
            testCaseSetting.setTestCaseParam(testCaseSettingBo.getTestCaseParam());
            testCaseSetting.setUtime(now);
            dao.update(testCaseSetting);
        }
        return Result.success(true);
    }

    public TestCaseParam getTestCaseParam(String serviceName, String methodName) {
        TestCaseSetting testCaseSetting = dao.fetch(TestCaseSetting.class,
                Cnd.where("service_name", "=", serviceName)
                        .and("method", "=", methodName));
        if (null != testCaseSetting) {
            return testCaseSetting.getTestCaseParam();
        }
        return  null;
    }
}
