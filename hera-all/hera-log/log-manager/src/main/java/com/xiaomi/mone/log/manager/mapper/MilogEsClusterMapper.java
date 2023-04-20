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
    List<MilogEsClusterDO> selectByArea(@Param("area") String area);

    List<MilogEsClusterDO> selectByAlias(@Param("alias") String alias);
}
