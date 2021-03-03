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


import com.xiaomi.youpin.quota.bo.Record;
import com.xiaomi.youpin.quota.bo.ResourceBo;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecordDao {

    @Autowired
    private Dao dao;

    public int getRecordCount() {
        return dao.count(Record.class);
    }

    public List<Record> getAllRecords() {
        return getAllRecords(-1, -1);
    }

    public List<Record> getAllRecords(int pageNumber, int pageSize) {
        Pager pager = null;
        if (pageNumber >= 1 && pageSize > 0) {
            pager = dao.createPager(pageNumber, pageSize);
        }
        Cnd cnd = Cnd.NEW();
        cnd.desc("id");
        return dao.query(Record.class, cnd, pager);
    }

    public Record getRecord(int id) {
        return dao.fetch(Record.class, Cnd.where("id", "=", id));
    }

    public void insert(Record record) {
        if (record == null) {
            return;
        }
        long time = System.currentTimeMillis();
        record.setCtime(time);
        record.setUtime(time);

        dao.insert(record);
    }

    public void update(Record record) {
        dao.update(record);
    }

}
