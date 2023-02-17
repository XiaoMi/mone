package com.xiaomi.mone.log.manager.mapper;

import com.xiaomi.mone.log.manager.model.pojo.LogAnalyseDashboardDO;
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
public interface MilogAnalyseDashboardMapper extends BaseMapper<LogAnalyseDashboardDO> {

    List<LogAnalyseDashboardDO> getByStoreId(@Param(value = "storeId") Long storeId);

}
