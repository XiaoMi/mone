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
import com.xiaomi.mone.log.manager.model.pojo.LogCountDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-01-07
 */
@Mapper
public interface MilogLogCountMapper extends BaseMapper<LogCountDO> {
    /**
     * Bulk insertion
     * @param logCountList
     * @return
     */
    Long batchInsert(@Param("logCountList") List<LogCountDO> logCountList);

    /**
     * Top 10 statistical log storage
     * @return
     */
    List<Map<String, Object>> collectTopCount(@Param("fromDay") String fromDay, @Param("toDay") String toDay);

    /**
     * Statistics on log storage trends
     */
    List<Map<String, Object>> collectTrend(@Param("fromDay") String fromDay, @Param("toDay") String toDay, @Param("tailId") Long tailId);

    /**
     * Statistics on the storage trend of SPACE logs
     */
    List<Map<String, Object>> collectSpaceTrend(@Param("fromDay") String fromDay, @Param("toDay") String toDay);


    /**
     * Whether this day has been counted
     * @param day
     * @return
     */
    Long isLogtailCountDone(@Param("day") String day);

    /**
     * Deletes data older than the specified date
     * @param day
     */
    void deleteBeforeDay(@Param("day") String day);

    /**
     * Delete statistics for this day
     * @param day
     */
    void deleteThisDay(@Param("day") String day);

    /**
     * Space log statistics top 10
     * @param fromDay
     * @param toDay
     * @return
     */
    List<Map<String, Object>> collectSpaceCount(@Param("fromDay") String fromDay, @Param("toDay") String toDay);

    /**
     * App log statistics
     */
    List<Map<String, Object>> collectAppLog(@Param("day") String day, @Param("threshold") Long threshold);
}
