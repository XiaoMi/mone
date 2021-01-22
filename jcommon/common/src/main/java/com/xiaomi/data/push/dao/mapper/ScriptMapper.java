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

import com.xiaomi.data.push.dao.model.Script;
import com.xiaomi.data.push.dao.model.ScriptExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScriptMapper {
    int countByExample(ScriptExample example);

    int deleteByExample(ScriptExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Script record);

    int insertSelective(Script record);

    List<Script> selectByExampleWithBLOBs(ScriptExample example);

    List<Script> selectByExample(ScriptExample example);

    Script selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Script record, @Param("example") ScriptExample example);

    int updateByExampleWithBLOBs(@Param("record") Script record, @Param("example") ScriptExample example);

    int updateByExample(@Param("record") Script record, @Param("example") ScriptExample example);

    int updateByPrimaryKeySelective(Script record);

    int updateByPrimaryKeyWithBLOBs(Script record);

    int updateByPrimaryKey(Script record);
}