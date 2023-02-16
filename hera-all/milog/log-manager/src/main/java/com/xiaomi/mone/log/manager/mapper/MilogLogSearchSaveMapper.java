package com.xiaomi.mone.log.manager.mapper;

import com.xiaomi.mone.log.manager.model.pojo.MilogLogSearchSaveDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-03-29
 */
public interface MilogLogSearchSaveMapper extends BaseMapper<MilogLogSearchSaveDO> {

    List<MilogLogSearchSaveDO> selectByStoreId(@Param(value = "storeId") Long storeId, @Param(value = "startIndex") Integer startIndex, @Param(value = "pageSize") Integer pageSize);

    Long countByStoreId(@Param(value = "storeId") Long storeId);

    Long countByStoreAndName(@Param(value = "storeId") Long storeId, @Param(value = "name") String name);

    int removeById(@Param(value = "id") Long id);
}
