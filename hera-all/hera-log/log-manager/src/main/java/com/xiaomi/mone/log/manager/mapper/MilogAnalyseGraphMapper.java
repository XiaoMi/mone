/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.log.manager.model.dto.DashboardGraphDTO;
import com.xiaomi.mone.log.manager.model.dto.GraphDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogAnalyseGraphDO;
import com.xiaomi.mone.log.manager.model.vo.GraphQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-08-18
 */
@Mapper
public interface MilogAnalyseGraphMapper extends BaseMapper<MilogAnalyseGraphDO> {

    List<DashboardGraphDTO> getDashboardGraph(@Param(value = "dashboardId") Long dashboardId);

    List<GraphDTO> search(@Param(value = "query") GraphQuery query);
}
