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

import com.xiaomi.data.push.dao.model.ActionConf;
import com.xiaomi.data.push.dao.model.ActionConfExample;
import com.xiaomi.data.push.dao.model.ActionConfWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ActionConfMapper {
    int countByExample(ActionConfExample example);

    int deleteByExample(ActionConfExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ActionConfWithBLOBs record);

    int insertSelective(ActionConfWithBLOBs record);

    List<ActionConfWithBLOBs> selectByExampleWithBLOBs(ActionConfExample example);

    List<ActionConf> selectByExample(ActionConfExample example);

    ActionConfWithBLOBs selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ActionConfWithBLOBs record, @Param("example") ActionConfExample example);

    int updateByExampleWithBLOBs(@Param("record") ActionConfWithBLOBs record, @Param("example") ActionConfExample example);

    int updateByExample(@Param("record") ActionConf record, @Param("example") ActionConfExample example);

    int updateByPrimaryKeySelective(ActionConfWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ActionConfWithBLOBs record);

    int updateByPrimaryKey(ActionConf record);
}