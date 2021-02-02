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

import com.xiaomi.data.push.dao.model.Mock;
import com.xiaomi.data.push.dao.model.MockExample;
import com.xiaomi.data.push.dao.model.MockWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MockMapper {
    int countByExample(MockExample example);

    int deleteByExample(MockExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MockWithBLOBs record);

    int insertSelective(MockWithBLOBs record);

    List<MockWithBLOBs> selectByExampleWithBLOBs(MockExample example);

    List<Mock> selectByExample(MockExample example);

    MockWithBLOBs selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MockWithBLOBs record, @Param("example") MockExample example);

    int updateByExampleWithBLOBs(@Param("record") MockWithBLOBs record, @Param("example") MockExample example);

    int updateByExample(@Param("record") Mock record, @Param("example") MockExample example);

    int updateByPrimaryKeySelective(MockWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(MockWithBLOBs record);

    int updateByPrimaryKey(Mock record);
}