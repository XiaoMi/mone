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

package com.xiaomi.youpin.gwdash.dao;

import com.xiaomi.youpin.gwdash.dao.mapper.DebugRecordMapper;
import com.xiaomi.youpin.gwdash.dao.model.DebugRecord;
import com.xiaomi.youpin.gwdash.dao.model.DebugRecordExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class DebugRecordDao {

    @Autowired
    private DebugRecordMapper debugRecordMapper;

    public DebugRecord getRecordByAid(int aid) {
        DebugRecordExample example = new DebugRecordExample();
        DebugRecordExample.Criteria criteria = example.createCriteria();
        criteria.andAidEqualTo(aid);

        List<DebugRecord> records = debugRecordMapper.selectByExampleWithBLOBs(example);
        if (records == null || records.size() <= 0) {
            return null;
        }

        return records.get(0);
    }

    /**
     * 基于api id插入或者更新测试记录
     * @param record
     * @return
     */
    public int insertOrUpdate(DebugRecord record) {
        return debugRecordMapper.insertOrUpdate(record);
    }



}
