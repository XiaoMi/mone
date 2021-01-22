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

package com.xiaomi.youpin.quota.service;

import com.xiaomi.youpin.quota.bo.Record;
import com.xiaomi.youpin.quota.bo.RecordBo;
import com.xiaomi.youpin.quota.bo.RecordResult;
import com.xiaomi.youpin.quota.dao.RecordDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service(interfaceClass = RecordService.class, group = "${dubbo.group}")
public class RecordServiceImpl implements RecordService {

    @Autowired
    RecordDao recordDao;

    @Override
    public RecordResult getAllRecords() {
        return getAllRecords(-1, -1);
    }

    @Override
    public RecordResult getAllRecords(int pageNumber, int pageSize) {
        RecordResult result = new RecordResult();
        result.setTotal(recordDao.getRecordCount());

        List<Record> records = recordDao.getAllRecords(pageNumber, pageSize);
        List<RecordBo> list = new LinkedList<>();

        for (Record record : records) {
            RecordBo recordBo = new RecordBo();
            recordBo.setId(record.getId());
            recordBo.setIp(record.getIp());
            recordBo.setBizId(record.getBizId());
            recordBo.setType(record.getType());
            recordBo.setCtime(record.getCtime());
            recordBo.setStatus(record.getStatus());
            recordBo.setOperator(record.getOperator());

            recordBo.setProjectBefore(record.getProjectBefore());
            recordBo.setProjectAfter(record.getProjectAfter());
            recordBo.setResourceBefore(record.getResourceBefore());
            recordBo.setResourceAfter(record.getResourceAfter());
            list.add(recordBo);
        }
        result.setRecords(list);
        return result;
    }
}
