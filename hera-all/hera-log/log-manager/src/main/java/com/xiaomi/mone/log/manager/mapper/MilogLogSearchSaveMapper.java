package com.xiaomi.mone.log.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.log.manager.model.dto.SearchSaveDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogSearchSaveDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-03-29
 */
public interface MilogLogSearchSaveMapper extends BaseMapper<MilogLogSearchSaveDO> {

    List<MilogLogSearchSaveDO> selectByStoreId(@Param(value = "storeId") Long storeId, @Param(value = "startIndex") Integer startIndex, @Param(value = "pageSize") Integer pageSize);

    Long countByStoreId(@Param(value = "storeId") Long storeId);

    Long countByStoreAndName(@Param(value = "name") String name, @Param(value = "creator") String creator);

    int removeById(@Param(value = "id") Long id);

    List<SearchSaveDTO> selectByCreator(@Param(value = "creator") String creator, @Param(value = "sort") Integer sort);

    Integer getMaxOrder(@Param(value = "creator") String creator, @Param(value = "sort") Integer sort);

    Integer isMyFavouriteStore(@Param(value = "creator") String creator, @Param(value = "storeId") Long storeId);

    Integer isMyFavouriteTail(@Param(value = "creator") String creator, @Param(value = "tailId") String tailId);

    List<MilogLogSearchSaveDO> getAll();
}
