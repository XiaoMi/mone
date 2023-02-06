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

import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ApiInfoMapper {
    long countByExample(ApiInfoExample example);

    int deleteByExample(ApiInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiInfo record);

    int insertSelective(ApiInfo record);

    List<ApiInfo> selectByExampleWithBLOBs(ApiInfoExample example);

    List<ApiInfo> selectByExample(ApiInfoExample example);

    ApiInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ApiInfo record, @Param("example") ApiInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") ApiInfo record, @Param("example") ApiInfoExample example);

    int updateByExample(@Param("record") ApiInfo record, @Param("example") ApiInfoExample example);

    int updateByPrimaryKeySelective(ApiInfo record);

    int updateByPrimaryKeyWithBLOBs(ApiInfo record);

    int updateByPrimaryKey(ApiInfo record);

    int batchInsert(@Param("list") List<ApiInfo> list);

    int batchInsertSelective(@Param("list") List<ApiInfo> list, @Param("selective") ApiInfo.Column ... selective);
}