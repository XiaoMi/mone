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
 * Mapper
 * </p>
 *
 * @author wanghaoyang
 * @since 2021-09-18
 */
@Mapper
public interface MilogEsClusterMapper extends BaseMapper<MilogEsClusterDO> {

    /**
     * obtain tag Obtain the tag corresponding to the ES client
     *
     * @param tag
     * @return
     */
    List<MilogEsClusterDO> selectByTag(@Param("tag") String tag);

    /**
     * Find all
     *
     * @return
     */
    List<MilogEsClusterDO> selectAll();

    /**
     * Obtain the ES client corresponding to the region
     *
     * @param region
     * @return
     */
    MilogEsClusterDO selectByRegion(@Param("region") String region);

    /**
     * Obtain the ES client corresponding to the area
     *
     * @param area
     * @return
     */
    List<MilogEsClusterDO> selectByArea(@Param("area") String area, @Param("label") String label);

    List<MilogEsClusterDO> selectByAlias(@Param("alias") String alias);

    /**
     * Obtain the ES cluster information corresponding to tags and areas
     *
     * @param tag
     * @param area
     * @return
     */
    MilogEsClusterDO selectByTagAndArea(@Param("tag") String tag, @Param("area") String area);
}
