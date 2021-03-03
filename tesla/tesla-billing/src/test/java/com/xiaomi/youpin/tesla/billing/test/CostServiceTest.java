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

package com.xiaomi.youpin.tesla.billing.test;


import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.tesla.billing.bo.CostBo;
import com.xiaomi.youpin.tesla.billing.bo.ResourceBo;
import com.xiaomi.youpin.tesla.billing.bo.ResourceUseInfo;
import com.xiaomi.youpin.tesla.billing.service.BillingServiceImpl;
import com.xiaomi.youpin.tesla.billing.service.CostService;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/8/5
 */
public class CostServiceTest extends BaseTest {


    @Test
    public void testBeginCost() {
        CostService costService = Ioc.ins().getBean(CostService.class);
        CostBo costBo = new CostBo();
        costBo.setAccountId(1);
        ResourceBo resourceBo = new ResourceBo();
        resourceBo.setBizId(1L);
        resourceBo.setSubBizId(2L);
        resourceBo.setProdcutId(1);
        resourceBo.setResourceKey("127.0.0.1");

        ResourceUseInfo resourceInfo = new ResourceUseInfo();
        resourceInfo.setCpuNum(24);
        resourceInfo.setUseCpuNum(12);
        resourceBo.setResourceInfo(resourceInfo);


        costBo.setResourceBoList(Lists.newArrayList(resourceBo));
        costService.beginCost(costBo);
    }


    @Test
    public void testStopCost() {
        CostService costService = Ioc.ins().getBean(CostService.class);
        CostBo costBo = new CostBo();
        costBo.setAccountId(1);
        ResourceBo resourceBo = new ResourceBo();
        resourceBo.setBizId(1L);
        resourceBo.setSubBizId(2L);
        resourceBo.setProdcutId(1);
        resourceBo.setResourceKey("127.0.0.1");
        costBo.setResourceBoList(Lists.newArrayList(resourceBo));
        costService.stopCost(costBo);
    }

    @Test
    public void testCostDay() {
        BillingServiceImpl billingService = Ioc.ins().getBean(BillingServiceImpl.class);
        billingService.billingTaskDay();

    }

    @Test
    public void testCostMonth() {
        BillingServiceImpl billingService = Ioc.ins().getBean(BillingServiceImpl.class);
        billingService.billingTaskMonth();
    }

    @Test
    public void getAppManagementBillingDetail() {
        BillingServiceImpl billingService = Ioc.ins().getBean(BillingServiceImpl.class);
        //billingService.billingTaskDay();
        billingService.getAppManagementBillingDetail(53);

    }



}
