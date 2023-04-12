package com.xiaomi.mone.log.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogstailDO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-01-10
 */
public interface MilogLogstailMapper extends BaseMapper<MilogLogstailDO> {
    /**
     * 获取统计日志条数所用的tail
     * @return
     */
    List<Map<String, Object>> getAllTailForCount();
}
