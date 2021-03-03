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

package com.xiaomi.youpin.quota.dao;

import com.xiaomi.youpin.quota.bo.Resource;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用来测试事务
 */
@Repository
public class TransactionDao {

    @Autowired
    private Dao dao;


    /**
     * 去掉的话就会插入一条数据(证明没有在一个事务当中)
     */
    @Transactional("masterTransactionManager")
    public void testTransaction() {
        Resource resource = new Resource();
        dao.insert(resource);
        if (1 == 1) {
            throw new RuntimeException("1==1");
        }
        dao.insert(new Resource());
    }

}
