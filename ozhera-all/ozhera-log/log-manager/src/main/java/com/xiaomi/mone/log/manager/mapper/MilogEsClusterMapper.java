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
import com.xiaomi.mone.log.manager.model.pojo.MilogEsClusterDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wanghaoyang
 * @since 2021-09-18
 */
@Mapper
public interface MilogEsClusterMapper extends BaseMapper<MilogEsClusterDO> {

    /**
     * 获取tag对应的ES 客户端
     *
     * @param tag
     * @return
     */
    List<MilogEsClusterDO> selectByTag(@Param("tag") String tag);

    /**
     * 查找所有
     *
     * @return
     */
    List<MilogEsClusterDO> selectAll();

    /**
     * 获取region对应的ES客户端
     *
     * @param region
     * @return
     */
    MilogEsClusterDO selectByRegion(@Param("region") String region);

    /**
     * 获取area对应的ES客户端
     *
     * @param area
     * @return
     */
    List<MilogEsClusterDO> selectByArea(@Param("area") String area, @Param("label") String label);

    List<MilogEsClusterDO> selectByAlias(@Param("alias") String alias);

    /**
     * 获取 tag 和 area 对应的 ES 集群信息
     *
     * @param tag
     * @param area
     * @return
     */
    MilogEsClusterDO selectByTagAndArea(@Param("tag") String tag, @Param("area") String area);
}
