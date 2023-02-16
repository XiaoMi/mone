package com.xiaomi.mone.log.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.log.manager.model.pojo.LogEsClusterDO;
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
public interface MilogEsClusterMapper extends BaseMapper<LogEsClusterDO> {

    /**
     * 获取tag对应的ES 客户端
     *
     * @param tag
     * @return
     */
    List<LogEsClusterDO> selectByTag(@Param("tag") String tag);

    /**
     * 查找所有
     *
     * @return
     */
    List<LogEsClusterDO> selectAll();

    /**
     * 获取region对应的ES客户端
     *
     * @param region
     * @return
     */
    LogEsClusterDO selectByRegion(@Param("region") String region);

    /**
     * 获取area对应的ES客户端
     *
     * @param area
     * @return
     */
    List<LogEsClusterDO> selectByArea(@Param("area") String area);

    List<LogEsClusterDO> selectByAlias(@Param("alias") String alias);
}
