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

package com.xiaomi.youpin;

import com.xiaomi.youpin.gwdash.bootstrap.GwDashBootstrap;
import com.xiaomi.youpin.gwdash.service.OnSiteInspectionService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.youpin.xiaomi.tesla.bo.ApiInfoList;
import com.youpin.xiaomi.tesla.service.TeslaOpsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author goodjava@qq.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GwDashBootstrap.class)
public class ServiceTest {


    @Autowired
    private TeslaOpsService teslaOpsService;

    @Autowired
    private OnSiteInspectionService onSiteInspectionService;


    @Test
    public void test1() {
        System.out.println("test1");
        Result<ApiInfoList> res = teslaOpsService.apiInfoList(2000, 5);
        System.out.println(res);
    }

    @Test
    public void test2(){
        onSiteInspectionService.deleteFromUsageRecord();
    }


}
