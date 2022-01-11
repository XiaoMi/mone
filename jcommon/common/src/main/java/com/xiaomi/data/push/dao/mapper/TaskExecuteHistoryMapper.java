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

import com.xiaomi.data.push.dao.model.TaskExecuteHistory;
import com.xiaomi.data.push.dao.model.TaskExecuteHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TaskExecuteHistoryMapper {
    int countByExample(TaskExecuteHistoryExample example);

    int deleteByExample(TaskExecuteHistoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TaskExecuteHistory record);

    int insertSelective(TaskExecuteHistory record);

    List<TaskExecuteHistory> selectByExample(TaskExecuteHistoryExample example);

    TaskExecuteHistory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TaskExecuteHistory record, @Param("example") TaskExecuteHistoryExample example);

    int updateByExample(@Param("record") TaskExecuteHistory record, @Param("example") TaskExecuteHistoryExample example);

    int updateByPrimaryKeySelective(TaskExecuteHistory record);

    int updateByPrimaryKey(TaskExecuteHistory record);
}