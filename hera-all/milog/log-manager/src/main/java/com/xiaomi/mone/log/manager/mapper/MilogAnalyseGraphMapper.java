package com.xiaomi.mone.log.manager.mapper;

import com.xiaomi.mone.log.manager.model.dto.DashboardGraphDTO;
import com.xiaomi.mone.log.manager.model.dto.GraphDTO;
import com.xiaomi.mone.log.manager.model.pojo.LogAnalyseGraphDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.log.manager.model.vo.GraphQuery;
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
public interface MilogAnalyseGraphMapper extends BaseMapper<LogAnalyseGraphDO> {

    List<DashboardGraphDTO> getDashboardGraph(@Param(value = "dashboardId") Long dashboardId);

    List<GraphDTO> search(@Param(value = "query") GraphQuery query);
}
