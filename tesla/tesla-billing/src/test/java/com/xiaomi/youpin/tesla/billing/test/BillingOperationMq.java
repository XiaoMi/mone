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

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.tesla.billing.bo.BillingOperationBo;
import com.xiaomi.youpin.tesla.billing.common.BillingOperationTypeEnum;
import com.xiaomi.youpin.tesla.billing.common.BillingPlatformEnum;
import com.xiaomi.youpin.tesla.billing.common.BillingTypeEnum;
import com.xiaomi.youpin.tesla.billing.dataobject.BillingNorms;
import com.xiaomi.youpin.tesla.billing.service.BillingOperationMqConsumer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 　　* @description: TODO
 * 　　* @author zhenghao
 *
 */
public class BillingOperationMq extends BaseTest {

    @Test
    public void testxiaohuiCost() {
        // 销毁资源
        BillingOperationBo bo = new BillingOperationBo();
        bo.setBillingPlatform(BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode());
        bo.setBillingType(BillingTypeEnum.BY_MINUTE.getCode());
        bo.setBillingOperation(BillingOperationTypeEnum.DESTROY_RESOURCE.getCode());
        bo.setSubType(1);
        bo.setAccountId(1);
        bo.setOperationTime(System.currentTimeMillis());
        List<String> resourceIds = new ArrayList<>();
        resourceIds.add("11111111");
        bo.setResourceIds(resourceIds);
        bo.setBizId(1);
        bo.setSubBizId(1);
        bo.setSendTime(System.currentTimeMillis());


        BillingNorms useBillingNorms = new BillingNorms();
        useBillingNorms.setCpu("2");
        BillingNorms allNorms = new BillingNorms();
        allNorms.setCpu("3");
        bo.setUseNorms(useBillingNorms);
        bo.setAllNorms(allNorms);
        BillingOperationMqConsumer billingOperationMqConsumer = Ioc.ins().getBean(BillingOperationMqConsumer.class);
        billingOperationMqConsumer.test(bo);



//        BillingOperationMqConsumer billingOperationMqConsumer = Ioc.ins().getBean(BillingOperationMqConsumer.class);
//        BillingOperationBo billingOperationBo = new BillingOperationBo();
//        billingOperationBo.setAccountId("1");
//        billingOperationBo.setSendId(UUID.randomUUID().toString());
//        //billingOperationBo.setResourceIds("11111111,3333333333,22222222222");
//        billingOperationBo.setBillingType(BillingTypeEnum.ALL_YEAR_ALL_DAY.getCode());// 包年包月
//        billingOperationBo.setBillingPlatform(BillingPlatformEnum.CLOUD_COMPUTER_ROOM.getCode());// cloud
//        billingOperationBo.setBillingOperation(BillingOperationTypeEnum.CREATE_RESOURCE.getCode());// 创建资源
//        //billingOperationBo.setEnvironment("c3");
//
//        String use = "{\n" +
//                "\t\"cpu\": 4,\n" +
//                "\t\"disk\": 500,\n" +
//                "\t\"memory\":32\n" +
//                "}";
//
//        String all = "{\n" +
//                "\t\"cpu\": 8,\n" +
//                "\t\"disk\": 500,\n" +
//                "\t\"memory\":32\n" +
//                "}";
//        billingOperationBo.setUseNorms(use);
//        billingOperationBo.setAllNorms(all);
//        billingOperationBo.setOperationTime(System.currentTimeMillis());
//        billingOperationBo.setSendTime(System.currentTimeMillis());
//        billingOperationMqConsumer.test(billingOperationBo);
    }

    @Test
    public void testBeginCost() {
        // 创建资源

        BillingOperationBo bo = new BillingOperationBo();
        bo.setBillingPlatform(BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode());
        bo.setBillingType(BillingTypeEnum.BY_MINUTE.getCode());
        bo.setBillingOperation(BillingOperationTypeEnum.CREATE_RESOURCE.getCode());
        bo.setSubType(1);
        bo.setAccountId(1);
        bo.setOperationTime(System.currentTimeMillis());
        List<String> resourceIds = new ArrayList<>();
        resourceIds.add("11111111");
        bo.setResourceIds(resourceIds);
        bo.setBizId(1);
        bo.setSubBizId(1);
        bo.setSendTime(System.currentTimeMillis());


        BillingNorms useBillingNorms = new BillingNorms();
        useBillingNorms.setCpu("2");
        BillingNorms allNorms = new BillingNorms();
        allNorms.setCpu("3");
        bo.setUseNorms(useBillingNorms);
        bo.setAllNorms(allNorms);
        BillingOperationMqConsumer billingOperationMqConsumer = Ioc.ins().getBean(BillingOperationMqConsumer.class);
        billingOperationMqConsumer.test(bo);

    }

    @Test
    public void testupgradeCost() {
        // 升配资源

        BillingOperationBo bo = new BillingOperationBo();
        bo.setBillingPlatform(BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode());
        bo.setBillingType(BillingTypeEnum.BY_MINUTE.getCode());
        bo.setBillingOperation(BillingOperationTypeEnum.RISE_RESOURCE.getCode());
        bo.setSubType(1);
        bo.setAccountId(1);
        bo.setOperationTime(System.currentTimeMillis());
        List<String> resourceIds = new ArrayList<>();
        resourceIds.add("11111111");
        bo.setResourceIds(resourceIds);
        bo.setBizId(1);
        bo.setSubBizId(1);
        bo.setSendTime(System.currentTimeMillis());


        BillingNorms useBillingNorms = new BillingNorms();
        useBillingNorms.setCpu("4");
        BillingNorms allNorms = new BillingNorms();
        allNorms.setCpu("6");
        bo.setUseNorms(useBillingNorms);
        bo.setAllNorms(allNorms);
        BillingOperationMqConsumer billingOperationMqConsumer = Ioc.ins().getBean(BillingOperationMqConsumer.class);
        billingOperationMqConsumer.test(bo);

    }

    @Test
    public void testdowngradeCost() {
        // 降配资源

        BillingOperationBo bo = new BillingOperationBo();
        bo.setBillingPlatform(BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode());
        bo.setBillingType(BillingTypeEnum.BY_MINUTE.getCode());
        bo.setBillingOperation(BillingOperationTypeEnum.DROP_RESOURCE.getCode());
        bo.setSubType(1);
        bo.setAccountId(1);
        bo.setOperationTime(System.currentTimeMillis());
        List<String> resourceIds = new ArrayList<>();
        resourceIds.add("11111111");
        bo.setResourceIds(resourceIds);
        bo.setBizId(1);
        bo.setSubBizId(1);
        bo.setSendTime(System.currentTimeMillis());


        BillingNorms useBillingNorms = new BillingNorms();
        useBillingNorms.setCpu("1");
        BillingNorms allNorms = new BillingNorms();
        allNorms.setCpu("2");
        bo.setUseNorms(useBillingNorms);
        bo.setAllNorms(allNorms);
        BillingOperationMqConsumer billingOperationMqConsumer = Ioc.ins().getBean(BillingOperationMqConsumer.class);
        billingOperationMqConsumer.test(bo);

    }

}
