package com.xiaomi.mone.log.manager.mapper;

import com.xiaomi.mone.log.manager.model.pojo.LogDashboardGraphRefDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-08-18
 */
@Mapper
public interface MilogAnalyseDashboardGraphRefMapper extends BaseMapper<LogDashboardGraphRefDO> {

    List<LogDashboardGraphRefDO> getByDashboardId(@Param(value = "dashboardId") Long dashboardId);

    void deleteGraphRef(@Param(value = "graphId") Long graphId);

    Long isRefed(@Param(value = "dashboardId") Long dashboardId, @Param(value = "graphId") Long graphId);

    int delRef(@Param(value = "dashboardId") Long dashboardId, @Param(value = "graphId") Long graphId);

    LogDashboardGraphRefDO getRef(@Param(value = "dashboardId") Long dashboardId, @Param(value = "graphId") Long graphId);
}
