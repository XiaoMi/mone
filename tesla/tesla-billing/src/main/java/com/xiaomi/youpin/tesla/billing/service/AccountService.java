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

package com.xiaomi.youpin.tesla.billing.service;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.tesla.billing.bo.AccountBo;
import com.xiaomi.youpin.tesla.billing.dataobject.Account;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;

@Service
public class AccountService {

    @Resource
    private NutDao dao;

    /**
     * 创建账户
     * @param accountBo
     */
    public void createAccount(AccountBo accountBo) {
        Account account = new Account();
        account.setBizId(accountBo.getBizId());
        account.setSubBizIdList(accountBo.getSubBizIdList());
        dao.insert(account);
    }


    /**
     * 更新账户
     * @param accountBo
     */
    public void updateAccount(AccountBo accountBo) {
        dao.update(Account.class, Chain.make("sub_biz_id_list", accountBo.getSubBizIdList()), Cnd.where("id", "=", accountBo.getId()));
    }

}
