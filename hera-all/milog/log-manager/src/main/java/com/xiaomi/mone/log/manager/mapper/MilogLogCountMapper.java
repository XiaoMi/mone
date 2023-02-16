package com.xiaomi.mone.log.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.log.manager.model.pojo.LogCountDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-01-07
 */
@Mapper
public interface MilogLogCountMapper extends BaseMapper<LogCountDO> {
    /**
     * 批量插入
     * @param logCountList
     * @return
     */
    Long batchInsert(@Param("logCountList") List<LogCountDO> logCountList);

    /**
     * 统计日志存储top10
     * @return
     */
    List<Map<String, Object>> collectTopCount(@Param("fromDay") String fromDay, @Param("toDay") String toDay);

    /**
     * 统计日志存储趋势
     */
    List<Map<String, Object>> collectTrend(@Param("fromDay") String fromDay, @Param("toDay") String toDay, @Param("tailId") Long tailId);

    /**
     * 统计space日志存储趋势
     */
    List<Map<String, Object>> collectSpaceTrend(@Param("fromDay") String fromDay, @Param("toDay") String toDay);


    /**
     * 此天是否已统计
     * @param day
     * @return
     */
    Long isLogtailCountDone(@Param("day") String day);

    /**
     * 删除指定日期之前的数据
     * @param day
     */
    void deleteBeforeDay(@Param("day") String day);

    /**
     * 删除此天的统计数据
     * @param day
     */
    void deleteThisDay(@Param("day") String day);

    /**
     * space日志统计量top10
     * @param fromDay
     * @param toDay
     * @return
     */
    List<Map<String, Object>> collectSpaceCount(@Param("fromDay") String fromDay, @Param("toDay") String toDay);

    /**
     * app日志量统计
     */
    List<Map<String, Object>> collectAppLog(@Param("day") String day, @Param("threshold") Long threshold, @Param("source") String source);
}
