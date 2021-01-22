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

import com.xiaomi.youpin.quota.bo.QuotaRequest;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author goodjava@qq.com
 */
@Repository
public class QuotaRequestDao {

    @Autowired
    private Dao dao;


    public void removeQuota(long bizId, Integer quotaId) {
        QuotaRequest data = dao.fetch(QuotaRequest.class, Cnd.where("biz_id", "=", bizId));
        if (null != data) {
            data.getQuotas().remove(quotaId);
            data.setNum(data.getQuotas().size());
        }
        dao.update(data);
    }

}
