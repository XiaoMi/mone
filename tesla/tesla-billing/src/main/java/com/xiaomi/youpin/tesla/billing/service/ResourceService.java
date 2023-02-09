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
import com.xiaomi.youpin.tesla.billing.dataobject.ResourceDo;
import com.xiaomi.youpin.tesla.billing.dataobject.ResourceOperatingRecord;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Service
public class ResourceService {


    @Resource
    private NutDao dao;

    /**
     * 增加一条记录
     *
     * @param record
     */
    public void addRecord(ResourceOperatingRecord record, long beginTime) {
        //没有就创建
        ResourceDo rd = dao.fetch(ResourceDo.class, Cnd.where("resource_key", "=", record.getResourceKey()));
        if (null == rd) {
            rd = new ResourceDo();
            rd.setProductId(1);
            rd.setResourceKey(record.getResourceKey());
            dao.insert(rd);
        }

        record.setResourceId(rd.getId());
        record.setBeginTime(beginTime);
        dao.insert(record);
    }


    /**
     * 关闭记录
     *
     * @param resourceKey
     * @param endTime
     */
    public void closeRecord(String resourceKey, long endTime) {
        List<ResourceOperatingRecord> list = dao.query(ResourceOperatingRecord.class, Cnd.where("resource_key", "=", resourceKey).limit(1, 1).desc("id"));
        if (list.size() > 0) {
            ResourceOperatingRecord re = list.get(0);
            re.setEndTime(endTime);
            re.setStatus(1);
            dao.update(re);
        }
    }


}
