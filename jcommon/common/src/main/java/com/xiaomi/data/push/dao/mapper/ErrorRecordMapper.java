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

package com.xiaomi.data.push.dao.mapper;

import com.xiaomi.data.push.dao.model.ErrorRecord;
import com.xiaomi.data.push.dao.model.ErrorRecordExample;
import com.xiaomi.data.push.dao.model.ErrorRecordWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ErrorRecordMapper {
    int countByExample(ErrorRecordExample example);

    int deleteByExample(ErrorRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ErrorRecordWithBLOBs record);

    int insertSelective(ErrorRecordWithBLOBs record);

    List<ErrorRecordWithBLOBs> selectByExampleWithBLOBs(ErrorRecordExample example);

    List<ErrorRecord> selectByExample(ErrorRecordExample example);

    ErrorRecordWithBLOBs selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ErrorRecordWithBLOBs record, @Param("example") ErrorRecordExample example);

    int updateByExampleWithBLOBs(@Param("record") ErrorRecordWithBLOBs record, @Param("example") ErrorRecordExample example);

    int updateByExample(@Param("record") ErrorRecord record, @Param("example") ErrorRecordExample example);

    int updateByPrimaryKeySelective(ErrorRecordWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ErrorRecordWithBLOBs record);

    int updateByPrimaryKey(ErrorRecord record);
}