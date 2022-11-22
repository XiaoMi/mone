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

package com.xiaomi.youpin.gwdash.dao.mapper;

import com.xiaomi.youpin.gwdash.dao.model.DebugRecord;
import com.xiaomi.youpin.gwdash.dao.model.DebugRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DebugRecordMapper {
    int countByExample(DebugRecordExample example);

    int deleteByExample(DebugRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DebugRecord record);

    int insertSelective(DebugRecord record);

    int insertOrUpdate(DebugRecord record);

    List<DebugRecord> selectByExampleWithBLOBs(DebugRecordExample example);

    List<DebugRecord> selectByExample(DebugRecordExample example);

    DebugRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DebugRecord record, @Param("example") DebugRecordExample example);

    int updateByExampleWithBLOBs(@Param("record") DebugRecord record, @Param("example") DebugRecordExample example);

    int updateByExample(@Param("record") DebugRecord record, @Param("example") DebugRecordExample example);

    int updateByPrimaryKeySelective(DebugRecord record);

    int updateByPrimaryKeyWithBLOBs(DebugRecord record);

    int updateByPrimaryKey(DebugRecord record);
}