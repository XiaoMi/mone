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
import com.xiaomi.youpin.tesla.billing.bo.AccountBo;
import com.xiaomi.youpin.tesla.billing.service.AccountService;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/8/5
 */
public class AccountServiceTest extends BaseTest {


    @Test
    public void testCreate() {
        AccountService service = Ioc.ins().getBean(AccountService.class);
        AccountBo accountBo = new AccountBo();
        accountBo.setBizId(1L);
        accountBo.setSubBizIdList(Lists.newArrayList(2L,3L));
        service.createAccount(accountBo);
    }

    @Test
    public void testUpdate() {
        AccountService service = Ioc.ins().getBean(AccountService.class);
        AccountBo accountBo = new AccountBo();
        accountBo.setId(1);
        accountBo.setSubBizIdList(Lists.newArrayList(2L,4L));
        service.updateAccount(accountBo);
    }

}
